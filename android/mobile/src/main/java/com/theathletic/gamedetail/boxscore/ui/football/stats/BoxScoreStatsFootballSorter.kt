package com.theathletic.gamedetail.boxscore.ui.football.stats

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.gamedetail.boxscore.ui.BoxScoreStatistics
import com.theathletic.gamedetail.boxscore.ui.BoxScoreStatsSorter
import com.theathletic.gamedetail.boxscore.ui.PlayerStatRow
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.StatisticCategory
import com.theathletic.utility.orShortDash

class BoxScoreStatsFootballSorter @AutoKoin constructor() : BoxScoreStatsSorter() {

    companion object {
        const val STAT_ASSISTS = "assists"
        const val STAT_ATTEMPTS = "attempts"
        const val STAT_AVG_YARDS = "avg_yards"
        const val STAT_EXTRA_POINTS = "extra_points"
        const val STAT_FIELD_GOALS = "field_goals"
        const val STAT_FUMBLES = "fumbles"
        const val STAT_INSIDE_20 = "inside_20"
        const val STAT_INTERCEPTIONS = "interceptions"
        const val STAT_LONGEST = "longest"
        const val STAT_LOST_FUMBLES = "lost_fumbles"
        const val STAT_NUMBER = "number"
        const val STAT_OPP_REC = "opp_rec"
        const val STAT_PASSES_DEFENDED = "passes_defended"
        const val STAT_PASSING = "passes"
        const val STAT_POINTS = "points"
        const val STAT_RATING = "rating"
        const val STAT_RECEPTIONS = "receptions"
        const val STAT_SACKS = "sacks"
        const val STAT_TACKLES = "tackles"
        const val STAT_TARGETS = "targets"
        const val STAT_TOUCHBACKS = "touchbacks"
        const val STAT_TOUCHDOWNS = "touchdowns"
        const val STAT_YARDS = "yards"
    }

    override fun sort(lineUp: GameDetailLocalModel.LineUp?): List<BoxScoreStatistics>? {
        lineUp ?: return null

        val statsTable = mutableMapOf<StatisticCategory, MutableList<PlayerStatRow>>()

        lineUp.players.forEach { player ->
            val playerCategoryMap = player.statistics.groupBy { it.category }
            for ((category, statsList) in playerCategoryMap) {
                if (!statsList.isNullOrEmpty()) {
                    val newPlayerRow = PlayerStatRow(
                        id = player.id,
                        playerName = player.displayName.orShortDash(),
                        playerPosition = player.position.alias,
                        playerOrder = 0,
                        stats = sortStatsForCategory(category, statsList)
                    )
                    val playerRows = statsTable.getOrElse(category) { mutableListOf() }
                    playerRows.add(newPlayerRow)
                    statsTable[category] = playerRows
                }
            }
        }

        return if (statsTable.isEmpty()) {
            null
        } else {
            sortPlayersInCategory(statsTable).toSortedMap(compareBy { it.displayOrder }).map {
                BoxScoreStatistics(
                    category = it.key,
                    playerStats = it.value
                )
            }
        }
    }

    private fun sortPlayersInCategory(
        statsTable: Map<StatisticCategory, List<PlayerStatRow>>
    ): Map<StatisticCategory, List<PlayerStatRow>> {
        val sortedTable = mutableMapOf<StatisticCategory, List<PlayerStatRow>>()
        statsTable.forEach { entry ->
            val categoryEntry = when (entry.key) {
                StatisticCategory.PASSING -> sortCategory(entry.value, passingColumns)
                StatisticCategory.RUSHING -> sortCategory(entry.value, rushingColumns)
                StatisticCategory.RECEIVING -> sortCategory(entry.value, receivingColumns)
                StatisticCategory.PUNTS -> sortCategory(entry.value, puntingColumns)
                StatisticCategory.PUNT_RETURNS,
                StatisticCategory.KICK_RETURNS -> sortCategory(entry.value, returnsColumns)
                StatisticCategory.DEFENSE -> sortCategory(entry.value, defenseColumns)
                StatisticCategory.FUMBLES -> sortCategory(entry.value, fumblesColumns)
                StatisticCategory.KICKING -> sortCategory(entry.value, kickingColumns)
                else -> {
                    entry.value
                }
            }
            sortedTable[entry.key] = categoryEntry
        }

        return sortedTable
    }

    private fun sortCategory(
        players: List<PlayerStatRow>,
        columnDefinitions: List<String>
    ): List<PlayerStatRow> {
        /**
         * Rule for sorting is if there is a STAT_YARDS column then sort by this first
         * and then by the first column (or second if the first is STAT_YARDS) and then
         * finally the player's name
         *
         * If column set does not contain a STAT_YARDS column then sort by the first then
         * by the second column and finally by the player's name
         */
        val sortIndexPair = if (columnDefinitions.contains(STAT_YARDS)) {
            val yardsIndex = columnDefinitions.indexOf(STAT_YARDS)
            Pair(yardsIndex, if (yardsIndex == 0) 1 else 0)
        } else {
            Pair(0, 1)
        }
        return orderDescendingByTwoColumnsThenPlayerName(
            players,
            sortIndexPair.first,
            sortIndexPair.second
        )
    }

    private fun sortStatsForCategory(
        category: StatisticCategory,
        unsorted: List<GameDetailLocalModel.Statistic>
    ): List<GameDetailLocalModel.Statistic> {
        return when (category) {
            StatisticCategory.PASSING -> unsorted.sortedBy { passingColumns.indexOf(it.type) }
            StatisticCategory.RUSHING -> unsorted.sortedBy { rushingColumns.indexOf(it.type) }
            StatisticCategory.RECEIVING -> unsorted.sortedBy { receivingColumns.indexOf(it.type) }
            StatisticCategory.FUMBLES -> unsorted.sortedBy { fumblesColumns.indexOf(it.type) }
            StatisticCategory.DEFENSE -> unsorted.sortedBy { defenseColumns.indexOf(it.type) }
            StatisticCategory.KICKING -> unsorted.sortedBy { kickingColumns.indexOf(it.type) }
            StatisticCategory.PUNTS -> unsorted.sortedBy { puntingColumns.indexOf(it.type) }
            StatisticCategory.KICK_RETURNS,
            StatisticCategory.PUNT_RETURNS -> unsorted.sortedBy { returnsColumns.indexOf(it.type) }
            else -> unsorted
        }
    }

    private val StatisticCategory.displayOrder
        get() = when (this) {
            StatisticCategory.PASSING -> 1
            StatisticCategory.RUSHING -> 2
            StatisticCategory.RECEIVING -> 3
            StatisticCategory.FUMBLES -> 4
            StatisticCategory.DEFENSE -> 5
            StatisticCategory.KICKING -> 6
            StatisticCategory.PUNTS -> 7
            StatisticCategory.KICK_RETURNS -> 8
            StatisticCategory.PUNT_RETURNS -> 9
            else -> Int.MAX_VALUE
        }

    private val passingColumns = listOf(
        STAT_PASSING,
        STAT_YARDS,
        STAT_AVG_YARDS,
        STAT_TOUCHDOWNS,
        STAT_INTERCEPTIONS,
        STAT_SACKS,
        STAT_RATING
    )

    private val rushingColumns = listOf(
        STAT_ATTEMPTS,
        STAT_YARDS,
        STAT_AVG_YARDS,
        STAT_TOUCHDOWNS,
        STAT_LONGEST
    )

    private val receivingColumns = listOf(
        STAT_RECEPTIONS,
        STAT_YARDS,
        STAT_AVG_YARDS,
        STAT_TOUCHDOWNS,
        STAT_LONGEST,
        STAT_TARGETS
    )

    private val fumblesColumns = listOf(
        STAT_FUMBLES,
        STAT_LOST_FUMBLES,
        STAT_OPP_REC
    )

    private val defenseColumns = listOf(
        STAT_TACKLES,
        STAT_ASSISTS,
        STAT_SACKS,
        STAT_PASSES_DEFENDED,
        STAT_INTERCEPTIONS,
        STAT_TOUCHDOWNS
    )

    private val kickingColumns = listOf(
        STAT_FIELD_GOALS,
        STAT_LONGEST,
        STAT_EXTRA_POINTS,
        STAT_POINTS
    )

    private val puntingColumns = listOf(
        STAT_ATTEMPTS,
        STAT_YARDS,
        STAT_AVG_YARDS,
        STAT_TOUCHBACKS,
        STAT_INSIDE_20,
        STAT_LONGEST
    )

    private val returnsColumns = listOf(
        STAT_NUMBER,
        STAT_YARDS,
        STAT_AVG_YARDS,
        STAT_LONGEST,
        STAT_TOUCHDOWNS
    )
}