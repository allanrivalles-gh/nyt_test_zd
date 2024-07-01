package com.theathletic.gamedetail.boxscore.ui.common

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.GameOddsUi
import com.theathletic.boxscore.ui.modules.GameOddsModule
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.ResourceString.StringWrapper
import com.theathletic.ui.asResourceString
import com.theathletic.utility.orShortDash
import java.util.concurrent.atomic.AtomicInteger

class BoxScoreGameOddsRenderers @AutoKoin constructor() {

    companion object {
        private const val DIRECTION_OVER = "over"
        private const val DIRECTION_UNDER = "under"
    }

    fun createGameOddsModule(game: GameDetailLocalModel) = game.renderGameOddsModule()

    @Deprecated("Use createGameOddsModule(game: GameDetailLocalModel)")
    fun createGameOddsModule(
        game: GameDetailLocalModel,
        pageOrder: AtomicInteger
    ): FeedModuleV2? {
        if (game.isGameScheduled.not() || game.oddsPregame.isEmpty()) return null
        pageOrder.getAndIncrement()
        return game.renderGameOddsModule()
    }

    private fun GameDetailLocalModel.renderGameOddsModule(): FeedModuleV2 {
        val firstTeamsOdds = processGameOdds(firstTeam?.team?.id.orEmpty(), oddsPregame, true)
        val secondTeamsOdds = processGameOdds(secondTeam?.team?.id.orEmpty(), oddsPregame, false)
        return GameOddsModule(
            id = id,
            firstTeamOdds = firstTeamsOdds.toTeamOdds(firstTeam?.team),
            secondTeamOdds = secondTeamsOdds.toTeamOdds(secondTeam?.team)
        )
    }

    private fun TeamsGameOdds.toTeamOdds(team: GameDetailLocalModel.Team?): GameOddsUi.TeamOdds {
        return GameOddsUi.TeamOdds(
            logoUrlList = team?.logos ?: emptyList(),
            label = team?.alias.orEmpty(),
            moneyUsOdds = this.moneyUsOdds.orShortDash(),
            spreadLine = StringWrapper(this.spreadLine.orShortDash()),
            spreadUsOdds = this.spreadUsOdds.orShortDash(),
            totalDirection = formatTotalsDirection(
                this.totalDirection,
                this.directionValue
            ),
            totalUsOdds = this.totalUsOdds.orShortDash()
        )
    }

    private fun formatTotalsDirection(direction: String?, value: String?): ResourceString =
        when (direction) {
            DIRECTION_OVER -> StringWithParams(
                R.string.box_score_game_odds_total_over,
                value.orShortDash()
            )
            DIRECTION_UNDER -> StringWithParams(
                R.string.box_score_game_odds_total_under,
                value.orShortDash()
            )
            else -> "-".asResourceString()
        }

    private fun processGameOdds(
        teamId: String,
        gameOdds: List<GameDetailLocalModel.GameOdds>,
        isFirstTeam: Boolean
    ): TeamsGameOdds {
        val teamsOdds = gameOdds.filter { odds ->
            when (odds) {
                is GameDetailLocalModel.GameOddsMoneyLine -> odds.team?.id == teamId
                is GameDetailLocalModel.GameOddsSpread -> odds.team?.id == teamId
                is GameDetailLocalModel.GameOddsTotals ->
                    (isFirstTeam && odds.direction == DIRECTION_OVER) ||
                        (!isFirstTeam && odds.direction == DIRECTION_UNDER)
                else -> false
            }
        }
        val oddsUiModel = TeamsGameOdds()
        teamsOdds.forEach { oddsValue ->
            when (oddsValue) {
                is GameDetailLocalModel.GameOddsMoneyLine -> {
                    oddsUiModel.moneyUsOdds = oddsValue.price.oddsUs
                }
                is GameDetailLocalModel.GameOddsSpread -> {
                    oddsUiModel.spreadLine = oddsValue.line
                    oddsUiModel.spreadUsOdds = oddsValue.price.oddsUs
                }
                is GameDetailLocalModel.GameOddsTotals -> {
                    oddsUiModel.totalDirection = oddsValue.direction
                    oddsUiModel.directionValue = oddsValue.line
                    oddsUiModel.totalUsOdds = oddsValue.price.oddsUs
                }
            }
        }
        return oddsUiModel
    }

    private class TeamsGameOdds {
        var spreadLine: String? = null
        var spreadUsOdds: String? = null
        var totalDirection: String? = null
        var directionValue: String? = null
        var totalUsOdds: String? = null
        var moneyUsOdds: String? = null
    }
}