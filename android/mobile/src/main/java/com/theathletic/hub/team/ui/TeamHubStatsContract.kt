package com.theathletic.hub.team.ui

import com.theathletic.feed.ui.FeedUiV2

interface TeamHubStatsContract {

    interface Interaction : com.theathletic.presenter.Interactor

    data class ViewState(
        val showSpinner: Boolean,
        val showEmptyState: Boolean,
        val feedUiModel: FeedUiV2
    ) : com.theathletic.ui.ViewState
}