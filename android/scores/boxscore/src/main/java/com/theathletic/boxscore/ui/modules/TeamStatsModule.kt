package com.theathletic.boxscore.ui.modules

import androidx.annotation.ColorRes
import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.TeamStats
import com.theathletic.data.SizedImages
import com.theathletic.feed.ui.FeedModuleV2

data class TeamStatsModule(
    val id: String,
    val firstTeamLogos: SizedImages,
    val secondTeamLogos: SizedImages,
    val stats: List<TeamStatsItem>
) : FeedModuleV2 {

    override val moduleId: String = "TeamStatsModule:$id"

    data class TeamStatsItem(
        val firstTeamValue: String,
        @ColorRes val firstTeamValueColor: Int,
        val secondTeamValue: String,
        @ColorRes val secondTeamValueColor: Int,
        val label: String,
        val isChildStat: Boolean
    )

    @Composable
    override fun Render() {
        TeamStats(
            firstTeamLogos = firstTeamLogos,
            secondTeamLogos = secondTeamLogos,
            stats = stats
        )
    }
}