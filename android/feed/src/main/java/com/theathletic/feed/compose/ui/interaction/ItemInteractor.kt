package com.theathletic.feed.compose.ui.interaction

import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.ui.models.SeeAllAnalyticsPayload
import com.theathletic.impressions.Visibility

class ItemInteractor(
    val onClick: (LayoutUiModel.Item) -> Unit = {},
    val onSeeAllClick: (deepLink: String, SeeAllAnalyticsPayload) -> Unit = { _, _ -> },
    val onLongClick: (LayoutUiModel.Item) -> Unit = {},
    val onNavLinkClick: (item: LayoutUiModel.Item, link: String, linkType: String?) -> Unit = { _, _, _ -> },
    val onVisibilityChange: (visible: Visibility, item: LayoutUiModel.Item) -> Unit = { _, _ -> }
)