package com.theathletic.hub.game.ui

import com.theathletic.boxscore.ui.GameDetailUi
import com.theathletic.boxscore.ui.formatters.BoxScoreBaseballInningFormatter
import com.theathletic.gamedetail.data.local.GameStatus
import com.theathletic.hub.game.R
import com.theathletic.hub.game.data.local.GameSummary
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asResourceString
import java.util.Collections

fun GameSummary.Team.mapToBasketballTimeouts(): List<GameDetailUi.TeamStatus> {
    if (this !is GameSummary.BasketballTeam) return emptyList()
    return listOf(
        GameDetailUi.TeamStatus.Timeouts(
            remainingTimeouts = remainingTimeouts ?: 0,
            usedTimeouts = usedTimeouts ?: 0
        )
    )
}

fun GameSummary.mapToBaseballInGameStatus(
    inningFormatter: BoxScoreBaseballInningFormatter
): GameDetailUi.GameStatus.BaseballInGameStatus {
    return GameDetailUi.GameStatus.BaseballInGameStatus(
        inningHalf = inningFormatter.format(baseballOutcome?.inning ?: 0, baseballOutcome?.inningHalf),
        occupiedBases = baseballOutcome?.occupiedBases ?: Collections.emptyList(),
        status = baseballOutcome?.toStatus() ?: "--".asResourceString(),
        isGameDelayed = status == GameStatus.DELAYED
    )
}

private fun GameSummary.BaseballOutcome.toStatus(): ResourceString {
    return when {
        strikes == 3 -> ResourceString.StringWithParams(
            R.string.box_score_baseball_live_status_strikeout_formatter,
            outs ?: 0
        )
        balls == 4 -> ResourceString.StringWithParams(
            R.string.box_score_baseball_live_status_walk_formatter,
            outs ?: 0
        )
        else -> ResourceString.StringWithParams(
            R.string.box_score_baseball_live_status_formatter,
            balls ?: 0,
            strikes ?: 0,
            outs ?: 0
        )
    }
}