package com.theathletic.gamedetail.boxscore.ui.baseball

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.ScoringSummaryUi
import com.theathletic.boxscore.ui.formatters.BoxScoreBaseballInningFormatter
import com.theathletic.boxscore.ui.formatters.OrdinalFormatter
import com.theathletic.boxscore.ui.modules.ScoringSummaryModule
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.InningHalf

class BoxScoreBaseballScoringPlaysRenderer @AutoKoin constructor(
    private val inningsFormatter: BoxScoreBaseballInningFormatter,
    private val ordinalFormatter: OrdinalFormatter
) {

    fun createScoringSummaryModule(game: GameDetailLocalModel): FeedModuleV2? {
        val scoringPlays =
            (game.sportExtras as? GameDetailLocalModel.BaseballExtras)?.scoringPlays ?: return null
        val groupedPlays = scoringPlays.groupBy { it.groupKey() }
        if (groupedPlays.isEmpty()) return null
        return ScoringSummaryModule(
            id = game.id,
            plays = groupedPlays.toScoringSummaryModule(game)
        )
    }

    private fun Map<InningsKey, List<GameDetailLocalModel.BaseballTeamPlay>>.toScoringSummaryModule(
        game: GameDetailLocalModel
    ): List<ScoringSummaryUi> {
        val feedList = mutableListOf<ScoringSummaryUi>()
        for ((inningGroup, plays) in this) {
            feedList.add(
                ScoringSummaryUi.Title(
                    inningsFormatter.longFormat(
                        ordinalFormatter.format(inningGroup.inning),
                        inningGroup.inningHalf
                    )
                )
            )
            feedList.addAll(
                plays.mapIndexed { index, play ->
                    ScoringSummaryUi.Play(
                        id = play.id,
                        teamLogos = play.team?.logos ?: emptyList(),
                        title = play.headerLabel.orEmpty(),
                        description = play.description,
                        clock = String(),
                        awayTeamAlias = game.awayTeam?.team?.alias,
                        homeTeamAlias = game.homeTeam?.team?.alias,
                        awayTeamScore = play.awayTeamScore.toString(),
                        homeTeamScore = play.homeTeamScore.toString(),
                        showScores = true,
                        showDivider = index < plays.lastIndex
                    )
                }
            )
        }
        return feedList
    }

    private fun GameDetailLocalModel.BaseballTeamPlay.groupKey() = InningsKey(inning, inningHalf)

    data class InningsKey(
        val inning: Int,
        val inningHalf: InningHalf?
    )
}