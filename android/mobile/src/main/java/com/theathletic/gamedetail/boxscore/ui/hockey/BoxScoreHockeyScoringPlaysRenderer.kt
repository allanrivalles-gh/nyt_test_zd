package com.theathletic.gamedetail.boxscore.ui.hockey

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.ScoringSummaryUi
import com.theathletic.boxscore.ui.modules.ScoringSummaryModule
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.HockeyPlayType
import com.theathletic.gamedetail.data.local.Period
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.utility.parseHexColor
import com.theathletic.utility.orShortDash

class BoxScoreHockeyScoringPlaysRenderer @AutoKoin constructor() {

    fun createScoringSummaryModule(game: GameDetailLocalModel): FeedModuleV2? {
        val scoringPlays =
            (game.sportExtras as? GameDetailLocalModel.HockeyExtras)?.scoringPlays ?: return null
        val groupedPlays = scoringPlays.sortedBy { it.occurredAt }.groupBy { it.period }
        return ScoringSummaryModule(
            id = game.id,
            plays = groupedPlays.toScoringSummaryModule(game)
        )
    }

    private fun Map<Period, List<GameDetailLocalModel.HockeyPlay>>.toScoringSummaryModule(
        game: GameDetailLocalModel
    ): List<ScoringSummaryUi> {
        val feedList = mutableListOf<ScoringSummaryUi>()
        for ((period, plays) in this) {
            if (period == Period.SHOOTOUT) {
                feedList.addAll(renderShootoutPlays(plays))
            } else {
                feedList.add(ScoringSummaryUi.Title(StringWithParams(period.toLabel())))
                feedList.addAll(
                    plays.mapIndexed { index, play ->
                        ScoringSummaryUi.Play(
                            id = play.id,
                            teamLogos = play.team?.logos ?: emptyList(),
                            title = play.headerLabel.orEmpty(),
                            description = play.description,
                            clock = play.clock().orEmpty(),
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
        }
        return feedList
    }

    private fun renderShootoutPlays(
        plays: List<GameDetailLocalModel.Play>
    ): List<ScoringSummaryUi> {
        val feedList = mutableListOf<ScoringSummaryUi>()
        feedList.add(
            ScoringSummaryUi.Title(
                StringWithParams(R.string.box_score_shootout)
            )
        )
        plays.filterIsInstance<GameDetailLocalModel.HockeyShootoutPlay>()
            .forEachIndexed { index, play ->
                feedList.add(
                    ScoringSummaryUi.HockeyShootoutPlay(
                        id = play.id,
                        headshots = play.playerHeadshots ?: emptyList(),
                        teamLogos = play.team?.logos ?: emptyList(),
                        teamColor = play.team?.primaryColor.parseHexColor(),
                        playerName = play.headerLabel.orShortDash(),
                        teamAlias = play.team?.alias.orShortDash(),
                        description = play.description,
                        isGoal = play.type == HockeyPlayType.SHOOTOUT_GOAL,
                        showDivider = index < plays.lastIndex
                    )
                )
            }
        return feedList
    }

    private fun Period.toLabel() = when (this) {
        Period.FIRST_PERIOD -> R.string.box_score_first_period
        Period.SECOND_PERIOD -> R.string.box_score_second_period
        Period.THIRD_PERIOD -> R.string.box_score_third_period
        Period.OVER_TIME -> R.string.box_score_overtime
        Period.SHOOTOUT -> R.string.box_score_shootout
        else -> R.string.box_score_unknown
    }

    private fun GameDetailLocalModel.Play.clock() = when (this) {
        is GameDetailLocalModel.HockeyTeamPlay -> clock
        else -> null
    }
}