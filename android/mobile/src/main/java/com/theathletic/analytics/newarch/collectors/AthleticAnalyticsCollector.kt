package com.theathletic.analytics.newarch.collectors

import com.theathletic.analytics.AnalyticsTracker
import com.theathletic.analytics.data.local.AnalyticsEvent
import com.theathletic.analytics.newarch.AnalyticsCollector
import com.theathletic.analytics.newarch.Event

/**
 * Separates key-value pairs that belong to a specific column from key-value pairs that belong in
 * the metablob before delegating to AnalyticsTracker for sending events to kafka
 */
class AthleticAnalyticsCollector(
    private val analyticsTracker: AnalyticsTracker
) : AnalyticsCollector {
    private val kafkaColumnKeys = setOf(
        "verb",
        "previous_view",
        "view",
        "element",
        "object_type",
        "object_id",
        "source"
    )

    override fun trackEvent(
        event: Event,
        properties: Map<String, String>,
        deeplinkParams: Map<String, String>
    ) {
        var eventDbModel = AnalyticsEvent(
            verb = event.eventName,
            view = properties["view"] ?: ""
        )

        val params = mutableMapOf<String, String>()
        for (property in properties) {
            if (!kafkaColumnKeys.contains(property.key)) {
                if (property.value.isNotEmpty()) {
                    params[property.key] = property.value
                }
            } else {
                eventDbModel = when (property.key) {
                    "verb" -> eventDbModel.copy(verb = properties[property.key] ?: eventDbModel.verb)
                    "previous_view" -> eventDbModel.copy(previousView = properties[property.key])
                    "element" -> eventDbModel.copy(element = properties[property.key])
                    "object_type" -> eventDbModel.copy(objectType = properties[property.key])
                    "object_id" -> eventDbModel.copy(objectId = properties[property.key])
                    "view" -> eventDbModel.copy(view = properties[property.key] ?: "")
                    "source" -> eventDbModel.copy(source = properties[property.key])
                    else -> throw IllegalStateException("'${property.key}' is not a kafka column or you do not need to provide it")
                }
            }
        }
        eventDbModel = eventDbModel.copy(metaBlob = params)
        analyticsTracker.trackEvent(eventDbModel)
    }
}