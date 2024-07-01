package com.theathletic.scores.standings.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.modules.StandingsTableModule
import com.theathletic.entity.main.League
import com.theathletic.feed.FeedType
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedInteractor
import com.theathletic.followable.FollowableId
import com.theathletic.followable.FollowableType
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.scores.data.ScoresRepository
import com.theathletic.scores.data.SupportedLeagues
import com.theathletic.scores.remote.toGraphqlLeagueCode
import com.theathletic.scores.standings.data.local.ScoresStandingsLocalModel
import com.theathletic.scores.ui.ScoresAnalyticsHandler
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.ComposeViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer
import com.theathletic.ui.widgets.buttons.ToggleButtonGroupModule
import com.theathletic.utility.PhoneVibrator
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.launch

class ScoresStandingsViewModel @AutoKoin constructor(
    @Assisted val params: Params,
    @Assisted val navigator: ScreenNavigator,
    private val scoresRepository: ScoresRepository,
    private val followableRepository: FollowableRepository,
    private val phoneVibrator: PhoneVibrator,
    private val scoresAnalytics: ScoresAnalyticsHandler,
    private val supportedLeagues: SupportedLeagues,
    private val nonNavigableStandingsTeamsUseCase: NonNavigableStandingsTeamsUseCase,
    transformer: ScoresStandingsTransformer
) : AthleticViewModel<ScoresStandingsState, ScoresStandingsContract.ViewState>(),
    ComposeViewModel,
    FeedInteractor,
    DefaultLifecycleObserver,
    Transformer<ScoresStandingsState, ScoresStandingsContract.ViewState> by transformer,
    ScoresStandingsContract.Presenter {
    data class Params(
        val league: League,
        val teamId: String?,
    )

    override val initialState by lazy {
        ScoresStandingsState(
            loadingState = LoadingState.INITIAL_LOADING,
            league = params.league,
            highlightedTeamId = params.teamId,
            standings = ScoresStandingsLocalModel(
                id = "",
                seasonName = "",
                groupings = emptyList()
            )
        )
    }

    // todo (Mark): Remove this when removing the FS also no longer required with new implementation
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        initialize()
    }

    override fun initialize() {
        setLabels()
        loadStandings()
        fetchStandings()
    }

    private fun loadStandings() {
        scoresRepository.getStandings(params.league).collectIn(viewModelScope) { data ->
            data?.let {
                val nonNavigableTeams = nonNavigableStandingsTeamsUseCase(it.groupings)
                updateState {
                    copy(
                        standings = it,
                        nonNavigableTeams = nonNavigableTeams
                    )
                }
                logStandingsViewEvent()
            }
        }
    }

    private fun fetchStandings() {
        viewModelScope.launch {
            scoresRepository.fetchStandings(params.league).join()
            var selectedIndex = 0
            if (params.teamId != null && supportedLeagues.isCollegeLeague(params.league)) {
                selectedIndex = state.standings.groupings.indexOfLast { grouping ->
                    grouping.groups.any { group ->
                        group.standings.any { it.team.id == params.teamId }
                    }
                }
                if (selectedIndex == -1) selectedIndex = 0 // in case index not found
            }
            updateState { copy(loadingState = LoadingState.FINISHED, selectedGroupIndex = selectedIndex) }
        }
    }

    private fun setLabels() {
        viewModelScope.launch {
            val leagueLabel = getLeaguesName(params.league)
            updateState {
                copy(
                    leagueLabel = leagueLabel
                )
            }
        }
    }

    override fun onBackPress() {
        navigator.finishActivity()
    }

    override fun onTeamRowClick(
        teamId: String,
        teamDisplayName: String,
        payload: ScoresStandingsRowUiModel.AnalyticsPayload
    ) {
        trackTeamToScheduleClick(payload)
        launchTeamHub(teamId)
        phoneVibrator.vibrate(PhoneVibrator.Duration.CLICK)
    }

    private fun launchTeamHub(teamId: String) {
        viewModelScope.launch {
            scoresRepository.getTeamDetails(teamId)?.let { teamDetails ->
                navigator.startHubActivity(FeedType.Team(teamDetails.legacyId))
            }
        }
    }

    override fun onGroupClick(index: Int) {
        if (index < state.standings.groupings.size) {
            updateState { copy(selectedGroupIndex = index) }
            logStandingsClickEvent()
            logStandingsViewEvent()
        }
    }

    private suspend fun getLeaguesName(league: League) =
        followableRepository.getLeague(
            FollowableId(
                id = league.leagueId.toString(),
                type = FollowableType.LEAGUE
            )
        )?.shortName.orEmpty()

    private fun trackTeamToScheduleClick(payload: ScoresStandingsRowUiModel.AnalyticsPayload) {
        scoresAnalytics.trackNavigateToTeamScheduleFromStandings(
            payload.teamId,
            payload.pageId,
            payload.index
        )
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

    private fun logStandingsViewEvent() {
        val groupName = state.standings.toGroupName(state.selectedGroupIndex)
        if (state.analyticsLastGroupView != groupName) {
            scoresAnalytics.trackStandingsPageView(
                leagueCode = state.league.toGraphqlLeagueCode.rawValue,
                group = groupName
            )
            updateState { copy(analyticsLastGroupView = groupName) }
        }
    }

    private fun logStandingsClickEvent() {
        scoresAnalytics.trackStandingsGroupClick(
            leagueCode = state.league.toGraphqlLeagueCode.rawValue,
            group = state.standings.toGroupName(state.selectedGroupIndex)
        )
    }

    private fun ScoresStandingsLocalModel.toGroupName(index: Int): String {
        return when {
            groupings.size <= index -> ""
            else -> groupings[index].groupLabel
        }
    }
}

data class ScoresStandingsState(
    val loadingState: LoadingState,
    val league: League,
    val highlightedTeamId: String?,
    val leagueLabel: String? = null,
    val standings: ScoresStandingsLocalModel,
    val selectedGroupIndex: Int = 0,
    val analyticsLastGroupView: String = "",
    val nonNavigableTeams: List<String> = emptyList()
) : DataState