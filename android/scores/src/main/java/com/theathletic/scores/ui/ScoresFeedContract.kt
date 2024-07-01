package com.theathletic.scores.ui

import com.theathletic.feed.FeedType
import com.theathletic.followable.Followable
import com.theathletic.main.ui.NavigationItem

interface ScoresFeedContract {

    data class ViewState(
        val isLoadingFullFeed: Boolean,
        val isLoadingDayFeed: Boolean,
        val navigationItems: List<NavigationItem> = emptyList(),
        val dayTabList: List<ScoresFeedUI.DayTabItem> = emptyList(),
        val dayFeed: List<ScoresFeedUI.FeedGroup> = emptyList(),
        val selectedDayIndex: Int = 0,
    ) : com.theathletic.ui.ViewState

    interface Interaction {
        data class OnNavItemClicked(val followableId: Followable.Id, val index: Int) : Interaction
        data class OnTabClicked(val index: Int, val dayId: String) : Interaction
        data class OnLeagueSectionClicked(val leagueId: Long, val index: Int) : Interaction
        data class OnAllGamesClicked(val leagueId: Long, val index: Int) : Interaction
        data class OnGameClicked(val gameId: String) : Interaction
        data class OnDiscussionLinkClicked(val gameId: String, val leagueId: Long?) : Interaction
        object OnPullToRefresh : Interaction
    }

    sealed class Event : com.theathletic.utility.Event() {
        data class NavigateToHub(val feedType: FeedType) : Event()
        data class NavigateToGame(val gameId: String, val showDiscussion: Boolean = false) : Event()
    }
}