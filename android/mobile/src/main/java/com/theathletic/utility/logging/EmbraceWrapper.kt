package com.theathletic.utility.logging

import android.content.Context
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.featureswitches.FeatureSwitch
import com.theathletic.featureswitches.FeatureSwitches
import io.embrace.android.embracesdk.Embrace
import io.embrace.android.embracesdk.Severity

class EmbraceWrapper @AutoKoin(Scope.SINGLE) constructor(
    private val featureSwitches: FeatureSwitches
) : ICrashLogHandler {

    fun initialize(context: Context) {
        Embrace.getInstance().start(context)
    }

    override fun logException(e: Throwable) {
        trackException(e)
    }

    override fun trackException(
        throwable: Throwable,
        cause: String?,
        message: String?,
        log: String?
    ) {
        if (!featureSwitches.isFeatureEnabled(FeatureSwitch.EMBRACE_LOGGING)) return

        Embrace.getInstance().logException(
            throwable,
            Severity.ERROR,
            mapOf(
                "cause" to cause,
                "message" to message,
                "log" to log
            ).filterValues { it != null }
        )
    }
}