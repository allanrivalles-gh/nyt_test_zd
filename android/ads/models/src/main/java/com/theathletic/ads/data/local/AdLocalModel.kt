package com.theathletic.ads.data.local

import com.theathletic.ads.AdConfig
import com.theathletic.ads.AdView

data class AdLocalModel(
    val id: String,
    val adConfig: AdConfig,
    val adView: AdView? = null,
    var discard: Boolean = false,
    val collapsed: Boolean = false
)