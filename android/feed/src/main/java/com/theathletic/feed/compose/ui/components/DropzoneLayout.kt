package com.theathletic.feed.compose.ui.components

import androidx.compose.runtime.Composable
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.items.DropzoneUi
import com.theathletic.feed.compose.ui.items.DropzoneUiModel

internal data class DropzoneLayoutUiModel(
    private val layout: LayoutUiModel
) : LayoutUiModel by layout {
    override val items: List<DropzoneUiModel> = layout.items.mapNotNull { it as? DropzoneUiModel }
}

@Composable
internal fun DropzoneLayout(uiModel: DropzoneLayoutUiModel) {
    uiModel.items.firstOrNull()?.let {
        DropzoneUi(uiModel = it)
    }
}