package com.theathletic.boxscore.ui.modules

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.TopLeaderPerformer
import com.theathletic.boxscore.ui.TopLeaderPerformerUi
import com.theathletic.feed.ui.FeedModuleV2

data class TopPerformersModule(
    val id: String,
    val playerStats: List<TopLeaderPerformerUi.Category>,
    @StringRes val titleResId: Int,
    val subtitle: String?
) : FeedModuleV2 {

    override val moduleId: String = "TopPerformersModule:$id"

    @Composable
    override fun Render() {
        TopLeaderPerformer(
            playerStats = playerStats,
            titleResId = titleResId,
            includeDivider = false, // todo (Mark): Remove this when cleaning up TopLeaderPerformerUiModel
            includeTopDivider = false, // todo (Mark): Remove this when cleaning up TopLeaderPerformerUiModel,
            subtitle = subtitle
        )
    }
}