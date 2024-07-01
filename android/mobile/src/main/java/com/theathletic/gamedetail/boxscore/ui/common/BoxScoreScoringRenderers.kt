package com.theathletic.gamedetail.boxscore.ui.common

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.BoxScoresScoreTableUiModel
import com.theathletic.boxscore.ui.modules.ScoreTableModule
import com.theathletic.entity.main.League
import com.theathletic.entity.main.Sport
import com.theathletic.extension.merge
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.boxscore.ui.baseball.BoxScoreBaseballScoringPlaysRenderer
import com.theathletic.gamedetail.boxscore.ui.football.BoxScoreFootballScoringPlaysRenderer
import com.theathletic.gamedetail.boxscore.ui.hockey.BoxScoreHockeyScoringPlaysRenderer
import com.theathletic.gamedetail.boxscore.ui.soccer.BoxScoreSoccerScoringPlaysRenderer
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.InningHalf
import com.theathletic.gamedetail.data.local.Period
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.asResourceString
import com.theathletic.utility.orShortDash
import java.util.concurrent.atomic.AtomicInteger

class BoxScoreScoringRenderers @AutoKoin constructor(
    private val footballScoringPlaysRenderer: BoxScoreFootballScoringPlaysRenderer,
    private val hockeyScoringPlaysRenderer: BoxScoreHockeyScoringPlaysRenderer,
    private val baseballScoringPlaysRenderer: BoxScoreBaseballScoringPlaysRenderer,
    private val soccerScoringPlaysRenderer: BoxScoreSoccerScoringPlaysRenderer,
) {

    companion object {
        const val STANDARD_INNINGS_COUNT = 9
    }

    fun createScoreTableModule(game: GameDetailLocalModel) = renderScoreTableModule(game)

    @Deprecated("Use createScoreTableModule(game: GameDetailLocalModel)")
    fun createScoreTableModule(
        game: GameDetailLocalModel,
        pageOrder: AtomicInteger
    ): FeedModuleV2? {
        return when {
            game.isGameInProgressOrCompleted -> {
                pageOrder.incrementAndGet()
                renderScoreTableModule(game)
            }
            else -> null
        }
    }

    private fun renderScoreTableModule(game: GameDetailLocalModel): FeedModuleV2 {
        return when (game.sport) {
            Sport.BASEBALL -> game.toBaseballScoreTableModule()
            else -> game.toGenericScoreTableModule()
        }
    }

    private fun GameDetailLocalModel.toBaseballScoreTableModule(): FeedModuleV2 {
        return ScoreTableModule(
            id = id,
            firstTeamName = firstTeam?.team?.alias.orEmpty(),
            firstTeamLogoUrlList = firstTeam?.team?.logos ?: emptyList(),
            secondTeamName = secondTeam?.team?.alias.orEmpty(),
            secondTeamLogoUrlList = secondTeam?.team?.logos ?: emptyList(),
            currentPeriodColumnIndex = if (isGameCompleted) -1 else (currentInning(this) - 1),
            columns = createBaseballInnings(),
            totalsColumns = createBaseballTotal(),
            scrollToInningIndex = if (isGameCompleted) 0 else currentInning(this) - 1
        )
    }

    private fun GameDetailLocalModel.toGenericScoreTableModule(): FeedModuleV2 {
        return ScoreTableModule(
            id = id,
            firstTeamName = firstTeam?.team?.alias.orEmpty(),
            firstTeamLogoUrlList = firstTeam?.team?.logos ?: emptyList(),
            secondTeamName = secondTeam?.team?.alias.orEmpty(),
            secondTeamLogoUrlList = secondTeam?.team?.logos ?: emptyList(),
            currentPeriodColumnIndex = highlightCurrentPeriodColumn(period) - 1,
            columns = getScoreValues(this),
            totalsColumns = listOf(
                BoxScoresScoreTableUiModel.ScoreTableColumn(
                    StringWithParams(R.string.box_score_scoring_total_title),
                    firstTeam?.score?.toString().orShortDash(),
                    secondTeam?.score?.toString().orShortDash()
                )
            ),
        )
    }

    fun createAmericanFootballScoringSummaryModule(game: GameDetailLocalModel): FeedModuleV2? =
        footballScoringPlaysRenderer.createScoringSummaryModule(game)

    fun createHockeyScoringSummaryModule(game: GameDetailLocalModel): FeedModuleV2? =
        hockeyScoringPlaysRenderer.createScoringSummaryModule(game)

    fun createBaseballScoringSummaryModule(game: GameDetailLocalModel): FeedModuleV2? =
        baseballScoringPlaysRenderer.createScoringSummaryModule(game)

    @Deprecated("Use the sport specific scoring summary function above")
    fun createScoringSummaryModule(
        game: GameDetailLocalModel,
        pageOrder: AtomicInteger
    ): FeedModuleV2? {
        if (game.isGameInProgressOrCompleted.not()) return null
        val feedModule = when (game.sport) {
            Sport.FOOTBALL -> footballScoringPlaysRenderer.createScoringSummaryModule(game)
            Sport.HOCKEY -> hockeyScoringPlaysRenderer.createScoringSummaryModule(game)
            Sport.BASEBALL -> baseballScoringPlaysRenderer.createScoringSummaryModule(game)
            Sport.SOCCER -> soccerScoringPlaysRenderer.createKeyMomentsModule(game)
            else -> null
        } ?: return null
        pageOrder.getAndIncrement()
        return feedModule
    }

    private fun Int?.toRunsDisplay(
        isCurrentInning: Boolean,
        currentInningHalf: InningHalf?,
        isGameCompleted: Boolean
    ): String {
        return when {
            this == null -> "-"
            isGameCompleted && isCurrentInning &&
                (currentInningHalf != InningHalf.BOTTOM && currentInningHalf != InningHalf.OVER) -> "-"
            isCurrentInning &&
                (currentInningHalf != InningHalf.BOTTOM && currentInningHalf != InningHalf.OVER) -> "-"
            else -> this.toString()
        }
    }

    private fun GameDetailLocalModel.BaseballGameTeam.toInningScores() =
        this.inningScores.sortedBy { it.inning }

    private val Period.isOvertimeOrShootout
        get() = this in setOf(
            Period.OVER_TIME,
            Period.OVER_TIME_2,
            Period.OVER_TIME_3,
            Period.OVER_TIME_4,
            Period.OVER_TIME_5,
            Period.OVER_TIME_6,
            Period.OVER_TIME_7,
            Period.OVER_TIME_8,
            Period.OVER_TIME_9,
            Period.OVER_TIME_10,
            Period.FULL_TIME_OT,
            Period.FULL_TIME_OT_2,
            Period.FULL_TIME_OT_3,
            Period.FULL_TIME_OT_4,
            Period.FULL_TIME_OT_5,
            Period.FULL_TIME_OT_6,
            Period.FULL_TIME_OT_7,
            Period.FULL_TIME_OT_8,
            Period.FULL_TIME_OT_9,
            Period.FULL_TIME_OT_10,
            Period.SHOOTOUT,
            Period.FULL_TIME_SO
        )

    private val Period.toOvertimeLabel: StringWithParams
        get() = when (this) {
            Period.FULL_TIME_OT,
            Period.OVER_TIME -> StringWithParams(R.string.box_score_scoring_ot_title)
            Period.FULL_TIME_OT_2,
            Period.OVER_TIME_2 -> StringWithParams(R.string.box_score_scoring_more_than_one_ot_title, 2)
            Period.FULL_TIME_OT_3,
            Period.OVER_TIME_3 -> StringWithParams(R.string.box_score_scoring_more_than_one_ot_title, 3)
            Period.FULL_TIME_OT_4,
            Period.OVER_TIME_4 -> StringWithParams(R.string.box_score_scoring_more_than_one_ot_title, 4)
            Period.FULL_TIME_OT_5,
            Period.OVER_TIME_5 -> StringWithParams(R.string.box_score_scoring_more_than_one_ot_title, 5)
            Period.FULL_TIME_OT_6,
            Period.OVER_TIME_6 -> StringWithParams(R.string.box_score_scoring_more_than_one_ot_title, 6)
            Period.FULL_TIME_OT_7,
            Period.OVER_TIME_7 -> StringWithParams(R.string.box_score_scoring_more_than_one_ot_title, 7)
            Period.FULL_TIME_OT_8,
            Period.OVER_TIME_8 -> StringWithParams(R.string.box_score_scoring_more_than_one_ot_title, 8)
            Period.FULL_TIME_OT_9,
            Period.OVER_TIME_9 -> StringWithParams(R.string.box_score_scoring_more_than_one_ot_title, 9)
            Period.FULL_TIME_OT_10,
            Period.OVER_TIME_10 -> StringWithParams(R.string.box_score_scoring_more_than_one_ot_title, 10)
            Period.SHOOTOUT,
            Period.FULL_TIME_SO -> StringWithParams(R.string.box_score_scoring_so_title)
            else -> StringWithParams(R.string.box_score_scoring_ot_title)
        }

    private fun highlightCurrentPeriodColumn(currentPeriod: Period): Int {
        return when (currentPeriod) {
            Period.FIRST_QUARTER,
            Period.FIRST_HALF,
            Period.FIRST_PERIOD -> 1
            Period.SECOND_QUARTER,
            Period.SECOND_HALF,
            Period.SECOND_PERIOD -> 2
            Period.THIRD_QUARTER,
            Period.THIRD_PERIOD -> 3
            Period.FOURTH_QUARTER -> 4
            Period.OVER_TIME,
            Period.OVER_TIME_2,
            Period.OVER_TIME_3,
            Period.OVER_TIME_4,
            Period.OVER_TIME_5,
            Period.OVER_TIME_6,
            Period.OVER_TIME_7,
            Period.OVER_TIME_8,
            Period.OVER_TIME_9,
            Period.OVER_TIME_10,
            Period.SHOOTOUT -> 5
            else -> -1
        }
    }

    private val League.standardPeriodHeading
        get() = when {
            sport == Sport.HOCKEY -> listOf(
                StringWithParams(R.string.box_score_scoring_1st_title),
                StringWithParams(R.string.box_score_scoring_2nd_title),
                StringWithParams(R.string.box_score_scoring_3rd_title)
            )
            sport == Sport.BASKETBALL && this == League.NCAA_BB -> listOf(
                StringWithParams(R.string.box_score_scoring_1st_title),
                StringWithParams(R.string.box_score_scoring_2nd_title)
            )
            else -> listOf(
                StringWithParams(R.string.box_score_scoring_1q_title),
                StringWithParams(R.string.box_score_scoring_2q_title),
                StringWithParams(R.string.box_score_scoring_3q_title),
                StringWithParams(R.string.box_score_scoring_4q_title)
            )
        }

    private fun getScoreValues(game: GameDetailLocalModel): List<BoxScoresScoreTableUiModel.ScoreTableColumn> {
        val headings = mutableListOf<StringWithParams>().apply {
            addAll(game.league.legacyLeague.standardPeriodHeading)
            if (game.period.isOvertimeOrShootout) {
                add(game.period.toOvertimeLabel)
            }
        }

        val scoresList = mutableListOf<BoxScoresScoreTableUiModel.ScoreTableColumn>()
        headings.forEachIndexed { index, parameterizedString ->
            scoresList.add(
                BoxScoresScoreTableUiModel.ScoreTableColumn(
                    title = parameterizedString,
                    firstTeamValue = game.firstTeam?.periodScore?.getOrNull(index)?.scoreDisplay.orShortDash(),
                    secondTeamValue = game.secondTeam?.periodScore?.getOrNull(index)?.scoreDisplay.orShortDash()
                )
            )
        }
        return scoresList
    }

    private fun GameDetailLocalModel.createBaseballTotal(): List<BoxScoresScoreTableUiModel.ScoreTableColumn> {
        val awayTeam = (awayTeam as? GameDetailLocalModel.BaseballGameTeam)
        val homeTeam = (homeTeam as? GameDetailLocalModel.BaseballGameTeam)
        return listOf(
            BoxScoresScoreTableUiModel.ScoreTableColumn(
                title = StringWithParams(R.string.box_score_baseball_score_table_runs_header),
                firstTeamValue = awayTeam?.totalRuns?.toString().orEmpty(),
                secondTeamValue = homeTeam?.totalRuns?.toString().orEmpty()
            ),
            BoxScoresScoreTableUiModel.ScoreTableColumn(
                title = StringWithParams(R.string.box_score_baseball_score_table_hits_header),
                firstTeamValue = awayTeam?.totalHits?.toString().orEmpty(),
                secondTeamValue = homeTeam?.totalHits?.toString().orEmpty()
            ),
            BoxScoresScoreTableUiModel.ScoreTableColumn(
                title = StringWithParams(R.string.box_score_baseball_score_table_errors_header),
                firstTeamValue = awayTeam?.totalErrors?.toString().orEmpty(),
                secondTeamValue = homeTeam?.totalErrors?.toString().orEmpty()
            )
        )
    }

    private fun GameDetailLocalModel.createBaseballInnings(): List<BoxScoresScoreTableUiModel.ScoreTableColumn> {
        val innings = mutableListOf<BoxScoresScoreTableUiModel.ScoreTableColumn>()
        val baseballExtras = sportExtras as? GameDetailLocalModel.BaseballExtras
        val awayInnings = (awayTeam as? GameDetailLocalModel.BaseballGameTeam)?.toInningScores()
        val homeInnings = (homeTeam as? GameDetailLocalModel.BaseballGameTeam)?.toInningScores()

        awayInnings?.merge(homeInnings ?: emptyList())?.mapIndexed { index, inningPair ->
            val (awayInning, homeInning) = inningPair
            innings.add(
                BoxScoresScoreTableUiModel.ScoreTableColumn(
                    title = awayInning?.inning.toString().asResourceString(),
                    firstTeamValue = awayInning?.runs?.toString().orShortDash(),
                    secondTeamValue = homeInning?.runs.toRunsDisplay(
                        isCurrentInning = baseballExtras?.inning == index + 1,
                        currentInningHalf = baseballExtras?.inningHalf,
                        isGameCompleted = isGameCompleted
                    )
                )
            )
        }

        for (i in innings.size until STANDARD_INNINGS_COUNT) {
            innings.add(
                BoxScoresScoreTableUiModel.ScoreTableColumn(
                    title = (i + 1).toString().asResourceString(),
                    firstTeamValue = "-",
                    secondTeamValue = "-"
                )
            )
        }

        return innings
    }

    private fun currentInning(game: GameDetailLocalModel) =
        (game.sportExtras as? GameDetailLocalModel.BaseballExtras)?.outcome?.inning ?: 1
}