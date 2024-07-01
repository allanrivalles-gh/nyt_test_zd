package com.theathletic.featureintro.data.local

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class FeatureIntro(
    val pages: List<IntroPage>,
    val destinationUrl: String? = null
) {
    val pageCount = pages.size
    fun getAnalyticsView(index: Int) = if (pages.isNotEmpty()) pages[index].analyticsView else ""

    data class IntroPage(
        val analyticsView: String,
        @StringRes val title: Int,
        @StringRes val description: Int,
        @DrawableRes val image: Int,
        @StringRes val buttonLabel: Int,
    )
}