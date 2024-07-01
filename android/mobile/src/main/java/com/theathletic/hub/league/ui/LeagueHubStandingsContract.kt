package com.theathletic.hub.league.ui

import com.theathletic.boxscore.ui.standings.RelegationItem
import com.theathletic.feed.ui.FeedUiV2

interface LeagueHubStandingsContract {

    interface Interaction : com.theathletic.presenter.Interactor

    data class ViewState(
        val showSpinner: Boolean,
        val showEmptyState: Boolean,
        val feedUiModel: FeedUiV2,
        val relegationLegendItemsV2: List<RelegationItem>,
    ) : com.theathletic.ui.ViewState

    sealed class Event : com.theathletic.utility.Event() {
        data class NavigateToTeamHub(val legacyTeamId: Long) : Event()
    }
}