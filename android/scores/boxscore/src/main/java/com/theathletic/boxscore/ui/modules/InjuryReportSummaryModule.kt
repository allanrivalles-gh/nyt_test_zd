package com.theathletic.boxscore.ui.modules

import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.InjuryReportSummary
import com.theathletic.boxscore.ui.InjuryReportUi
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedModuleV2

data class InjuryReportSummaryModule(
    val id: String,
    val firstTeam: InjuryReportUi.TeamDetails,
    val secondTeam: InjuryReportUi.TeamDetails,
    val firstTeamInjuries: List<InjuryReportUi.PlayerInjury>,
    val secondTeamInjuries: List<InjuryReportUi.PlayerInjury>,
) : FeedModuleV2 {

    override val moduleId: String = "InjuryReportModule:id"

    @Composable
    override fun Render() {
        InjuryReportSummary(
            gameId = id,
            firstTeam = firstTeam,
            secondTeam = secondTeam,
            firstTeamInjuries = firstTeamInjuries,
            secondTeamInjuries = secondTeamInjuries,
        )
    }

    interface Interaction {
        data class OnShowFullInjuryReportClick(
            val id: String,
            val isFirstTeamSelected: Boolean
        ) : FeedInteraction
    }
}