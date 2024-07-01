package com.theathletic.boxscore.ui.modules

import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.ScoringSummary
import com.theathletic.boxscore.ui.ScoringSummaryUi
import com.theathletic.feed.ui.FeedModuleV2

data class ScoringSummaryModule(
    val id: String,
    val plays: List<ScoringSummaryUi>
) : FeedModuleV2 {

    override val moduleId: String = "ScoringSummaryModule:$id"

    @Composable
    override fun Render() {
        ScoringSummary(
            includeHeaderDivider = false, // todo: Remove when removing FS and clean up old code
            plays = plays
        )
    }
}