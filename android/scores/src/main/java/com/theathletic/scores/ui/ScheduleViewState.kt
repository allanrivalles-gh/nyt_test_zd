package com.theathletic.scores.ui

import com.theathletic.ui.LoadingState

data class ScheduleViewState(
    val fullLoadingState: LoadingState = LoadingState.INITIAL_LOADING,
    val partialLoadingState: LoadingState = LoadingState.FINISHED,
    val showScheduleNoDataMessage: Boolean = false,
    val showErrorLoadingDataMessage: Boolean = false,
    val scheduleTabs: List<ScoresFeedUI.DayTabItem> = emptyList(),
    val scheduleFeed: List<ScoresFeedUI.FeedGroup> = emptyList(),
    val showFilters: Boolean = false,
    val showNoGamesMessage: Boolean = false,
    val scheduleFilters: List<ScoresFeedUI.ScheduleFilter>? = null,
    val selectedFilter: ScoresFeedUI.ScheduleFilter? = null,
    val selectedTab: Int = -1,
)

sealed interface ScheduleViewEvent {
    data class NavigateToGame(val gameId: String, val showDiscussion: Boolean) : ScheduleViewEvent
    data class NavigateToTicketingSite(val url: String) : ScheduleViewEvent
}