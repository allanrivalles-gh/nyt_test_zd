package com.theathletic.feed.ui.models

import com.theathletic.analytics.AnalyticsPayload
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.ui.UiModel

data class FeedAnnouncement(
    val id: String,
    val backgroundImageUrl: String,
    val title: String,
    val subtext: String,
    val ctaText: String,
    val analyticsPayload: FeedAnnouncementAnalyticsPayload?,
    override val impressionPayload: ImpressionPayload?
) : UiModel {
    override val stableId get() = id

    interface Interactor {
        fun onAnnouncementClick(id: String, analyticsPayload: FeedAnnouncementAnalyticsPayload?)
        fun onAnnouncementDismiss(id: String)
    }
}

data class FeedAnnouncementAnalyticsPayload(
    val pageOrder: Int
) : AnalyticsPayload