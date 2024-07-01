package com.theathletic.news.container.ui

import androidx.annotation.DimenRes
import com.theathletic.ui.ContentTextSize
import com.theathletic.ui.UiModel

class HeadlineContainerLedeItem(
    val lede: String? = "",
    val textSize: ContentTextSize,
    @DimenRes val textSizeRes: Int
) : UiModel {
    override val stableId: String = "ledeId"

    companion object {
        fun fromDataModel(lede: String?, textSize: ContentTextSize, textSizeRes: Int) =
            HeadlineContainerLedeItem(
                lede = lede,
                textSize = textSize,
                textSizeRes = textSizeRes
            )
    }
}