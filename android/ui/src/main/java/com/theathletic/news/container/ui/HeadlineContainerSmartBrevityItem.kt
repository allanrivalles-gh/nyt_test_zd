package com.theathletic.news.container.ui

import androidx.annotation.DimenRes
import com.theathletic.ui.ContentTextSize
import com.theathletic.ui.UiModel

class HeadlineContainerSmartBrevityItem(
    val smartBrevity: CharSequence? = "",
    val textSize: ContentTextSize,
    @DimenRes val textSizeRes: Int
) : UiModel {
    override val stableId: String = "smartBrevityId"

    companion object {
        fun fromDataModel(smartBrevity: CharSequence?, textSize: ContentTextSize, textSizeRes: Int) =
            HeadlineContainerSmartBrevityItem(
                smartBrevity = smartBrevity,
                textSize = textSize,
                textSizeRes = textSizeRes
            )
    }
}