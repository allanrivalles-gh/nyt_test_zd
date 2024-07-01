package com.theathletic.analytics

import com.theathletic.analytics.newarch.Event

interface IAnalytics {
    fun trackEvent(
        event: Event,
        propertiesMap: Map<String, String>,
        isNoisy: Boolean = false
    )
}