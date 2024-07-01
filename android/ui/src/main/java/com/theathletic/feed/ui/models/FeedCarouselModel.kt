package com.theathletic.feed.ui.models

import com.theathletic.ui.CarouselUiModel
import com.theathletic.utility.RecyclerLayout

interface FeedCarouselModel : CarouselUiModel {
    val title: String?
    val subtitle: String?
    val snapScroll: Boolean get() = true
    val titleTopPadding: Boolean get() = false
    val displaySubtitle: Boolean get() = !subtitle.isNullOrBlank()
    val recyclerLayout: RecyclerLayout get() = RecyclerLayout.LINEAR_HORIZONTAL
}