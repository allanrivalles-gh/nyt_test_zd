package com.theathletic.analytics.newarch.collectors

import com.google.gson.Gson
import com.theathletic.analytics.AnalyticsTracker
import com.theathletic.analytics.data.local.FlexibleAnalyticsEvent
import com.theathletic.analytics.newarch.AnalyticsCollector
import com.theathletic.analytics.newarch.Event

class FlexibleSchemaCollector(
    private val analyticsTracker: AnalyticsTracker,
    private val gson: Gson
) : AnalyticsCollector {

    override fun trackEvent(
        event: Event,
        properties: Map<String, String>,
        deeplinkParams: Map<String, String>
    ) {
        val topic = event.kafkaTopic ?: return

        val schemaJsonBlob = gson.toJson(event)
        val extrasJsonBlob = gson.toJson(properties)

        val eventDbModel = FlexibleAnalyticsEvent(
            kafkaTopicName = topic.topic,
            schemaJsonBlob = schemaJsonBlob,
            extrasJsonBlob = extrasJsonBlob
        )

        analyticsTracker.trackEvent(eventDbModel)
    }
}