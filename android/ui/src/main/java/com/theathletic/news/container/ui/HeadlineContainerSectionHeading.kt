package com.theathletic.news.container.ui

import com.theathletic.ui.UiModel

data class HeadlineContainerSectionHeading(
    val title: String
) : UiModel {
    override val stableId: String = "HeadlineSectionHeader-$title"
}