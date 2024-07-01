package com.theathletic.gamedetail.ui

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.GameDetailUi
import com.theathletic.entity.main.League
import com.theathletic.entity.main.Sport
import com.theathletic.gamedetail.data.local.GameSummaryLocalModel
import com.theathletic.gamedetail.data.local.Period
import com.theathletic.scores.data.SupportedLeagues
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asResourceString
import com.theathletic.utility.safeLet

class GameSummaryTeamRenderer @AutoKoin constructor(
    private val supportedLeagues: SupportedLeagues
) {

    fun getTeamSummary(
        game: GameSummaryLocalModel?,
        isFirstTeam: Boolean,
        isLoaded: Boolean,
        isFollowable: Boolean
    ): GameDetailUi.TeamSummary {
        val currentTeam = if (isFirstTeam) game?.firstTeam else game?.secondTeam
        return currentTeam?.let { team ->
            val teamCurrentRanking = team.toCurrentRanking()
            GameDetailUi.TeamSummary(
                teamId = team.id,
                legacyId = team.legacyId,
                name = team.alias.asResourceString(),
                logoUrls = team.logos,
                score = if (game?.isGameInProgressOrCompleted == true) {
                    currentTeam.score
                } else {
                    null
                },
                currentRecord = getCurrentRecord(game, currentTeam),
                isWinner = game.isTeamWin(isFirstTeam),
                isFollowable = isFollowable,
                currentRanking = teamCurrentRanking,
                showCollegeCurrentRanking = game?.league?.legacyLeague?.let { supportedLeagues.isCollegeLeague(it) } ?: false,
                showCurrentRanking = showTeamCurrentRanking(teamCurrentRanking, game)

            )
        } ?: GameDetailUi.TeamSummary(
            teamId = "",
            legacyId = 0L,
            name = if (isLoaded) {
                ResourceString.StringWithParams(R.string.global_tbc)
            } else {
                "-".asResourceString()
            },
            logoUrls = emptyList(),
            score = null,
            isWinner = false,
            currentRecord = null,
            currentRanking = null,
            showCurrentRanking = false,
            isFollowable = true
        )
    }

    private fun showTeamCurrentRanking(
        teamCurrentRanking: String?,
        game: GameSummaryLocalModel?
    ) = if (teamCurrentRanking == null) {
        false
    } else {
        game?.league?.legacyLeague?.isCurrentRankingSupportedLeague() ?: false
    }

    private fun getCurrentRecord(
        game: GameSummaryLocalModel?,
        currentTeam: GameSummaryLocalModel.GameSummaryTeam
    ) = if (game?.sport == Sport.SOCCER) {
        null
    } else {
        currentTeam.currentRecord
    }

    fun getTeamStatus(
        game: GameSummaryLocalModel?,
        isFirstTeam: Boolean
    ): List<GameDetailUi.TeamStatus> {
        if (game == null) return emptyList()
        val statusList = mutableListOf<GameDetailUi.TeamStatus>()
        val currentTeam = if (isFirstTeam) game.firstTeam else game.secondTeam
        when (game.sport) {
            Sport.HOCKEY -> statusList.addAll(renderHockeyStatus(game, currentTeam))
            Sport.BASKETBALL -> statusList.addAll(renderBasketballStatus(game, currentTeam))
            Sport.FOOTBALL -> statusList.addAll(renderAmericanFootballStatus(game, currentTeam))
            else -> { /* Nothing to add */
            }
        }
        return statusList
    }

    private fun renderHockeyStatus(
        game: GameSummaryLocalModel,
        currentTeam: GameSummaryLocalModel.GameSummaryTeam?
    ): List<GameDetailUi.TeamStatus> {
        val statusList = mutableListOf<GameDetailUi.TeamStatus>()
        if (game.isGameInProgress) {
            (currentTeam as? GameSummaryLocalModel.HockeyGameSummaryTeam)?.toPowerPlay()?.let {
                statusList.add(it)
            }
        }
        return statusList
    }

    private fun renderBasketballStatus(
        game: GameSummaryLocalModel,
        currentTeam: GameSummaryLocalModel.GameSummaryTeam?
    ): List<GameDetailUi.TeamStatus> {
        val statusList = mutableListOf<GameDetailUi.TeamStatus>()
        if (game.isGameInProgress) {
            (currentTeam as? GameSummaryLocalModel.BasketballGameSummaryTeam)?.toTimeouts()?.let {
                statusList.add(it)
            }
        }
        return statusList
    }

    private fun renderAmericanFootballStatus(
        game: GameSummaryLocalModel,
        currentTeam: GameSummaryLocalModel.GameSummaryTeam?
    ): List<GameDetailUi.TeamStatus> {
        val statusList = mutableListOf<GameDetailUi.TeamStatus>()
        val team = (currentTeam as? GameSummaryLocalModel.AmericanFootballGameSummaryTeam)
        if (game.isGameInProgress) {
            if (team?.hasPossession == true && game.period != Period.HALF_TIME) {
                statusList.add(GameDetailUi.TeamStatus.Possession)
            }
            team?.toTimeouts()?.let {
                statusList.add(it)
            }
        }
        return statusList
    }

    private fun GameSummaryLocalModel?.isTeamWin(isFirstTeam: Boolean): Boolean {
        return safeLet(this?.firstTeam?.score, this?.secondTeam?.score) { firstScore, secondScore ->
            when {
                this?.status != com.theathletic.gamedetail.data.local.GameStatus.FINAL -> true
                isFirstTeam && secondScore > firstScore -> false
                !isFirstTeam && firstScore > secondScore -> false
                else -> true
            }
        } ?: true
    }

    private fun GameSummaryLocalModel.HockeyGameSummaryTeam.toPowerPlay() =
        GameDetailUi.TeamStatus.HockeyPowerPlay(
            inPowerPlay = strength == com.theathletic.gamedetail.data.local.HockeyStrength.POWERPLAY
        )

    private fun GameSummaryLocalModel.BasketballGameSummaryTeam.toTimeouts() =
        GameDetailUi.TeamStatus.Timeouts(
            remainingTimeouts = remainingTimeouts ?: 0,
            usedTimeouts = usedTimeouts ?: 0
        )

    private fun GameSummaryLocalModel.AmericanFootballGameSummaryTeam.toTimeouts() =
        GameDetailUi.TeamStatus.Timeouts(
            remainingTimeouts = remainingTimeouts ?: 0,
            usedTimeouts = usedTimeouts ?: 0
        )

    private fun GameSummaryLocalModel.GameSummaryTeam.toCurrentRanking() =
        when (this) {
            is GameSummaryLocalModel.SoccerGameSummaryTeam -> this.currentRanking
            is GameSummaryLocalModel.AmericanFootballGameSummaryTeam -> this.currentRanking
            is GameSummaryLocalModel.BasketballGameSummaryTeam -> this.currentRanking
            else -> null
        }

    private fun League.isCurrentRankingSupportedLeague() =
        when (this) {
            League.EPL,
            League.EFL,
            League.LEAGUE_ONE,
            League.LEAGUE_TWO,
            League.SCOTTISH_PREMIERE,
            League.MLS,
            League.NWSL,
            League.LA_LIGA -> true
            else -> false
        }
}