package com.theathletic.analytics

import android.content.Context
import android.util.Log
import com.datadog.android.Datadog
import com.datadog.android.core.configuration.Configuration
import com.datadog.android.core.configuration.Credentials
import com.datadog.android.log.Logger
import com.datadog.android.privacy.TrackingConsent
import com.datadog.android.timber.DatadogTree
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import timber.log.Timber

interface DatadogLogger {
    val analyticsLogger: Logger
    val crashLogger: Logger

    fun isInitialized(): Boolean
    fun setVerbosity(logLevel: Int)
}

class DatadogLoggerImpl @AutoKoin(Scope.SINGLE) constructor(context: Context) : DatadogLogger {
    private val logBuilder = Logger.Builder()
        .setServiceName(context.packageName)
        .setDatadogLogsEnabled(true)

    override val analyticsLogger: Logger
    override val crashLogger: Logger

    init {
        val configuration = Configuration.Builder(
            logsEnabled = true,
            tracesEnabled = false,
            rumEnabled = false,
            crashReportsEnabled = true
        ).build()
        val env = if (BuildConfig.DEBUG) { "staging" } else "prod"
        val credentials = Credentials(CLIENT_TOKEN, env, BuildConfig.FLAVOR, APPLICATION_ID)

        Datadog.initialize(context, credentials, configuration, TrackingConsent.GRANTED)

        analyticsLogger = logBuilder.setNetworkInfoEnabled(true).build()
        analyticsLogger.addTag(ANALYTICS_LOGGER_TAG)

        crashLogger = logBuilder.setDatadogLogsMinPriority(Log.ERROR).build()
        crashLogger.addTag(CRASH_LOGGER_TAG)

        val timberLogger = logBuilder.setDatadogLogsMinPriority(Log.WARN).build()
        Timber.plant(DatadogTree(timberLogger))
    }

    override fun isInitialized(): Boolean = Datadog.isInitialized()

    override fun setVerbosity(logLevel: Int) {
        Datadog.setVerbosity(logLevel)
    }

    companion object {
        private const val ANALYTICS_LOGGER_TAG = "android:analytics"
        private const val CRASH_LOGGER_TAG = "android:crashloghandler"

        private const val CLIENT_TOKEN = "pub275e942b390145397e80e4f15a2bf0dd"
        private const val APPLICATION_ID = "2f8a0737-5f8a-4112-bfc6-34eb45c645e4"
    }
}