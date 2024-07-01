package com.theathletic.boxscore.ui.modules

import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.BoxScoreSeasonStatsUiModel
import com.theathletic.boxscore.ui.SeasonStats
import com.theathletic.feed.ui.FeedModuleV2

data class SeasonStatsModule(
    val id: String,
    val seasonStats: BoxScoreSeasonStatsUiModel
) : FeedModuleV2 {

    override val moduleId: String = "SeasonStatsModule:$id"

    @Composable
    override fun Render() {
        SeasonStats(
            firstTeamLogos = seasonStats.firstTeamLogos,
            secondTeamLogos = seasonStats.secondTeamLogos,
            stats = seasonStats.statsItems,
            headerSubtitle = seasonStats.headerSubtitle
        )
    }
}