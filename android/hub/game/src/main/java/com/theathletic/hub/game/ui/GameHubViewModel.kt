package com.theathletic.hub.game.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ScrollToModule
import com.theathletic.boxscore.ui.GameDetailUi
import com.theathletic.boxscore.ui.formatters.BoxScoreBaseballInningFormatter
import com.theathletic.datetime.DateUtility
import com.theathletic.hub.game.data.local.GameSummary
import com.theathletic.hub.game.di.FetchGameSummaryUseCase
import com.theathletic.hub.game.di.ObserveGameSummaryUpdatesUseCase
import com.theathletic.scores.GameDetailTab
import com.theathletic.scores.GameDetailTabParamKey
import com.theathletic.scores.data.SupportedLeagues
import com.theathletic.ui.LoadingState
import com.theathletic.ui.updateState
import com.theathletic.utility.LocaleUtility
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class GameHubViewModel @AutoKoin constructor(
    @Assisted private val params: Params,
    observeGameSummaryUpdates: ObserveGameSummaryUpdatesUseCase,
    private val fetchGameSummary: FetchGameSummaryUseCase,
    private val supportedLeagues: SupportedLeagues,
    private val localeUtility: LocaleUtility,
    private val dateUtility: DateUtility,
    private val inningFormatter: BoxScoreBaseballInningFormatter,
) : ViewModel(), GameDetailUi.Interactor {

    data class Params(
        val gameId: String,
        val commentId: String,
        val initialTab: GameDetailTab,
        val initialTabExtras: Map<GameDetailTabParamKey, String?>,
        val scrollToModule: ScrollToModule,
        val view: String
    )

    private val _viewState = MutableStateFlow(
        GameHubViewState(
            selectedTab = params.initialTab,
            selectedTabExtras = params.initialTabExtras[GameDetailTabParamKey.PostId]
        )
    )
    val viewState = _viewState.asStateFlow()

    private val _viewEvents = MutableSharedFlow<GameHubViewEvent>()
    val viewEvents = _viewEvents.asSharedFlow()

    init {
        fetchGameSummary()
        observeGameSummaryUpdates(params.gameId)
            .onEach { it?.let { update -> onGameSummaryUpdates(update) } }
            .launchIn(viewModelScope)
    }

    private fun fetchGameSummary() {
        viewModelScope.launch {
            fetchGameSummary(params.gameId)
                .onFailure {
                    // todo (Mark): Add some suitable error state
                    _viewState.updateState { copy(loadingState = LoadingState.FINISHED) }
                }.onSuccess {
                    _viewState.updateState { copy(loadingState = LoadingState.FINISHED) }
                }
        }
    }

    private fun onGameSummaryUpdates(gameSummary: GameSummary) {
        val isLoaded = _viewState.value.loadingState.isFreshLoadingState.not()
        _viewState.updateState {
            copy(
                toolbarLabel = gameSummary.mapToToolbarLabelUiModel(),
                firstTeam = gameSummary.mapToTeamUiModel(
                    isFirstTeam = true,
                    isLoaded = isLoaded,
                    isFollowable = true, // todo (Mark): Add correct logic for this
                    supportedLeagues = supportedLeagues
                ),
                secondTeam = gameSummary.mapToTeamUiModel(
                    isFirstTeam = false,
                    isLoaded = isLoaded,
                    isFollowable = true, // todo (Mark): Add correct logic for this
                    supportedLeagues = supportedLeagues
                ),
                firstTeamStatus = gameSummary.firstTeam.mapToTeamStatus(
                    isGameInProgress = gameSummary.isGameInProgress,
                    sport = gameSummary.sport
                ),
                secondTeamStatus = gameSummary.secondTeam.mapToTeamStatus(
                    isGameInProgress = gameSummary.isGameInProgress,
                    sport = gameSummary.sport
                ),
                gameStatus = gameSummary.mapToGameStatus(
                    dateUtility = dateUtility,
                    inningFormatter = inningFormatter
                ),
                gameInfo = gameSummary.mapToGameInfoUiModel(
                    supportedLeagues = supportedLeagues,
                    isUnitedStatesOrCanada = localeUtility.isUnitedStatesOrCanada()
                ),
                gameTitle = gameSummary.mapToGameTitleUiModel()
            )
        }
    }

    override fun onBackButtonClicked() {
        viewModelScope.launch { _viewEvents.emit(GameHubViewEvent.NavigateBack) }
    }

    override fun onTabClicked(tab: GameDetailTab) {
        // todo (Mark): Implement in coming PR
    }

    override fun onTeamClicked(teamId: String, legacyId: Long, teamName: String) {
        // todo (Mark): Implement in coming PR
    }

    override fun onShareClick(shareLink: String) {
        // todo (Mark): Implement in coming PR
    }
}