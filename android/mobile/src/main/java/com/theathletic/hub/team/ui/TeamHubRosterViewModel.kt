package com.theathletic.hub.team.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedInteractor
import com.theathletic.hub.team.data.TeamHubRepository
import com.theathletic.hub.team.data.local.TeamHubRosterLocalModel
import com.theathletic.hub.ui.SortablePlayerValuesTableUi
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.ComposeViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.launch

class TeamHubRosterViewModel @AutoKoin constructor(
    @Assisted private val params: Params,
    private val teamHubRepository: TeamHubRepository,
    private val rosterGrouper: TeamHubRosterGrouper,
    private val analytics: TeamHubAnalyticsHandler,
    transformer: TeamHubRosterTransformer
) : AthleticViewModel<TeamHubRosterState, TeamHubRosterContract.ViewState>(),
    TeamHubRosterContract.Interaction,
    FeedInteractor,
    ComposeViewModel,
    DefaultLifecycleObserver,
    Transformer<TeamHubRosterState, TeamHubRosterContract.ViewState> by transformer {

    data class Params(
        val teamId: String,
        val leagueId: String,
    )

    override val initialState by lazy {
        TeamHubRosterState(
            loadingState = LoadingState.INITIAL_LOADING,
        )
    }

    override fun initialize() {
        super.initialize()
        loadData()
        fetchData()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        analytics.trackViewOfTeamRoster(params.teamId, params.leagueId)
    }

    private fun loadData() {
        teamHubRepository.getTeamRoster(params.teamId).collectIn(viewModelScope) { data ->
            data?.let {
                updateState {
                    copy(
                        teamDetails = it.teamDetails,
                        rosters = rosterGrouper.groupAndDefaultSort(
                            sport = it.teamDetails.sport,
                            roster = it.roster,
                        )
                    )
                }
            }
        }
    }

    private fun fetchData() {
        viewModelScope.launch {
            teamHubRepository.fetchTeamRoster(params.teamId).join()
            updateState { copy(loadingState = LoadingState.FINISHED) }
        }
    }

    override fun send(interaction: FeedInteraction) {
        when (interaction) {
            is SortablePlayerValuesTableUi.Interaction.OnColumnSortClick -> {
                val categoryType = TeamHubRosterState.CategoryType.valueOf(interaction.id.category)
                val sortType = TeamHubRosterState.SortType.valueOf(interaction.id.type)
                updateState {
                    copy(
                        rosters = rosterGrouper.resortColumn(
                            rosters = state.rosters,
                            categoryType = categoryType,
                            sortType = sortType,
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

data class TeamHubRosterState(
    val loadingState: LoadingState,
    val teamDetails: TeamHubRosterLocalModel.TeamDetails? = null,
    val rosters: List<Category> = emptyList(),
) : DataState {

    data class Category(
        val type: CategoryType,
        val sortType: SortType = SortType.Default,
        val order: SortablePlayerValuesTableUi.ColumnOrder = SortablePlayerValuesTableUi.ColumnOrder.None,
        val roster: List<TeamHubRosterLocalModel.PlayerDetails>,
    )

    enum class CategoryType {
        Offense,
        Defense,
        SpecialTeams,
        GoalKeepers,
        OutfieldPlayers,
        Centers,
        LeftWings,
        RightWings,
        Goalies,
        Pitchers,
        Catchers,
        Infielders,
        Outfielders,
        DesignatedHitter,
        NoCategories
    }

    enum class SortType {
        Position,
        Height,
        Weight,
        DateOfBirth,
        Age,
        Default
    }
}