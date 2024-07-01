package com.theathletic.hub.team.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.main.Sport
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.PlayerPosition
import com.theathletic.gamedetail.data.local.StatisticCategory
import com.theathletic.hub.team.data.local.TeamHubStatsLocalModel
import com.theathletic.hub.ui.SortablePlayerValuesTableUi

private const val GAMES_PLAYED_TYPE = "games_played"
private const val FOOTBALL_YARDS_TYPE = "yards"
private const val FOOTBALL_TACKLES_TYPE = "tackles"
private const val FOOTBALL_FIELD_GOALS_MADE_TYPE = "field_goals_made"
private const val FOOTBALL_ATTEMPTS_TYPE = "attempts"
private const val FOOTBALL_RETURNS_TYPE = "returns"
private const val SOCCER_GAMES_PLAYED_TYPE = "total_games_played"
private const val HOCKEY_TOTAL_POINTS_TYPE = "total_points"
private const val HOCKEY_TOTAL_GAMES_PLAYED_TYPE = "total_games_played"
private const val BASEBALL_AT_BAT_TYPE = "at_bat"
private const val BASKETBALL_TOTAL_GAMES_STARTED_TYPE = "total_games_started"

class TeamHubPlayerStatsGrouper @AutoKoin constructor() {

    fun group(
        sport: Sport,
        playerStats: List<TeamHubStatsLocalModel.PlayerStats>
    ): List<TeamHubStatsState.Category> {
        return when (sport) {
            Sport.FOOTBALL -> groupAndSortAmericanFootballPlayerStats(playerStats)
            Sport.SOCCER -> groupAndSortSoccerPlayerStats(playerStats)
            Sport.HOCKEY -> groupAndSortHockeyPlayerStats(playerStats)
            Sport.BASEBALL -> groupAndSortBaseballPlayerStats(playerStats)
            Sport.BASKETBALL -> sortBasketballPlayerStats(playerStats)
            else -> emptyList()
        }
    }

    fun resortColumn(
        tables: List<TeamHubStatsState.Category>,
        categoryType: TeamHubStatsState.CategoryType,
        sortColumn: String?,
        currentOrder: SortablePlayerValuesTableUi.ColumnOrder
    ): List<TeamHubStatsState.Category> {
        val newOrder = when (currentOrder) {
            SortablePlayerValuesTableUi.ColumnOrder.None,
            SortablePlayerValuesTableUi.ColumnOrder.Ascending -> SortablePlayerValuesTableUi.ColumnOrder.Descending
            SortablePlayerValuesTableUi.ColumnOrder.Descending -> SortablePlayerValuesTableUi.ColumnOrder.Ascending
        }
        return tables.map {
            if (it.type == categoryType) {
                val colIndex = getSortColumnIndex(it.playerStats.firstOrNull(), sortColumn.orEmpty())
                // Some players don't have all their stats so filter them out they don't have current column
                // and then add them back in at the bottom of the list
                val invalid = it.playerStats.filter { player -> player.stats.size <= colIndex }
                val validSorted = it.playerStats
                    .mapNotNull { player -> if (invalid.contains(player)) null else player }
                    .sortedWith(
                        orderByColumn(
                            colIndex = colIndex,
                            isDescending = newOrder == SortablePlayerValuesTableUi.ColumnOrder.Descending
                        )
                    )
                TeamHubStatsState.Category(
                    type = it.type,
                    order = newOrder,
                    currentSortColumn = sortColumn,
                    playerStats = validSorted + invalid
                )
            } else {
                it
            }
        }
    }

    private fun groupAndSortAmericanFootballPlayerStats(
        playerStats: List<TeamHubStatsLocalModel.PlayerStats>
    ): List<TeamHubStatsState.Category> {
        if (playerStats.isEmpty()) return emptyList()
        return listOf(
            filterAndSortForCategory(
                type = TeamHubStatsState.CategoryType.Passing,
                playerStats = playerStats,
                category = StatisticCategory.PASSING,
                defaultSortType = FOOTBALL_YARDS_TYPE,
            ),
            filterAndSortForCategory(
                type = TeamHubStatsState.CategoryType.Rushing,
                playerStats = playerStats,
                category = StatisticCategory.RUSHING,
                defaultSortType = FOOTBALL_YARDS_TYPE,
            ),
            filterAndSortForCategory(
                type = TeamHubStatsState.CategoryType.Receiving,
                playerStats = playerStats,
                category = StatisticCategory.RECEIVING,
                defaultSortType = FOOTBALL_YARDS_TYPE,
            ),
            filterAndSortForCategory(
                type = TeamHubStatsState.CategoryType.Defense,
                playerStats = playerStats,
                category = StatisticCategory.DEFENSE,
                defaultSortType = FOOTBALL_TACKLES_TYPE,
            ),
            filterAndSortForCategory(
                type = TeamHubStatsState.CategoryType.Kicking,
                playerStats = playerStats,
                category = StatisticCategory.KICKING,
                defaultSortType = FOOTBALL_FIELD_GOALS_MADE_TYPE,
            ),
            filterAndSortForCategory(
                type = TeamHubStatsState.CategoryType.Punts,
                playerStats = playerStats,
                category = StatisticCategory.PUNTS,
                defaultSortType = FOOTBALL_ATTEMPTS_TYPE,
            ),
            filterAndSortForCategory(
                type = TeamHubStatsState.CategoryType.KickReturns,
                playerStats = playerStats,
                category = StatisticCategory.KICK_RETURNS,
                defaultSortType = FOOTBALL_RETURNS_TYPE,
            ),
            filterAndSortForCategory(
                type = TeamHubStatsState.CategoryType.PuntReturns,
                playerStats = playerStats,
                category = StatisticCategory.PUNT_RETURNS,
                defaultSortType = FOOTBALL_RETURNS_TYPE,
            ),
        )
    }

    private fun filterAndSortForCategory(
        type: TeamHubStatsState.CategoryType,
        playerStats: List<TeamHubStatsLocalModel.PlayerStats>,
        category: StatisticCategory,
        defaultSortType: String,
    ) = TeamHubStatsState.Category(
        type = type,
        playerStats = filterPlayerStatsForCategory(
            playerStats = playerStats,
            category = category,
            defaultSortType = defaultSortType,
            addGamesPlayedColumn = true
        )
    )

    private fun groupAndSortSoccerPlayerStats(
        playerStats: List<TeamHubStatsLocalModel.PlayerStats>
    ): List<TeamHubStatsState.Category> {
        if (playerStats.isEmpty()) return emptyList()
        // After the pos column gets added at pos 0 we need to shift across 1 for the sort
        val sortIndex = getSortColumnIndex(playerStats.firstOrNull(), SOCCER_GAMES_PLAYED_TYPE) + 1
        return listOf(
            TeamHubStatsState.Category(
                type = TeamHubStatsState.CategoryType.GoalKeepers,
                playerStats = filterSoccerPlayerStats(playerStats, filterForGoalkeepers = true)
                    .sortedWith(orderByColumnThenPlayerName(sortIndex))
            ),
            TeamHubStatsState.Category(
                type = TeamHubStatsState.CategoryType.OutfieldPlayers,
                playerStats = filterSoccerPlayerStats(playerStats, filterForGoalkeepers = false)
                    .sortedWith(orderByColumnThenPlayerName(sortIndex))
            ),
        )
    }

    private fun groupAndSortHockeyPlayerStats(
        playerStats: List<TeamHubStatsLocalModel.PlayerStats>
    ): List<TeamHubStatsState.Category> {
        if (playerStats.isEmpty()) return emptyList()
        // After the pos column gets added at pos 0 we need to shift across 1 for the sort
        val skatingSortIndex = getSortColumnIndex(playerStats.firstOrNull(), HOCKEY_TOTAL_POINTS_TYPE) + 1
        val goaltendingSortIndex = getSortColumnIndex(playerStats.firstOrNull(), HOCKEY_TOTAL_GAMES_PLAYED_TYPE) + 1
        return listOf(
            TeamHubStatsState.Category(
                type = TeamHubStatsState.CategoryType.Skating,
                playerStats = filterHockeyPlayerStats(playerStats, filterForGoalies = false)
                    .sortedWith(orderByColumnThenPlayerName(skatingSortIndex))
            ),
            TeamHubStatsState.Category(
                type = TeamHubStatsState.CategoryType.GoalTending,
                playerStats = filterHockeyPlayerStats(playerStats, filterForGoalies = true)
                    .sortedWith(orderByColumnThenPlayerName(goaltendingSortIndex))
            )
        )
    }

    private fun groupAndSortBaseballPlayerStats(
        playerStats: List<TeamHubStatsLocalModel.PlayerStats>
    ): List<TeamHubStatsState.Category> {
        if (playerStats.isEmpty()) return emptyList()
        return listOf(
            TeamHubStatsState.Category(
                type = TeamHubStatsState.CategoryType.Batting,
                playerStats = filterPlayerStatsForCategory(
                    playerStats = playerStats,
                    category = StatisticCategory.BATTING,
                    defaultSortType = BASEBALL_AT_BAT_TYPE,
                )
            ),
            TeamHubStatsState.Category(
                type = TeamHubStatsState.CategoryType.Pitching,
                playerStats = filterPlayerStatsForCategory(
                    playerStats = playerStats,
                    category = StatisticCategory.PITCHING,
                    defaultSortType = GAMES_PLAYED_TYPE
                )
            ),
        )
    }

    private fun sortBasketballPlayerStats(
        playerStats: List<TeamHubStatsLocalModel.PlayerStats>
    ): List<TeamHubStatsState.Category> {
        if (playerStats.isEmpty()) return emptyList()
        // After the pos column gets added at pos 0 we need to shift across 1 for the sort
        val sortIndex = getSortColumnIndex(playerStats.firstOrNull(), BASKETBALL_TOTAL_GAMES_STARTED_TYPE) + 1
        return listOf(
            TeamHubStatsState.Category(
                type = TeamHubStatsState.CategoryType.Basketball,
                playerStats = addBasketballPlayerPositionColumn(playerStats)
                    .sortedWith(orderByColumnThenPlayerName(sortIndex))
            ),
        )
    }

    private fun orderByColumn(
        colIndex: Int,
        isDescending: Boolean,
    ) = if (isDescending) {
        compareByDescending<TeamHubStatsLocalModel.PlayerStats> { it.stats.getOrNull(colIndex)?.comparable() }
    } else {
        compareBy<TeamHubStatsLocalModel.PlayerStats> { it.stats.getOrNull(colIndex)?.comparable() }
    }

    private fun orderByColumnThenPlayerName(
        colIndex: Int
    ) = compareByDescending<TeamHubStatsLocalModel.PlayerStats> {
        it.stats.getOrNull(colIndex)?.comparable()
    }.thenBy { it.displayName }

    private fun getSortColumnIndex(
        playerStats: TeamHubStatsLocalModel.PlayerStats?,
        category: String
    ): Int {
        if (playerStats == null) return 0
        playerStats.stats.forEachIndexed { index, statistic ->
            if (statistic.type == category) return index
        }
        return 0
    }

    private fun filterPlayerStatsForCategory(
        playerStats: List<TeamHubStatsLocalModel.PlayerStats>,
        category: StatisticCategory,
        defaultSortType: String,
        addGamesPlayedColumn: Boolean = false,
    ): List<TeamHubStatsLocalModel.PlayerStats> {
        val filteredPlayers = playerStats.mapNotNull { player ->
            val categoryStats = player.stats.filter { it.category == category }.toMutableList()
            if (categoryStats.isNotEmpty()) {
                if (addGamesPlayedColumn) {
                    player.stats.find { it.type == GAMES_PLAYED_TYPE }?.let { gamesPlayedStat ->
                        categoryStats.add(0, gamesPlayedStat)
                    }
                }
                // Add Player Position column
                categoryStats.add(0, createPlayerColumnStat(category, player.position))
                TeamHubStatsLocalModel.PlayerStats(
                    id = player.id,
                    displayName = player.displayName,
                    headshots = player.headshots,
                    jerseyNumber = player.jerseyNumber,
                    position = player.position,
                    stats = categoryStats
                )
            } else {
                null
            }
        }
        return filteredPlayers.sortedWith(
            orderByColumnThenPlayerName(
                getSortColumnIndex(filteredPlayers.firstOrNull(), defaultSortType)
            )
        )
    }

    @Suppress("ComplexCondition")
    private fun filterSoccerPlayerStats(
        playerStats: List<TeamHubStatsLocalModel.PlayerStats>,
        filterForGoalkeepers: Boolean
    ): List<TeamHubStatsLocalModel.PlayerStats> {
        return playerStats.mapNotNull { player ->
            if ((player.position == PlayerPosition.GOALKEEPER) && filterForGoalkeepers ||
                (player.position != PlayerPosition.GOALKEEPER) && filterForGoalkeepers.not()
            ) {
                val stats = player.stats.toMutableList()
                // Add Player Position column
                stats.add(0, createPlayerColumnStat(StatisticCategory.UNKNOWN, player.position))
                populatePlayer(player, stats)
            } else {
                null
            }
        }
    }

    @Suppress("ComplexCondition")
    private fun filterHockeyPlayerStats(
        playerStats: List<TeamHubStatsLocalModel.PlayerStats>,
        filterForGoalies: Boolean
    ): List<TeamHubStatsLocalModel.PlayerStats> {
        return playerStats.mapNotNull { player ->
            if ((player.position == PlayerPosition.GOALIE) && filterForGoalies ||
                (player.position != PlayerPosition.GOALIE) && filterForGoalies.not()
            ) {
                val stats = player.stats.toMutableList()
                // Add Player Position column
                stats.add(0, createPlayerColumnStat(StatisticCategory.UNKNOWN, player.position))
                populatePlayer(player, stats)
            } else {
                null
            }
        }
    }

    private fun addBasketballPlayerPositionColumn(
        playerStats: List<TeamHubStatsLocalModel.PlayerStats>,
    ): List<TeamHubStatsLocalModel.PlayerStats> {
        return playerStats.map { player ->
            val stats = player.stats.toMutableList()
            // Add Player Position column
            stats.add(0, createPlayerColumnStat(StatisticCategory.UNKNOWN, player.position))
            populatePlayer(player, stats)
        }
    }

    private fun populatePlayer(
        player: TeamHubStatsLocalModel.PlayerStats,
        stats: List<GameDetailLocalModel.Statistic>
    ) = TeamHubStatsLocalModel.PlayerStats(
        id = player.id,
        displayName = player.displayName,
        headshots = player.headshots,
        jerseyNumber = player.jerseyNumber,
        position = player.position,
        stats = stats
    )

    private fun createPlayerColumnStat(
        category: StatisticCategory,
        playerPosition: PlayerPosition
    ) = GameDetailLocalModel.StringStatistic(
        id = "",
        category = category,
        headerLabel = "Pos",
        label = "Position",
        type = "player_position",
        lessIsBest = false,
        isChildStat = false,
        value = playerPosition.alias,
        longHeaderLabel = "Position"
    )

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