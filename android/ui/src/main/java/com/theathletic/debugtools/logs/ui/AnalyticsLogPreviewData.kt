package com.theathletic.debugtools.logs.ui

object AnalyticsLogPreviewData {

    val analyticsLogItems = listOf(
        AnalyticsLogUi.AnalyticsLogItem(
            "My Analytics event name",
            mapOf("element" to "view", "property" to "value"),
            "Kafka 1"
        ),
        AnalyticsLogUi.AnalyticsLogItem(
            "My Analytics event name",
            mapOf("element" to "view", "property" to "value"),
            "Kafka 2"
        )
    )
}