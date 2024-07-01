package com.theathletic.compass

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.theathletic.BuildConfig
import com.theathletic.compass.codegen.VariantKey
import com.theathletic.datetime.TimeProvider
import com.theathletic.network.ResponseStatus
import com.theathletic.repository.safeApiRequest
import com.theathletic.user.IUserManager
import com.theathletic.user.IUserManager.Companion.NO_USER
import com.theathletic.utility.IPreferences
import com.theathletic.utility.LocaleUtility
import com.theathletic.utility.coroutines.DispatcherProvider
import com.theathletic.utility.device.DeviceInfo
import com.theathletic.utility.logging.ICrashLogHandler
import java.util.Calendar
import java.util.Calendar.HOUR
import java.util.Date
import java.util.HashMap
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import timber.log.Timber

/**,
 * The CompassClient is responsible for getting a remote experiment configuration from the server, persisting that
 * configuration, and making that configuration available to other consumers within the application.
 */

@Suppress("LongParameterList")
class CompassClient(
    private val compassExperiment: ICompassExperiment,
    private val compassPreferences: ICompassPreferences,
    private val gson: Gson,
    private val crashLogHandler: ICrashLogHandler,
    private val deviceInfo: DeviceInfo,
    private val compassApi: CompassApi,
    private val dispatcherProvider: DispatcherProvider,
    private val scope: CoroutineScope = CoroutineScope(dispatcherProvider.io),
    private val userManager: IUserManager,
    private val localeUtility: LocaleUtility,
    private val timeProvider: TimeProvider,
    private val preferences: IPreferences
) {
    var configState: AtomicReference<ConfigState> = AtomicReference(ConfigState.NOT_POPULATED)
    private val lock = Mutex()

    enum class ConfigState { NOT_POPULATED, IS_POPULATING, POPULATED }

    companion object {
        const val TIMEOUT_MS = 2500L
        const val HOURS_TO_WAIT_BETWEEN_NETWORK_FETCHES = 4
        const val FORCE_REFRESH_CHANGE_DATE = "1980-01-01T08:00:00Z"
    }

    class ConcurrentCompassLoadConfigException : RuntimeException("Cannot call loadConfig while the client is already loading a configuration")

    /**
     * Should only be called once at app startup and once upon authentication.
     */
    suspend fun loadConfig(
        isUserLoggedIn: Boolean,
        forceFetchFromNetwork: Boolean = false
    ) {
        lock.withLock {
            val lastState = configState.get()
            if (lastState == ConfigState.IS_POPULATING) {
                ConcurrentCompassLoadConfigException().run {
                    if (BuildConfig.DEBUG) {
                        throw this
                    } else {
                        crashLogHandler.logException(this)
                        return@loadConfig
                    }
                }
            }
            configState.set(ConfigState.IS_POPULATING)
        }
        val savedConfig = getConfigFromDisk()
        if ((savedConfig.timestamp.isBlank() && !isUserLoggedIn) || forceFetchFromNetwork) {
            initializeFromNetwork(savedConfig)
        } else {
            initializeFromDisk(
                savedConfig,
                compassPreferences.lastUpdatedInMillis
            )
        }
        cleanExposureSet()
        configState.set(ConfigState.POPULATED)
    }

    @SuppressLint("CheckResult")
    fun postExposure(
        experiment: Experiment,
        userId: Long
    ) {
        if (configState.get() != ConfigState.POPULATED) throw CompassNotInitializedException()
        if (experiment.exists && !compassPreferences.exposureSet.contains(experiment.name)) {
            scope.launch {
                val response = safeApiRequest(
                    coroutineContext = EmptyCoroutineContext,
                    block = {
                        experiment.activeVariant?.let {activeVariant ->
                            compassApi.postExposure(
                                CompassApi.ExposedRequest(
                                    experimentId = experiment.name,
                                    variantId = activeVariant._name,
                                    identity = CompassApi.Identity(
                                        deviceId = deviceInfo.deviceId,
                                        userId = if (userId != NO_USER) userId else null
                                    )
                                )
                            )
                        }
                    }
                )
                if (response is Error) {
                    crashLogHandler.logException(response)
                } else {
                    compassPreferences.exposureSet += experiment.name
                }
            }
        }
    }

    /**
     * Returns true if we successfully initialized from the network
     */
    private suspend fun initializeFromNetwork(
        lastSavedConfig: CompassApi.CompassConfigResponse
    ) {
        val config = try {
            getConfigFromNetworkAndSave(FORCE_REFRESH_CHANGE_DATE)
        } catch (e: Exception) {
            lastSavedConfig
        }
        val experimentMap = constructExperimentMapFromConfig(config)
        updateExperimentMap(experimentMap)
    }

    @SuppressLint("CheckResult", "SimpleDateFormat")
    private suspend fun initializeFromDisk(
        lastSavedConfig: CompassApi.CompassConfigResponse,
        lastUpdatedInMillis: Long
    ) {
        try {
            val timeAtWhichToRefresh = Calendar.getInstance().apply {
                time = Date(lastUpdatedInMillis)
                add(HOUR, HOURS_TO_WAIT_BETWEEN_NETWORK_FETCHES)
            }
            if (timeProvider.currentTimeMs > timeAtWhichToRefresh.timeInMillis) {
                scope.launch {
                    try {
                        val timeStamp = if (lastSavedConfig.timestamp.isNotBlank()) {
                            lastSavedConfig.timestamp
                        } else {
                            FORCE_REFRESH_CHANGE_DATE
                        }
                        getConfigFromNetworkAndSave(
                            timeStamp,
                            networkTimeout = 10_000L
                        )
                    } catch (e: Exception) {
                        crashLogHandler.logException(e)
                    }
                }
            }
        } catch (e: Exception) {
            crashLogHandler.logException(e)
        }
        val experimentMap = constructExperimentMapFromConfig(lastSavedConfig)
        updateExperimentMap(experimentMap)
    }

    private suspend fun getConfigFromNetworkAndSave(
        lastChangeDate: String,
        networkTimeout: Long = TIMEOUT_MS
    ): CompassApi.CompassConfigResponse = withTimeout(networkTimeout) {
        val deviceUserId = userManager.getCurrentUserId().let { if (it != NO_USER) it else null }
        val response = safeApiRequest(coroutineContext = EmptyCoroutineContext) {
            compassApi.getConfig(
                lastChangeDate = lastChangeDate,
                deviceId = deviceInfo.deviceId,
                osVersion = deviceInfo.osSdkVersion.toString(),
                deviceModel = deviceInfo.model,
                deviceBrand = deviceInfo.brand,
                deviceCarrier = deviceInfo.carrier,
                requestorType = if (deviceInfo.isTablet) "tablet" else "mobile",
                localeString = localeUtility.acceptLanguage,
                userId = deviceUserId,
                devicePushToken = preferences.pushTokenKey
            )
        }
        when (response) {
            is ResponseStatus.Success -> {
                val configResponseJsonString = gson.toJson(response.body)
                Timber.d("writing configResponse to disk: $configResponseJsonString")
                compassPreferences.lastConfig = configResponseJsonString
                compassPreferences.lastUpdatedInMillis = timeProvider.currentTimeMs
                response.body
            }
            is ResponseStatus.Error -> {
                throw response.throwable
            }
        }
    }

    private fun updateExperimentMap(newMap: HashMap<String, Experiment>): Boolean {
        compassExperiment.experimentMap = newMap
        newMap.entries.forEach { it.value.client = this@CompassClient }
        return true
    }

    private fun constructExperimentMapFromConfig(config: CompassApi.CompassConfigResponse): HashMap<String, Experiment> {
        val newMap: HashMap<String, Experiment> = HashMap(compassExperiment.experimentMap.size)
        compassExperiment.experimentMap.forEach { newMap[it.key] = it.value }
        config.experiments.forEach { experiment ->
            val clientExperiment = compassExperiment.experimentMap[experiment.id]
            if (clientExperiment != null) {
                val fieldMap = mutableMapOf<String, FieldResponse>()
                experiment.data?.forEach { fieldMap[it.key] = it }
                val newVariant = compassExperiment.variantMap[VariantKey(experiment.id, experiment.variant)]
                if (newVariant != null) {
                    newMap[experiment.id] = clientExperiment.copy(
                        activeVariant = newVariant.populateFromFieldMap(fieldMap, crashLogHandler),
                        exists = true
                    )
                } else {
                    crashLogHandler.logException(VariantDoesNotExistException())
                }
            }
        }
        return newMap
    }

    private fun getConfigFromDisk(): CompassApi.CompassConfigResponse {
        val configJsonStr = compassPreferences.lastConfig
        if (configJsonStr.isNullOrEmpty()) {
            return CompassApi.CompassConfigResponse("", emptyList())
        }
        return try {
            val configResponse = gson.fromJson(configJsonStr, CompassApi.CompassConfigResponse::class.java)
            Timber.d("reading configResponse from disk: $configResponse")
            configResponse
        } catch (e: Exception) {
            crashLogHandler.logException(e)
            CompassApi.CompassConfigResponse("", emptyList())
        }
    }

    /**
     * Removes decommissioned experiments from the persisted set of experiment names the user has
     * been exposed to.
     */
    private fun cleanExposureSet() {
        val commissionedExperiments = compassExperiment.experimentMap.keys
        val decommissionedExperiments = compassPreferences.exposureSet subtract commissionedExperiments
        val cleanedExposureSet = compassPreferences.exposureSet - decommissionedExperiments
        compassPreferences.exposureSet = cleanedExposureSet
    }
}