package com.theathletic.gamedetail.boxscore.ui.football

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.modules.AmericanFootballDriveModule
import com.theathletic.boxscore.ui.modules.AmericanFootballPlayModule
import com.theathletic.boxscore.ui.modules.PlaysSimplePeriodHeaderModule
import com.theathletic.boxscore.ui.modules.RecentPlaysModule
import com.theathletic.boxscore.ui.modules.TwoItemToggleButtonModule
import com.theathletic.feed.ui.FeedModule
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.boxscore.ui.common.toPeriodLabel
import com.theathletic.gamedetail.boxscore.ui.playbyplay.BoxScorePlayByPlayState
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.Period
import com.theathletic.gamedetail.data.local.PlayByPlayLocalModel
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.modules.SpacingModule
import com.theathletic.ui.orEmpty
import com.theathletic.utility.orShortDash
import com.theathletic.utility.safeLet

class FootballPlayByPlayRenderers @AutoKoin constructor() {

    fun renderModules(data: BoxScorePlayByPlayState): List<FeedModule> {
        return data.gamePlays?.let { gamePlays ->
            val modules = mutableListOf<FeedModule>()
            val drives = data.gamePlays.plays.filterIsInstance<GameDetailLocalModel.AmericanFootballDrivePlay>()
            val hasScoringPlays = drives.hasScoringPlays
            if (hasScoringPlays) {
                modules.add(
                    TwoItemToggleButtonModule(
                        id = data.gamePlays.id,
                        itemOneLabel = StringWithParams(R.string.plays_american_football_plays_option_title_all_plays),
                        itemTwoLabel = StringWithParams(R.string.plays_american_football_plays_option_title_scoring_plays),
                        isFirstItemSelected = data.isFirstViewItemSelected
                    )
                )
            }
            val drivesGroupedByPeriod = if (!data.isFirstViewItemSelected && hasScoringPlays) {
                drives.filter { it.plays.hasAScoringPlay }.groupBy { it.period }
            } else {
                drives.groupBy { it.period }
            }
            modules.addAll(
                drivesGroupedByPeriod.toRenderedPeriods(gamePlays, data.expandedPlays)
            )
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

    fun createFootballRecentPlaysModule(
        game: GameDetailLocalModel,
        recentPlays: List<GameDetailLocalModel.AmericanFootballPlay>
    ): FeedModuleV2 {
        val awayTeamAlias = game.awayTeam?.team?.alias.orShortDash()
        val homeTeamAlias = game.homeTeam?.team?.alias.orShortDash()
        return RecentPlaysModule(
            id = game.id,
            recentPlays = recentPlays.map { play ->
                RecentPlaysModule.RecentPlay.AmericanFootballPlay(
                    title = play.headerLabel.orEmpty(),
                    description = play.description,
                    possession = play.possession?.toLabel().orEmpty(),
                    awayTeamAlias = awayTeamAlias,
                    homeTeamAlias = homeTeamAlias,
                    awayTeamScore = play.awayTeamScore.toString(),
                    homeTeamScore = play.homeTeamScore.toString(),
                    teamLogos = play.team?.logos ?: emptyList(),
                    teamColor = play.team?.accentColor,
                    isScoringPlay = play.isScoringPlay,
                    showDivider = true,
                    clock = play.clock
                )
            }
        )
    }

    private val List<GameDetailLocalModel.AmericanFootballDrivePlay>.hasScoringPlays: Boolean
        get() = firstOrNull { it.plays.hasAScoringPlay } != null

    private val List<GameDetailLocalModel.AmericanFootballPlay>.hasAScoringPlay: Boolean
        get() = firstOrNull { it.isScoringPlay } != null

    private fun Map<Period, List<GameDetailLocalModel.AmericanFootballDrivePlay>>.toRenderedPeriods(
        gamePlays: PlayByPlayLocalModel,
        expandedDrives: List<String>
    ): List<FeedModule> {
        val modules = mutableListOf<FeedModule>()
        entries.forEach { (period, scoringPlays) ->
            modules.add(
                PlaysSimplePeriodHeaderModule(
                    id = period.toString(),
                    title = period.toPeriodLabel
                )
            )
            modules.addAll(
                renderDrives(
                    gamePlays = gamePlays,
                    plays = scoringPlays,
                    expandedDrives = expandedDrives
                )
            )
        }
        return modules
    }

    private fun renderDrives(
        gamePlays: PlayByPlayLocalModel,
        plays: List<GameDetailLocalModel.AmericanFootballDrivePlay>,
        expandedDrives: List<String>
    ): List<FeedModule> {
        val awayTeamAlias = gamePlays.awayTeam?.alias.orShortDash()
        val homeTeamAlias = gamePlays.homeTeam?.alias.orShortDash()
        val modules = mutableListOf<FeedModule>()
        plays.forEach { drive ->
            val isExpanded = expandedDrives.contains(drive.id)
            modules.add(
                drive.toFeedModule(awayTeamAlias, homeTeamAlias, isExpanded)
            )
            if (isExpanded) {
                modules.addAll(
                    drive.plays.mapIndexed { index, play ->
                        play.toFeedModule(drive.plays.lastIndex == index)
                    }
                )
            }
        }
        return modules
    }

    private fun GameDetailLocalModel.AmericanFootballDrivePlay.toFeedModule(
        awayTeamAlias: String,
        homeTeamAlias: String,
        isExpanded: Boolean
    ) = AmericanFootballDriveModule(
        id = id,
        teamLogos = team.logos,
        title = description.orShortDash(),
        stats = StringWithParams(
            R.string.plays_american_football_drive_stats_subtitle,
            playCount,
            yards,
            duration.orShortDash()
        ),
        awayTeamAlias = awayTeamAlias,
        homeTeamAlias = homeTeamAlias,
        awayTeamScore = awayTeamScore.toString(),
        homeTeamScore = homeTeamScore.toString(),
        isExpanded = isExpanded
    )

    private fun GameDetailLocalModel.AmericanFootballPlay.toFeedModule(isLastPlay: Boolean) =
        AmericanFootballPlayModule(
            id = id,
            title = headerLabel.orEmpty(),
            description = description,
            possession = possession?.toLabel(),
            clock = clock,
            showDivider = isLastPlay
        )
}

fun GameDetailLocalModel.Possession.toLabel(): ResourceString? {
    safeLet(down, locationYardLine, yards) { safeDown, safeYardLine, safeYards ->
        val team = toTeam(safeDown)
        return if (goalToGo) {
            goalPossessionLabel(safeDown, safeYardLine, team)
        } else {
            yardPossessionLabel(safeDown, safeYards, safeYardLine, team)
        }
    }
    return null
}

private fun goalPossessionLabel(
    down: Int,
    locationYardLine: Int,
    team: GameDetailLocalModel.Team?
): ResourceString {
    return if (down > 4) {
        StringWithParams(R.string.box_score_last_play_no_down, team?.name.orShortDash())
    } else {
        StringWithParams(
            down.toGoalResource(),
            team?.alias.orShortDash(),
            locationYardLine
        )
    }
}

private fun yardPossessionLabel(
    down: Int,
    yards: Int,
    locationYardLine: Int,
    team: GameDetailLocalModel.Team?
): ResourceString {
    return StringWithParams(
        down.toResource(),
        team?.name.orShortDash(),
        yards,
        team?.alias.orShortDash(),
        locationYardLine
    )
}

private fun Int.toResource(): Int {
    return when (this) {
        1 -> R.string.box_score_last_play_1st_down
        2 -> R.string.box_score_last_play_2nd_down
        3 -> R.string.box_score_last_play_3rd_down
        4 -> R.string.box_score_last_play_4th_down
        else -> R.string.box_score_last_play_no_down
    }
}

private fun Int.toGoalResource(): Int {
    return when (this) {
        1 -> R.string.box_score_last_play_1st_down_with_goal
        2 -> R.string.box_score_last_play_2nd_down_with_goal
        3 -> R.string.box_score_last_play_3rd_down_with_goal
        4 -> R.string.box_score_last_play_4th_down_with_goal
        else -> R.string.box_score_last_play_no_down
    }
}

private fun GameDetailLocalModel.Possession.toTeam(down: Int): GameDetailLocalModel.Team? {
    return if (down in 1..4) {
        locationTeam
    } else {
        team
    }
}