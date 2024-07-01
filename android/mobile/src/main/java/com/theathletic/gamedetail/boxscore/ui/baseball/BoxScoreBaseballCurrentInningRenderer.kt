package com.theathletic.gamedetail.boxscore.ui.baseball

import androidx.compose.ui.graphics.Color
import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.CurrentInningUi
import com.theathletic.boxscore.ui.modules.BaseballPlayModule
import com.theathletic.boxscore.ui.modules.CurrentInningModule
import com.theathletic.boxscore.ui.modules.IndicatorType
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreCommonRenderers
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.Handedness
import com.theathletic.gamedetail.data.local.InningHalf
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.ResourceString.StringWrapper
import com.theathletic.ui.asResourceString
import com.theathletic.ui.orEmpty
import com.theathletic.ui.utility.parseHexColor
import com.theathletic.utility.orShortDash
import com.theathletic.utility.safeLet

class BoxScoreBaseballCurrentInningRenderer @AutoKoin constructor(
    private val commonRenderers: BoxScoreCommonRenderers,
) {

    fun createCurrentInningModule(game: GameDetailLocalModel): FeedModuleV2? {
        return safeLet(
            (game.sportExtras as? GameDetailLocalModel.BaseballExtras)?.outcome,
            (game.sportExtras as? GameDetailLocalModel.BaseballExtras)?.currentInningPlays
        ) { outcome, inningPlays ->
            CurrentInningModule(
                id = game.id,
                batter = getPlayerDetails(outcome, game, false),
                pitcher = getPlayerDetails(outcome, game, true),
                playStatus = getInningStatusMap(outcome),
                currentInning = getCurrentInning(inningPlays)
            )
        }
    }

    private fun getPlayerDetails(
        outcome: GameDetailLocalModel.BaseballOutcome?,
        game: GameDetailLocalModel,
        isPitcher: Boolean
    ): CurrentInningUi.PlayerSummary {
        if (isPitcher) {
            return CurrentInningUi.PlayerSummary(
                teamColor = getPitcherTeamColor(game, outcome),
                stats = getPitcherStats(outcome?.pitcher).orEmpty(),
                playInfo = getPlayerInfo(outcome?.pitcher?.player, true),
                title = StringWithParams(R.string.box_score_baseball_pitching),
                lastPlay = getPitcherStrikesCount(outcome?.pitcher).orEmpty(),
                name = outcome?.pitcher?.player?.displayName.orShortDash(),
                headshotList = outcome?.pitcher?.player?.headshots ?: emptyList()
            )
        } else {
            return CurrentInningUi.PlayerSummary(
                headshotList = outcome?.batter?.player?.headshots ?: emptyList(),
                name = outcome?.batter?.player?.displayName.orShortDash(),
                lastPlay = StringWithParams(
                    R.string.box_score_baseball_current_inning_next_batter,
                    outcome?.nextBatter?.player?.displayName.orEmpty(),
                    outcome?.nextBatter?.player?.position?.alias.orEmpty()
                ),
                title = StringWithParams(R.string.box_score_baseball_batting),
                playInfo = getPlayerInfo(outcome?.batter?.player, false),
                stats = getBatterStats(outcome?.batter).orEmpty(),
                teamColor = getBatterTeamColor(game, outcome)
            )
        }
    }

    private fun getCurrentInning(inningPlays: List<GameDetailLocalModel.BaseballPlay>?): List<CurrentInningUi.Play> {
        if (inningPlays == null) return emptyList()
        return inningPlays.map {
            when (it) {
                is GameDetailLocalModel.BaseballStandardPlay ->
                    CurrentInningUi.Play(
                        it.description,
                        getPitchPlays(it.plays)
                    )
                is GameDetailLocalModel.BaseballTeamPlay ->
                    CurrentInningUi.Play(
                        it.description,
                        getPitchPlays(it.plays)
                    )
                else -> {
                    CurrentInningUi.Play(
                        it.description,
                        emptyList()
                    )
                }
            }
        }
    }

    private fun getPitchPlays(plays: List<GameDetailLocalModel.BaseballPlay>): List<BaseballPlayModule.PitchPlay> {
        return plays.filterIsInstance<GameDetailLocalModel.BaseballPitchPlay>().map {
            BaseballPlayModule.PitchPlay(
                description = it.pitchDescription.orEmpty(),
                title = it.description,
                pitchZone = it.pitchZone,
                pitchOutcomeType = it.pitchOutcome.toPitchOutcomeType,
                pitchNumber = it.number,
                occupiedBases = it.bases,
                hitZone = it.hitZone
            )
        }
    }

    private fun getInningStatusMap(outcome: GameDetailLocalModel.BaseballOutcome?): List<CurrentInningUi.CurrentPlayStatus> {
        if (outcome == null) return emptyList()
        return listOf(
            CurrentInningUi.CurrentPlayStatus(IndicatorType.Balls, outcome.balls),
            CurrentInningUi.CurrentPlayStatus(IndicatorType.Strikes, outcome.strikes),
            CurrentInningUi.CurrentPlayStatus(IndicatorType.Outs, outcome.outs)
        )
    }

    private fun getPitcherStrikesCount(pitcher: GameDetailLocalModel.BaseballPlayer?): ResourceString? {
        pitcher?.let {
            val pitchCount =
                pitcher.gameStats.find { it.type == "pitch_count" }?.let { commonRenderers.formatStatisticValue(it) }
                    .orEmpty()

            val strikes =
                pitcher.gameStats.find { it.type == "strikes" }?.let { commonRenderers.formatStatisticValue(it) }
                    .orEmpty()

            return StringWithParams(
                R.string.box_score_baseball_current_inning_pitcher_strikes,
                pitchCount,
                strikes
            )
        }
        return null
    }

    private fun getPitcherStats(pitcher: GameDetailLocalModel.BaseballPlayer?): ResourceString? {
        pitcher?.let {
            val displayStats = it.gameStats.filterNot { it.type == "pitch_count" || it.type == "strikes" }
            return StringWrapper(
                displayStats.joinToString(", ") {
                    commonRenderers.formatStatisticValue(it).orShortDash() + " " + it.headerLabel
                }
            )
        }
        return null
    }

    private fun getBatterStats(batter: GameDetailLocalModel.BaseballPlayer?): ResourceString? {
        batter?.let {
            val hits = batter.gameStats.find { it.type == "hits" }?.let { commonRenderers.formatStatisticValue(it) }
                .orEmpty()
            val bats = batter.gameStats.find { it.type == "at_bat" }?.let { commonRenderers.formatStatisticValue(it) }
                .orEmpty()
            val avg = batter.seasonAvg?.let { commonRenderers.formatStatisticValue(it) }.orEmpty()

            return StringWithParams(
                R.string.box_score_baseball_current_inning_batter_stats,
                hits,
                bats,
                avg
            )
        }
        return null
    }

    private fun getBatterTeamColor(
        game: GameDetailLocalModel,
        outcome: GameDetailLocalModel.BaseballOutcome?
    ): Color {
        return if (outcome?.inningHalf == InningHalf.TOP) {
            game.awayTeam?.team?.primaryColor.parseHexColor()
        } else {
            game.homeTeam?.team?.primaryColor.parseHexColor()
        }
    }

    private fun getPitcherTeamColor(
        game: GameDetailLocalModel,
        outcome: GameDetailLocalModel.BaseballOutcome?
    ): Color {
        return if (outcome?.inningHalf == InningHalf.TOP) {
            game.homeTeam?.team?.primaryColor.parseHexColor()
        } else {
            game.awayTeam?.team?.primaryColor.parseHexColor()
        }
    }

    private fun getPlayerInfo(
        baseballTeamMember: GameDetailLocalModel.BaseballTeamMember?,
        isPitcher: Boolean
    ) =
        if (isPitcher) {
            when (baseballTeamMember?.throwHandedness) {
                Handedness.LEFT -> StringWithParams(R.string.box_score_baseball_pitcher_lh)
                Handedness.RIGHT -> StringWithParams(R.string.box_score_baseball_pitcher_rh)
                else -> "".asResourceString()
            }
        } else {
            baseballTeamMember?.position?.alias.orEmpty().asResourceString()
        }
}