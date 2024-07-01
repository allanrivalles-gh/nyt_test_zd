package com.theathletic.hub.game.ui

import com.theathletic.boxscore.ui.GameDetailUi
import com.theathletic.boxscore.ui.SoccerRecentFormHeaderModel
import com.theathletic.datetime.DateUtility
import com.theathletic.datetime.Datetime
import com.theathletic.gamedetail.data.local.GameStatus
import com.theathletic.hub.game.R
import com.theathletic.hub.game.data.local.GameSummary
import com.theathletic.ui.ResourceString
import com.theathletic.ui.orShortDash
import com.theathletic.utility.orShortDash
import com.theathletic.utility.safeLet

fun GameSummary.mapToSoccerPostGameWinnerTitle(): GameDetailUi.GameInfo.PostGameWinnerTitle? {
    val firstTeamPenaltyScore = (firstTeam as GameSummary.SoccerTeam).penaltyScore
    val secondTeamPenaltyScore = (secondTeam as GameSummary.SoccerTeam).penaltyScore
    val aggWinnerTeamName = soccerInfo?.aggregateWinnerName

    return when {
        firstTeamPenaltyScore != null && secondTeamPenaltyScore != null -> {
            val (penaltyWinnerTeam, winnerScore, loserScore) = if (firstTeamPenaltyScore > secondTeamPenaltyScore) {
                Triple(firstTeam.displayName, firstTeamPenaltyScore, secondTeamPenaltyScore)
            } else {
                Triple(secondTeam.displayName, secondTeamPenaltyScore, firstTeamPenaltyScore)
            }

            GameDetailUi.GameInfo.PostGameWinnerTitle(
                ResourceString.StringWithParams(
                    R.string.game_detail_header_soccer_penalty_winner_title,
                    penaltyWinnerTeam.orShortDash(),
                    winnerScore,
                    loserScore
                ),
            )
        }

        aggWinnerTeamName != null ->
            GameDetailUi.GameInfo.PostGameWinnerTitle(
                ResourceString.StringWithParams(R.string.game_detail_header_soccer_agg_winner_title, aggWinnerTeamName),
            )

        else -> null
    }
}

fun GameSummary.mapToRecentFormGameInfo(isUnitedStatesOrCanada: Boolean): GameDetailUi.GameInfo.RecentForm {

    val isReversed = isUnitedStatesOrCanada.not()
    val firstTeamRecentForm = (firstTeam as GameSummary.SoccerTeam).lastSix.toRecentForm(isReversed)
    val secondTeamRecentForm = (secondTeam as GameSummary.SoccerTeam).lastSix.toRecentForm(isReversed)

    return GameDetailUi.GameInfo.RecentForm(
        firstTeamRecentForm = firstTeamRecentForm,
        secondTeamRecentForm = secondTeamRecentForm,
        expectedGoals = mapToSoccerExpectedGoals(),
        isReverse = isReversed,
        showRecentForm = firstTeamRecentForm.isNotEmpty() && secondTeamRecentForm.isNotEmpty()
    )
}

fun GameSummary.mapToSoccerInGameStatus(): GameDetailUi.GameStatus.SoccerInGameStatus {
    return GameDetailUi.GameStatus.SoccerInGameStatus(
        gameStatePrimary = gameStatePrimary,
        showAggregate = showAggregateScore(
            currentGameScheduleAt = scheduleAt,
            relatedGameScheduleAt = soccerInfo?.relatedGameScheduleAt ?: Datetime(0L)
        ),
        aggregate = mapToAggregateScore(),
        isGameDelayed = status == GameStatus.DELAYED
    )
}

fun GameSummary.mapToSoccerPostGameStatus(dateUtility: DateUtility): GameDetailUi.GameStatus.SoccerPostGameStatus {
    return GameDetailUi.GameStatus.SoccerPostGameStatus(
        gamePeriod = period.toHeaderPeriodLabel(sport, status).orShortDash(),
        scheduledDate = formattedStartDate(dateUtility),
        showAggregate = showAggregateScore(
            currentGameScheduleAt = scheduleAt,
            relatedGameScheduleAt = soccerInfo?.relatedGameScheduleAt ?: Datetime(0L)
        ),
        aggregate = mapToAggregateScore()
    )
}

fun String.toRecentForm(isReversed: Boolean): List<SoccerRecentFormHeaderModel.SoccerRecentFormIcons> {
    if (isEmpty()) return emptyList()

    val matchesList = MutableList(5) { SoccerRecentFormHeaderModel.SoccerRecentFormIcons.NONE }
    take(5).forEachIndexed { index, result ->
        when (result) {
            'W' -> matchesList[index] = SoccerRecentFormHeaderModel.SoccerRecentFormIcons.WIN
            'L' -> matchesList[index] = SoccerRecentFormHeaderModel.SoccerRecentFormIcons.LOSS
            'D' -> matchesList[index] = SoccerRecentFormHeaderModel.SoccerRecentFormIcons.DRAW
            else -> matchesList[index] = SoccerRecentFormHeaderModel.SoccerRecentFormIcons.NONE
        }
    }
    return if (isReversed) matchesList.reversed() else matchesList
}

private fun GameSummary.mapToSoccerExpectedGoals(): SoccerRecentFormHeaderModel.ExpectedGoals {
    safeLet(
        (firstTeam as GameSummary.SoccerTeam).expectedGoals,
        (secondTeam as GameSummary.SoccerTeam).expectedGoals
    ) { firstTeamValue, secondTeamValue ->
        return SoccerRecentFormHeaderModel.ExpectedGoals(
            firstTeamValue = firstTeamValue.label,
            secondTeamValue = secondTeamValue.label,
            showExpectedGoals = true
        )
    }

    return SoccerRecentFormHeaderModel.ExpectedGoals(
        firstTeamValue = "",
        secondTeamValue = "",
        showExpectedGoals = false
    )
}

private fun GameSummary.mapToAggregateScore(): ResourceString {
    val firstTeamScore = (firstTeam as GameSummary.SoccerTeam).aggregateScore ?: 0
    val secondTeamScore = (secondTeam as GameSummary.SoccerTeam).aggregateScore ?: 0
    return ResourceString.StringWithParams(
        R.string.box_score_soccer_header_aggregate_score,
        firstTeamScore,
        secondTeamScore
    )
}

// If there is aggregate score, show it but only if the game is the Leg 2 game.
private fun GameSummary.showAggregateScore(
    currentGameScheduleAt: Datetime,
    relatedGameScheduleAt: Datetime
): Boolean {
    safeLet(
        (firstTeam as GameSummary.SoccerTeam).aggregateScore,
        (secondTeam as GameSummary.SoccerTeam).aggregateScore
    ) { _, _ ->
        return currentGameScheduleAt > relatedGameScheduleAt
    }
    return false
}