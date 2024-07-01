package com.theathletic.feed.ui.renderers

import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.feed.ui.models.FeedArticleAnalyticsPayload
import com.theathletic.feed.ui.models.FeedMostPopularArticle
import com.theathletic.feed.ui.models.FeedMostPopularCarousel

class FeedItemRenderers @AutoKoin constructor() {

    fun mostPopularArticles(
        articles: List<ArticleEntity>,
        moduleIndex: Int,
        isTablet: Boolean
    ) = articles.mapIndexed { index, articleEntity ->
        FeedMostPopularArticle(
            id = articleEntity.id.toLong(),
            number = (index + 1).toString(),
            tag = articleEntity.primaryTag ?: "",
            title = articleEntity.articleTitle ?: "",
            imageUrl = articleEntity.articleHeaderImg ?: "",
            isTablet = isTablet,
            isInLastColumn = true,
            isTopItem = index == 0,
            analyticsPayload = FeedArticleAnalyticsPayload(
                moduleIndex = moduleIndex,
                container = "popular",
                vIndex = index,
                hIndex = 0
            ),
            impressionPayload = ImpressionPayload(
                element = "popular",
                objectType = "article_id",
                objectId = articleEntity.id,
                pageOrder = moduleIndex,
                container = "popular",
                vIndex = index.toLong(),
                hIndex = 0L
            )
        )
    }

    fun mostPopularArticlesCarousel(
        articles: List<ArticleEntity>,
        moduleIndex: Int,
        isTablet: Boolean
    ): FeedMostPopularCarousel {
        val itemsPerColumn = articles.size / 2 + articles.size % 2
        val mostPopularArticles = articles.mapIndexed { index, articleEntity ->
            val vIndex = index % itemsPerColumn
            val hIndex = index / itemsPerColumn

            FeedMostPopularArticle(
                id = articleEntity.id.toLong(),
                number = (index + 1).toString(),
                tag = articleEntity.primaryTag ?: "",
                title = articleEntity.articleTitle ?: "",
                imageUrl = articleEntity.articleHeaderImg ?: "",
                isTablet = isTablet,
                isInLastColumn = articles.size <= itemsPerColumn || index >= itemsPerColumn,
                isTopItem = index % itemsPerColumn == 0,
                analyticsPayload = FeedArticleAnalyticsPayload(
                    moduleIndex = moduleIndex,
                    container = "popular",
                    vIndex = vIndex,
                    hIndex = hIndex
                ),
                impressionPayload = ImpressionPayload(
                    element = "popular",
                    objectType = "article_id",
                    objectId = articleEntity.id,
                    pageOrder = moduleIndex,
                    container = "popular",
                    vIndex = vIndex.toLong(),
                    hIndex = hIndex.toLong()
                )
            )
        }

        return FeedMostPopularCarousel(
            moduleIndex,
            isTablet,
            if (!isTablet) mostPopularArticles else mostPopularArticles.sortedWith(
                compareBy(
                    { it.analyticsPayload.vIndex },
                    { it.analyticsPayload.hIndex }
                )
            )
        )
    }
}