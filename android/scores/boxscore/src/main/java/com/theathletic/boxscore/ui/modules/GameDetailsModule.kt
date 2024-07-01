package com.theathletic.boxscore.ui.modules

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.GameDetails
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.ui.ResourceString

data class GameDetailsModule(
    val id: String,
    val details: List<DetailsItem>,
    @StringRes val titleResId: Int
) : FeedModuleV2 {

    override val moduleId: String = "GameDetailsModule:$id"

    data class DetailsItem(
        val label: ResourceString,
        val value: ResourceString,
        val showDivider: Boolean = true
    )

    @Composable
    override fun Render() {
        GameDetails(
            details = details,
            titleResId = titleResId
        )
    }
}