package com.theathletic.hub.league.ui

import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.modules.StandingsTableModule
import com.theathletic.entity.main.League
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedInteractor
import com.theathletic.scores.data.ScoresRepository
import com.theathletic.scores.data.SupportedLeagues
import com.theathletic.scores.remote.toGraphqlLeagueCode
import com.theathletic.scores.standings.data.local.StandingsGrouping
import com.theathletic.scores.standings.ui.NonNavigableStandingsTeamsUseCase
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer
import com.theathletic.ui.widgets.buttons.RoundedDropDownMenuModule
import com.theathletic.ui.widgets.buttons.ToggleButtonGroupModule
import com.theathletic.ui.widgets.tabs.ScrollableTabsModule
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.launch

class LeagueHubStandingsViewModel @AutoKoin constructor(
    @Assisted private val params: Params,
    private val scoresRepository: ScoresRepository,
    private val filterStandingsUseCase: FilterStandingsUseCase,
    transformer: LeagueHubStandingsTransformer,
    private val analytics: LeagueHubAnalyticsHandler,
    private val nonNavigableStandingsTeamsUseCase: NonNavigableStandingsTeamsUseCase,
    private val supportedLeagues: SupportedLeagues,
) : AthleticViewModel<LeagueHubStandingsState, LeagueHubStandingsContract.ViewState>(),
    FeedInteractor,
    Transformer<LeagueHubStandingsState, LeagueHubStandingsContract.ViewState> by transformer {

    data class Params(
        val league: League
    )

    override val initialState by lazy {
        LeagueHubStandingsState(
            loadingState = LoadingState.INITIAL_LOADING,
            league = params.league
        )
    }

    init {
        loadStandings()
        fetchStandings()
    }

    private fun loadStandings() {
        scoresRepository.getStandings(params.league).collectIn(viewModelScope) { data ->
            data?.let {
                val nonNavigableTeams = nonNavigableStandingsTeamsUseCase(it.groupings)
                val standingsGroups = filterStandingsUseCase(it.groupings)
                initStandingsFilterForCollegeLeagues(standingsGroups)
                updateState {
                    copy(
                        isStandingsFilteringEnabled = supportedLeagues.isCollegeLeague(league),
                        groupings = standingsGroups,
                        nonNavigableTeams = nonNavigableTeams,
                        loadingState = LoadingState.FINISHED
                    )
                }
            }
        }
    }

    private fun initStandingsFilterForCollegeLeagues(standingsGroups: List<StandingsGrouping>) {
        if (supportedLeagues.isCollegeLeague(params.league)) {
            updateState {
                copy(
                    selectedGroupIndex = 0,
                    selectedGroupName = standingsGroups.first().groupLabel
                )
            }
        }
    }

    private fun fetchStandings() {
        viewModelScope.launch {
            scoresRepository.fetchStandings(params.league).join()
        }
    }

    fun onRefresh() {
        updateState { copy(loadingState = LoadingState.RELOADING) }
        fetchStandings()
    }

    override fun send(interaction: FeedInteraction) {
        val leagueId = state.league.toGraphqlLeagueCode.rawValue
        when (interaction) {
            is ToggleButtonGroupModule.Interaction.ToggleButtonGroup -> {
                onGroupClick(interaction.buttonTabClicked)
                logDifferentStandingsTabSelected(leagueId, interaction.title)
            }
            is StandingsTableModule.Interaction.OnTeamClick -> {
                launchTeamHub(interaction.teamId)
                logTeamClickedFromStandings(interaction.teamId, leagueId)
            }
            is ScrollableTabsModule.Interaction.OnScrollableTabsClick -> {
                onGroupClick(interaction.tabClicked)
                state.league.toGraphqlLeagueCode
                logDifferentStandingsTabSelected(leagueId, interaction.title)
            }
            is RoundedDropDownMenuModule.Interaction.OnOptionSelected -> {
                updateState {
                    copy(
                        selectedGroupIndex = interaction.index,
                        selectedGroupName = interaction.option
                    )
                }
            }
        }
    }

    private fun logTeamClickedFromStandings(teamId: String, leagueId: String) {
        analytics.trackClickTeamFromStandingsTab(teamId, leagueId)
    }

    private fun logDifferentStandingsTabSelected(leagueId: String, title: String) {
        analytics.trackViewDifferentTabInStandings(leagueId, title)
    }

    private fun launchTeamHub(teamId: String) {
        viewModelScope.launch {
            scoresRepository.getTeamDetails(teamId)?.let { teamDetails ->
                if (teamDetails.legacyId > 0) {
                    sendEvent(LeagueHubStandingsContract.Event.NavigateToTeamHub(teamDetails.legacyId))
                }
            }
        }
    }

    private fun onGroupClick(index: Int) {
        if (index < state.groupings.size) {
            updateState { copy(selectedGroupIndex = index) }
        }
    }
}

data class LeagueHubStandingsState(
    val loadingState: LoadingState,
    val league: League = League.UNKNOWN,
    val groupings: List<StandingsGrouping> = emptyList(),
    val selectedGroupIndex: Int = 0,
    val selectedGroupName: String? = null,
    val nonNavigableTeams: List<String> = emptyList(),
    val isStandingsFilteringEnabled: Boolean = false
) : DataState