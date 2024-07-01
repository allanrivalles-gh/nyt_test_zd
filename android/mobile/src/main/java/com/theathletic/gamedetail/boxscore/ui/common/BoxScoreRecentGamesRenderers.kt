package com.theathletic.gamedetail.boxscore.ui.common

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.RecentGamesUi
import com.theathletic.boxscore.ui.modules.RecentGamesModule
import com.theathletic.datetime.DateUtility
import com.theathletic.datetime.formatter.DisplayFormat
import com.theathletic.entity.main.Sport
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.Period
import com.theathletic.gamedetail.data.local.overtimePeriods
import com.theathletic.gamedetail.data.local.shootoutPeriods
import com.theathletic.themes.AthColor
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.ResourceString.StringWrapper
import com.theathletic.utility.orShortDash
import java.util.concurrent.atomic.AtomicInteger

data class DisplayScore(
    val firstTeamScore: Int,
    val secondTeamScore: Int
)

class BoxScoreRecentGamesRenderers @AutoKoin constructor(
    private val dateUtility: DateUtility
) {

    fun createRecentGamesModule(game: GameDetailLocalModel) = renderRecentGamesModule(game)

    @Deprecated("Use createRecentGamesModule(game: GameDetailLocalModel)")
    fun createRecentGamesModule(
        game: GameDetailLocalModel,
        pageOrder: AtomicInteger
    ): FeedModuleV2? {
        if (game.isGameScheduled.not() &&
            game.firstTeam?.recentGames.areGamesAvailable() &&
            game.secondTeam?.recentGames.areGamesAvailable()
        ) {
            return null
        }

        pageOrder.getAndIncrement()
        return renderRecentGamesModule(game)
    }

    private fun List<GameDetailLocalModel.RecentGame>?.areGamesAvailable(): Boolean {
        return this?.isEmpty() == true
    }

    private fun renderRecentGamesModule(game: GameDetailLocalModel): FeedModuleV2 {
        return RecentGamesModule(
            id = game.id,
            teams = RecentGamesUi.Teams(
                firstTeamName = game.firstTeam?.team?.displayName.orShortDash(),
                secondTeamName = game.secondTeam?.team?.displayName.orShortDash()
            ),
            firstTeamRecentGames = game.toRecentGames(true),
            secondTeamRecentGames = game.toRecentGames(false),
            titleId = if (game.sport == Sport.SOCCER) {
                R.string.box_score_recent_matches_title
            } else {
                R.string.box_score_last_games_title
            },
            noGamesTitleId = if (game.sport == Sport.SOCCER) {
                R.string.box_score_recent_matches_not_available
            } else {
                R.string.box_score_recent_games_not_available
            },
            leagueName = if (game.sport == Sport.SOCCER) game.league.displayName else null
        )
    }

    private fun GameDetailLocalModel.toRecentGames(isFirstTeam: Boolean): List<RecentGamesUi.RecentGame> {
        val currentTeam = currentTeam(isFirstTeam)
        val recentGames = mutableListOf<RecentGamesUi.RecentGame>()
        currentTeam?.recentGames?.sortedByDescending { it.scheduleAt }?.forEachIndexed { index, recentGame ->
            val thisAndOtherTeamScores = if (recentGame.firstTeam(sport)?.alias == currentTeam.team?.alias) {
                Pair(recentGame.firstTeam(sport)?.score ?: 0, recentGame.secondTeam(sport)?.score ?: 0)
            } else {
                Pair(recentGame.secondTeam(sport)?.score ?: 0, recentGame.firstTeam(sport)?.score ?: 0)
            }
            val (stringRes, colorRes) = getResultSummaryAndColor(
                thisAndOtherTeamScores.first,
                thisAndOtherTeamScores.second,
                inOvertime = recentGame.period.finishInOvertime(),
                inShootout = recentGame.period.finishInShootout()
            )

            val firstTeamScore = recentGame.firstTeam(sport)?.score ?: 0
            val secondTeamScore = recentGame.secondTeam(sport)?.score ?: 0
            val displayScore = displayScore(firstTeamScore, secondTeamScore)

            recentGames.add(
                RecentGamesUi.RecentGame(
                    id = recentGame.id,
                    teamId = currentTeam.id,
                    date = dateUtility.formatGMTDate(
                        recentGame.scheduleAt,
                        DisplayFormat.MONTH_DATE_YEAR_SHORT
                    ),
                    opponentLogoUrlList = currentTeam.opponentLogo(recentGame) ?: emptyList(),
                    opponentTeamAlias = currentTeam.opponentAlias(recentGame),
                    firstTeamScore = displayScore.firstTeamScore.toString(),
                    secondTeamScore = displayScore.secondTeamScore.toString(),
                    isFirstTeamWinners = recentGame.isFirstTeamWinner(sport),
                    isSecondTeamWinners = recentGame.isSecondTeamWinner(sport),
                    result = StringWithParams(stringRes),
                    resultColor = colorRes
                )
            )
        }
        return recentGames
    }

    private fun GameDetailLocalModel.currentTeam(isFirstTeam: Boolean): GameDetailLocalModel.GameTeam? {
        return if (isFirstTeam) {
            firstTeam
        } else {
            secondTeam
        }
    }

    private fun GameDetailLocalModel.displayScore(
        firstTeamScore: Int,
        secondTeamScore: Int
    ): DisplayScore {
        return if (sport == Sport.SOCCER || firstTeamScore >= secondTeamScore) {
            DisplayScore(firstTeamScore, secondTeamScore)
        } else {
            DisplayScore(secondTeamScore, firstTeamScore)
        }
    }

    private fun GameDetailLocalModel.RecentGame.isFirstTeamWinner(sport: Sport) =
        ((firstTeam(sport)?.score ?: 0) >= (secondTeam(sport)?.score ?: 0) && sport == Sport.SOCCER)

    private fun GameDetailLocalModel.RecentGame.isSecondTeamWinner(sport: Sport) =
        ((secondTeam(sport)?.score ?: 0) >= (firstTeam(sport)?.score ?: 0) && sport == Sport.SOCCER)

    private fun GameDetailLocalModel.GameTeam.opponentLogo(
        recentGame: GameDetailLocalModel.RecentGame
    ) = if (recentGame.awayTeam?.alias == team?.alias) {
        recentGame.homeTeam?.logos
    } else {
        recentGame.awayTeam?.logos
    }

    private fun GameDetailLocalModel.GameTeam.opponentAlias(
        recentGame: GameDetailLocalModel.RecentGame
    ) = if (recentGame.awayTeam?.alias == team?.alias) {
        StringWithParams(
            R.string.box_score_away_team_game,
            recentGame.homeTeam?.alias.orShortDash()
        )
    } else {
        StringWrapper(
            recentGame.awayTeam?.alias.orShortDash()
        )
    }

    private fun Period.finishInOvertime() = overtimePeriods.contains(this)

    private fun Period.finishInShootout() = shootoutPeriods.contains(this)

    private fun getResultSummaryAndColor(
        thisTeamsScore: Int,
        otherTeamsScore: Int,
        inOvertime: Boolean,
        inShootout: Boolean
    ): Pair<Int, Color> {
        return when {
            thisTeamsScore == otherTeamsScore -> Pair(R.string.box_score_last_games_tie, AthColor.Gray500)
            thisTeamsScore > otherTeamsScore -> Pair(
                getWinIndicator(inOvertime, inShootout),
                AthColor.Green800
            )
            else -> Pair(
                getLostIndicator(inOvertime, inShootout),
                AthColor.RedUser
            )
        }
    }

    @StringRes
    private fun getWinIndicator(
        inOvertime: Boolean,
        inShootout: Boolean
    ) = when {
        inOvertime -> R.string.box_score_last_games_win_in_overtime
        inShootout -> R.string.box_score_last_games_win_in_shootout
        else -> R.string.box_score_last_games_win
    }

    @StringRes
    private fun getLostIndicator(
        inOvertime: Boolean,
        inShootout: Boolean
    ) = when {
        inOvertime -> R.string.box_score_last_games_lose_in_overtime
        inShootout -> R.string.box_score_last_games_lose_in_shootout
        else -> R.string.box_score_last_games_lose
    }

    private fun GameDetailLocalModel.RecentGame.firstTeam(sport: Sport) = when {
        sport.homeTeamFirst -> homeTeam
        else -> awayTeam
    }

    private fun GameDetailLocalModel.RecentGame.secondTeam(sport: Sport) = when {
        sport.homeTeamFirst -> awayTeam
        else -> homeTeam
    }
}