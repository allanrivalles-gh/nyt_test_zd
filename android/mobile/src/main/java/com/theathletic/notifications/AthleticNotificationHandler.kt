package com.theathletic.notifications

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import com.theathletic.activity.DeepLinkDispatcherActivity
import com.theathletic.analytics.IAnalytics
import com.theathletic.annotation.autokoin.AutoKoin

class AthleticNotificationHandler @AutoKoin constructor(
    private val analytics: IAnalytics,
) {
    fun onNotificationOpen(context: Context, payload: AthleticNotificationPayload?) {
        val campaignData = NotificationCampaignData.fromAthletic(payload)

        campaignData.trackPushOpen(analytics)

        campaignData.url?.let { url ->
            val dispatcherIntent = DeepLinkDispatcherActivity.newIntent(context, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            ContextCompat.startActivity(context, dispatcherIntent, null)
        }
    }
}