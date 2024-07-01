package com.theathletic.analytics.newarch.collectors

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.theathletic.analytics.newarch.AnalyticsCollector
import com.theathletic.analytics.newarch.Event

class FirebaseCollector(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsCollector {
    override fun trackEvent(
        event: Event,
        properties: Map<String, String>,
        deeplinkParams: Map<String, String>
    ) {
        val transformedName = event.eventName.replace("-", "_")
        firebaseAnalytics.logEvent(transformedName) {
            properties.forEach { prop ->
                param(prop.key, prop.value)
            }
        }
    }
}