package com.theathletic.notifications

import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import com.iterable.iterableapi.IterableAction
import com.iterable.iterableapi.IterableActionContext
import com.iterable.iterableapi.IterableApi
import com.iterable.iterableapi.IterableCustomActionHandler
import com.iterable.iterableapi.IterableUrlHandler
import com.theathletic.AthleticApplication
import com.theathletic.activity.DeepLinkDispatcherActivity
import com.theathletic.analytics.IAnalytics
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.main.DeeplinkThrottle
import timber.log.Timber

class AthleticIterableHandler @AutoKoin constructor(
    private val deeplinkThrottle: DeeplinkThrottle,
    private val analytics: IAnalytics,
) : IterableCustomActionHandler, IterableUrlHandler {

    override fun handleIterableCustomAction(
        action: IterableAction,
        actionContext: IterableActionContext
    ): Boolean {
        Timber.d("[push]: handleIterableCustomAction, deeplink: $action")
        deeplinkThrottle.startThrottle()
        action.type?.let { routeToActivity("theathletic://$it") }
        trackIterableOpen()
        return true
    }

    override fun handleIterableURL(uri: Uri, actionContext: IterableActionContext): Boolean {
        Timber.i("Attempting to follow deeplink: $uri")
        deeplinkThrottle.startThrottle()
        routeToActivity(uri.toString())
        trackIterableOpen()
        return true
    }

    // this method is called whenever an iterable push notification is open
    // it doesn't matter whether it was a link or a custom action
    private fun trackIterableOpen() {
        val payload = IterableApi.getInstance().payloadData?.let { IterableNotificationPayload.fromBundle(it) }
        val campaignData = NotificationCampaignData.fromIterable(payload)
        campaignData.trackPushOpen(analytics)
    }

    private fun routeToActivity(deepLink: String) {
        val context = AthleticApplication.getContext()
        val intent = DeepLinkDispatcherActivity.newIntent(context, Uri.parse(deepLink)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        Timber.i("[push]: routeToActivity with deepLink: $deepLink")
        startActivity(context, intent, null)
    }
}