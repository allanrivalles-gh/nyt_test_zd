package com.theathletic.gamedetail.ui

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.GameDetailUi
import com.theathletic.boxscore.ui.SoccerRecentFormHeaderModel
import com.theathletic.entity.main.Sport
import com.theathletic.gamedetail.data.local.GameStatus
import com.theathletic.gamedetail.data.local.GameSummaryLocalModel
import com.theathletic.scores.data.SupportedLeagues
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.utility.LocaleUtility
import com.theathletic.utility.orShortDash
import com.theathletic.utility.safeLet

class GameSummaryGameInfoRenderer @AutoKoin constructor(
    private val localeUtility: LocaleUtility,
    private val supportedLeagues: SupportedLeagues
) {
    fun getGameInfo(game: GameSummaryLocalModel?): GameDetailUi.GameInfo? {
        return game?.let {
            when (it.status) {
                GameStatus.FINAL -> {
                    when (it.sport) {
                        Sport.SOCCER -> it.toSoccerPostGameWinnerTitle
                        else -> null
                    }
                }
                GameStatus.IN_PROGRESS -> null
                else -> if (
                    it.sport == Sport.SOCCER &&
                    supportedLeagues.isRecentFormSupportingLeague(it.league.legacyLeague)
                ) {
                    it.toRecentFormGameInfo
                } else {
                    null
                }
            }
        }
    }

    private val GameSummaryLocalModel.toRecentFormGameInfo: GameDetailUi.GameInfo.RecentForm
        get() {
            val isReversed = localeUtility.isUnitedStatesOrCanada().not()
            val firstTeamRecentForm =
                (homeTeam as GameSummaryLocalModel.SoccerGameSummaryTeam).lastSix.toRecentForm(isReversed)
            val secondTeamRecentForm =
                (awayTeam as GameSummaryLocalModel.SoccerGameSummaryTeam).lastSix.toRecentForm(isReversed)

            return GameDetailUi.GameInfo.RecentForm(
                firstTeamRecentForm = firstTeamRecentForm,
                secondTeamRecentForm = secondTeamRecentForm,
                expectedGoals = toExpectedGoals(),
                isReverse = isReversed,
                showRecentForm = firstTeamRecentForm.isNotEmpty() && secondTeamRecentForm.isNotEmpty()
            )
        }

    private val GameSummaryLocalModel.toSoccerPostGameWinnerTitle: GameDetailUi.GameInfo.PostGameWinnerTitle?
        get() {
            val soccerExtras = (extras as GameSummaryLocalModel.SportExtras.Soccer)
            val firstTeamPenaltyScore = (firstTeam as GameSummaryLocalModel.SoccerGameSummaryTeam).penaltyScore
            val secondTeamPenaltyScore = (secondTeam as GameSummaryLocalModel.SoccerGameSummaryTeam).penaltyScore
            val aggWinnerTeamName = soccerExtras.aggregateWinnerName
            return if (firstTeamPenaltyScore != null && secondTeamPenaltyScore != null) {
                val (penaltyWinnerTeam, winnerScore, loserScore) = if (firstTeamPenaltyScore > secondTeamPenaltyScore) {
                    Triple(firstTeam?.displayName, firstTeamPenaltyScore, secondTeamPenaltyScore)
                } else {
                    Triple(secondTeam?.displayName, secondTeamPenaltyScore, firstTeamPenaltyScore)
                }
                GameDetailUi.GameInfo.PostGameWinnerTitle(
                    StringWithParams(
                        R.string.game_detail_header_soccer_penalty_winner_title,
                        penaltyWinnerTeam.orShortDash(),
                        winnerScore,
                        loserScore
                    ),
                )
            } else if (aggWinnerTeamName != null) {
                GameDetailUi.GameInfo.PostGameWinnerTitle(
                    StringWithParams(R.string.game_detail_header_soccer_agg_winner_title, aggWinnerTeamName),
                )
            } else {
                null
            }
        }

    private fun String.toRecentForm(isReversed: Boolean): List<SoccerRecentFormHeaderModel.SoccerRecentFormIcons> {
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

    private fun GameSummaryLocalModel.toExpectedGoals(): SoccerRecentFormHeaderModel.ExpectedGoals {
        safeLet(
            (homeTeam as GameSummaryLocalModel.SoccerGameSummaryTeam).expectedGoals,
            (awayTeam as GameSummaryLocalModel.SoccerGameSummaryTeam).expectedGoals
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
}