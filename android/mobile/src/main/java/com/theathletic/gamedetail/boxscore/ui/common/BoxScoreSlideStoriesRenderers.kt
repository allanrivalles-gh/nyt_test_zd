package com.theathletic.gamedetail.boxscore.ui.common

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.SlideStoriesLaunchUiModel
import com.theathletic.boxscore.ui.modules.SlideStoriesLaunchModule
import com.theathletic.featureswitch.Features
import com.theathletic.feed.ui.FeedModuleV2

class BoxScoreSlideStoriesRenderers @AutoKoin constructor(
    val features: Features
) {
    // todo (Mark or Adil): Just dummy data until we get real data from BE
    fun createSlideStoriesModule(): FeedModuleV2? {
        if (features.isSlideStoriesEnabled.not()) return null
        return SlideStoriesLaunchModule(
            id = "DummySSId",
            uiModel = SlideStoriesLaunchUiModel(id = "DummySSId")
        )
    }
}