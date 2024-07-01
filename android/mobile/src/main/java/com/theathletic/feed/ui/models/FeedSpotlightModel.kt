package com.theathletic.feed.ui.models

import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.ui.UiModel
import com.theathletic.ui.widgets.AuthorImageStackModel

data class FeedSpotlightModel(
    val id: Long,
    val imageUrl: String,
    val title: String,
    val excerpt: String,
    val commentNumber: String,
    val showComment: Boolean,
    val authorsNames: String,
    val avatarModel: AuthorImageStackModel,
    val isBookmarked: Boolean,
    val isRead: Boolean,
    val date: String,
    val analyticsPayload: FeedArticleAnalyticsPayload,
    override val impressionPayload: ImpressionPayload
) : UiModel {
    override val stableId = "FeedSpotlightModel:$id"

    interface Interactor : FeedArticleInteractor
}