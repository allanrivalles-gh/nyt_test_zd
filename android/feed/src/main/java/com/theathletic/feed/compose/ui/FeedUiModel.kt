package com.theathletic.feed.compose.ui

import com.theathletic.feed.compose.data.PostType
import com.theathletic.feed.compose.ui.analytics.AnalyticsData
import com.theathletic.feed.compose.ui.components.FeedDetailsMenuOption
import com.theathletic.feed.compose.ui.items.LayoutHeaderUiModel
import com.theathletic.feed.compose.ui.reusables.ArticleUiModel
import com.theathletic.links.deep.Deeplink

internal data class FeedUiModel(
    val id: String,
    val layouts: List<LayoutUiModel>,
    val pageInfo: PageInfo,
    val scoresCarouselPosition: Int
) {
    data class PageInfo(
        val currentPage: Int,
        val hasNextPage: Boolean,
    )
}

interface LayoutUiModel {
    val id: String
    val title: String
    val icon: String
    val action: String
    val deepLink: String
    val items: List<Item>

    interface Item {
        val id: String
        val permalink: String?
        val analyticsData: AnalyticsData?
        fun deepLink(): Deeplink?
    }
}

internal val LayoutUiModel.header: LayoutHeaderUiModel
    get() = LayoutHeaderUiModel(
        title = title,
        icon = icon,
        actionText = action,
        deepLink = deepLink
    )

val LayoutUiModel.Item.detailsMenuOptions: List<FeedDetailsMenuOption>
    get() {
        val list = mutableListOf<FeedDetailsMenuOption>()
        if (this is ArticleUiModel && postType == PostType.ARTICLE) {
            list.add(FeedDetailsMenuOption.Save(id.toLong(), isBookmarked))
            list.add(FeedDetailsMenuOption.MarkRead(id.toLong(), isRead))
        }
        permalink?.let { list.add(FeedDetailsMenuOption.Share(it)) }
        return list
    }

internal fun layoutUiModel(
    id: String,
    title: String,
    icon: String = "",
    action: String = "",
    deepLink: String = "",
    items: List<LayoutUiModel.Item> = emptyList(),
) = object : LayoutUiModel {
    override val id: String = id
    override val title: String = title
    override val icon: String = icon
    override val action: String = action
    override val deepLink: String = deepLink
    override val items: List<LayoutUiModel.Item> = items
}

internal fun analyticsPreviewData() = AnalyticsData("", 0, "")