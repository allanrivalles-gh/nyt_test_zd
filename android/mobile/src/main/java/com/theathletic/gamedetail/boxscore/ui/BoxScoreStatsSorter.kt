package com.theathletic.gamedetail.boxscore.ui

import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.PlayerPosition
import com.theathletic.gamedetail.data.local.StatisticCategory

abstract class BoxScoreStatsSorter {

    abstract fun sort(
        lineUp: GameDetailLocalModel.LineUp?
    ): List<BoxScoreStatistics>?

    protected fun createPlayerRow(
        player: GameDetailLocalModel.Player,
        columnOrder: List<String>
    ) = PlayerStatRow(
        id = player.id,
        playerName = player.displayName.orEmpty(),
        playerPosition = getPlayerPosition(player),
        playerOrder = 0, // used only for baseball
        stats = player.statistics.sortedBy { columnOrder.indexOf(it.type) }
    )

    private fun getPlayerPosition(player: GameDetailLocalModel.Player) =
        if (player.position == PlayerPosition.UNKNOWN) "" else player.position.alias

    protected fun orderDescendingByColumnThenPlayerName(
        playerRows: List<PlayerStatRow>,
        column: Int
    ): List<PlayerStatRow> {
        return playerRows.sortedWith(
            compareByDescending<PlayerStatRow> { row ->
                row.stats.getOrNull(column)?.comparable()
            }.thenBy { row -> row.playerName }
        )
    }

    protected fun orderDescendingByTwoColumnsThenPlayerName(
        playerRows: List<PlayerStatRow>,
        firstColumn: Int,
        secondColumn: Int
    ): List<PlayerStatRow> {
        return playerRows.sortedWith(
            compareByDescending<PlayerStatRow> { row ->
                row.stats.getOrNull(firstColumn)?.comparable()
            }.thenByDescending { row ->
                row.stats.getOrNull(secondColumn)?.comparable()
            }.thenBy { it.playerName }
        )
    }

    protected fun orderByPlayerName(
        playerRows: List<PlayerStatRow>
    ) = playerRows.sortedWith(compareBy { row -> row.playerName })

    private fun GameDetailLocalModel.Statistic.comparable(): Comparable<*> {
        return when (this) {
            is GameDetailLocalModel.DecimalStatistic -> this
            is GameDetailLocalModel.FractionStatistic -> this
            is GameDetailLocalModel.PercentageStatistic -> this
            is GameDetailLocalModel.StringStatistic -> this
            is GameDetailLocalModel.TimeStatistic -> this
            is GameDetailLocalModel.IntegerStatistic -> this
            else -> object : Comparable<Any> {
                override fun compareTo(other: Any): Int { return 0 }
            }
        }
    }
}

data class BoxScoreStatistics(
    val category: StatisticCategory,
    val playerStats: List<PlayerStatRow>
)

data class PlayerStatRow(
    val id: String,
    val playerName: String,
    val playerPosition: String?,
    val playerOrder: Int,
    val stats: List<GameDetailLocalModel.Statistic>
)