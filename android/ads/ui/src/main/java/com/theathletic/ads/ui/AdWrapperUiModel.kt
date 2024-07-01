package com.theathletic.ads.ui

import com.theathletic.ads.AdView
import com.theathletic.ads.data.local.AdLocalModel
import com.theathletic.ui.UiModel

data class AdWrapperUiModel(
    val id: String,
    val page: Int,
    val adView: AdView?
) : UiModel {
    override val stableId: String
        get() = id

    interface Interactor {
        fun collapseAdView(id: String)
        fun showAdView(ad: AdLocalModel)
    }
}