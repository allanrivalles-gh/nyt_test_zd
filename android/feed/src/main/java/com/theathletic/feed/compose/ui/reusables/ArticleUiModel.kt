package com.theathletic.feed.compose.ui.reusables

import com.theathletic.feed.compose.SOURCE_FEED
import com.theathletic.feed.compose.data.PostType
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.analytics.AnalyticsData
import com.theathletic.links.deep.Deeplink

data class ArticleUiModel(
    override val id: String,
    val title: String,
    val excerpt: String,
    val imageUrl: String,
    val byline: String,
    val commentCount: String,
    val isBookmarked: Boolean,
    val isRead: Boolean,
    val postType: PostType,
    override val permalink: String,
    override val analyticsData: AnalyticsData
) : LayoutUiModel.Item {
    override fun deepLink(): Deeplink = Deeplink.article(id).addSource(SOURCE_FEED)
}