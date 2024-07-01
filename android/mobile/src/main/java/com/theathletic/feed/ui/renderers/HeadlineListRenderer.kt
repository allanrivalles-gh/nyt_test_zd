package com.theathletic.feed.ui.renderers

import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.feed.ui.models.CuratedItemType
import com.theathletic.feed.ui.models.FeedCuratedItemAnalyticsPayload
import com.theathletic.feed.ui.models.FeedHeadlineListItem
import com.theathletic.headline.data.local.HeadlineEntity
import com.theathletic.liveblog.data.local.LiveBlogEntity

class HeadlineListRenderer @AutoKoin constructor() {
    fun renderHeadlineListItem(
        entity: AthleticEntity,
        index: Int,
        moduleIndex: Int,
        parentId: String
    ) = when (entity) {
        is HeadlineEntity -> entity.toHeadlineListItem(index, moduleIndex, parentId)
        is LiveBlogEntity -> entity.toHeadlineListItem(index, moduleIndex, parentId)
        is ArticleEntity -> entity.toHeadlineListItem(index, moduleIndex, parentId)
        else -> null
    }

    private fun HeadlineEntity.toHeadlineListItem(
        index: Int,
        moduleIndex: Int,
        parentId: String
    ) = FeedHeadlineListItem(
        id,
        headline,
        CuratedItemType.HEADLINE,
        FeedCuratedItemAnalyticsPayload(
            objectType = "headline_id",
            moduleIndex = moduleIndex,
            container = "headline_multiple",
            vIndex = index,
            parentId = parentId
        ),
        ImpressionPayload(
            element = "headline_multiple",
            container = "headline_multiple",
            objectType = "headline_id",
            objectId = id,
            pageOrder = moduleIndex,
            vIndex = index.toLong()
        )
    )

    private fun LiveBlogEntity.toHeadlineListItem(
        index: Int,
        moduleIndex: Int,
        parentId: String
    ) = FeedHeadlineListItem(
        id,
        title,
        CuratedItemType.LIVE_BLOG,
        FeedCuratedItemAnalyticsPayload(
            objectType = "blog_id",
            moduleIndex = moduleIndex,
            container = "headline_multiple",
            vIndex = index,
            parentId = parentId
        ),
        ImpressionPayload(
            element = "headline_multiple",
            container = "headline_multiple",
            objectType = "blog_id",
            objectId = id,
            pageOrder = moduleIndex,
            vIndex = index.toLong()
        )
    )

    private fun ArticleEntity.toHeadlineListItem(
        index: Int,
        moduleIndex: Int,
        parentId: String
    ) = FeedHeadlineListItem(
        id,
        articleTitle.orEmpty(),
        getArticleType(),
        FeedCuratedItemAnalyticsPayload(
            objectType = "article_id",
            moduleIndex = moduleIndex,
            container = "headline_multiple",
            vIndex = index,
            parentId = parentId
        ),
        ImpressionPayload(
            element = "headline_multiple",
            container = "headline_multiple",
            objectType = "article_id",
            objectId = id,
            pageOrder = moduleIndex,
            vIndex = index.toLong()
        )
    )
}