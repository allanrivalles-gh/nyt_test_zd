package com.theathletic.feed.ui.models

import com.theathletic.analytics.AnalyticsPayload
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.ui.CarouselUiModel
import com.theathletic.ui.ResourceString
import com.theathletic.ui.UiModel

data class FeedLiveBlogCarousel(
    val index: Int,
    override val carouselItemModels: List<UiModel>,
    override val impressionPayload: ImpressionPayload
) : CarouselUiModel {
    override val stableId: String = "feed_live_blog_carousel_$index"
}

data class LiveBlogCarouselItem(
    val id: String,
    val title: String,
    val lastActivity: ResourceString,
    val analyticsPayload: LiveBlogAnalyticsPayload
) : UiModel {
    override val stableId: String = id

    interface Interactor {
        fun onLiveBlogClick(id: String, analyticsPayload: LiveBlogAnalyticsPayload)
    }
}

data class LiveBlogAnalyticsPayload(
    val pageOrder: String = "",
    val horizontalIndex: String = "",
    val verticalIndex: String = "-1"
) : AnalyticsPayload