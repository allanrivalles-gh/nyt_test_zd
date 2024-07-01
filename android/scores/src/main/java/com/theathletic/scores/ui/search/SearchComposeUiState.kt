package com.theathletic.scores.ui.search

import com.theathletic.feed.FeedType
import com.theathletic.followable.Followable

data class SearchComposeUiState(
    val results: List<ResultItem> = emptyList(),
    val following: List<ResultItem> = emptyList(),
    val searchText: String = ""
)
sealed interface SearchComposeEvent {
    data class OnSearchTextUpdate(val searchText: String) : SearchComposeEvent
    data class OnSearchResultClicked(val followableId: Followable.Id, val index: Int) : SearchComposeEvent
}

sealed class Event : com.theathletic.utility.Event() {
    data class NavigateToHub(val feedType: FeedType) : Event()
}