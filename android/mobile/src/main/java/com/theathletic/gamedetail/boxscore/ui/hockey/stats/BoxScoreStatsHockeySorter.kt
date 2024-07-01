package com.theathletic.gamedetail.boxscore.ui.hockey.stats

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.gamedetail.boxscore.ui.BoxScoreStatistics
import com.theathletic.gamedetail.boxscore.ui.BoxScoreStatsSorter
import com.theathletic.gamedetail.boxscore.ui.PlayerStatRow
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.PlayerPosition
import com.theathletic.gamedetail.data.local.StatisticCategory

class BoxScoreStatsHockeySorter @AutoKoin constructor() : BoxScoreStatsSorter() {

    companion object {
        const val STAT_SHOTS_AGAINST = "shots_against"
        const val STAT_GOALS_AGAINST = "goals_against"
        const val STAT_SAVES = "saves"
        const val STAT_SAVES_PCT = "saves_pct"
        const val STAT_TOTAL_TIME_ON_ICE = "total_time_on_ice"
        const val STAT_GOALS = "goals"
        const val STAT_ASSISTS = "assists"
        const val STAT_PLUS_MINUS = "plus_minus"
        const val STAT_PENALTY_MINUTES = "penalty_minutes"
        const val STAT_SHOTS = "shots"
        const val STAT_HITS = "hits"
        const val STAT_BLOCKED_SHOTS = "blocked_shots"
    }

    override fun sort(
        lineUp: GameDetailLocalModel.LineUp?
    ): List<BoxScoreStatistics>? {
        lineUp ?: return null

        // Group players into Goalies and Skaters
        val (goalieList, skaterList) = lineUp.players.partition { it.position == PlayerPosition.GOALIE }

        val goalies = BoxScoreStatistics(
            category = StatisticCategory.GOALIES,
            playerStats = goalieList.map { createPlayerRow(it, goalieColumnOrder) }
                .filterNot { it.stats.isEmpty() }
                .orderPlayersByName()
        )

        val skaters = BoxScoreStatistics(
            category = StatisticCategory.SKATERS,
            playerStats = skaterList.map { createPlayerRow(it, skatersColumnOrder) }
                .filterNot { it.stats.isEmpty() }
                .orderPlayersByName()
        )

        return listOf(goalies, skaters)
    }

    private fun List<PlayerStatRow>.orderPlayersByName() = orderByPlayerName(this)

    private val goalieColumnOrder = listOf(
        STAT_SHOTS_AGAINST,
        STAT_GOALS_AGAINST,
        STAT_SAVES,
        STAT_SAVES_PCT,
        STAT_TOTAL_TIME_ON_ICE
    )

    private val skatersColumnOrder = listOf(
        STAT_GOALS,
        STAT_ASSISTS,
        STAT_PLUS_MINUS,
        STAT_PENALTY_MINUTES,
        STAT_SHOTS,
        STAT_HITS,
        STAT_BLOCKED_SHOTS,
        STAT_TOTAL_TIME_ON_ICE
    )
}