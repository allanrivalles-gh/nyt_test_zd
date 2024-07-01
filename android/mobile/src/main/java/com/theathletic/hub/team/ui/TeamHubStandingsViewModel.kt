package com.theathletic.hub.team.ui

import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.modules.StandingsTableModule
import com.theathletic.entity.main.League
import com.theathletic.feed.FeedType
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedInteractor
import com.theathletic.hub.team.data.TeamHubRepository
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.scores.data.ScoresRepository
import com.theathletic.scores.remote.toGraphqlLeagueCode
import com.theathletic.scores.standings.data.local.StandingsGrouping
import com.theathletic.scores.standings.ui.NonNavigableStandingsTeamsUseCase
import com.theathletic.scores.ui.ScoresAnalyticsHandler
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer
import com.theathletic.ui.widgets.buttons.ToggleButtonGroupModule
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.launch

class TeamHubStandingsViewModel @AutoKoin constructor(
    @Assisted private val params: Params,
    @Assisted val navigator: ScreenNavigator,
    private val teamHubRepository: TeamHubRepository,
    private val scoresRepository: ScoresRepository,
    private val scoresAnalytics: ScoresAnalyticsHandler,
    private val teamHubAnalytics: TeamHubAnalyticsHandler,
    private val teamHubEventProducer: TeamHubEventProducer,
    private val nonNavigableStandingsTeamsUseCase: NonNavigableStandingsTeamsUseCase,
    transformer: TeamHubStandingsTransformer,
) : AthleticViewModel<TeamHubStandingsState, TeamHubStandingsContract.ViewState>(),
    FeedInteractor,
    Transformer<TeamHubStandingsState, TeamHubStandingsContract.ViewState> by transformer {

    data class Params(
        val teamId: String
    )

    override val initialState by lazy {
        TeamHubStandingsState(
            loadingState = LoadingState.INITIAL_LOADING,
            highlightedTeamId = params.teamId,
        )
    }

    init {
        loadStandings()
        fetchStandings()
    }

    private fun loadStandings() {
        teamHubRepository.getTeamStandings(params.teamId).collectIn(viewModelScope) { data ->
            data?.let {
                val nonNavigableTeams = nonNavigableStandingsTeamsUseCase(it.groupings)
                updateState {
                    copy(
                        league = it.league,
                        groupings = it.groupings.toStandingsGroupings().toStandingsGroupings(),
                        nonNavigableTeams = nonNavigableTeams,
                        loadingState = LoadingState.FINISHED
                    )
                }
            }
        }
    }

    private fun fetchStandings() {
        viewModelScope.launch {
            teamHubRepository.fetchTeamStandings(params.teamId).join()
        }
    }

    fun onRefresh() {
        updateState { copy(loadingState = LoadingState.RELOADING) }
        fetchStandings()
    }

    override fun send(interaction: FeedInteraction) {
        when (interaction) {
            is ToggleButtonGroupModule.Interaction.ToggleButtonGroup -> {
                onGroupClick(interaction.buttonTabClicked)
            }
            is StandingsTableModule.Interaction.OnTeamClick -> {
                if (interaction.teamId != params.teamId) launchTeamHub(interaction.teamId)
            }
        }
    }

    private fun launchTeamHub(teamId: String) {
        viewModelScope.launch {
            scoresRepository.getTeamDetails(teamId)?.let { teamDetails ->
                navigator.startHubActivity(FeedType.Team(teamDetails.legacyId))
            }
        }
    }

    private fun onGroupClick(index: Int) {
        if (index < state.groupings.size) {
            updateState { copy(selectedGroupIndex = index) }
            logStandingsClickEvent()
            logStandingsViewEvent()
        }
    }

    private fun logStandingsViewEvent() {
        val groupName = state.groupings.toGroupName(state.selectedGroupIndex)
        if (state.analyticsLastGroupView != groupName) {
            scoresAnalytics.trackStandingsPageView(
                leagueCode = state.league.toGraphqlLeagueCode.rawValue,
                group = groupName
            )
            updateState { copy(analyticsLastGroupView = groupName) }
        }
        teamHubAnalytics.trackViewOfStandings(params.teamId, state.league.toGraphqlLeagueCode.rawValue)
    }

    private fun logStandingsClickEvent() {
        scoresAnalytics.trackStandingsGroupClick(
            leagueCode = state.league.toGraphqlLeagueCode.rawValue,
            group = state.groupings.toGroupName(state.selectedGroupIndex)
        )
    }

    private fun List<StandingsGrouping>.toGroupName(index: Int): String {
        return when {
            size <= index -> ""
            else -> this[index].groupLabel
        }
    }

    private fun List<StandingsGrouping>.toStandingsGroupings(): List<StandingsGrouping> {
        return this.filter { it.groups.firstOrNull()?.standings?.isNotEmpty() == true }
    }

    fun autoScrollCompleted() {
        viewModelScope.launch {
            teamHubEventProducer.emit(Event.AutoScrollCompleted)
        }
    }
}

data class TeamHubStandingsState(
    val loadingState: LoadingState,
    val league: League = League.UNKNOWN,
    val groupings: List<StandingsGrouping> = emptyList(),
    val selectedGroupIndex: Int = 0,
    val highlightedTeamId: String?,
    val analyticsLastGroupView: String = "",
    val nonNavigableTeams: List<String> = emptyList()
) : DataState