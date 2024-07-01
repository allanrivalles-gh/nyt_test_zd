package com.theathletic.hub.team.ui

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.data.SizedImages
import com.theathletic.entity.main.Sport
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.FeedUiV2
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreCommonRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreLeadersRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreSeasonStatsRenderers
import com.theathletic.hub.team.data.local.TeamHubStatsLocalModel
import com.theathletic.hub.team.ui.modules.TeamHubPlayerStatsTableModule
import com.theathletic.hub.ui.SortablePlayerValuesTableUi
import com.theathletic.themes.AthColor
import com.theathletic.ui.LoadingState
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.Transformer
import com.theathletic.ui.asResourceString
import com.theathletic.ui.orShortDash
import com.theathletic.ui.utility.parseHexColor
import com.theathletic.ui.widgets.buttons.TwoItemToggleButtonModule
import com.theathletic.utility.orShortDash

class TeamHubStatsTransformer @AutoKoin constructor(
    private val leadersRenderers: BoxScoreLeadersRenderers,
    private val seasonStatsRenderers: BoxScoreSeasonStatsRenderers,
    private val commonRenderers: BoxScoreCommonRenderers,
) : Transformer<TeamHubStatsState, TeamHubStatsContract.ViewState> {

    override fun transform(data: TeamHubStatsState): TeamHubStatsContract.ViewState {
        return TeamHubStatsContract.ViewState(
            showSpinner = data.loadingState.isFreshLoadingState,
            showEmptyState = data.areSomeStatsAvailable.not(),
            feedUiModel = FeedUiV2(modules = createStatModules(data)),
        )
    }

    private fun createStatModules(data: TeamHubStatsState): List<FeedModuleV2> {
        if (data.hasNoStatsAvailable || data.statsData == null) return emptyList()
        return mutableListOf<FeedModuleV2>().apply {
            add(createTeamPlayerButtonTab(data.statsData.teamId, data.isTeamViewSelected))
            if (data.isTeamViewSelected) {
                add(leadersRenderers.createTeamLeaderForTeamHubModule(data.statsData))
                add(seasonStatsRenderers.createTeamSeasonStatsModule(data.statsData))
            } else {
                data.playerStats.forEach { category ->
                    add(category.toModule(data.statsData))
                }
            }
        }
    }

    private fun createTeamPlayerButtonTab(
        teamId: String,
        isTeamViewSelected: Boolean,
    ) = TwoItemToggleButtonModule(
        id = teamId,
        itemOneLabel = StringWithParams(R.string.team_hub_stats_team_button_label),
        itemTwoLabel = StringWithParams(R.string.team_hub_stats_player_button_label),
        isFirstItemSelected = isTeamViewSelected,
        includeTopDivider = false
    )

    private fun TeamHubStatsState.Category.toModule(statsData: TeamHubStatsLocalModel) =
        TeamHubPlayerStatsTableModule(
            id = statsData.teamId,
            headingResId = type.labelResId,
            showHeading = statsData.sport != Sport.BASKETBALL, // Basketball has no categories
            statsTable = SortablePlayerValuesTableUi(
                playerColumn = createPlayerColumn(
                    teamLogos = statsData.teamLogos,
                    teamColor = statsData.primaryColor,
                    playerStats = playerStats
                ),
                valueColumns = createStatisticsColumn(category = this)
            )
        )

    private fun createPlayerColumn(
        teamLogos: SizedImages,
        teamColor: String?,
        playerStats: List<TeamHubStatsLocalModel.PlayerStats>,
    ): List<SortablePlayerValuesTableUi.PlayerColumnItem> {
        return mutableListOf<SortablePlayerValuesTableUi.PlayerColumnItem>().apply {
            add(
                SortablePlayerValuesTableUi.PlayerColumnItem.HeaderCell(
                    R.string.team_hub_player_stats_column_title
                )
            )
            addAll(
                playerStats.map { player ->
                    SortablePlayerValuesTableUi.PlayerColumnItem.PlayerCell(
                        name = player.displayName.orShortDash(),
                        jerseyNumber = player.jerseyNumber?.let { jerseyNumber ->
                            StringWithParams(
                                R.string.team_hub_player_jersey_number_formatter,
                                jerseyNumber
                            )
                        }.orShortDash(),
                        headshots = player.headshots,
                        teamLogos = teamLogos,
                        teamColor = teamColor.parseHexColor(AthColor.Gray500)
                    )
                }
            )
        }
    }

    private fun createStatisticsColumn(
        category: TeamHubStatsState.Category,
    ): List<List<SortablePlayerValuesTableUi.ValueColumnItem>> {
        if (category.playerStats.isEmpty()) return emptyList()
        val numOfColumns = category.playerStats.firstOrNull()?.stats?.size ?: 0
        val columns = mutableListOf<List<SortablePlayerValuesTableUi.ValueColumnItem>>()
        for (index in 0 until numOfColumns) {
            category.playerStats.firstOrNull()?.let { firstPlayer ->
                columns.add(
                    mutableListOf<SortablePlayerValuesTableUi.ValueColumnItem>().apply {
                        val highlighted = category.currentSortColumn == firstPlayer.stats[index].type
                        add(
                            SortablePlayerValuesTableUi.ValueColumnItem.HeaderCell(
                                id = SortablePlayerValuesTableUi.CellId(
                                    category = category.type.name,
                                    type = firstPlayer.stats[index].type
                                ),
                                title = firstPlayer.stats[index].headerLabel.orShortDash().asResourceString(),
                                order = category.toColumnOrder(firstPlayer.stats[index].type),
                                highlighted = highlighted
                            )
                        )
                        addAll(
                            category.playerStats.map {
                                SortablePlayerValuesTableUi.ValueColumnItem.ValueCell(
                                    value = if (it.stats.size > index) {
                                        commonRenderers.formatStatisticValue(it.stats[index])?.asResourceString()
                                            .orShortDash()
                                    } else {
                                        StringWithParams(R.string.single_dash)
                                    },
                                    highlighted = highlighted
                                )
                            }
                        )
                    }
                )
            }
        }
        return columns
    }

    private fun TeamHubStatsState.Category.toColumnOrder(currentSortColumn: String) =
        if (this.currentSortColumn == currentSortColumn) order else SortablePlayerValuesTableUi.ColumnOrder.None

    private val TeamHubStatsState.areSomeStatsAvailable: Boolean
        get() = loadingState != LoadingState.FINISHED || hasNoStatsAvailable.not()

    private val TeamHubStatsState.hasNoStatsAvailable: Boolean
        get() = playerStats.isEmpty() &&
            statsData?.teamLeaders.isNullOrEmpty() &&
            statsData?.seasonStats.isNullOrEmpty()

    private val TeamHubStatsState.CategoryType.labelResId: Int
        get() = when (this) {
            TeamHubStatsState.CategoryType.Passing -> R.string.team_hub_player_stats_category_passing
            TeamHubStatsState.CategoryType.Rushing -> R.string.team_hub_player_stats_category_rushing
            TeamHubStatsState.CategoryType.Receiving -> R.string.team_hub_player_stats_category_receiving
            TeamHubStatsState.CategoryType.Defense -> R.string.team_hub_player_stats_category_defense
            TeamHubStatsState.CategoryType.Kicking -> R.string.team_hub_player_stats_category_kicking
            TeamHubStatsState.CategoryType.Punts -> R.string.team_hub_player_stats_category_punts
            TeamHubStatsState.CategoryType.KickReturns -> R.string.team_hub_player_stats_category_kick_returns
            TeamHubStatsState.CategoryType.PuntReturns -> R.string.team_hub_player_stats_category_punt_returns
            TeamHubStatsState.CategoryType.Batting -> R.string.team_hub_player_stats_category_batting
            TeamHubStatsState.CategoryType.Pitching -> R.string.team_hub_player_stats_category_pitching
            TeamHubStatsState.CategoryType.Skating -> R.string.team_hub_player_stats_category_skating
            TeamHubStatsState.CategoryType.GoalTending -> R.string.team_hub_player_stats_category_goaltending
            TeamHubStatsState.CategoryType.GoalKeepers -> R.string.team_hub_player_stats_category_goal_keepers
            TeamHubStatsState.CategoryType.OutfieldPlayers -> R.string.team_hub_player_stats_category_outfield_players
            TeamHubStatsState.CategoryType.Basketball -> R.string.empty_string
            TeamHubStatsState.CategoryType.NoCategories -> R.string.empty_string
        }
}