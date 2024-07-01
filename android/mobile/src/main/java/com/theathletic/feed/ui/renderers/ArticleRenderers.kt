package com.theathletic.feed.ui.renderers

import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.article.data.local.InsiderEntity
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.feed.ui.models.FeedCarousel
import com.theathletic.feed.ui.models.FeedInsiderAnalyticsPayload
import com.theathletic.feed.ui.models.FeedInsiderItem
import com.theathletic.utility.datetime.DateUtilityImpl

class ArticleRenderers @AutoKoin constructor() {

    fun renderInsidersCarousel(
        entities: List<List<AthleticEntity>>,
        moduleIndex: Int
    ): FeedCarousel {
        val pairs = entities.mapNotNull {
            val articleEntity = it.getOrNull(0) as? ArticleEntity ?: return@mapNotNull null
            val insiderEntity = it.getOrNull(1) as? InsiderEntity ?: return@mapNotNull null

            articleEntity to insiderEntity
        }

        return FeedCarousel(
            carouselItemModels = pairs.mapIndexed { index, pair ->
                createInsiderPair(pair.first, pair.second, moduleIndex, index)
            }
        )
    }

    private fun createInsiderPair(
        article: ArticleEntity,
        insider: InsiderEntity,
        moduleIndex: Int,
        carouselIndex: Int
    ) = FeedInsiderItem(
        authorId = insider.id,
        articleId = article.articleId,
        name = "${insider.firstName}\n${insider.lastName}",
        authorImageUrl = insider.insiderImageUrl,
        role = insider.role,
        articleTitle = article.articleTitle.orEmpty(),
        formattedDate = DateUtilityImpl.formatTimeAgoFromGMT(article.articlePublishDate, short = true),
        commentCount = "${article.commentsCount}",
        analyticsInfo = FeedInsiderAnalyticsPayload(
            authorId = insider.id,
            moduleIndex = moduleIndex,
            hIndex = carouselIndex
        ),
        impressionPayload = ImpressionPayload(
            element = "insider",
            objectType = "author_id",
            objectId = insider.id,
            pageOrder = moduleIndex,
            container = "insider",
            hIndex = carouselIndex.toLong()
        )
    )
}