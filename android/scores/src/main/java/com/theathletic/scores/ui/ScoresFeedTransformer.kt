package com.theathletic.scores.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.scores.data.ScoresFeedUiMapper
import com.theathletic.ui.Transformer

class ScoresFeedTransformer @AutoKoin constructor(
    private val mapper: ScoresFeedUiMapper,
) : Transformer<ScoresFeedState, ScoresFeedContract.ViewState> {
    override fun transform(data: ScoresFeedState): ScoresFeedContract.ViewState {
        return ScoresFeedContract.ViewState(
            isLoadingFullFeed = data.isLoadingFullFeed,
            isLoadingDayFeed = data.isLoadingDayFeed,
            navigationItems = data.navigationItems,
            dayTabList = mapper.mapToDayTabBarUi(data.scoresFeedLocalModel),
            dayFeed = mapper.mapToDayFeedUi(data.scoresFeedLocalModel, data.selectedDayIndex),
            selectedDayIndex = data.selectedDayIndex,
        )
    }
}