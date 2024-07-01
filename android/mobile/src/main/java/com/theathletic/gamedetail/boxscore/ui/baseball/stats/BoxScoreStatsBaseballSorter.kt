package com.theathletic.gamedetail.boxscore.ui.baseball.stats

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.gamedetail.boxscore.ui.BoxScoreStatistics
import com.theathletic.gamedetail.boxscore.ui.BoxScoreStatsSorter
import com.theathletic.gamedetail.boxscore.ui.PlayerStatRow
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.StatisticCategory
import com.theathletic.utility.orLongDash

const val STAT_EARNED_RUNS = "earned_runs"
const val STAT_ERA = "era"
const val STAT_HITS = "hits"
const val STAT_HOME_RUNS = "home_runs"
const val STAT_INNINGS_PITCHED = "innings_pitched"
const val STAT_PITCHES_TO_STRIKES = "pitches_to_strikes"
const val STAT_RUNS = "runs"
const val STAT_STRIKEOUTS = "strikeouts"
const val STAT_AT_BAT = "at_bat"
const val STAT_RBI = "rbi"
const val STAT_WALKS = "walks"
const val STAT_LOB = "lob"
const val STAT_2ND_BASE = "2nd_base"
const val STAT_3RD_BASE = "3rd_base"
const val STAT_AVG = "avg"
const val STAT_OBP = "obp"
const val STAT_SLG = "slg"
const val STAT_WHIP = "whip"

class BoxScoreStatsBaseballSorter @AutoKoin constructor() : BoxScoreStatsSorter() {

    override fun sort(
        lineUp: GameDetailLocalModel.LineUp?
    ): List<BoxScoreStatistics>? {
        lineUp ?: return null

        val statsTable = mutableMapOf<StatisticCategory, MutableList<PlayerStatRow>>()

        lineUp.players.forEach { player ->
            val categoryMap = player.statistics.groupBy { it.category }
            for ((category, statsList) in categoryMap) {
                val newPlayerRow = PlayerStatRow(
                    id = player.id + category.toString(),
                    playerName = player.displayName.orLongDash(),
                    playerPosition = player.toPositionOrOutcome(category),
                    stats = sortStatsForCategory(category, statsList),
                    playerOrder = player.playerOrder,
                )
                val playersRows = statsTable.getOrElse(category) { mutableListOf() }
                playersRows.add(newPlayerRow)
                statsTable[category] = playersRows
            }
            if (categoryMap.isEmpty() && player.playerOrder > 0) {
                // Batter has not got any stats yet but still need to show them
                // Only batters have a display order greater than 0
                val newPlayerRow = PlayerStatRow(
                    id = player.id + StatisticCategory.BATTING.toString(),
                    playerName = player.displayName.orLongDash(),
                    playerPosition = player.toPositionOrOutcome(StatisticCategory.BATTING),
                    stats = createEntryStatsForBatter(player.id),
                    playerOrder = player.playerOrder,
                )
                val playersRows = statsTable.getOrElse(StatisticCategory.BATTING) { mutableListOf() }
                playersRows.add(newPlayerRow)
                statsTable[StatisticCategory.BATTING] = playersRows
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

    private fun GameDetailLocalModel.Player.toPositionOrOutcome(category: StatisticCategory) =
        if (category == StatisticCategory.BATTING) {
            position.alias
        } else {
            outcome?.let { "($it)" }
        }

    private fun sortPlayersInCategory(
        statsTable: Map<StatisticCategory, List<PlayerStatRow>>
    ): Map<StatisticCategory, List<PlayerStatRow>> {
        val sortedTable = mutableMapOf<StatisticCategory, List<PlayerStatRow>>()
        statsTable.forEach { entry ->
            val categoryEntry = when (entry.key) {
                StatisticCategory.BATTING -> sortHitters(entry.value)
                else -> entry.value
            }
            sortedTable[entry.key] = categoryEntry
        }
        return sortedTable
    }

    private fun sortHitters(players: List<PlayerStatRow>): List<PlayerStatRow> {
        return players.sortedBy { it.playerOrder }
            .compressDuplicatePlayers()
            .indentSubstitutePlayers()
    }

    private fun List<PlayerStatRow>.compressDuplicatePlayers(): List<PlayerStatRow> {
        return foldIndexed(mutableListOf()) { index, newList, player ->
            when {
                index == 0 -> newList.add(player)
                get(index - 1).playerName == player.playerName &&
                    get(index - 1).playerOrder == player.playerOrder -> newList[newList.lastIndex] = player.copy(
                    playerPosition = "${newList[newList.lastIndex].playerPosition} -${player.playerPosition}"
                )
                else -> newList.add(player)
            }
            newList
        }
    }

    private fun List<PlayerStatRow>.indentSubstitutePlayers(): List<PlayerStatRow> {
        return foldIndexed(mutableListOf()) { index, newList, player ->
            when {
                index == 0 -> newList.add(player)
                get(index - 1).playerOrder == player.playerOrder -> newList.add(
                    player.copy(playerName = "    ${player.playerName}")
                )
                else -> newList.add(player)
            }
            newList
        }
    }

    private fun sortStatsForCategory(
        category: StatisticCategory,
        unsorted: List<GameDetailLocalModel.Statistic>
    ): List<GameDetailLocalModel.Statistic> {
        return when (category) {
            StatisticCategory.BATTING -> unsorted.sortedBy { battingColumns.indexOf(it.type) }
            StatisticCategory.PITCHING -> unsorted.sortedBy { pitchingColumns.indexOf(it.type) }
            else -> unsorted
        }
    }

    private fun createEntryStatsForBatter(playerId: String): List<GameDetailLocalModel.Statistic> {
        return mutableListOf<GameDetailLocalModel.Statistic>().apply {
            for (index in 1..battingColumns.size) {
                this.add(
                    GameDetailLocalModel.StringStatistic(
                        id = "$playerId:$index",
                        category = StatisticCategory.BATTING,
                        headerLabel = null,
                        label = "",
                        type = "",
                        lessIsBest = false,
                        value = "--",
                        isChildStat = false,
                        referenceOnly = false,
                        longHeaderLabel = ""
                    )
                )
            }
        }
    }

    private val StatisticCategory.displayOrder
        get() = when (this) {
            StatisticCategory.BATTING -> 1
            StatisticCategory.PITCHING -> 2
            else -> Int.MAX_VALUE
        }

    private val battingColumns = listOf(
        STAT_AT_BAT,
        STAT_RUNS,
        STAT_HITS,
        STAT_RBI,
        STAT_WALKS,
        STAT_STRIKEOUTS,
        STAT_LOB,
        STAT_2ND_BASE,
        STAT_3RD_BASE,
        STAT_HOME_RUNS,
        STAT_AVG,
        STAT_OBP,
        STAT_SLG
    )

    private val pitchingColumns = listOf(
        STAT_INNINGS_PITCHED,
        STAT_HITS,
        STAT_RUNS,
        STAT_EARNED_RUNS,
        STAT_STRIKEOUTS,
        STAT_WALKS,
        STAT_HOME_RUNS,
        STAT_PITCHES_TO_STRIKES,
        STAT_ERA
    )
}