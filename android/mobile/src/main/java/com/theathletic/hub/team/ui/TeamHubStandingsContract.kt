package com.theathletic.hub.team.ui

import com.theathletic.boxscore.ui.standings.RelegationItem
import com.theathletic.feed.ui.FeedUiV2

interface TeamHubStandingsContract {

    interface Interaction : com.theathletic.presenter.Interactor

    data class ViewState(
        val showSpinner: Boolean,
        val showEmptyState: Boolean,
        val feedUiModel: FeedUiV2,
        val initialIndex: Int,
        val relegationLegendItemsV2: List<RelegationItem>,
    ) : com.theathletic.ui.ViewState
}