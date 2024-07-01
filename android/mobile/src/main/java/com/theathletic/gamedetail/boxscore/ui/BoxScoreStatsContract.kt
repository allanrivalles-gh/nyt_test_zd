package com.theathletic.gamedetail.boxscore.ui

import com.theathletic.feed.ui.FeedUi
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreCommonTeamSwitcherUiModel
import com.theathletic.presenter.Interactor

interface BoxScoreStatsContract {

    interface Presenter :
        Interactor,
        BoxScoreCommonTeamSwitcherUiModel.Interactor

    data class ViewState(
        val showSpinner: Boolean,
        val feedUiModel: FeedUi
    ) : com.theathletic.ui.ViewState
}