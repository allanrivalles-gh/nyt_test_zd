package com.theathletic.scores.standings.ui

import com.theathletic.boxscore.ui.standings.RelegationItem
import com.theathletic.feed.ui.FeedUiV2
import com.theathletic.presenter.Interactor
import com.theathletic.ui.binding.ParameterizedString

interface ScoresStandingsContract {

    interface Presenter :
        Interactor,
        ScoresStandingsRowUiModel.Interactor,
        ScoresStandingsGroupTitleUiModel.Interactor {
        fun onBackPress()
    }

    data class ViewState(
        val showSpinner: Boolean,
        val title: String,
        val logoUrl: String,
        val seasonLabel: ParameterizedString?,
        val groupsTitleList: List<ScoresStandingsGroupTitleUiModel>,
        val standingsGroupList: List<ScoresStandingsGroupUiModel>,
        val relegationLegendItems: List<ScoresStandingsRelegationLegendUiModel>,
        val autoNavigationIndex: Int,
        val feedUiModel: FeedUiV2,
        val relegationLegendItemsV2: List<RelegationItem>,
        val showEmptyState: Boolean,
    ) : com.theathletic.ui.ViewState
}