package com.theathletic.analytics.newarch

enum class CollectorKey(val collectorName: String) {
    MAIN("main"),
    FIREBASE("firebase"),
    KOCHAVA("kochava"),
    FLEXIBLE("flexible"),
    DATADOG("datadog"),
    @Deprecated("Please use MAIN Collector instead")
    ARTICLE_VIEW_PHP("article-view /log_analytics"),
}

/**
 * A collector is responsible for delivering an event from a client to a remote server
 */
interface AnalyticsCollector {
    fun trackEvent(
        event: Event,
        properties: Map<String, String>,
        deeplinkParams: Map<String, String>
    )
}