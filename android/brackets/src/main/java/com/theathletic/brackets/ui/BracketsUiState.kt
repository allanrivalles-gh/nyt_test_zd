package com.theathletic.brackets.ui

import com.theathletic.brackets.ui.components.HeaderRowUi
import com.theathletic.ui.LoadingState

data class BracketsUiState(
    val rounds: List<BracketsUi.Round> = emptyList(),
    val tabs: List<HeaderRowUi.BracketTab> = emptyList(),
    val currentTabIndex: Int? = null,
    val loadingState: LoadingState = LoadingState.INITIAL_LOADING
)

sealed class Event : com.theathletic.utility.Event() {
    data class NavigateToGameDetails(val gameId: String) : Event()
}

sealed interface BracketsEvent {
    data class OnRoundSelected(val selectedRoundIndex: Int) : BracketsEvent
    data class OnMatchClicked(val match: BracketsUi.Match) : BracketsEvent
    data class OnReplayMatchClicked(val matchId: String) : BracketsEvent
    object OnPullToRefresh : BracketsEvent
}