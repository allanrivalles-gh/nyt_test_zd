package com.theathletic.utility.extensions

import com.theathletic.ads.ui.AdWrapperUiModel
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.ListVerticalPadding

fun List<UiModel>.filterPaddingAroundAds(): List<UiModel> {
    return this.filterIndexed { index, model ->
        if (this.size > index + 1) {
            val nextModel = this[index + 1]
            model !is ListVerticalPadding || nextModel !is AdWrapperUiModel
        } else {
            true
        }
    }
}