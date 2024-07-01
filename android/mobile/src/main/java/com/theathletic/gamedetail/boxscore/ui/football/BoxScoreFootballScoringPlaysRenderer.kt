package com.theathletic.gamedetail.boxscore.ui.football

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.ScoringSummaryUi
import com.theathletic.boxscore.ui.modules.ScoringSummaryModule
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.Period
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams

class BoxScoreFootballScoringPlaysRenderer @AutoKoin constructor() {

    fun createScoringSummaryModule(game: GameDetailLocalModel): FeedModuleV2? {
        val scoringPlays =
            (game.sportExtras as? GameDetailLocalModel.AmericanFootballExtras)?.scoringPlays
        if (scoringPlays.isNullOrEmpty()) return null
        return ScoringSummaryModule(
            id = game.id,
            plays = groupPlays(scoringPlays).toScoringSummaryModule(game)
        )
    }

    private fun Map<Period, List<GameDetailLocalModel.ScoringPlay>>.toScoringSummaryModule(
        game: GameDetailLocalModel
    ): List<ScoringSummaryUi> {
        val feedList = mutableListOf<ScoringSummaryUi>()
        for ((period, plays) in this) {
            feedList.add(
                ScoringSummaryUi.Title(
                    title = period.toLabel()
                )
            )
            feedList.addAll(
                plays.mapIndexed { index, play ->
                    ScoringSummaryUi.Play(
                        id = play.id,
                        teamLogos = play.team?.logos ?: emptyList(),
                        awayTeamScore = play.awayTeamScore.toString(),
                        homeTeamScore = play.homeTeamScore.toString(),
                        awayTeamAlias = game.awayTeam?.team?.alias,
                        homeTeamAlias = game.homeTeam?.team?.alias,
                        clock = play.clock,
                        description = play.description,
                        title = play.headerLabel.orEmpty(),
                        showScores = true,
                        showDivider = index < plays.lastIndex
                    )
                }
            )
        }
        return feedList
    }

    private fun groupPlays(
        scoringPlays: List<GameDetailLocalModel.ScoringPlay>
    ) = scoringPlays.groupBy { it.period }

    private fun Period.toLabel(): ResourceString = when (this) {
        Period.FIRST_QUARTER -> StringWithParams(R.string.box_score_first_quarter)
        Period.SECOND_QUARTER -> StringWithParams(R.string.box_score_second_quarter)
        Period.THIRD_QUARTER -> StringWithParams(R.string.box_score_third_quarter)
        Period.FOURTH_QUARTER -> StringWithParams(R.string.box_score_forth_quarter)
        Period.OVER_TIME -> StringWithParams(R.string.box_score_overtime)
        Period.OVER_TIME_2 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 2)
        Period.OVER_TIME_3 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 3)
        Period.OVER_TIME_4 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 4)
        Period.OVER_TIME_5 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 5)
        Period.OVER_TIME_6 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 6)
        Period.OVER_TIME_7 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 7)
        Period.OVER_TIME_8 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 8)
        Period.OVER_TIME_9 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 9)
        Period.OVER_TIME_10 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 10)
        else -> StringWithParams(R.string.box_score_unknown)
    }
}