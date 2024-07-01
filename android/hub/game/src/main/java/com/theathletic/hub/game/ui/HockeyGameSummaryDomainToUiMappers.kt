package com.theathletic.hub.game.ui

import com.theathletic.boxscore.ui.GameDetailUi
import com.theathletic.hub.game.data.local.GameSummary

fun GameSummary.Team.mapToPowerPlay(): List<GameDetailUi.TeamStatus> {
    if (this !is GameSummary.HockeyTeam) return emptyList()
    return listOf(
        GameDetailUi.TeamStatus.HockeyPowerPlay(
            inPowerPlay = strength == com.theathletic.gamedetail.data.local.HockeyStrength.POWERPLAY
        )
    )
}