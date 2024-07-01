package com.theathletic.news.container.ui

import androidx.annotation.StringRes
import com.theathletic.ui.UiModel

data class HeadlineContainerCommentModeration(
    @StringRes val messageId: Int
) : UiModel {
    override val stableId: String = "HeadlineContainerCommentModeration"
}