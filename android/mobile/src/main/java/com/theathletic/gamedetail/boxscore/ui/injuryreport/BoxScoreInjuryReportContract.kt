package com.theathletic.gamedetail.boxscore.ui.injuryreport

import com.theathletic.boxscore.ui.InjuryReportUi
import com.theathletic.presenter.Interactor
import com.theathletic.ui.ResourceString

interface BoxScoreInjuryReportContract {

    interface Presenter :
        Interactor,
        InjuryReportUi.Interactor

    data class ViewState(
        val gameDetails: ResourceString,
        val firstTeam: InjuryReportUi.TeamDetails,
        val secondTeam: InjuryReportUi.TeamDetails,
        val firstTeamInjuries: List<InjuryReportUi.PlayerInjury>,
        val secondTeamInjuries: List<InjuryReportUi.PlayerInjury>,
        val isFirstTeamSelected: Boolean,
    ) : com.theathletic.ui.ViewState
}