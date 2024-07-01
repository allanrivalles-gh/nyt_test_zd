package com.theathletic.analytics.newarch.collectors

import android.util.Log
import com.theathletic.analytics.DatadogWrapper
import com.theathletic.analytics.newarch.AnalyticsCollector
import com.theathletic.analytics.newarch.Event

class DatadogCollector(private val datadogWrapper: DatadogWrapper) : AnalyticsCollector {

    override fun trackEvent(
        event: Event,
        properties: Map<String, String>,
        deeplinkParams: Map<String, String>
    ) {
        val attributes = mapOf<String, Any>(
            "metadata" to mapOf("eventName" to event.eventName),
            "arguments" to properties,
            "deeplink" to deeplinkParams
        )
        datadogWrapper.sendLog(Log.INFO, event.eventName, attributes = attributes)
    }
}