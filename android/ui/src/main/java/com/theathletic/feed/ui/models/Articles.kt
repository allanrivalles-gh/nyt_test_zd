package com.theathletic.feed.ui.models

import com.theathletic.analytics.AnalyticsPayload
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.ui.UiModel

interface FeedArticleInteractor {
    fun onArticleClicked(id: Long, analyticsPayload: FeedArticleAnalyticsPayload, title: String)
    fun onArticleLongClicked(id: Long): Boolean
}

data class FeedArticleAnalyticsPayload(
    val moduleIndex: Int,
    val container: String,
    val vIndex: Int? = null,
    val hIndex: Int = -1,
    val parentType: String = "",
    val parentId: String = ""
) : AnalyticsPayload

data class FeedInsiderItem(
    val authorId: String,
    val articleId: Long,
    val name: String,
    val authorImageUrl: String,
    val role: String,
    val articleTitle: String,
    val formattedDate: String,
    val commentCount: String,
    val analyticsInfo: FeedInsiderAnalyticsPayload,
    override val impressionPayload: ImpressionPayload
) : UiModel {
    override val stableId = "FeedInsider:$authorId:$articleId"

    interface Interactor {
        fun onInsiderArticleClicked(articleId: Long, analyticsInfo: FeedInsiderAnalyticsPayload, title: String)
    }
}

data class FeedInsiderAnalyticsPayload(
    val authorId: String,
    val moduleIndex: Int,
    val hIndex: Int = -1,
)