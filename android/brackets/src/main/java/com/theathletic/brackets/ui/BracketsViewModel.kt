package com.theathletic.brackets.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.brackets.data.BracketsRepository
import com.theathletic.brackets.data.PlaceholderTeams
import com.theathletic.brackets.data.local.TournamentRoundGame
import com.theathletic.brackets.data.local.TournamentRoundGameTitleFormatter
import com.theathletic.brackets.data.remote.ReplayGameUseCase
import com.theathletic.brackets.ui.Event.NavigateToGameDetails
import com.theathletic.other.UniqueSubscriptionsManager
import com.theathletic.type.LeagueCode
import com.theathletic.ui.ComposeViewModel
import com.theathletic.ui.LoadingState
import com.theathletic.utility.Event
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * This ViewModel does not follow our standard architecture patterns.
 *
 * Developers should exercise caution when using this ViewModel as a reference as some of the patterns
 * implemented may have changed or be out dated.
 *
 * For more information on our
 * recommended Android architecture patterns, see the following link:
 *
 * https://theathletic.atlassian.net/wiki/spaces/ENG/pages/1734148123/Architecture+Patterns
 *
 * Note: We should remove/update this when we solidify our architecture patterns.
 */

private const val TYPE_THIRD_FINAL = "third_final"

class BracketsViewModel @AutoKoin constructor(
    @Assisted private val params: Params,
    private val bracketsRepository: BracketsRepository,
    private val tournamentRoundGameTitleFormatter: TournamentRoundGameTitleFormatter,
    private val bracketsAnalytics: BracketsAnalytics,
    private val replayGameUseCase: ReplayGameUseCase
) : ViewModel(), ComposeViewModel {
    private val subscriptionsManager = UniqueSubscriptionsManager(::subscribeToGamesUpdates)
    private var placeholderTeams: Map<String, PlaceholderTeams>? = null
    private val _eventConsumer = MutableSharedFlow<Event>()
    private val _uiState = mutableStateOf(BracketsUiState())
    val eventConsumer = _eventConsumer.asSharedFlow()
    val uiState: State<BracketsUiState>
        get() = _uiState

    data class Params(
        val leagueCode: LeagueCode,
        val seasonId: String?,
    )

    init {
        listenForLocalDataChange()
        viewModelScope.launch {
            bracketsRepository.fetchTournament(params.leagueCode, params.seasonId)
        }
        bracketsAnalytics.viewBracketsTab(params.leagueCode.rawValue)
    }

    fun onEvent(event: BracketsEvent) {
        when (event) {
            is BracketsEvent.OnRoundSelected -> onRoundSelected(event.selectedRoundIndex)
            is BracketsEvent.OnMatchClicked -> onMatchClicked(event.match)
            is BracketsEvent.OnPullToRefresh -> refresh()
            is BracketsEvent.OnReplayMatchClicked -> replayMatch(event.matchId)
        }
    }

    private fun refresh() {
        _uiState.value = _uiState.value.copy(loadingState = LoadingState.RELOADING)
        viewModelScope.launch {
            bracketsRepository.fetchTournament(params.leagueCode, params.seasonId).join()
            _uiState.value = _uiState.value.copy(loadingState = LoadingState.FINISHED)
        }
    }

    private fun onMatchClicked(match: BracketsUi.Match) {
        if (match.firstTeam.isPlaceholderTeam() || match.secondTeam.isPlaceholderTeam()) return
        bracketsAnalytics.trackGameBoxScoreClick(
            leagueId = params.leagueCode.rawValue,
            gameId = match.id,
            phase = match.phase?.ordinal ?: -1
        )
        viewModelScope.launch {
            _eventConsumer.emit(NavigateToGameDetails(match.id))
        }
    }

    private fun onRoundSelected(selectedRoundIndex: Int) {
        _uiState.value = _uiState.value.copy(currentTabIndex = selectedRoundIndex)
        val leagueId = params.leagueCode.rawValue
        bracketsAnalytics.trackRoundTabClick(leagueId, selectedRoundIndex)
    }

    private fun listenForLocalDataChange() {
        bracketsRepository.getTournament(params.leagueCode).collectIn(viewModelScope) { tournamentData ->
            if (tournamentData == null) return@collectIn
            // Ignore third final rounds
            val rounds = tournamentData.rounds.filter { it.type != TYPE_THIRD_FINAL }
            _uiState.value = _uiState.value.copy(
                rounds = rounds.toRoundUiModels(tournamentRoundGameTitleFormatter),
                tabs = rounds.toTabUiModels(),
                currentTabIndex = _uiState.value.currentTabIndex ?: rounds.indexOfLiveRound(),
                loadingState = LoadingState.FINISHED
            )

            val allGames = rounds.flatMap { it.groups }.flatMap { it.games }

            // we need to save it for later because the subscription does not contain this info
            if (placeholderTeams == null) {
                placeholderTeams = allGames
                    .filter { it.placeholderTeams != null }
                    .associate { Pair(it.id, it.placeholderTeams!!) }
            }

            subscriptionsManager.set(allGames.filter { it.needsUpdate() }.map { it.id }.toSet())
        }
    }

    private fun subscribeToGamesUpdates(ids: Set<String>): () -> Unit {
        val job = viewModelScope.launch {
            bracketsRepository.subscribeToTournamentGamesUpdates(
                params.leagueCode,
                placeholderTeams ?: mapOf(),
                ids.toList()
            )
        }
        return { job.cancel() }
    }

    private fun replayMatch(matchId: String) {
        viewModelScope.launch {
            replayGameUseCase.invoke(matchId)
        }
    }
}

private fun TournamentRoundGame.needsUpdate(): Boolean {
    return phase != TournamentRoundGame.Phase.PostGame
}