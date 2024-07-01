package com.theathletic.gamedetail.boxscore.ui.basketball.stats

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.modules.PlayerStatsTableModule
import com.theathletic.boxscore.ui.modules.TwoItemToggleButtonModule
import com.theathletic.feed.ui.FeedModule
import com.theathletic.gamedetail.boxscore.ui.BoxScoreStatistics
import com.theathletic.gamedetail.boxscore.ui.BoxScoreStatsState
import com.theathletic.gamedetail.boxscore.ui.PlayerStatRow
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreCommonRenderers
import com.theathletic.gamedetail.boxscore.ui.football.stats.BoxScoreStatsCategoryGroupUiModel
import com.theathletic.gamedetail.boxscore.ui.football.stats.BoxScoreStatsCategoryHeaderUiModel
import com.theathletic.gamedetail.boxscore.ui.football.stats.BoxScoreStatsPlayerRowUiModel
import com.theathletic.gamedetail.boxscore.ui.football.stats.BoxScoreStatsValuesRowHeaderItemUiModel
import com.theathletic.gamedetail.boxscore.ui.football.stats.BoxScoreStatsValuesRowItemUiModel
import com.theathletic.gamedetail.boxscore.ui.football.stats.BoxScoreStatsValuesRowUiModel
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.StatisticCategory
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.UiModel
import com.theathletic.ui.asResourceString
import com.theathletic.ui.binding.ParameterizedString
import com.theathletic.utility.orShortDash
import com.theathletic.utility.safeLet

class BoxScoreStatsRenderer @AutoKoin constructor(
    private val commonRenderers: BoxScoreCommonRenderers
) {

    fun renderFeedModels(data: BoxScoreStatsState): List<FeedModule> {
        val statsData = if (data.firstTeamSelected) data.firstTeamStats else data.secondTeamStats
        return safeLet(data.game, statsData) { game, stats ->
            val renderedTables = renderCategoryTables(game, stats, data.firstTeamSelected)
            mutableListOf<FeedModule>().apply {
                add(
                    TwoItemToggleButtonModule(
                        id = game.id,
                        itemOneLabel = game.firstTeam?.team?.displayName.orShortDash().asResourceString(),
                        itemTwoLabel = game.secondTeam?.team?.displayName.orShortDash().asResourceString(),
                        isFirstItemSelected = data.firstTeamSelected
                    )
                )
                renderedTables.forEach { group ->
                    add(
                        PlayerStatsTableModule(
                            id = game.id,
                            playerColumn = group.toPlayerColumn(),
                            statisticColumns = group.toStatisticColumns()
                        )
                    )
                }
            }
        } ?: emptyList()
    }

    private fun BoxScoreStatsCategoryGroupUiModel.toPlayerColumn():
        List<PlayerStatsTableModule.PlayerColumnItem> {
        return mutableListOf<PlayerStatsTableModule.PlayerColumnItem>().apply {
            add(
                PlayerStatsTableModule.Category(StringWithParams(category.label))
            )
            players.mapNotNull { model ->
                if (model is BoxScoreStatsPlayerRowUiModel) {
                    add(
                        PlayerStatsTableModule.Player(
                            playerName = model.name,
                            position = model.position
                        )
                    )
                } else {
                    null
                }
            }
        }
    }

    private fun BoxScoreStatsCategoryGroupUiModel.toStatisticColumns():
        List<List<PlayerStatsTableModule.StatisticColumnItem>> {
        val statsTable = mutableListOf<List<PlayerStatsTableModule.StatisticColumnItem>>()
        val statsRows = stats.filterIsInstance<BoxScoreStatsValuesRowUiModel>()
        val numOfStatColumns = statsRows.firstOrNull()?.values?.size ?: 0
        for (col in 0 until numOfStatColumns) {
            statsTable.add(
                statsRows.mapNotNull { row ->
                    if (col < row.values.size) {
                        when (val model = row.values[col]) {
                            is BoxScoreStatsValuesRowHeaderItemUiModel ->
                                PlayerStatsTableModule.StatisticLabel(model.value)
                            is BoxScoreStatsValuesRowItemUiModel ->
                                PlayerStatsTableModule.StatisticValue(model.value)
                            else -> { null /* Not Supported */ }
                        }
                    } else {
                        PlayerStatsTableModule.StatisticValue("-")
                    }
                }
            )
        }
        return statsTable
    }

    private fun renderCategoryTables(
        game: GameDetailLocalModel,
        statsData: List<BoxScoreStatistics>,
        firstTeamSelected: Boolean,
    ): List<BoxScoreStatsCategoryGroupUiModel> {

        val teamId = (
            if (firstTeamSelected) game.firstTeam?.id else game.secondTeam?.id
            ) ?: return emptyList()

        val tables = mutableListOf<BoxScoreStatsCategoryGroupUiModel>()
        for ((category, players) in statsData) {
            if (players.isNotEmpty()) {
                tables.add(
                    BoxScoreStatsCategoryGroupUiModel(
                        id = teamId,
                        category = category,
                        players = renderPlayersNameList(
                            teamId,
                            category,
                            players
                        ),
                        stats = renderPlayerStatValuesRows(
                            teamId,
                            category,
                            players
                        )
                    )
                )
            }
        }
        return tables
    }

    private fun renderPlayersNameList(
        teamId: String,
        category: StatisticCategory,
        playerStats: List<PlayerStatRow>
    ) = mutableListOf<UiModel>().apply {
        add(
            BoxScoreStatsCategoryHeaderUiModel(
                id = teamId,
                category = category,
                label = ParameterizedString(category.label)
            )
        )
        playerStats.forEach { player ->
            add(
                BoxScoreStatsPlayerRowUiModel(
                    id = player.id,
                    category = category,
                    name = player.playerName,
                    position = player.playerPosition.orEmpty()
                )
            )
        }
    }

    private fun renderPlayerStatValuesRows(
        teamId: String,
        category: StatisticCategory,
        playerRows: List<PlayerStatRow>
    ) = mutableListOf<UiModel>().apply {
        val rows = mutableListOf<BoxScoreStatsValuesRowUiModel>()
        add(
            BoxScoreStatsValuesRowUiModel(
                id = teamId,
                category = category,
                values = renderStatsValuesHeaderRow(
                    teamId,
                    category,
                    playerRows.first().stats
                )
            )
        )

        playerRows.forEach { player ->
            val playerStats = mutableListOf<BoxScoreStatsValuesRowItemUiModel>()
            player.stats.forEachIndexed { index, statistic ->
                playerStats.add(
                    BoxScoreStatsValuesRowItemUiModel(
                        id = player.id,
                        category = category,
                        index = index,
                        value = commonRenderers.formatStatisticValue(statistic).orShortDash()
                    )
                )
            }
            rows.add(
                BoxScoreStatsValuesRowUiModel(
                    id = player.id,
                    category = category,
                    values = playerStats
                )
            )
        }
        addAll(rows)
    }

    private fun renderStatsValuesHeaderRow(
        teamId: String,
        category: StatisticCategory,
        stats: List<GameDetailLocalModel.Statistic>
    ) = stats.mapIndexed { index, statistic ->
        BoxScoreStatsValuesRowHeaderItemUiModel(
            id = teamId,
            category = category,
            index = index,
            value = statistic.headerLabel.orShortDash()
        )
    }

    private val StatisticCategory.label: Int
        get() = when (this) {
            // American Football
            StatisticCategory.ADVANCED -> R.string.box_score_stats_category_advanced
            StatisticCategory.DEFENSE -> R.string.box_score_stats_category_defense
            StatisticCategory.EFFICIENCY_FOURTH_DOWN -> R.string.box_score_stats_category_eff_4th_down
            StatisticCategory.EFFICIENCY_GOAL_TO_GO -> R.string.box_score_stats_category_eff_goal_to_go
            StatisticCategory.EFFICIENCY_RED_ZONE -> R.string.box_score_stats_category_eff_red_zone
            StatisticCategory.EFFICIENCY_THIRD_DOWN -> R.string.box_score_stats_category_eff_3rd_down
            StatisticCategory.EXTRA_POINTS_CONVERSIONS -> R.string.box_score_stats_category_extra_points_convs
            StatisticCategory.EXTRA_POINTS_KICKS -> R.string.box_score_stats_category_extra_points_kicks
            StatisticCategory.FIELD_GOALS -> R.string.box_score_stats_category_field_goals
            StatisticCategory.FIRST_DOWNS -> R.string.box_score_stats_category_first_downs
            StatisticCategory.FUMBLES -> R.string.box_score_stats_category_fumbles
            StatisticCategory.INTERCEPTIONS -> R.string.box_score_stats_category_interceptions
            StatisticCategory.INT_RETURNS -> R.string.box_score_stats_category_int_returns
            StatisticCategory.KICKOFFS -> R.string.box_score_stats_category_kickoffs
            StatisticCategory.KICKING -> R.string.box_score_stats_category_kicking
            StatisticCategory.KICK_RETURNS -> R.string.box_score_stats_category_kick_returns
            StatisticCategory.MISC_RETURNS -> R.string.box_score_stats_category_misc_returns
            StatisticCategory.PASSING -> R.string.box_score_stats_category_passing
            StatisticCategory.PENALTIES -> R.string.box_score_stats_category_penalties
            StatisticCategory.PUNTS -> R.string.box_score_stats_category_punts
            StatisticCategory.PUNT_RETURNS -> R.string.box_score_stats_category_punt_returns
            StatisticCategory.RECEIVING -> R.string.box_score_stats_category_receiving
            StatisticCategory.RUSHING -> R.string.box_score_stats_category_rushing
            StatisticCategory.STANDARD -> R.string.box_score_stats_category_standard
            StatisticCategory.SUMMARY -> R.string.box_score_stats_category_summary
            StatisticCategory.TOUCHDOWNS -> R.string.box_score_stats_category_touchdowns
            // Basketball
            StatisticCategory.STARTERS -> R.string.box_score_stats_category_basketball_starters
            StatisticCategory.BENCH -> R.string.box_score_stats_category_basketball_bench
            // Hockey
            StatisticCategory.GOALIES -> R.string.box_score_stats_category_hockey_goalies
            StatisticCategory.SKATERS -> R.string.box_score_stats_category_hockey_skaters
            // Baseball
            StatisticCategory.BATTING -> R.string.box_score_stats_category_baseball_batting
            StatisticCategory.PITCHING -> R.string.box_score_stats_category_baseball_pitching
            else -> R.string.box_score_stats_category_unknown
        }
}