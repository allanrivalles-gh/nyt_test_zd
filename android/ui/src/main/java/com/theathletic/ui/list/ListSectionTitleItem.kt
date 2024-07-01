package com.theathletic.ui.list

import androidx.annotation.StringRes
import com.theathletic.ui.UiModel

class ListSectionTitleItem(@StringRes val title: Int) : UiModel {
    override val stableId: String get() = "$title"
}