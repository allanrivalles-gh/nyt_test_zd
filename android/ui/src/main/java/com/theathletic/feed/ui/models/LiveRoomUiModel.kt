package com.theathletic.feed.ui.models

import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.ui.UiModel
import com.theathletic.ui.widgets.AuthorImageStackModel

data class LiveRoomUiModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val hostImageUrls: AuthorImageStackModel,
    val topicLogo1: String?,
    val topicLogo2: String?,
    val analyticsPayload: LiveRoomAnalyticsPayload,
    override val impressionPayload: ImpressionPayload?
) : UiModel {
    override val stableId get() = id

    interface Interactor {
        fun onJoinRoomClicked(roomId: String, payload: LiveRoomAnalyticsPayload)
    }
}

data class LiveRoomAnalyticsPayload(
    val moduleIndex: Int
)