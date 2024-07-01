package com.theathletic.analytics.newarch.collectors.php

import com.theathletic.analytics.data.remote.AnalyticsApi
import com.theathletic.analytics.newarch.AnalyticsCollector
import com.theathletic.analytics.newarch.Event
import com.theathletic.utility.ArticlePreferences

/**
 * Specifically for when the `article-view` event is sent to the `/log_analytics` php endpoint
 */
class ArticleViewPhpCollector(
    private val analyticsApi: AnalyticsApi,
    private val phpCallQueue: PhpCallQueue,
    private val preferences: ArticlePreferences
) : AnalyticsCollector {

    override fun trackEvent(
        event: Event,
        properties: Map<String, String>,
        deeplinkParams: Map<String, String>
    ) {
        val percentRead = try {
            properties["percent_read"]?.let { Integer.parseInt(it) }
        } catch (e: NumberFormatException) {
            null
        }

        val customOptions = mutableMapOf<String, String>()
        properties["paywall_type"]?.let { type ->
            if (type.isNotEmpty()) {
                customOptions["paywall_type"] = type
            }
        }

        analyticsApi.sendLogArticleViewAnalytics(
            eventName = event.eventName,
            eventKey = "article_id",
            eventValue = properties["article_id"]!!,
            hasPaywall = Integer.parseInt(properties["has_paywall"]!!),
            percentRead = percentRead,
            source = properties["source"]!!,
            deepLinkParams = deeplinkParams,
            customOptions = customOptions,
            referrer = preferences.referrerURI.orEmpty()
        ).run {
            phpCallQueue.addAPICall(this)
        }

        // reset referrer
        preferences.referrerURI = null
    }
}