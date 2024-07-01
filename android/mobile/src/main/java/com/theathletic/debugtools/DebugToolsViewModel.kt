package com.theathletic.debugtools

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Process
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.webkit.URLUtil
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.databinding.ObservableLong
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Configuration
import androidx.work.testing.WorkManagerTestInitHelper
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.theathletic.AthleticApplication
import com.theathletic.AthleticConfig
import com.theathletic.R
import com.theathletic.analytics.AnalyticsTracker
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.context.DeepLinkParams
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Named
import com.theathletic.brackets.data.remote.ReplayGameUseCase
import com.theathletic.compass.Experiment
import com.theathletic.compass.Variant
import com.theathletic.compass.codegen.CompassExperiment
import com.theathletic.event.DataChangeEvent
import com.theathletic.event.ToastEvent
import com.theathletic.extension.ObservableString
import com.theathletic.extension.ObservableStringNonNull
import com.theathletic.extension.extGetColor
import com.theathletic.extension.extGetString
import com.theathletic.featureswitches.FeatureSwitch
import com.theathletic.links.deep.DeeplinkEventProducer
import com.theathletic.onboarding.data.OnboardingRepository
import com.theathletic.remoteconfig.local.RemoteConfigEntry
import com.theathletic.user.UserManager
import com.theathletic.utility.AdPreferences
import com.theathletic.utility.Event
import com.theathletic.utility.Preferences
import com.theathletic.viewmodel.BaseViewModel
import com.theathletic.widget.StatefulLayout
import com.theathletic.worker.UserUpdateScheduler
import java.io.File
import java.util.Calendar
import java.util.Date
import java.util.SortedMap
import kotlin.collections.set
import kotlin.math.max
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.core.component.KoinComponent

@SuppressLint("StaticFieldLeak")
class DebugToolsViewModel @AutoKoin constructor(
    private val debugToolsDao: DebugToolsDao,
    private val debugPreferences: DebugPreferences,
    private val adPreferences: AdPreferences,
    @Named("application-context") private val applicationContext: Context,
    private val updateUserScheduler: UserUpdateScheduler,
    private val analytics: Analytics,
    onboardingRepository: OnboardingRepository,
    private val deeplinkEventProducer: DeeplinkEventProducer,
    private val analyticsTracker: AnalyticsTracker,
    private val replayGameUseCase: ReplayGameUseCase
) : BaseViewModel(), LifecycleObserver, KoinComponent {
    val state = ObservableInt(StatefulLayout.CONTENT)
    val toolsRecyclerList: ObservableArrayList<DebugToolsBaseItem> = ObservableArrayList()

    init {
        initSettings()
        viewModelScope.launch { onboardingRepository.fetchSurvey() }
    }

    private fun initSettings() {
        toolsRecyclerList.clear()
        toolsRecyclerList.add(generateAppVersionHeader())
        val remoteConfigEntries: HashMap<String, RemoteConfigEntity> = HashMap()

        // Tt add local values first so they can be overridden by actual value from firebase
        remoteConfigEntries.putAll(getAllLocalConfigs())

        // Tt we allow modification of only Boolean values for now
        FirebaseRemoteConfig.getInstance().all.forEach { remoteConfigEntry ->
            var booleanValue: Boolean? = null

            try {
                booleanValue = remoteConfigEntry.value.asBoolean()
            } catch (e: IllegalArgumentException) {
                // Do not log any error - it is pointless
            }

            booleanValue?.let {
                // Tt show only those values which are currently supported in the app
                if (FeatureSwitch.values().map { it.key }.contains(remoteConfigEntry.key) ||
                    RemoteConfigEntry.values().map { it.value }
                        .contains(remoteConfigEntry.key)
                ) {
                    remoteConfigEntries.put(
                        remoteConfigEntry.key,
                        RemoteConfigEntity(remoteConfigEntry.key, it, ObservableBoolean(true))
                    )
                }
            }
        }

        // Tt replacing default entries in HashMap by values from DB
        debugToolsDao.getModifiedRemoteConfigSync().forEach { databaseEntry ->
            remoteConfigEntries.put(databaseEntry.entryKey, databaseEntry)
        }

        addRemoteConfigEntries(remoteConfigEntries.toSortedMap())
        addCustomItems()
    }

    // Tt this may not accurately represent actual value - there might be some predefined value in XML
    private fun getAllLocalConfigs(): HashMap<String, RemoteConfigEntity> {
        val localConfigEntries: HashMap<String, RemoteConfigEntity> = HashMap()

        // not including FirebaseRemoteConfigEntry - it does not contain only booleans
        FeatureSwitch.values().forEach {
            localConfigEntries.put(
                it.key,
                RemoteConfigEntity(it.key, false, ObservableBoolean(false))
            )
        } // defaulting to default false value

        return localConfigEntries
    }

    private fun addRemoteConfigEntries(remoteConfigEntries: SortedMap<String, RemoteConfigEntity>) {
        toolsRecyclerList.removeAll { it is RemoteConfigEntity }
        toolsRecyclerList.addAll(1, remoteConfigEntries.map { it.value })
        toolsRecyclerList.add(
            1,
            DebugToolsSectionHeader(R.string.debug_tools_section_feature_switches.extGetString())
        )
        toolsRecyclerList.add(
            DebugToolsCustomButton(
                "Reset all local overrides",
                onButtonClick = {
                    debugToolsDao.clearModifiedRemoteConfig()
                    sendEvent(ToastEvent("All local changes cleared!"))
                    initSettings()
                }
            )
        )
        sendEvent(DataChangeEvent())
    }

    private fun generateAppVersionHeader() = try {
        val info = applicationContext
            .packageManager
            .getPackageInfo(applicationContext.packageName, 0)
        DebugToolsSectionHeader("App Version: ${info.versionName} (${info.versionCode})")
    } catch (e: PackageManager.NameNotFoundException) {
        DebugToolsSectionHeader("Missing Version Info")
    }

    @Suppress("LongMethod")
    private fun addCustomItems() {
        // Tt Custom switches
        toolsRecyclerList.add(DebugToolsSectionHeader(R.string.debug_tools_section_custom_switches.extGetString()))

        toolsRecyclerList.add(
            DebugToolsCustomSwitch(
                title = "Force unsubscribed status",
                state = MutableLiveData(debugPreferences.forceUnsubscribedStatus),
                setToOn = { debugPreferences.forceUnsubscribedStatus = true },
                setToOff = { debugPreferences.forceUnsubscribedStatus = false }
            )
        )

        toolsRecyclerList.add(
            DebugToolsCustomSwitch(
                title = "Enable analytics toasts",
                state = MutableLiveData(this.debugPreferences.areToastsEnabled),
                setToOn = { this.debugPreferences.areToastsEnabled = true },
                setToOff = { this.debugPreferences.areToastsEnabled = false }
            )
        )

        toolsRecyclerList.add(
            DebugToolsCustomSwitch(
                title = "Show noisy events",
                state = MutableLiveData(debugPreferences.showNoisyEvents),
                setToOn = { debugPreferences.showNoisyEvents = true },
                setToOff = { debugPreferences.showNoisyEvents = false }
            )
        )

        toolsRecyclerList.add(
            DebugToolsCustomSwitch(
                title = "Disable article caching",
                state = MutableLiveData(debugPreferences.disableArticleCaching),
                setToOn = { debugPreferences.disableArticleCaching = true },
                setToOff = { debugPreferences.disableArticleCaching = false }
            )
        )

        toolsRecyclerList.add(
            DebugToolsCustomSwitch(
                title = "Enable debug billing tools",
                state = MutableLiveData(debugPreferences.enableDebugBillingTools),
                setToOn = { debugPreferences.enableDebugBillingTools = true },
                setToOff = { debugPreferences.enableDebugBillingTools = false }
            )
        )

        toolsRecyclerList.add(
            DebugToolsCustomSwitch(
                title = "Force not accepted code of conduct status",
                state = MutableLiveData(debugPreferences.forceNotAcceptedCodeOfConduct),
                setToOn = { debugPreferences.forceNotAcceptedCodeOfConduct = true },
                setToOff = { debugPreferences.forceNotAcceptedCodeOfConduct = false }
            )
        )

        fillAdvertisementOptions(toolsRecyclerList)
        fillPrivacyOptions(toolsRecyclerList)

        // Tt Custom Compass variant switches
        fillCompassVariantSwitches(toolsRecyclerList)

        // Tt Custom buttons - temporary
        toolsRecyclerList.add(DebugToolsSectionHeader(R.string.debug_tools_section_custom_buttons_temporary.extGetString()))

        toolsRecyclerList.add(
            DebugToolsCustomButton(
                "Test add notification source data",
                onButtonClick = {
                    val testString =
                        "{\"defaultAction\":{\"type\":\"openApp\"},\"attachment-url\":\"\",\"campaignId\":1086282,\"isGhostPush\":false,\"messageId\":\"102d0ad52681415993c62fd1d8ff94b3\",\"actionButtons\":[],\"templateId\":1522382}"
                    val json = JSONObject(testString)
                    val pushMetablobParams = HashMap<String, String>()
                    if (json.has("campaignId")) {
                        pushMetablobParams.put("campaignId", json.getLong("campaignId").toString())
                    }
                    if (json.has("isGhostPush")) {
                        pushMetablobParams.put(
                            "isGhostPush",
                            json.getBoolean("isGhostPush").toString()
                        )
                    }
                    val source = "iterable_push"

                    analytics.updateContext(DeepLinkParams(source, pushMetablobParams))
                    sendEvent(ToastEvent("Success!"))
                }
            )
        )

        toolsRecyclerList.add(
            DebugToolsCustomButton(
                "Test delete notif source data",
                onButtonClick = {
                    analytics.updateContext(null)
                    sendEvent(ToastEvent("cleared analyticsDeeplinkParameters from pref&analytics"))
                }
            )
        )

        toolsRecyclerList.add(
            DebugToolsCustomButton(
                "Create Live Room",
                onButtonClick = { sendEvent(StartCreateLiveRoomActivity) }
            )
        )

        toolsRecyclerList.add(
            DebugToolsCustomButton(
                "Audio Room Demo Screen",
                onButtonClick = { sendEvent(StartAudioRoomDemoActivity) }
            )
        )

        // Tt Custom buttons - permanent
        toolsRecyclerList.add(DebugToolsSectionHeader(R.string.debug_tools_section_custom_buttons_permanent.extGetString()))

        toolsRecyclerList.add(
            DebugToolsBaseUrlOverride(
                "Athletic Base rest URL",
                ObservableString(AthleticConfig.REST_BASE_URL),
                onSetClick = {
                    if (URLUtil.isValidUrl(it)) {
                        debugPreferences.baseUrlOverride = it
                        sendEvent(ToastEvent("New base URL set: ${debugPreferences.baseUrlOverride}"))
                        killAndRestartApp()
                    } else {
                        sendEvent(ToastEvent("Invalid URL schema"))
                    }
                },
                onResetClick = {
                    debugPreferences.baseUrlOverride = null
                    sendEvent(ToastEvent("Base URL is default: ${AthleticConfig.REST_BASE_URL}"))
                    killAndRestartApp()
                }
            )
        )

        toolsRecyclerList.add(
            DebugToolsBaseUrlOverride(
                "Athletic Base GraphQL URL",
                ObservableString(AthleticConfig.GRAPHQL_SERVER_BASE_URL),
                onSetClick = {
                    if (URLUtil.isValidUrl(it)) {
                        debugPreferences.baseGraphQLUrlOverride = it
                        sendEvent(ToastEvent("New graphql URL set: ${debugPreferences.baseGraphQLUrlOverride}"))
                        killAndRestartApp()
                    } else {
                        sendEvent(ToastEvent("Invalid URL schema"))
                    }
                },
                onResetClick = {
                    debugPreferences.baseGraphQLUrlOverride = null
                    sendEvent(ToastEvent("Base URL is default: ${AthleticConfig.GRAPHQL_SERVER_BASE_URL}"))
                    killAndRestartApp()
                }
            )
        )

        toolsRecyclerList.apply {
            add(
                DebugToolsSendDeeplink(
                    title = "Test Deeplink URL",
                    deeplinkUrl = ObservableString("theathletic://"),
                    onSendClick = {
                        viewModelScope.launch {
                            deeplinkEventProducer.emit(it)
                        }
                    }
                )
            )

            add(
                DebugToolsSendDeeplink(
                    title = "Replay Game",
                    deeplinkUrl = ObservableString(""),
                    onSendClick = {
                        viewModelScope.launch {
                            replayGameUseCase.invoke(it)
                        }
                    }
                )
            )

            add(
                DebugToolsCountdown(
                    title = "Temp Ban From Comments Until...",
                    bumpButtonLabel = "+5 Min",
                    onBumpCountdown = { endTime: ObservableLong ->
                        val baseTime = Calendar.getInstance().apply<Calendar> {
                            time = Date(max(endTime.get(), Date().time))
                        }
                        val newTime: Long = baseTime.let { it ->
                            it.add(Calendar.MINUTE, 5)
                            it.timeInMillis
                        }
                        endTime.set(newTime)
                        debugPreferences.tempBanEndTime = newTime
                    },
                    onResetCountdown = { endTime: ObservableLong ->
                        val newTime = 0L
                        endTime.set(newTime)
                        debugPreferences.tempBanEndTime = newTime
                    }
                ).apply {
                    endTime.set(debugPreferences.tempBanEndTime)
                }
            )

            add(
                DebugToolsCustomButton(
                    "Design Systems Stylesheet",
                    onButtonClick = { sendEvent(StartStylesheet) }
                )
            )

            add(
                DebugToolsCustomButton(
                    "Open Analytics Logs",
                    onButtonClick = { sendEvent(StartAnalyticsHistoryLogsActivity) }
                )
            )

            add(
                DebugToolsCustomButton(
                    title = "Open User Info Page",
                    onButtonClick = { sendEvent(StartDebugUserInfoActivity) }
                )
            )

            add(
                DebugToolsCustomButton(
                    title = "Run UpdateUserWorker",
                    onButtonClick = {
                        enableSynchronousWorkManager()
                        updateUserScheduler.schedule(applicationContext)
                    }
                )
            )

            add(
                DebugToolsCustomButton(
                    title = "Run AnalyticsUploadWorker",
                    onButtonClick = {
                        analyticsTracker.startOneOffUploadWork()
                    }
                )
            )
        }

        toolsRecyclerList.add(
            DebugToolsCustomButton(
                "Clear Cache Dir+Glide image cache",
                onButtonClick = {
                    val imageCacheDirectory =
                        File(AthleticApplication.getContext().cacheDir, "image_manager_disk_cache")
                    imageCacheDirectory.deleteRecursively()
                    sendEvent(ClearGlideCacheEvent())
                    sendEvent(ToastEvent("Cleared! - Force kill app for full effect."))
                }
            )
        )

        toolsRecyclerList.add(
            DebugToolsCustomButton(
                "Force crash app",
                onButtonClick = {
                    ArrayList<Int>()[42]
                }
            )
        )

        toolsRecyclerList.add(
            DebugToolsCustomButton(
                "Reset feed \"last refreshed\" to 0",
                onButtonClick = {
                    Preferences.clearFeedRefreshData()
                    sendEvent(ToastEvent("Done!"))
                }
            )
        )

        toolsRecyclerList.add(
            DebugToolsCustomButton(
                "Open Billing Config",
                onButtonClick = {
                    sendEvent(StartBillingConfigActivity)
                }
            )
        )

        toolsRecyclerList.add(
            DebugToolsCustomButton(
                "Clear user preferences and logout", R.color.red.extGetColor(),
                onButtonClick = {
                    Preferences.clear()
                    UserManager.logOutWithAuthenticationStart()
                }
            )
        )

        toolsRecyclerList.add(
            DebugToolsCustomButton(
                "Force kill & restart app (to apply some changes)", R.color.red.extGetColor(),
                onButtonClick = {
                    killAndRestartApp()
                }
            )
        )

        sendEvent(DataChangeEvent())
    }

    fun onFeatureSwitchChange(entryKey: String) {
        val remoteConfigEntity = toolsRecyclerList.filterIsInstance<RemoteConfigEntity>()
            .firstOrNull { it.entryKey == entryKey }
        remoteConfigEntity?.let {
            remoteConfigEntity.entryValue = !remoteConfigEntity.entryValue
            remoteConfigEntity.certainValue.set(true)
            debugToolsDao.insertModifiedRemoteConfig(remoteConfigEntity)
        }
    }

    fun compassDefaultExperimentVariantChange(experiment: Experiment, variant: Variant?) {
        val selectedVariantMap = this.debugPreferences.compassSelectedVariantMap.toMutableMap()

        if (variant?._name == null) {
            selectedVariantMap.remove(experiment.name)
        } else {
            selectedVariantMap[experiment.name] = variant._name
        }

        this.debugPreferences.compassSelectedVariantMap = selectedVariantMap
    }

    /**
     * Even when unused, keep this function for future usecase of adding
     * a temp button to test a worker in synchronous execution mode
     */
    private fun enableSynchronousWorkManager() {
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(com.theathletic.worker.SynchronousExecutor())
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(applicationContext, config)
    }

    private fun fillCompassVariantSwitches(
        toolsRecyclerList: ObservableArrayList<DebugToolsBaseItem>
    ) {
        val selectedVariantMap = this.debugPreferences.compassSelectedVariantMap
        val serverDefaultString =
            R.string.debug_tools_compass_selected_variant_server_default.extGetString()

        // TT Add Section Header
        toolsRecyclerList.add(DebugToolsSectionHeader(R.string.debug_tools_section_compass_switches.extGetString()))

        // Tt Iterate trough all experiments and generate variant pickers for them
        CompassExperiment.experimentMap.forEach { mapEntry ->
            val experiment = mapEntry.value

            // Tt Add Variant Header
            toolsRecyclerList.add(DebugToolsSectionSubHeader(experiment.name))

            // Tt Add Active Variant switch item
            val observableActiveVariantName = ObservableStringNonNull(
                selectedVariantMap[experiment.name]
                    ?: serverDefaultString
            )
            toolsRecyclerList.add(
                DebugToolsCompassVariantSelectText(
                    observableActiveVariantName,
                    experiment
                )
            )
        }
    }

    private fun fillAdvertisementOptions(toolsRecyclerList: ObservableArrayList<DebugToolsBaseItem>) {
        toolsRecyclerList.add(DebugToolsSectionHeader(R.string.debug_tools_section_advertising.extGetString()))

        val observableAdKeyword = ObservableString(
            adPreferences.adKeyword
        )
        toolsRecyclerList.add(
            DebugToolsTextInput(
                R.string.debug_tools_ad_keyword_title.extGetString(),
                observableAdKeyword,
                object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {}

                    override fun afterTextChanged(s: Editable?) {}

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        val text = s.toString()
                        observableAdKeyword.set(text)
                        adPreferences.adKeyword = text
                    }
                }
            )
        )
    }

    private fun fillPrivacyOptions(toolsRecyclerList: ObservableArrayList<DebugToolsBaseItem>) {
        toolsRecyclerList.add(DebugToolsSectionHeader(R.string.debug_tools_privacy.extGetString()))

        toolsRecyclerList.add(
            DebugToolsCustomSwitch(
                title = "Enable privacy geo location",
                state = MutableLiveData(this.adPreferences.privacyEnabled),
                setToOn = { this.adPreferences.privacyEnabled = true },
                setToOff = { this.adPreferences.privacyEnabled = false }
            )
        )

        val countryCodeObservable = ObservableString(
            adPreferences.privacyCountryCode
        )
        val stateAbbrObservable = ObservableString(
            adPreferences.privacyStateAbbr
        )
        toolsRecyclerList.add(
            DebugToolsTextInput(
                R.string.debug_tools_privacy_country_code.extGetString(),
                countryCodeObservable,
                object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {}

                    override fun afterTextChanged(s: Editable?) {}

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        val text = s.toString()
                        countryCodeObservable.set(text)
                        adPreferences.privacyCountryCode = text
                    }
                }
            )
        )
        toolsRecyclerList.add(
            DebugToolsTextInput(
                R.string.debug_tools_privacy_state.extGetString(),
                stateAbbrObservable,
                object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {}

                    override fun afterTextChanged(s: Editable?) {}

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        val text = s.toString()
                        stateAbbrObservable.set(text)
                        adPreferences.privacyStateAbbr = text
                    }
                }
            )
        )
    }

    private class DebugTextWatcher(val observableString: ObservableString) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            observableString.set(s.toString())
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun killAndRestartApp() {
        Handler().postDelayed(
            {
                val startActivity =
                    applicationContext.packageManager.getLaunchIntentForPackage(AthleticApplication.getContext().packageName)
                val pendingIntentId = 42
                val pendingIntent = PendingIntent.getActivity(
                    AthleticApplication.getContext(),
                    pendingIntentId,
                    startActivity,
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val alarm = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
                alarm.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pendingIntent)
                Process.killProcess(Process.myPid())
            },
            1000
        )
    }

    internal object StartDebugUserInfoActivity : Event()
    internal object StartAudioRoomDemoActivity : Event()
    internal object StartCreateLiveRoomActivity : Event()
    internal object StartAnalyticsHistoryLogsActivity : Event()
    internal object StartStylesheet : Event()
    internal class StartCodeOfConductDevTools : Event()
    internal class ClearGlideCacheEvent : Event()
    internal object StartBillingConfigActivity : Event()
}