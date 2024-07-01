package com.theathletic.gamedetail.boxscore.ui.baseball

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.formatters.BoxScoreBaseballInningFormatter
import com.theathletic.boxscore.ui.formatters.OrdinalFormatter
import com.theathletic.boxscore.ui.modules.BaseballInningsHeaderModule
import com.theathletic.boxscore.ui.modules.BaseballPitchOutcomeType
import com.theathletic.boxscore.ui.modules.BaseballPlayModule
import com.theathletic.boxscore.ui.modules.PlayModule
import com.theathletic.boxscore.ui.modules.PlaysSimplePeriodHeaderModule
import com.theathletic.boxscore.ui.modules.TwoItemToggleButtonModule
import com.theathletic.extension.toStringOrShortDash
import com.theathletic.feed.ui.FeedModule
import com.theathletic.gamedetail.boxscore.ui.playbyplay.BoxScorePlayByPlayState
import com.theathletic.gamedetail.data.local.BaseballPitchOutcome
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.InningHalf
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.asResourceString
import com.theathletic.ui.modules.SpacingModule
import com.theathletic.ui.orEmpty
import com.theathletic.utility.orShortDash

class BaseballPlayByPlayRenderers @AutoKoin constructor(
    private val inningFormatter: BoxScoreBaseballInningFormatter,
    private val ordinalFormatter: OrdinalFormatter
) {

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
        val groupedByInningHalves = plays
            .filterIsInstance<GameDetailLocalModel.BaseballPlayWithInnings>()
            .groupBy { it.toKey() }
        groupedByInningHalves.entries.forEach { (inningKey, plays) ->
            modules.add(data.toInningHeaderModule(inningKey))
            plays.forEach { play ->
                play.toModule(
                    awayTeam = data.gamePlays.awayTeam,
                    homeTeam = data.gamePlays.homeTeam,
                    expandedPlays = data.expandedPlays
                )?.let { modules.add(it) }
            }
        }
        return modules
    }

    data class InningKey(val inning: Int, val inningHalf: InningHalf?)

    private fun GameDetailLocalModel.BaseballPlayWithInnings.toKey() =
        InningKey(this.inning, this.inningHalf)

    private fun BoxScorePlayByPlayState.toInningHeaderModule(
        inningKey: InningKey,
    ): FeedModule {
        val title = inningFormatter.format(inningKey.inning, inningKey.inningHalf)
        return BaseballInningsHeaderModule(
            id = title.toString(),
            title = title,
            inningStats = if (inningKey.inningHalf == InningHalf.TOP) {
                gamePlays?.awayTeamScores?.toInningStats(inningKey.inning).orEmpty()
            } else {
                gamePlays?.homeTeamScores?.toInningStats(inningKey.inning).orEmpty()
            },
            teamLogos = if (inningKey.inningHalf == InningHalf.TOP) {
                gamePlays?.awayTeam?.logos ?: emptyList()
            } else {
                gamePlays?.homeTeam?.logos ?: emptyList()
            },
            awayTeamAlias = gamePlays?.awayTeam?.alias.orShortDash(),
            homeTeamAlias = gamePlays?.homeTeam?.alias.orShortDash(),
            awayTeamScore = gamePlays?.awayTeamScores?.toInningScore(inningKey.inning).orShortDash(),
            homeTeamScore = if (inningKey.inningHalf == InningHalf.TOP) {
                gamePlays?.homeTeamScores?.toInningScore(inningKey.inning - 1).orShortDash()
            } else {
                gamePlays?.homeTeamScores?.toInningScore(inningKey.inning).orShortDash()
            }
        )
    }

    private fun List<GameDetailLocalModel.ScoreType>.toInningStats(inning: Int): ResourceString {
        val currentInning = getOrNull(inning - 1) ?: return "".asResourceString()
        if (currentInning !is GameDetailLocalModel.InningScore) return "".asResourceString()
        return StringWithParams(
            R.string.plays_baseball_inning_stats_subtitle,
            currentInning.runs,
            currentInning.hits
        )
    }

    private fun List<GameDetailLocalModel.ScoreType>.toInningScore(inning: Int): String {
        val scores = filterIsInstance<GameDetailLocalModel.InningScore>()
        return scores.filter { it.inning <= inning }.sumOf { it.runs }.toString()
    }

    private fun GameDetailLocalModel.Play.toModule(
        awayTeam: GameDetailLocalModel.Team?,
        homeTeam: GameDetailLocalModel.Team?,
        expandedPlays: List<String>
    ) = when (this) {
        is GameDetailLocalModel.BaseballStandardPlay ->
            BaseballPlayModule(
                id = id,
                description = description,
                awayTeamAlias = null,
                homeTeamAlias = null,
                awayTeamScore = null,
                homeTeamScore = null,
                isExpanded = expandedPlays.contains(id),
                subPlays = plays.toSubPlay()
            )
        is GameDetailLocalModel.BaseballTeamPlay ->
            BaseballPlayModule(
                id = id,
                description = description,
                awayTeamAlias = awayTeam?.alias.orShortDash(),
                homeTeamAlias = homeTeam?.alias.orShortDash(),
                awayTeamScore = awayTeamScore.toStringOrShortDash(),
                homeTeamScore = homeTeamScore.toStringOrShortDash(),
                showScores = true,
                isExpanded = expandedPlays.contains(id),
                subPlays = plays.toSubPlay()
            )
        is GameDetailLocalModel.BaseballLineUpChangePlay ->
            BaseballPlayModule(
                id = id,
                description = description,
                awayTeamAlias = null,
                homeTeamAlias = null,
                awayTeamScore = null,
                homeTeamScore = null,
                showScores = false,
                isExpanded = false,
                subPlays = emptyList()
            )
        else -> {
            null
        }
    }

    private fun List<GameDetailLocalModel.BaseballPlay>.toSubPlay(): List<BaseballPlayModule.SubPlay> {
        return mapNotNull { play ->
            when (play) {
                is GameDetailLocalModel.BaseballPitchPlay -> BaseballPlayModule.PitchPlay(
                    title = play.description,
                    description = play.pitchDescription,
                    pitchNumber = play.number,
                    pitchOutcomeType = play.pitchOutcome.toPitchOutcomeType,
                    occupiedBases = play.bases,
                    hitZone = play.hitZone,
                    pitchZone = play.pitchZone
                )
                is GameDetailLocalModel.BaseballStandardPlay -> BaseballPlayModule.StandardSubPlay(
                    description = play.description
                )
                else -> null
            }
        }
    }

    private val BaseballPitchOutcome.toPitchOutcomeType: BaseballPitchOutcomeType
        get() = when (this) {
            BaseballPitchOutcome.BALL -> BaseballPitchOutcomeType.BALL
            BaseballPitchOutcome.DEAD_BALL -> BaseballPitchOutcomeType.DEAD_BALL
            BaseballPitchOutcome.HIT -> BaseballPitchOutcomeType.HIT
            BaseballPitchOutcome.STRIKE -> BaseballPitchOutcomeType.STRIKE
            BaseballPitchOutcome.UNKNOWN -> BaseballPitchOutcomeType.UNKNOWN
        }

    private val List<GameDetailLocalModel.Play>.hasScoringPlays: Boolean
        get() = find { it is GameDetailLocalModel.BaseballTeamPlay } != null

    private fun renderScoringPlays(data: BoxScorePlayByPlayState): List<FeedModule> {
        val modules = mutableListOf<FeedModule>()
        val plays = data.gamePlays?.plays ?: return emptyList()
        val groupByInningHalves = plays
            .filterIsInstance<GameDetailLocalModel.BaseballTeamPlay>()
            .groupBy { inningFormatter.longFormat(ordinalFormatter.format(it.inning), it.inningHalf) }
        groupByInningHalves.entries.forEach { (inningHalf, plays) ->
            modules.add(
                PlaysSimplePeriodHeaderModule(
                    id = inningHalf.toString(),
                    title = inningHalf
                )
            )
            plays.mapIndexed { index, play ->
                val isLastPlay = plays.lastIndex != index
                modules.add(
                    PlayModule(
                        id = play.id,
                        teamLogos = play.team?.logos ?: emptyList(),
                        title = play.headerLabel,
                        description = play.description,
                        clock = "", // not displayed for baseball
                        awayTeamAlias = data.gamePlays.awayTeam?.alias.orShortDash(),
                        homeTeamAlias = data.gamePlays.homeTeam?.alias.orShortDash(),
                        awayTeamScore = play.awayTeamScore.toString(),
                        homeTeamScore = play.homeTeamScore.toString(),
                        showScores = true,
                        showDivider = isLastPlay
                    )
                )
            }
        }
        return modules
    }
}

val BaseballPitchOutcome.toPitchOutcomeType: BaseballPitchOutcomeType
    get() = when (this) {
        BaseballPitchOutcome.BALL -> BaseballPitchOutcomeType.BALL
        BaseballPitchOutcome.DEAD_BALL -> BaseballPitchOutcomeType.DEAD_BALL
        BaseballPitchOutcome.HIT -> BaseballPitchOutcomeType.HIT
        BaseballPitchOutcome.STRIKE -> BaseballPitchOutcomeType.STRIKE
        BaseballPitchOutcome.UNKNOWN -> BaseballPitchOutcomeType.UNKNOWN
    }