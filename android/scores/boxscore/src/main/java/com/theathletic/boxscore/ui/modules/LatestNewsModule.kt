package com.theathletic.boxscore.ui.modules

import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.BoxScoreUiModel
import com.theathletic.boxscore.ui.LatestNewsroomUi
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedModuleV2

data class LatestNewsModule(
    val latestNewsUiModel: BoxScoreUiModel.LatestNewsUiModel
) : FeedModuleV2 {

    // todo: Adil remove this interaction once, feed has finalised interactions
    interface Interaction {
        data class LatestNewsArticle(val permalink: String, val articleId: String) : FeedInteraction
        data class ArticleLongClick(
            val articleId: String,
            val isRead: Boolean,
            val isBookmarked: Boolean,
            val permalink: String
        ) : FeedInteraction

        data class PodcastPlayControl(val episodeId: String) : FeedInteraction
        data class PodcastClick(val episodeId: String) : FeedInteraction
        data class PodcastOptionsMenu(
            val podcastId: String,
            val episodeId: String,
            val permalink: String
        ) : FeedInteraction
    }

    override val moduleId: String = "LatestNewsModule:${latestNewsUiModel.id}"

    @Composable
    override fun Render() {
        LatestNewsroomUi(latestNewsUiModel = latestNewsUiModel)
    }
}