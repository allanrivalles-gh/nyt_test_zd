package com.theathletic.feed.ui.models

import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.ui.CarouselUiModel
import com.theathletic.ui.UiModel
import com.theathletic.utility.RecyclerLayout

data class FeedMostPopularCarousel(
    val id: Int,
    val isTablet: Boolean,
    override val carouselItemModels: List<UiModel>
) : CarouselUiModel {
    override val stableId = "FeedMostPopularCarousel:$id"

    val recyclerLayout = when {
        isTablet -> RecyclerLayout.SIDE_BY_SIDE_GRID
        carouselItemModels.size in 7..8 -> RecyclerLayout.FOUR_ROW_CAROUSEL
        carouselItemModels.size in 5..6 -> RecyclerLayout.THREE_ROW_CAROUSEL
        carouselItemModels.size in 3..4 -> RecyclerLayout.TWO_ROW_CAROUSEL
        else -> RecyclerLayout.LINEAR_HORIZONTAL
    }

    companion object {
        const val MOST_POPULAR_MAX_ITEMS = 8
        const val MOST_POPULAR_MAX_ITEMS_SINGLE_COLUMN = 4
    }
}

data class FeedMostPopularArticle(
    val id: Long,
    val number: String,
    val tag: String,
    val title: String,
    val imageUrl: String,
    val isTopItem: Boolean,
    val isTablet: Boolean,
    val isInLastColumn: Boolean,
    val analyticsPayload: FeedArticleAnalyticsPayload,
    override val impressionPayload: ImpressionPayload
) : UiModel {
    override val stableId = "FeedMostPopular:$id"

    interface Interactor : FeedArticleInteractor
}