package com.theathletic.gamedetail.boxscore.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.feed.ui.FeedUi
import com.theathletic.gamedetail.boxscore.ui.basketball.stats.BoxScoreStatsRenderer
import com.theathletic.gamedetail.data.local.listOfSportsWithStatsTab
import com.theathletic.ui.Transformer

class BoxScoreStatsTransformer @AutoKoin constructor(
    private val statsRenderer: BoxScoreStatsRenderer
) : Transformer<BoxScoreStatsState, BoxScoreStatsContract.ViewState> {

    override fun transform(data: BoxScoreStatsState): BoxScoreStatsContract.ViewState {

        val feedUiModel = FeedUi(
            modules = if (listOfSportsWithStatsTab.contains(data.game?.sport)) {
                statsRenderer.renderFeedModels(data)
            } else {
                emptyList()
            }
        )

        return BoxScoreStatsContract.ViewState(
            showSpinner = data.loadingState.isFreshLoadingState,
            feedUiModel = feedUiModel
        )
    }
}