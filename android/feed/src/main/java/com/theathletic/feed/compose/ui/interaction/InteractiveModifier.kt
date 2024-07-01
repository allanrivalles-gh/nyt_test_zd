package com.theathletic.feed.compose.ui.interaction

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.impressions.onVisibilityChange

@OptIn(ExperimentalFoundationApi::class)
internal fun Modifier.interactive(item: LayoutUiModel.Item, interactor: ItemInteractor): Modifier = composed {
    val hapticFeedback = LocalHapticFeedback.current

    this
        .onVisibilityChange { visibility -> interactor.onVisibilityChange(visibility, item) }
        .combinedClickable(
            onLongClick = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                interactor.onLongClick(item)
            },
            onClick = { interactor.onClick(item) }
        )
}