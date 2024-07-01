package com.theathletic.gamedetail.boxscore.ui.baseball

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.StartingPitchersUi
import com.theathletic.boxscore.ui.modules.PitcherModule
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.boxscore.ui.baseball.stats.STAT_ERA
import com.theathletic.gamedetail.boxscore.ui.baseball.stats.STAT_INNINGS_PITCHED
import com.theathletic.gamedetail.boxscore.ui.baseball.stats.STAT_STRIKEOUTS
import com.theathletic.gamedetail.boxscore.ui.baseball.stats.STAT_WHIP
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreCommonRenderers
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.Handedness
import com.theathletic.gamedetail.data.local.PitcherState
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asResourceString
import com.theathletic.ui.utility.parseHexColor

class BoxScoreBaseballPitcherRenderer @AutoKoin constructor(
    private val commonRenderers: BoxScoreCommonRenderers,
) {

    companion object {
        private const val GAMES_PLAYED = "games_played"
        private const val GAMES_WON = "games_won"
        private const val GAMES_LOST = "games_lost"
    }

    fun createPitcherModule(game: GameDetailLocalModel): FeedModuleV2? {
        val awayTeam = (game.awayTeam as? GameDetailLocalModel.BaseballGameTeam)
        val homeTeam = (game.homeTeam as? GameDetailLocalModel.BaseballGameTeam)

        if (awayTeam?.startingPitcher == null && homeTeam?.startingPitcher == null) {
            return null
        }

        val pitchers = listOf(awayTeam, homeTeam)
        val showProbableTag = pitchers.any { it?.pitcherState == PitcherState.CONFIRMED }
        val title = if (pitchers.all { it?.pitcherState == PitcherState.PROBABLE }) {
            R.string.box_score_baseball_probable_pitchers_title
        } else {
            R.string.box_score_baseball_starting_pitchers_title
        }

        return PitcherModule(
            id = game.id,
            titleId = title,
            awayTeamPitcher = awayTeam?.startingPitcher?.toPitcher(
                showProbableTag,
                awayTeam.pitcherState,
                awayTeam.team?.alias,
                awayTeam.team?.primaryColor
            ) ?: awayTeam.toTBDPitcher(),
            homeTeamPitcher = homeTeam?.startingPitcher?.toPitcher(
                showProbableTag,
                homeTeam.pitcherState,
                homeTeam.team?.alias,
                homeTeam.team?.primaryColor
            ) ?: homeTeam.toTBDPitcher()
        )
    }

    private fun GameDetailLocalModel.BaseballGameTeam?.toTBDPitcher(): StartingPitchersUi.TBDPitcher {
        return StartingPitchersUi.TBDPitcher(
            teamLogo = this?.team?.logos.orEmpty(),
            teamColor = this?.team?.primaryColor.parseHexColor(),
            details = ResourceString.StringWithParams(R.string.core_raw_parameterized_string, this?.team?.alias.orEmpty())
        )
    }

    private fun GameDetailLocalModel.BaseballPlayer.toPitcher(
        showProbableTag: Boolean,
        pitcherState: PitcherState?,
        alias: String?,
        primaryColor: String?
    ): StartingPitchersUi.PitcherStats {
        val isProbable = showProbableTag && pitcherState == PitcherState.PROBABLE

        return this.player.let { pitcher ->
            val pitchingStats = pitcher.seasonStats.toStartingPitcherStats()
            StartingPitchersUi.PitcherStats(
                name = pitcher.displayName.orEmpty().asResourceString(),
                details = pitcher.throwHandedness.toShortName(alias),
                headshotList = pitcher.headshots,
                seasonStatsHeader = pitchingStats.keys.toList(),
                seasonStatsValues = pitchingStats.values.toList(),
                teamColor = primaryColor.parseHexColor(),
                isProbable = isProbable
            )
        }
    }

    private fun List<GameDetailLocalModel.Statistic>.toStartingPitcherStats(): HashMap<ResourceString, String> {
        val requiredStatsTypes = listOf(STAT_ERA, STAT_STRIKEOUTS, STAT_WHIP, STAT_INNINGS_PITCHED)
        val requiredStats = filter { it.type in requiredStatsTypes }
            .sortedBy { requiredStatsTypes.indexOf(it.type) }

        val pitcherStats = linkedMapOf<ResourceString, String>()

        find { it.type == GAMES_PLAYED }?.let { gamesPlayed ->
            pitcherStats[ResourceString.StringWithParams(R.string.box_score_baseball_stats_games)] = commonRenderers.formatStatisticValue(
                gamesPlayed
            ).toString()
        }

        val gamesWon = find { it.type == GAMES_WON }?.let { commonRenderers.formatStatisticValue(it) } ?: "0"
        val gamesLost = find { it.type == GAMES_LOST }?.let { commonRenderers.formatStatisticValue(it) } ?: "0"

        pitcherStats[ResourceString.StringWithParams(R.string.box_score_baseball_stats_win_loss)] = "$gamesWon-$gamesLost"

        requiredStats.forEach { stat ->
            stat.longHeaderLabel?.let { header ->
                pitcherStats[header.asResourceString()] = commonRenderers.formatStatisticValue(stat).toString()
            }
        }

        return pitcherStats
    }

    private fun Handedness.toShortName(teamShortName: String?): ResourceString {
        return when (this) {
            Handedness.LEFT -> teamShortName?.let {
                ResourceString.StringWithParams(
                    R.string.box_score_baseball_pitcher_lh_with_team,
                    it
                )
            } ?: ResourceString.StringWithParams(R.string.box_score_baseball_pitcher_lh)
            Handedness.RIGHT -> teamShortName?.let {
                ResourceString.StringWithParams(
                    R.string.box_score_baseball_pitcher_rh_with_team,
                    it
                )
            } ?: ResourceString.StringWithParams(R.string.box_score_baseball_pitcher_rh)
            else -> "".asResourceString()
        }
    }
}