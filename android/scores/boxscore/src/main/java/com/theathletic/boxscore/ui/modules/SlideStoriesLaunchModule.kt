package com.theathletic.boxscore.ui.modules

import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.SlideStoriesLaunchUi
import com.theathletic.boxscore.ui.SlideStoriesLaunchUiModel
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.LocalFeedInteractor

data class SlideStoriesLaunchModule(
    val id: String,
    val uiModel: SlideStoriesLaunchUiModel
) : FeedModuleV2 {
    override val moduleId: String = "SlideStoriesModule:$id"

    interface Interaction {
        data class SlideStoryClick(val id: String) : FeedInteraction
    }

    @Composable
    override fun Render() {
        val interactor = LocalFeedInteractor.current

        SlideStoriesLaunchUi(
            uiModel = uiModel,
            onClick = { id -> interactor.send(Interaction.SlideStoryClick(id)) }
        )
    }
}