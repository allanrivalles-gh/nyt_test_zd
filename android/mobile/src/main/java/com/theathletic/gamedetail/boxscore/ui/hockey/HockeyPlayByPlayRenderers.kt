package com.theathletic.gamedetail.boxscore.ui.hockey

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.modules.HockeyShootoutPlayModule
import com.theathletic.boxscore.ui.modules.PlayModule
import com.theathletic.boxscore.ui.modules.PlaysPeriodHeaderModule
import com.theathletic.boxscore.ui.modules.PlaysSimplePeriodHeaderModule
import com.theathletic.boxscore.ui.modules.RecentPlaysModule
import com.theathletic.boxscore.ui.modules.StoppagePlayModule
import com.theathletic.boxscore.ui.modules.TimeoutPlayModule
import com.theathletic.boxscore.ui.modules.TwoItemToggleButtonModule
import com.theathletic.feed.ui.FeedModule
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.boxscore.ui.common.toPeriodLabel
import com.theathletic.gamedetail.boxscore.ui.playbyplay.BoxScorePlayByPlayState
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.GameStatus
import com.theathletic.gamedetail.data.local.HockeyPlayType
import com.theathletic.gamedetail.data.local.Period
import com.theathletic.gamedetail.data.local.PlayByPlayLocalModel
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.modules.SpacingModule
import com.theathletic.ui.utility.parseHexColor
import com.theathletic.utility.orShortDash

class HockeyPlayByPlayRenderers @AutoKoin constructor() {

    fun renderModules(data: BoxScorePlayByPlayState): List<FeedModule> {
        return data.gamePlays?.let { gamePlays ->
            val modules = mutableListOf<FeedModule>()
            val hasScoringPlays = data.gamePlays.plays.hasScoringPlays
            if (hasScoringPlays) {
                modules.add(
                    TwoItemToggleButtonModule(
                        id = data.gamePlays.id,
                        itemOneLabel = StringWithParams(R.string.plays_hockey_plays_option_title_all_plays),
                        itemTwoLabel = StringWithParams(R.string.plays_hockey_plays_option_title_scoring_plays),
                        isFirstItemSelected = data.isFirstViewItemSelected
                    )
                )
            }
            if (!data.isFirstViewItemSelected && hasScoringPlays) {
                modules.addAll(renderScoringPlays(data))
            } else {
                modules.addAll(renderAllPlays(data))
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

    private fun renderAllPlays(data: BoxScorePlayByPlayState): List<FeedModule> {
        val modules = mutableListOf<FeedModule>()
        val plays = data.gamePlays?.plays ?: return emptyList()
        val groupByPeriod = plays
            .filterIsInstance<GameDetailLocalModel.HockeyPlay>()
            .groupBy { it.period }
        groupByPeriod.entries.forEach { (period, plays) ->
            val (awayScore, homeScore) = plays.toHockeyScore(data.gamePlays.status)
            val (awaySog, homeSog) = plays.toShotsAtGoal(data.gamePlays.status)
            modules.add(
                PlaysPeriodHeaderModule(
                    id = period.toString(),
                    expanded = data.currentExpandedPeriod == period,
                    title = period.toPeriodLabel,
                    periodData = if (period == Period.SHOOTOUT) {
                        plays.toShootoutShotsAtGoal
                    } else {
                        data.gamePlays.toShotsAtGoal(awaySog, homeSog)
                    },
                    firstTeamAlias = data.gamePlays.awayTeam?.alias.orShortDash(),
                    secondTeamAlias = data.gamePlays.homeTeam?.alias.orShortDash(),
                    firstTeamScore = awayScore.orShortDash(),
                    secondTeamScore = homeScore.orShortDash()
                )
            )
            if (data.currentExpandedPeriod == period) {
                modules.addAll(
                    renderHockeyPlays(
                        data.gamePlays,
                        plays,
                        isForScoringPlayList = false
                    )
                )
            }
        }
        return modules
    }

    private val List<GameDetailLocalModel.Play>.hasScoringPlays: Boolean
        get() = this.find { it.isScoringPlay || it.isShootoutPlay } != null

    private fun renderScoringPlays(data: BoxScorePlayByPlayState): List<FeedModule> {
        val modules = mutableListOf<FeedModule>()
        val plays = data.gamePlays?.plays ?: return emptyList()
        val groupByPeriod = plays
            .filterIsInstance<GameDetailLocalModel.HockeyPlay>()
            .filter {
                it.isScoringPlay || it.isShootoutPlay
            }.groupBy { it.period }

        groupByPeriod.entries.forEach { (period, scoringPlays) ->
            modules.add(
                PlaysSimplePeriodHeaderModule(
                    id = period.toString(),
                    title = period.toPeriodLabel
                )
            )
            modules.addAll(
                renderHockeyPlays(
                    data.gamePlays,
                    scoringPlays,
                    isForScoringPlayList = true
                )
            )
        }
        return modules
    }

    private fun renderHockeyPlays(
        gamePlays: PlayByPlayLocalModel,
        plays: List<GameDetailLocalModel.HockeyPlay>,
        isForScoringPlayList: Boolean,
    ): List<FeedModule> {
        return plays.mapIndexed { index, play ->
            play.toFeedModule(
                awayTeam = gamePlays.awayTeam,
                homeTeam = gamePlays.homeTeam,
                showDivider = plays.lastIndex != index,
                isForScoringPlayList = isForScoringPlayList
            )
        }
    }

    fun createHockeyRecentPlaysModule(
        game: GameDetailLocalModel,
        recentPlays: List<GameDetailLocalModel.HockeyPlay>
    ): FeedModuleV2 {
        return RecentPlaysModule(
            id = game.id,
            recentPlays = recentPlays.map { play ->
                when {
                    play.isTimeoutOrEndOfPeriod -> play.toRecentPlaysTimeoutModule()
                    play.isStoppage -> play.toRecentPlaysStoppageModule()
                    play.isScoringPlay -> play.toRecentPlaysScoringPlayModule(
                        game.awayTeam?.team?.alias.orShortDash(),
                        game.homeTeam?.team?.alias.orShortDash(),
                        play.awayTeamScore.toString(),
                        play.homeTeamScore.toString()
                    )
                    play.isShootoutPlay && play is GameDetailLocalModel.HockeyShootoutPlay ->
                        play.toRecentPlaysHockeyShootoutPlayModule()
                    else -> play.toRecentPlaysPlayModule()
                }
            }
        )
    }

    private fun GameDetailLocalModel.HockeyPlay.toRecentPlaysTimeoutModule() =
        RecentPlaysModule.RecentPlay.Timeout(
            id = id,
            title = headerLabel.orShortDash()
        )

    private fun GameDetailLocalModel.HockeyPlay.toRecentPlaysStoppageModule() =
        RecentPlaysModule.RecentPlay.Stoppage(
            id = id,
            title = headerLabel.orShortDash(),
            description = description
        )

    private fun GameDetailLocalModel.HockeyPlay.toRecentPlaysScoringPlayModule(
        awayTeamAlias: String,
        homeTeamAlias: String,
        awayTeamScore: String,
        homeTeamScore: String
    ) = RecentPlaysModule.RecentPlay.Play(
        id = id,
        teamLogos = team?.logos ?: emptyList(),
        title = headerLabel.orShortDash(),
        description = description,
        clock = clock.orEmpty(),
        awayTeamAlias = awayTeamAlias,
        homeTeamAlias = homeTeamAlias,
        awayTeamScore = awayTeamScore,
        homeTeamScore = homeTeamScore,
        showScores = true
    )

    private fun GameDetailLocalModel.HockeyPlay.toRecentPlaysPlayModule() =
        RecentPlaysModule.RecentPlay.Play(
            id = id,
            teamLogos = team?.logos ?: emptyList(),
            title = headerLabel.orShortDash(),
            description = description,
            clock = clock.orEmpty(),
            awayTeamAlias = "",
            homeTeamAlias = "",
            awayTeamScore = "",
            homeTeamScore = "",
            showScores = false
        )

    private fun GameDetailLocalModel.HockeyShootoutPlay.toRecentPlaysHockeyShootoutPlayModule() =
        RecentPlaysModule.RecentPlay.HockeyShootout(
            id = id,
            headshots = playerHeadshots ?: emptyList(),
            teamLogos = team?.logos ?: emptyList(),
            teamColor = team?.accentColor.parseHexColor(),
            playerName = headerLabel.orShortDash(),
            teamAlias = team?.alias.orShortDash(),
            description = description,
            isGoal = type == HockeyPlayType.SHOOTOUT_GOAL
        )

    private fun GameDetailLocalModel.HockeyPlay.toFeedModule(
        awayTeam: GameDetailLocalModel.Team?,
        homeTeam: GameDetailLocalModel.Team?,
        showDivider: Boolean,
        isForScoringPlayList: Boolean
    ): FeedModule {
        return when {
            isTimeoutOrEndOfPeriod -> toTimeoutModule(showDivider)
            isStoppage -> toStoppageModule(showDivider)
            isScoringPlay -> toScoringPlayModule(
                awayTeamAlias = awayTeam?.alias.orShortDash(),
                homeTeamAlias = homeTeam?.alias.orShortDash(),
                awayTeamScore = awayTeamScore.toString(),
                homeTeamScore = homeTeamScore.toString(),
                showTeamCurtain = isForScoringPlayList.not(),
                showDivider = showDivider
            )
            isShootoutPlay && this is GameDetailLocalModel.HockeyShootoutPlay -> toShootoutPlayModule(
                showDivider
            )
            else -> this.toPlayModule(showDivider)
        }
    }

    private val GameDetailLocalModel.Play.isShootoutPlay: Boolean
        get() = when (toHockeyStandardPlayType) {
            HockeyPlayType.SHOOTOUT_GOAL,
            HockeyPlayType.SHOOTOUT_SHOT_MISSED,
            HockeyPlayType.SHOOTOUT_SHOT_SAVED -> true
            else -> false
        }

    private val GameDetailLocalModel.Play.isTimeoutOrEndOfPeriod: Boolean
        get() = when (toHockeyStandardPlayType) {
            HockeyPlayType.TV_TIMEOUT,
            HockeyPlayType.END_PERIOD,
            HockeyPlayType.END_SHOOTOUT_PERIOD -> true
            else -> false
        }

    private val GameDetailLocalModel.Play.isStoppage: Boolean
        get() = toHockeyStandardPlayType == HockeyPlayType.STOPPAGE

    private val GameDetailLocalModel.Play.isScoringPlay: Boolean
        get() = when (toHockeyStandardPlayType) {
            HockeyPlayType.GOAL,
            HockeyPlayType.AWARDED_GOAL,
            HockeyPlayType.EMPTY_NET_GOAL,
            HockeyPlayType.OWN_GOAL,
            HockeyPlayType.PENALTY_GOAL -> true
            else -> false
        }

    private val GameDetailLocalModel.Play.toHockeyStandardPlayType: HockeyPlayType?
        get() = when (this) {
            is GameDetailLocalModel.HockeyStandardPlay -> type
            is GameDetailLocalModel.HockeyTeamPlay -> type
            is GameDetailLocalModel.HockeyShootoutPlay -> type
            else -> null
        }

    private fun GameDetailLocalModel.HockeyPlay.toPlayModule(showDivider: Boolean) =
        PlayModule(
            id = id,
            teamLogos = team?.logos ?: emptyList(),
            title = headerLabel.orShortDash(),
            description = description,
            clock = clock.orEmpty(),
            showDivider = showDivider
        )

    private fun GameDetailLocalModel.Play.toTimeoutModule(showDivider: Boolean) =
        TimeoutPlayModule(
            id = id,
            title = headerLabel.orShortDash(),
            showDivider = showDivider
        )

    private fun GameDetailLocalModel.Play.toStoppageModule(showDivider: Boolean) =
        StoppagePlayModule(
            id = id,
            title = headerLabel.orShortDash(),
            description = description,
            showDivider = showDivider
        )

    private fun GameDetailLocalModel.HockeyPlay.toScoringPlayModule(
        awayTeamAlias: String,
        homeTeamAlias: String,
        awayTeamScore: String,
        homeTeamScore: String,
        showTeamCurtain: Boolean,
        showDivider: Boolean
    ) = PlayModule(
        id = id,
        teamLogos = team?.logos ?: emptyList(),
        teamColor = team?.accentColor,
        title = headerLabel.orShortDash(),
        description = description,
        clock = clock.orEmpty(),
        awayTeamAlias = awayTeamAlias,
        homeTeamAlias = homeTeamAlias,
        awayTeamScore = awayTeamScore,
        homeTeamScore = homeTeamScore,
        showScores = true,
        showTeamCurtain = showTeamCurtain,
        showDivider = showDivider
    )

    private fun GameDetailLocalModel.HockeyShootoutPlay.toShootoutPlayModule(
        showDivider: Boolean
    ) = HockeyShootoutPlayModule(
        id = id,
        headshots = playerHeadshots ?: emptyList(),
        teamLogos = team?.logos ?: emptyList(),
        teamColor = team?.accentColor.parseHexColor(),
        playerName = headerLabel.orShortDash(),
        teamAlias = team?.alias.orShortDash(),
        description = description,
        isGoal = type == HockeyPlayType.SHOOTOUT_GOAL,
        showDivider = showDivider
    )

    private val GameDetailLocalModel.Play.clock: String?
        get() = when (this) {
            is GameDetailLocalModel.HockeyStandardPlay -> clock
            is GameDetailLocalModel.HockeyTeamPlay -> clock
            // HockeyShootoutPlay does not contain a clock
            else -> null
        }

    private fun PlayByPlayLocalModel.toShotsAtGoal(awaySog: Int?, homeSog: Int?) =
        StringWithParams(
            R.string.plays_hockey_shots_at_goal,
            awayTeam?.alias.orShortDash(),
            awaySog ?: 0,
            homeTeam?.alias.orShortDash(),
            homeSog ?: 0
        )

    private val List<GameDetailLocalModel.Play>.toShootoutShotsAtGoal: ResourceString
        get() {
            val lastPlay = filterIsInstance<GameDetailLocalModel.HockeyShootoutPlay>()
                .maxByOrNull { it.occurredAt }
            return StringWithParams(
                R.string.plays_hockey_shootout_shots_at_goal,
                (lastPlay?.awayShootoutShots ?: 0) + (lastPlay?.homeShootoutShots ?: 0)
            )
        }

    /**
     * For games in progress, the backend returns the plays newest to oldest but when the game is completed
     * it returns them from oldest to newest. Due to this need to retrieve the last or first play for the period
     * depending upon game status.
     */
    private fun List<GameDetailLocalModel.HockeyPlay>.toHockeyScore(status: GameStatus): Pair<String?, String?> {
        return when (status) {
            GameStatus.FINAL -> Pair(
                lastOrNull()?.awayTeamScore.toString(),
                lastOrNull()?.homeTeamScore.toString()
            )
            GameStatus.IN_PROGRESS -> Pair(
                firstOrNull()?.awayTeamScore.toString(),
                firstOrNull()?.homeTeamScore.toString()
            )
            else -> Pair(null, null)
        }
    }

    /**
     * For games in progress, the backend returns the plays newest to oldest but when the game is completed
     * it returns them from oldest to newest. Due to this need to retrieve the last or first play for the period
     * depending upon game status.
     */
    private fun List<GameDetailLocalModel.HockeyPlay>.toShotsAtGoal(status: GameStatus): Pair<Int?, Int?> {
        // Hockey Plays contain the shots at goal for each team
        val plays = filterIsInstance<GameDetailLocalModel.HockeyStandardPlay>()
        return when (status) {
            GameStatus.FINAL -> Pair(plays.lastOrNull()?.awayShotsAtGoal, plays.lastOrNull()?.homeShotsAtGoal)
            GameStatus.IN_PROGRESS -> Pair(plays.firstOrNull()?.awayShotsAtGoal, plays.firstOrNull()?.homeShotsAtGoal)
            else -> Pair(null, null)
        }
    }
}