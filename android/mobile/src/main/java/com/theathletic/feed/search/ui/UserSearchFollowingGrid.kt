package com.theathletic.feed.search.ui

import com.theathletic.ui.CarouselUiModel
import com.theathletic.ui.UiModel

data class UserSearchFollowingGrid(
    override val carouselItemModels: List<UiModel>
) : CarouselUiModel {
    override val stableId = "SearchFollowingGrid"
}