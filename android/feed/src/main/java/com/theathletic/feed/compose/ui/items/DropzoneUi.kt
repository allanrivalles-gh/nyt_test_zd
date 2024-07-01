package com.theathletic.feed.compose.ui.items

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.theathletic.ads.ui.AdState
import com.theathletic.ads.ui.AdWrapperUi
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.analytics.AnalyticsData

data class DropzoneUiModel(
    override val id: String,
    val adState: AdState,
) : LayoutUiModel.Item {
    override val permalink: String?
        get() = null
    override val analyticsData: AnalyticsData?
        get() = null

    override fun deepLink() = null
}

@Composable
internal fun DropzoneUi(uiModel: DropzoneUiModel) {
    AdWrapperUi(
        state = uiModel.adState,
        lightMode = isSystemInDarkTheme().not()
    )
}