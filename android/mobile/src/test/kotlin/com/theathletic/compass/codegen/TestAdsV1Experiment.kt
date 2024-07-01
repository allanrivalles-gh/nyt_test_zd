package com.theathletic.compass.codegen

import com.theathletic.BuildConfig
import com.theathletic.compass.CompassClient
import com.theathletic.compass.Experiment
import com.theathletic.compass.FieldResponse
import com.theathletic.compass.Variant
import com.theathletic.debugtools.DebugPreferences
import com.theathletic.user.UserManager
import com.theathletic.utility.logging.ICrashLogHandler

class TestAdsV1Experiment(
    override var name: String = "Ads v1",
    override var exists: Boolean = false,
    override var activeVariant: Variant? = null,
    override val crashLogHandler: ICrashLogHandler,
    override val debugPreferences: DebugPreferences
) : Experiment() {
    /**
     * Returns the active variant indicated by the server config, or CTRL if not able to obtain a
     * config
     */
    val variant: AdsV1ExperimentVariant
        get() {
            if (client == null || client?.configState?.get() != CompassClient.ConfigState.POPULATED) {
                val exception = IllegalAccessException("""The compass client configState must be POPULATED
          in order to reference a variant""")
                if (BuildConfig.DEBUG) {
                    throw exception
                } else {
                    crashLogHandler.logException(exception)
                }
            }

            if (debugPreferences.compassSelectedVariantMap[name] != null) {
                return when (debugPreferences.compassSelectedVariantMap[name]) {
                    "A" -> AdsV1ExperimentVariant.A()
                    else -> AdsV1ExperimentVariant.CTRL()
                }
            }

            if (exists) {
                return activeVariant as AdsV1ExperimentVariant
            }

            return AdsV1ExperimentVariant.CTRL()
        }

    override fun copy(activeVariant: Variant, exists: Boolean) =
        TestAdsV1Experiment(activeVariant = activeVariant, exists = exists, crashLogHandler =
        crashLogHandler, debugPreferences = debugPreferences)

    fun postExposure(userId: Long = UserManager.getCurrentUserId()) {
        client?.postExposure(this, userId)
    }

    sealed class AdsV1ExperimentVariant : Variant {
        data class CTRL(
            override val _name: String = "CTRL"
        ) : AdsV1ExperimentVariant() {
            override fun populateFromFieldMap(fieldMap: Map<String, FieldResponse>,
                                              crashLogHandler: ICrashLogHandler) = CTRL(
            )
        }

        data class A(
            override val _name: String = "A"
        ) : AdsV1ExperimentVariant() {
            override fun populateFromFieldMap(fieldMap: Map<String, FieldResponse>,
                                              crashLogHandler: ICrashLogHandler) = A(
            )
        }
    }
}