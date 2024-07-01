package com.theathletic.scores.ui.search

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.extension.toStringOrEmpty
import com.theathletic.feed.FeedType
import com.theathletic.followable.Followable
import com.theathletic.followable.legacyId
import com.theathletic.followables.SearchFollowablesWithScoresUseCase
import com.theathletic.followables.data.domain.Filter
import com.theathletic.scores.ui.ScoresFeedAnalytics
import com.theathletic.scores.ui.ScoresFeedAnalyticsHandler
import com.theathletic.scores.ui.usecases.LeagueIdToLeagueCodeUseCase
import com.theathletic.ui.ComposeViewModel
import com.theathletic.utility.Event
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SearchComposeViewModel @AutoKoin constructor(
    private val scoresSearchUseCase: SearchFollowablesWithScoresUseCase,
    private val scoresFeedAnalytics: ScoresFeedAnalyticsHandler,
    private val leagueIdToLeagueCodeUseCase: LeagueIdToLeagueCodeUseCase,
) : ViewModel(),
    ComposeViewModel,
    ScoresFeedAnalytics by scoresFeedAnalytics {
    private val _eventConsumer = MutableSharedFlow<Event>()
    val eventConsumer = _eventConsumer.asSharedFlow()

    private val followableFilter = Filter.NonFollowing(type = Filter.Type.ALL)
    private val _uiState = mutableStateOf(SearchComposeUiState())
    val uiState: State<SearchComposeUiState>
        get() = _uiState

    init {
        loadUserFollowing()
        setupSearch()
        scoresFeedAnalytics.trackSearchScreenView()
    }

    private fun setupSearch() {
        scoresSearchUseCase(followableFilter).onEach {
            if (_uiState.value.searchText.isNotEmpty()) {
                _uiState.value = _uiState.value.copy(results = it.toFollowingUi())
            }
        }.launchIn(viewModelScope)
    }

    private fun loadUserFollowing() {
        scoresSearchUseCase(Filter.Simple()).onEach {
            _uiState.value = _uiState.value.copy(following = it.toFollowingUi())
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: SearchComposeEvent) {
        when (event) {
            is SearchComposeEvent.OnSearchTextUpdate -> {
                trackSearchBarClearEvent(event.searchText)
                updateSearchTextResults(event.searchText)
            }
            is SearchComposeEvent.OnSearchResultClicked -> navigateToHub(event.followableId, event.index)
        }
    }

    private fun trackSearchBarClearEvent(searchText: String) {
        if (searchText.isEmpty()) {
            scoresFeedAnalytics.trackClickClearSearchInput(_uiState.value.searchText)
        }
    }

    private fun updateSearchTextResults(searchText: String) {
        _uiState.value = _uiState.value.copy(searchText = searchText)
        followableFilter.update { Filter.NonFollowing(searchText, it.type) }
    }

    private fun navigateToHub(followableId: Followable.Id, index: Int) {
        trackSearchResultNavigation(followableId)

        viewModelScope.launch {
            _eventConsumer.emit(
                com.theathletic.scores.ui.search.Event.NavigateToHub(FeedType.fromFollowable(followableId))
            )
        }
    }

    private fun trackSearchResultNavigation(followableId: Followable.Id) {
        viewModelScope.launch {
            if (followableId.type == Followable.Type.TEAM) {
                scoresFeedAnalytics.trackClickTeamAfterSearch(
                    search = uiState.value.searchText,
                    teamId = followableId.id
                )
            } else {
                scoresFeedAnalytics.trackClickLeagueAfterSearch(
                    search = uiState.value.searchText,
                    leagueId = followableId.legacyId?.let { leagueIdToLeagueCodeUseCase(it) }.toStringOrEmpty()
                )
            }
        }
    }

    fun trackSearchScreenCancel() {
        scoresFeedAnalytics.trackClickCancelSearchScreen(_uiState.value.searchText)
    }
}