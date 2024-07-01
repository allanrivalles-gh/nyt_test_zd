package com.theathletic.feed.ui.models

import com.theathletic.ui.UiModel
import com.theathletic.ui.stableId
import com.theathletic.utility.RecyclerLayout

data class FeedCarousel(
    override val carouselItemModels: List<UiModel>,
    override val title: String? = null,
    override val subtitle: String? = null,
    override val snapScroll: Boolean = false,
    override val titleTopPadding: Boolean = true,
    override val recyclerLayout: RecyclerLayout = RecyclerLayout.LINEAR_HORIZONTAL
) : FeedCarouselModel {
    override val stableId = carouselItemModels.stableId
}