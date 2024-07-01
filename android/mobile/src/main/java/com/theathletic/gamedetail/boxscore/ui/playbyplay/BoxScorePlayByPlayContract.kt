package com.theathletic.gamedetail.boxscore.ui.playbyplay

import com.theathletic.feed.ui.FeedUi
import com.theathletic.presenter.Interactor

interface BoxScorePlayByPlayContract {

    interface Presenter : Interactor

    data class ViewState(
        val showSpinner: Boolean,
        val feedUiModel: FeedUi
    ) : com.theathletic.ui.ViewState
}