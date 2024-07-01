package com.theathletic.ui.list

import com.theathletic.R
import com.theathletic.ui.UiModel

interface IBasicRowView {
    fun onSimpleRowClicked(row: BasicRowItem)
}

interface BasicRowItem {

    val stableId: String
    val text: String

    data class Text(
        override val stableId: String,
        override val text: String,
        val dividerStartPadding: Int = R.dimen.global_spacing_0
    ) : UiModel, BasicRowItem

    data class LeftDrawableUri(
        override val stableId: String,
        override val text: String,
        val leftDrawableUri: String,
        val dividerStartPadding: Int = R.dimen.global_spacing_0
    ) : UiModel, BasicRowItem
}