package com.theathletic.boxscore.ui

import androidx.compose.ui.graphics.Color
import com.theathletic.ui.asResourceString

object InjuryReportPreviewData {
    val gameId = "kjwhd8736diuegdutdiu"
    val gameDetail = "PHL @ IND".asResourceString()
    val firstTeam = InjuryReportUi.TeamDetails(
        name = "Eagles",
        logoUrls = emptyList(),
        teamColor = Color.Cyan
    )
    val secondTeam = InjuryReportUi.TeamDetails(
        name = "Colts",
        logoUrls = emptyList(),
        teamColor = Color.Cyan
    )
    val injuriesMoreThan2 = listOf(
        player1,
        player2,
        player3,
        player4
    )

    val interactor = object : InjuryReportUi.Interactor {
        override fun onBackButtonClicked() {}
        override fun onTeamSelected(firstTeamSelected: Boolean) {}
    }

    val summaryInteractor = object : InjuryReportUi.SummaryInteractor {
        override fun onInjuryReportFullReportClick(gameId: String, isFirstTeamSelected: Boolean) {}
    }

    private val player1: InjuryReportUi.PlayerInjury
        get() = InjuryReportUi.PlayerInjury(
            name = "DeForest Buckner",
            position = "DT",
            injury = "Knee - Buckners knee looks pretty swollen but I think he is faking it".asResourceString(),
            type = InjuryReportUi.InjuryType.DAY,
            headshots = emptyList()
        )

    private val player2: InjuryReportUi.PlayerInjury
        get() = InjuryReportUi.PlayerInjury(
            name = "Eric Fisher",
            position = "OT",
            injury = "Shoulder/Toe/Knee".asResourceString(),
            type = InjuryReportUi.InjuryType.OUT,
            headshots = emptyList()
        )

    private val player3: InjuryReportUi.PlayerInjury
        get() = InjuryReportUi.PlayerInjury(
            name = "Shaun Bradley",
            position = "LB",
            injury = "Knee".asResourceString(),
            type = InjuryReportUi.InjuryType.OUT,
            headshots = emptyList()
        )

    private val player4: InjuryReportUi.PlayerInjury
        get() = InjuryReportUi.PlayerInjury(
            name = "Fletcher Cox",
            position = "DT",
            injury = "Knee - The team planned for for Fletcher Cox to rest this week.".asResourceString(),
            type = InjuryReportUi.InjuryType.OUT,
            headshots = emptyList()
        )
}