package com.theathletic.gamedetail.boxscore.ui.basketball.stats

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.gamedetail.boxscore.ui.BoxScoreStatistics
import com.theathletic.gamedetail.boxscore.ui.BoxScoreStatsSorter
import com.theathletic.gamedetail.boxscore.ui.PlayerStatRow
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.PlayerPosition
import com.theathletic.gamedetail.data.local.StatisticCategory

class BoxScoreStatsBasketballSorter @AutoKoin constructor() : BoxScoreStatsSorter() {

    companion object {
        const val STAT_ASSISTS = "assists"
        const val STAT_BLOCKS = "blocks"
        const val STAT_FIELD_GOALS = "field_goals"
        const val STAT_FREE_THROWS = "free_throws"
        const val STAT_MINUTES = "minutes"
        const val STAT_PERSONAL_FOULS = "personal_fouls"
        const val STAT_PLUS_MINUS = "pls_min"
        const val STAT_POINTS = "points"
        const val STAT_REBOUNDS = "rebounds"
        const val STAT_STEALS = "steals"
        const val STAT_THREE_POINTS = "three_points"
        const val STAT_TURNOVERS = "turnovers"
    }

    override fun sort(
        lineUp: GameDetailLocalModel.LineUp?
    ): List<BoxScoreStatistics>? {
        lineUp ?: return null

        // Group players into Starters and Bench
        val (startersList, benchList) = lineUp.players.partition { it.starter }

        // Order for each player the stats columns
        val starters = BoxScoreStatistics(
            category = StatisticCategory.STARTERS,
            playerStats = startersList.map { createPlayerRow(it, columnOrder) }
                .filterNot { it.stats.isEmpty() }
                .orderByPosition()
        )
        val bench = BoxScoreStatistics(
            category = StatisticCategory.BENCH,
            playerStats = benchList.map { createPlayerRow(it, columnOrder) }
                .filterNot { it.stats.isEmpty() }
                .orderByMinutesThenPlayerName()
        )

        return listOf(starters, bench)
    }

    private fun List<PlayerStatRow>.orderByPosition() =
        sortedBy { row ->
            startersOrder.find { it.alias == row.playerPosition }?.order
        }

    private fun List<PlayerStatRow>.orderByMinutesThenPlayerName() =
        orderDescendingByColumnThenPlayerName(this, columnOrder.indexOf(STAT_MINUTES))

    private val columnOrder = listOf(
        STAT_MINUTES,
        STAT_POINTS,
        STAT_REBOUNDS,
        STAT_ASSISTS,
        STAT_FIELD_GOALS,
        STAT_THREE_POINTS,
        STAT_FREE_THROWS,
        STAT_STEALS,
        STAT_BLOCKS,
        STAT_TURNOVERS,
        STAT_PERSONAL_FOULS,
        STAT_PLUS_MINUS
    )

    private val startersOrder = listOf(
        PlayerPosition.POINT_GUARD,
        PlayerPosition.SHOOTING_GUARD,
        PlayerPosition.SMALL_FORWARD,
        PlayerPosition.POWER_FORWARD,
        PlayerPosition.CENTER,
    )
}