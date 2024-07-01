package com.theathletic.feed.ui.models

import com.theathletic.ui.UiModel

data class StandaloneFeedHeader(
    val title: String,
    val isVisible: Boolean
) : UiModel {
    override val stableId = "StandaloneFeedHeader:$title"
}