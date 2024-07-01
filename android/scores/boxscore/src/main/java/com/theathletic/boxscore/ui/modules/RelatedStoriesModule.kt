package com.theathletic.boxscore.ui.modules

import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.RelatedStories
import com.theathletic.boxscore.ui.RelatedStoriesUi
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.LocalFeedInteractor

data class RelatedStoriesModule(
    val id: String,
    val articles: List<RelatedStoriesUi.Article>
) : FeedModuleV2 {

    override val moduleId: String = "RelatedStoriesModule:$id"

    @Composable
    override fun Render() {
        val interactor = LocalFeedInteractor.current
        RelatedStories(
            articles = articles,
            onArticleClick = {
                interactor.send(RelatedStoriesUi.Interaction.OnArticleClick(it))
            }
        )
    }
}