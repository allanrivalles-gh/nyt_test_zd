package com.theathletic.analytics

import android.util.Log
import com.theathletic.utility.logging.ICrashLogHandler

class DatadogWrapper(private val datadog: DatadogLogger) : ICrashLogHandler {

    init {
        datadog.setVerbosity(Log.WARN)
    }

    fun sendLog(
        priority: Int = Log.INFO,
        message: String,
        throwable: Throwable? = null,
        attributes: Map<String, Any> = emptyMap()
    ) {
        if (!datadog.isInitialized()) {
            error("Datadog has not been properly initialized.")
        }
        datadog.analyticsLogger.let { log ->
            when (priority) {
                Log.DEBUG -> log.d(message = message, throwable = throwable, attributes = attributes)
                Log.INFO -> log.i(message = message, throwable = throwable, attributes = attributes)
                Log.WARN -> log.w(message = message, throwable = throwable, attributes = attributes)
                Log.ERROR -> log.e(message = message, throwable = throwable, attributes = attributes)
                else -> log.v(message = message, attributes = attributes)
            }
        }
    }

    override fun logException(e: Throwable) {
        if (!datadog.isInitialized()) {
            error("Datadog has not been properly initialized.")
        }
        datadog.crashLogger.e(message = e.message ?: e.toString(), throwable = e)
    }

    override fun trackException(
        throwable: Throwable,
        cause: String?,
        message: String?,
        log: String?
    ) {
        if (!datadog.isInitialized()) {
            error("Datadog has not been properly initialized.")
        }
        datadog.crashLogger.e(
            throwable.message ?: throwable.toString(),
            throwable,
            mapOf(
                "cause" to cause,
                "message" to message,
                "log" to log
            )
        )
    }
}