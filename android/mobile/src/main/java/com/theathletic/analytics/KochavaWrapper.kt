package com.theathletic.analytics

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.kochava.tracker.Tracker
import com.kochava.tracker.attribution.InstallAttributionApi
import com.kochava.tracker.deeplinks.DeeplinkApi
import com.kochava.tracker.events.Event
import com.kochava.tracker.events.EventType
import com.kochava.tracker.log.LogLevel
import com.theathletic.AthleticConfig
import com.theathletic.activity.article.ReferredArticleIdManager
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event as AnalyticsEvent
import com.theathletic.analytics.newarch.track
import com.theathletic.extension.deviceID
import com.theathletic.extension.extLogError
import java.util.Date
import org.json.JSONObject

class KochavaWrapper(
    private val analytics: Analytics,
    private val applicationContext: Context,
) {
    val tracker = Tracker.getInstance()
    val kochavaDeviceId = tracker.deviceId

    fun initialize(
        context: Context,
        startTimestamp: Long,
        referredArticleManager: ReferredArticleIdManager
    ) {
        val useDebug = AthleticConfig.DEBUG || AthleticConfig.IS_EXCEPTION_TRACKING_ENABLED.not()
        val kochavaAppGuid =
            if (useDebug) {
                "kothe-athletic-android-test-89yl0e"
            } else {
                "kothe-athletic-android-prod-fo1euj"
            }

        tracker.apply {
            startWithAppGuid(context, kochavaAppGuid)
            if (useDebug) setLogLevel(LogLevel.DEBUG) else setLogLevel(LogLevel.NONE)
            registerIdentityLink("ta_device_id", applicationContext.deviceID())
            if (installAttribution.isRetrieved.not()) {
                retrieveInstallAttribution { attribution ->
                    val attributionInfo = extractAttributionInfo(attribution)
                        ?: return@retrieveInstallAttribution
                    referredArticleManager.setArticleId(attributionInfo.deferredArticleId)
                    analytics.track(
                        AnalyticsEvent.Meta.ReceiveKochavaAttribution(
                            article_id = attributionInfo.siteId,
                            time_interval_from_start = ((Date().time - startTimestamp) / 1000).toString()
                        )
                    )
                }
            }
        }
    }

    @VisibleForTesting
    fun extractAttributionInfo(attributionData: InstallAttributionApi): AttributionInfo? {
        return try {
            val attributionObject = attributionData.toJson()
            if (attributionData.isAttributed.not()) {
                null
            } else {
                val siteId = attributionObject.getString("site_id")
                val creativeId = attributionObject.getString("creative_id")
                val articleId = siteId.toLongOrNull()
                    ?: parseArticleIdFromAdgroup(attributionObject)
                AttributionInfo(siteId, creativeId, articleId)
            }
        } catch (exception: Throwable) {
            exception.extLogError()
            null
        }
    }

    private fun parseArticleIdFromAdgroup(json: JSONObject): Long? {
        if (!json.has("adgroup_name")) return null
        return try {
            val article = json.getString("adgroup_name")
                .substringAfter("article_id=")
                .substringBefore(" ")
            article.toLongOrNull()
        } catch (e: Throwable) {
            null
        }
    }

    fun processDeeplink(linkString: String, value: Double, linkHandler: (DeeplinkApi) -> Unit) {
        tracker.processDeeplink(linkString, value, linkHandler)
    }

    fun sendRegistrationComplete(userId: String) = KochavaEvent.registrationComplete
        .setCustomStringValue("user_id", userId)
        .send()

    data class AttributionInfo(
        val siteId: String,
        val creativeId: String,
        val deferredArticleId: Long? = null
    )

    object KochavaEvent {
        val registrationComplete = Event.buildWithEventType(EventType.REGISTRATION_COMPLETE)
        val checkoutStart = Event.buildWithEventType(EventType.CHECKOUT_START)
        val startTrial = Event.buildWithEventType(EventType.START_TRIAL)
        val purchase = Event.buildWithEventType(EventType.PURCHASE)
        val tutorialComplete = Event.buildWithEventType(EventType.TUTORIAL_COMPLETE)

        fun named(eventName: String) = Event.buildWithEventName(eventName)
    }
}