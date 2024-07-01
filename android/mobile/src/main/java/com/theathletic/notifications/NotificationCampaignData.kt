package com.theathletic.notifications

import com.theathletic.analytics.IAnalytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track

data class NotificationCampaignData(
    val source: String,
    val isGhostPush: Boolean,
    val url: String?,
    val campaignId: String?,
    val templateId: String?,
    val messageId: String?,
    val notificationType: String?,
    val contentId: String?,
) {
    fun trackPushOpen(analytics: IAnalytics) {
        analytics.track(
            Event.Notification.Open(
                type = notificationType ?: "",
                message_id = messageId ?: "",
                is_ghost_push = isGhostPush.toString(),
                push_url = url ?: "",
                campaign_id = campaignId ?: "",
                content_id = contentId ?: "",
            )
        )
    }

    companion object {
        fun fromIterable(payload: IterableNotificationPayload?): NotificationCampaignData {
            return NotificationCampaignData(
                source = "iterable_push",
                isGhostPush = payload?.isGhostPush ?: false,
                url = payload?.url,
                campaignId = payload?.campaignId?.toString(),
                templateId = payload?.templateId?.toString(),
                messageId = payload?.messageId,
                notificationType = payload?.defaultAction?.type,
                contentId = null,
            )
        }

        fun fromAthletic(payload: AthleticNotificationPayload?): NotificationCampaignData {
            return NotificationCampaignData(
                source = "athletic_push",
                isGhostPush = payload?.athletic?.isGhostPush ?: false,
                url = payload?.url,
                campaignId = payload?.athletic?.campaignId,
                templateId = null,
                messageId = payload?.messageId,
                notificationType = null,
                contentId = payload?.contentId
            )
        }
    }
}