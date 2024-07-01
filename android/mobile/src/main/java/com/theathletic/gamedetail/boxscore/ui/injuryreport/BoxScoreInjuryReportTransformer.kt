package com.theathletic.gamedetail.boxscore.ui.injuryreport

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.gamedetail.boxscore.ui.common.toSummary
import com.theathletic.gamedetail.boxscore.ui.common.toUi
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.Transformer
import com.theathletic.ui.orShortDash
import com.theathletic.utility.orShortDash

class BoxScoreInjuryReportTransformer @AutoKoin constructor() :
    Transformer<BoxScoreInjuryReportState, BoxScoreInjuryReportContract.ViewState> {

    override fun transform(data: BoxScoreInjuryReportState): BoxScoreInjuryReportContract.ViewState {
        return BoxScoreInjuryReportContract.ViewState(
            gameDetails = data.game.toGameDetails,
            firstTeam = data.game?.firstTeam?.team.toSummary,
            secondTeam = data.game?.secondTeam?.team.toSummary,
            firstTeamInjuries = data.game?.firstTeam?.injuries?.toUi() ?: emptyList(),
            secondTeamInjuries = data.game?.secondTeam?.injuries?.toUi() ?: emptyList(),
            isFirstTeamSelected = data.isFirstTeamSelected
        )
    }

    private val GameDetailLocalModel?.toGameDetails: ResourceString
        get() = this?.let {
            StringWithParams(
                R.string.box_score_game_details_summary,
                awayTeam?.team?.alias.orShortDash(),
                homeTeam?.team?.alias.orShortDash(),
            )
        }.orShortDash()
}