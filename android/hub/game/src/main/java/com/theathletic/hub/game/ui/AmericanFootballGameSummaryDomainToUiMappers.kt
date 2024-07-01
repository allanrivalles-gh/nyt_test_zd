package com.theathletic.hub.game.ui

import com.theathletic.boxscore.ui.GameDetailUi
import com.theathletic.hub.game.data.local.GameSummary

fun GameSummary.Team.mapToAmericanFootballTimeouts(): List<GameDetailUi.TeamStatus> {
    if (this !is GameSummary.AmericanFootballTeam) return emptyList()
    return listOf(
        GameDetailUi.TeamStatus.Timeouts(
            remainingTimeouts = remainingTimeouts ?: 0,
            usedTimeouts = usedTimeouts ?: 0
        )
    )
}