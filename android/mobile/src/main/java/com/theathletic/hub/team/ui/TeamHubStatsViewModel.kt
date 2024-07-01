package com.theathletic.hub.team.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedInteractor
import com.theathletic.hub.team.data.TeamHubRepository
import com.theathletic.hub.team.data.local.TeamHubStatsLocalModel
import com.theathletic.hub.ui.SortablePlayerValuesTableUi
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.ComposeViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer
import com.theathletic.ui.widgets.buttons.TwoItemToggleButtonModule
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.launch

class TeamHubStatsViewModel @AutoKoin constructor(
    @Assisted private val params: Params,
    private val teamHubRepository: TeamHubRepository,
    private val statsGrouper: TeamHubPlayerStatsGrouper,
    private val analytics: TeamHubAnalyticsHandler,
    transformer: TeamHubStatsTransformer
) : AthleticViewModel<TeamHubStatsState, TeamHubStatsContract.ViewState>(),
    TeamHubStatsContract.Interaction,
    FeedInteractor,
    ComposeViewModel,
    DefaultLifecycleObserver,
    Transformer<TeamHubStatsState, TeamHubStatsContract.ViewState> by transformer {

    data class Params(
        val teamId: String,
        val leagueId: String,
    )

    override fun initialize() {
        super.initialize()
        loadData()
        fetchData()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        analytics.trackViewOfTeamStats(params.teamId, params.leagueId)
    }

    private fun loadData() {
        teamHubRepository.getTeamStats(params.teamId).collectIn(viewModelScope) { data ->
            data?.let {
                updateState {
                    copy(
                        statsData = it,
                        playerStats = statsGrouper.group(it.sport, it.playerStats)
                    )
                }
            }
        }
    }

    private fun fetchData() {
        viewModelScope.launch {
            teamHubRepository.fetchTeamStats(params.teamId).join()
            updateState { copy(loadingState = LoadingState.FINISHED) }
        }
    }

    override val initialState by lazy {
        TeamHubStatsState(
            loadingState = LoadingState.INITIAL_LOADING
        )
    }

    override fun send(interaction: FeedInteraction) {
        when (interaction) {
            is TwoItemToggleButtonModule.Interaction.TwoItemToggleClick -> {
                updateState { copy(isTeamViewSelected = interaction.isFirstItemSelected) }
                if (interaction.isFirstItemSelected) {
                    analytics.trackViewOfTeamStats(params.teamId, params.leagueId)
                } else {
                    analytics.trackViewOfPlayerStats(params.teamId, params.leagueId)
                }
            }
            is SortablePlayerValuesTableUi.Interaction.OnColumnSortClick -> {
                val category = TeamHubStatsState.CategoryType.valueOf(interaction.id.category)
                updateState {
                    copy(
                        playerStats = statsGrouper.resortColumn(
                            tables = playerStats,
                            categoryType = category,
                            sortColumn = interaction.id.type,
                            currentOrder = interaction.order
                        )
                    )
                }
            }
        }
    }

    fun onRefresh() {
        // todo: Add data refreshing logic
    }
}

data class TeamHubStatsState(
    val loadingState: LoadingState,
    val statsData: TeamHubStatsLocalModel? = null,
    val playerStats: List<Category> = emptyList(),
    val isTeamViewSelected: Boolean = true,
) : DataState {

    data class Category(
        val type: CategoryType,
        val currentSortColumn: String? = null,
        val order: SortablePlayerValuesTableUi.ColumnOrder = SortablePlayerValuesTableUi.ColumnOrder.None,
        val playerStats: List<TeamHubStatsLocalModel.PlayerStats>
    )

    enum class CategoryType {
        Passing,
        Rushing,
        Receiving,
        Defense,
        Kicking,
        Punts,
        KickReturns,
        PuntReturns,
        Batting,
        Pitching,
        Skating,
        GoalTending,
        GoalKeepers,
        OutfieldPlayers,
        Basketball,
        NoCategories
    }
}