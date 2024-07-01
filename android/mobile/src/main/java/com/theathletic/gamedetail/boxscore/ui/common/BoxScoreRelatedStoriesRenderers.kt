package com.theathletic.gamedetail.boxscore.ui.common

import com.theathletic.R
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.RelatedStoriesUi
import com.theathletic.boxscore.ui.modules.RelatedStoriesModule
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.data.local.GameArticlesLocalModel
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.ResourceString.StringWrapper
import java.util.concurrent.atomic.AtomicInteger

class BoxScoreRelatedStoriesRenderers @AutoKoin constructor() {

    fun createRelatedStoriesModule(
        game: GameDetailLocalModel,
        gameArticles: List<GameArticlesLocalModel.GameArticle>,
        orderOnPage: Int
    ): FeedModuleV2 = RelatedStoriesModule(
        id = game.id,
        articles = gameArticles.toArticles(game, orderOnPage)
    )

    @Deprecated("Use createRelatedStoriesModule(game, gameArticles, orderOnPage)")
    fun createRelatedStoriesModuleLegacy(
        game: GameDetailLocalModel,
        gameArticles: List<GameArticlesLocalModel.GameArticle>?,
        pageOrder: AtomicInteger
    ): FeedModuleV2? {
        if (gameArticles.isNullOrEmpty()) return null
        pageOrder.getAndIncrement()
        return RelatedStoriesModule(
            id = game.id,
            articles = gameArticles.toArticles(game, pageOrder.get())
        )
    }

    private fun List<GameArticlesLocalModel.GameArticle>.toArticles(
        game: GameDetailLocalModel,
        pageOrder: Int
    ) = mapIndexed { index, article ->
        RelatedStoriesUi.Article(
            id = article.id,
            gameId = game.id,
            title = article.title,
            authors = formatAuthors(article.authors),
            commentCount = article.commentCount.toString(),
            showCommentCount = article.commentCount > 0,
            imageUrl = article.imageUrl.orEmpty(),
            analyticsPayload = RelatedStoriesUi.RelatedStoriesAnalyticsPayload(
                articleId = article.id,
                articlePosition = index + 1,
                leagueId = game.league.id,
                pageOrder = pageOrder,
                gameId = game.id,
            ),
            impressionPayload = ImpressionPayload(
                objectType = "article_id",
                objectId = article.id,
                element = "related_stories",
                container = "related_stories",
                pageOrder = pageOrder,
                vIndex = index.toLong()
            )
        )
    }

    private fun formatAuthors(authors: List<GameArticlesLocalModel.GameArticleAuthor>): ResourceString {
        val names = authors.sortedBy { it.displayOrder }.map { it.name }
        return when (names.size) {
            0 -> StringWithParams(R.string.box_score_related_stories_the_athletic_staff_author)
            1 -> StringWrapper(names[0])
            2 -> StringWithParams(R.string.box_score_related_stories_authors, names[0], names[1])
            else -> StringWithParams(
                R.string.box_score_related_stories_authors,
                names.dropLast(1).joinToString(", "),
                names.last()
            )
        }
    }
}