package com.theathletic.analytics.newarch

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.theathletic.BuildConfig
import com.theathletic.analytics.AnalyticsTracker
import com.theathletic.analytics.DatadogWrapper
import com.theathletic.analytics.IAnalytics
import com.theathletic.analytics.data.remote.AnalyticsApi
import com.theathletic.analytics.newarch.collectors.AthleticAnalyticsCollector
import com.theathletic.analytics.newarch.collectors.DatadogCollector
import com.theathletic.analytics.newarch.collectors.FirebaseCollector
import com.theathletic.analytics.newarch.collectors.FlexibleSchemaCollector
import com.theathletic.analytics.newarch.collectors.KochavaCollector
import com.theathletic.analytics.newarch.collectors.php.ArticleViewPhpCollector
import com.theathletic.analytics.newarch.collectors.php.PhpCallQueue
import com.theathletic.analytics.newarch.context.ContextInfoPreferences
import com.theathletic.analytics.newarch.context.ContextInfoProvider
import com.theathletic.analytics.newarch.context.ContextInfoProvider.ContextInfo
import com.theathletic.analytics.newarch.context.DeepLinkParams
import com.theathletic.links.AnalyticsContextUpdater
import com.theathletic.utility.ArticlePreferences
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Analytics is responsible for delegating sending analytics events to various backend services.
 * Each backend service has an associated Collector that has the responsibility for making or
 * scheduling an API call to that service.
 */
@Suppress("LongParameterList")
class Analytics constructor(
    private val analyticsTracker: AnalyticsTracker,
    private val contextInfoProvider: ContextInfoProvider,
    private val analyticsEventProducer: AnalyticsEventProducer,
    private val phpCallQueue: PhpCallQueue,
    private val analyticsApi: AnalyticsApi,
    private val firebaseAnalytics: FirebaseAnalytics,
    private val datadogWrapper: DatadogWrapper,
    private val contextPreferences: ContextInfoPreferences,
    private val gson: Gson,
    private val preferences: ArticlePreferences,
) : IAnalytics, AnalyticsContextUpdater {
    private val collectorMap: MutableMap<CollectorKey, AnalyticsCollector> = mutableMapOf()

    override fun trackEvent(
        event: Event,
        propertiesMap: Map<String, String>,
        isNoisy: Boolean
    ) {
        val contextInfo = contextInfoProvider.buildContextInfo()

        if (BuildConfig.DEBUG_TOOLS_ENABLED) {
            check(event.eventName.isNotEmpty()) { "eventName cannot be empty" }
            check(event.collectors.isNotEmpty()) { "collectors must not be empty" }

            GlobalScope.launch {
                analyticsEventProducer.emit(
                    DebugToolEvent(event, propertiesMap, isNoisy, contextInfo)
                )
            }
        }

        if (BuildConfig.DEBUG) {
            val properties = propertiesMap.map { "${it.key}: ${it.value}" }.joinToString(", ")
            Timber.v("analytics_event: ${event.eventName} : { $properties }")
        }

        event.collectors.forEach { collectorKey ->
            val collector = collectorMap[collectorKey] ?: createCollector(collectorKey).also {
                collectorMap[collectorKey] = it
            }
            collector.trackEvent(
                event,
                propertiesMap,
                contextInfo.deepLinkParams?.convertToRequestParameterFormat() ?: hashMapOf()
            )
            Timber.v("[${collectorKey.collectorName} collector] track: $event where context=$contextInfo")
        }
    }

    override fun updateContext(deepLinkParams: DeepLinkParams?) {
        contextPreferences.analyticsDeeplinkParameters = deepLinkParams
    }

    fun clearDeeplinkParams() {
        contextPreferences.analyticsDeeplinkParameters = null
    }

    private fun createCollector(collectorKey: CollectorKey): AnalyticsCollector {
        return when (collectorKey) {
            CollectorKey.FIREBASE -> FirebaseCollector(firebaseAnalytics)
            CollectorKey.MAIN -> AthleticAnalyticsCollector(analyticsTracker)
            CollectorKey.FLEXIBLE -> FlexibleSchemaCollector(analyticsTracker, gson)
            CollectorKey.ARTICLE_VIEW_PHP -> ArticleViewPhpCollector(analyticsApi, phpCallQueue, preferences)
            CollectorKey.KOCHAVA -> KochavaCollector()
            CollectorKey.DATADOG -> DatadogCollector(datadogWrapper)
        }
    }

    data class DebugToolEvent(
        val event: Event,
        val properties: Map<String, String>,
        val isNoisy: Boolean,
        val contextInfo: ContextInfo
    )
}