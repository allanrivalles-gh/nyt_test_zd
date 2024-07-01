package com.theathletic.news.container.ui

import com.theathletic.ui.UiModel

class HeadlineContainerPreviouslyHeading(
    val id: String
) : UiModel {
    override val stableId: String = "HeadlineContainerPreviously-$id"

    companion object {
        fun fromDataModel(id: String) =
            HeadlineContainerPreviouslyHeading(
                id = id
            )
    }
}