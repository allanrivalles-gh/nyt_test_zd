package com.theathletic.gamedetail.boxscore.ui.basketball

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.modules.PlayModule
import com.theathletic.boxscore.ui.modules.PlaysPeriodHeaderModule
import com.theathletic.boxscore.ui.modules.RecentPlaysModule
import com.theathletic.boxscore.ui.modules.StoppagePlayModule
import com.theathletic.boxscore.ui.modules.TimeoutPlayModule
import com.theathletic.feed.ui.FeedModule
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.boxscore.ui.common.toPeriodLabel
import com.theathletic.gamedetail.boxscore.ui.playbyplay.BoxScorePlayByPlayState
import com.theathletic.gamedetail.data.local.BasketballPlayType
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.GameStatus
import com.theathletic.gamedetail.data.local.Period
import com.theathletic.gamedetail.data.local.PlayByPlayLocalModel
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.modules.SpacingModule
import com.theathletic.utility.orShortDash

class BasketballPlayByPlayRenderers @AutoKoin constructor() {

    fun renderModules(data: BoxScorePlayByPlayState): List<FeedModule> {
        return data.gamePlays?.let { gamePlays ->
            val modules = mutableListOf<FeedModule>()
            val groupByPeriod = gamePlays.plays
                .filterIsInstance<GameDetailLocalModel.BasketballPlay>()
                .groupBy { it.period }
            groupByPeriod.entries.forEach { (period, plays) ->
                val (awayScore, homeScore) = plays.toScore(data.gamePlays.status)
                modules.add(
                    PlaysPeriodHeaderModule(
                        id = period.toString(),
                        expanded = data.currentExpandedPeriod == period,
                        title = period.toPeriodLabel,
                        periodData = data.gamePlays.toPeriodScores(period),
                        firstTeamAlias = data.gamePlays.awayTeam?.alias.orShortDash(),
                        secondTeamAlias = data.gamePlays.homeTeam?.alias.orShortDash(),
                        firstTeamScore = awayScore.orShortDash(),
                        secondTeamScore = homeScore.orShortDash()
                    )
                )
                if (data.currentExpandedPeriod == period) {
                    modules.addAll(
                        renderBasketballPlays(
                            data.gamePlays,
                            plays.filterIsInstance<GameDetailLocalModel.BasketballPlay>()
                        )
                    )
                }
            }
            if (modules.isNotEmpty()) modules.add(
                SpacingModule(
                    id = gamePlays.id,
                    color = SpacingModule.Background.StandardForegroundColor,
                    height = SpacingModule.Height.ExtraLarge
                )
            )
            modules
        } ?: emptyList()
    }

    private fun renderBasketballPlays(
        gamePlays: PlayByPlayLocalModel,
        plays: List<GameDetailLocalModel.BasketballPlay>
    ): List<FeedModule> {
        return plays.mapIndexed { index, play ->
            val isLastPlay = plays.lastIndex != index
            when {
                play.isTeamTimeout -> play.toPlayModule(isLastPlay)
                play.isNonTeamTimeoutOrEndOfPeriod -> play.toTimeoutModule(isLastPlay)
                play.isStoppage -> play.toStoppageModule(isLastPlay)
                play.isScoringPlay -> play.toScoringPlayModule(
                    awayTeamAlias = gamePlays.awayTeam?.alias.orShortDash(),
                    homeTeamAlias = gamePlays.homeTeam?.alias.orShortDash(),
                    awayTeamScore = play.awayTeamScore.toString(),
                    homeTeamScore = play.homeTeamScore.toString(),
                    showDivider = isLastPlay
                )
                else -> play.toPlayModule(isLastPlay)
            }
        }.toMutableList()
    }

    fun createBasketballRecentPlaysModule(
        game: GameDetailLocalModel,
        recentPlays: List<GameDetailLocalModel.BasketballPlay>
    ): FeedModuleV2 {
        return RecentPlaysModule(
            id = game.id,
            recentPlays = recentPlays.map { play ->
                when {
                    play.isTeamTimeout -> play.toRecentPlaysPlayModule()
                    play.isNonTeamTimeoutOrEndOfPeriod -> play.toRecentPlaysTimeoutModule()
                    play.isStoppage -> play.toRecentPlaysStoppageModule()
                    play.isScoringPlay -> play.toRecentPlaysScoringPlayModule(
                        awayTeamAlias = game.awayTeam?.team?.alias.orShortDash(),
                        homeTeamAlias = game.homeTeam?.team?.alias.orShortDash(),
                        awayTeamScore = play.awayTeamScore.toString(),
                        homeTeamScore = play.homeTeamScore.toString(),
                        showDivider = true
                    )
                    else -> play.toRecentPlaysPlayModule()
                }
            },
        )
    }

    private fun GameDetailLocalModel.BasketballPlay.toRecentPlaysScoringPlayModule(
        awayTeamAlias: String,
        homeTeamAlias: String,
        awayTeamScore: String,
        homeTeamScore: String,
        showDivider: Boolean
    ) = RecentPlaysModule.RecentPlay.Play(
        id = id,
        teamLogos = team?.logos ?: emptyList(),
        title = headerLabel.orShortDash(),
        description = description,
        clock = clock,
        awayTeamAlias = awayTeamAlias,
        homeTeamAlias = homeTeamAlias,
        awayTeamScore = awayTeamScore,
        homeTeamScore = homeTeamScore,
        showScores = true
    )

    private fun GameDetailLocalModel.BasketballPlay.toRecentPlaysStoppageModule() =
        RecentPlaysModule.RecentPlay.Stoppage(
            id = id,
            title = headerLabel.orShortDash(),
            description = description
        )

    private fun GameDetailLocalModel.BasketballPlay.toRecentPlaysTimeoutModule() =
        RecentPlaysModule.RecentPlay.Timeout(
            id = id,
            title = headerLabel.orShortDash(),
        )

    private fun GameDetailLocalModel.BasketballPlay.toRecentPlaysPlayModule() =
        RecentPlaysModule.RecentPlay.Play(
            id = id,
            teamLogos = team?.logos ?: emptyList(),
            title = headerLabel.orShortDash(),
            description = description,
            clock = clock,
            awayTeamAlias = "",
            homeTeamAlias = "",
            awayTeamScore = "",
            homeTeamScore = "",
            showScores = false
        )

    private fun GameDetailLocalModel.BasketballPlay.toPlayModule(showDivider: Boolean) = PlayModule(
        id = id,
        teamLogos = team?.logos ?: emptyList(),
        title = headerLabel.orShortDash(),
        description = description,
        clock = clock,
        showDivider = showDivider
    )

    private fun GameDetailLocalModel.BasketballPlay.toScoringPlayModule(
        awayTeamAlias: String,
        homeTeamAlias: String,
        awayTeamScore: String,
        homeTeamScore: String,
        showDivider: Boolean
    ) = PlayModule(
        id = id,
        teamLogos = team?.logos ?: emptyList(),
        title = headerLabel.orShortDash(),
        description = description,
        clock = clock,
        awayTeamAlias = awayTeamAlias,
        homeTeamAlias = homeTeamAlias,
        awayTeamScore = awayTeamScore,
        homeTeamScore = homeTeamScore,
        showScores = true,
        showDivider = showDivider,
        teamColor = team?.primaryColor,
        showTeamCurtain = true
    )

    private fun GameDetailLocalModel.BasketballPlay.toStoppageModule(showDivider: Boolean) =
        StoppagePlayModule(
            id = id,
            title = headerLabel.orShortDash(),
            description = description,
            showDivider = showDivider
        )

    private fun GameDetailLocalModel.BasketballPlay.toTimeoutModule(showDivider: Boolean) =
        TimeoutPlayModule(
            id = id,
            title = headerLabel.orShortDash(),
            showDivider = showDivider
        )

    /**
     * For games in progress, the backend returns the plays newest to oldest but when the game is completed
     * it returns them from oldest to newest. Due to this need to retrieve the last or first play for the period
     * depending upon game status.
     */
    private fun List<GameDetailLocalModel.BasketballPlay>.toScore(status: GameStatus): Pair<String?, String?> {
        return when (status) {
            GameStatus.FINAL -> Pair(last().awayTeamScore.toString(), last().homeTeamScore.toString())
            GameStatus.IN_PROGRESS -> Pair(first().awayTeamScore.toString(), first().homeTeamScore.toString())
            else -> Pair(null, null)
        }
    }

    private fun PlayByPlayLocalModel.toPeriodScores(period: Period) =
        StringWithParams(
            R.string.plays_basketball_period_scores,
            awayTeam?.alias.orShortDash(),
            period.toScore(awayTeamScores).orShortDash(),
            homeTeam?.alias.orShortDash(),
            period.toScore(homeTeamScores).orShortDash()
        )

    private fun Period.toScore(scores: List<GameDetailLocalModel.ScoreType>?) =
        scores?.filterIsInstance<GameDetailLocalModel.PeriodScore>()
            ?.find { it.period == this }?.scoreDisplay

    private val GameDetailLocalModel.BasketballPlay.isNonTeamTimeoutOrEndOfPeriod: Boolean
        get() = when (playType) {
            BasketballPlayType.END_PERIOD,
            BasketballPlayType.CHALLENGE_TIMEOUT,
            BasketballPlayType.TV_TIMEOUT,
            BasketballPlayType.OFFICIAL_TIMEOUT -> true
            else -> false
        }

    private val GameDetailLocalModel.BasketballPlay.isTeamTimeout: Boolean
        get() = playType == BasketballPlayType.TEAM_TIMEOUT

    private val GameDetailLocalModel.BasketballPlay.isStoppage: Boolean
        get() = when (playType) {
            BasketballPlayType.STOPPAGE,
            BasketballPlayType.REVIEW,
            BasketballPlayType.REQUEST_REVIEW -> true
            else -> false
        }

    private val GameDetailLocalModel.BasketballPlay.isScoringPlay: Boolean
        get() = when (playType) {
            BasketballPlayType.THREE_POINT_MADE,
            BasketballPlayType.TWO_POINT_MADE,
            BasketballPlayType.FREE_THROW_MADE -> true
            else -> false
        }
}