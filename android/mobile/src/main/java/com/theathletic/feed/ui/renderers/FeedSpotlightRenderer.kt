package com.theathletic.feed.ui.renderers

import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.article.data.ArticleRepository
import com.theathletic.article.data.local.InsiderEntity
import com.theathletic.datetime.formatter.DisplayFormat
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.feed.ui.models.FeedArticleAnalyticsPayload
import com.theathletic.feed.ui.models.FeedSpotlightModel
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.ui.widgets.AuthorImageStackModel
import com.theathletic.utility.datetime.DateUtilityImpl
import com.theathletic.utility.formatters.CommentsCountNumberFormat

class FeedSpotlightRenderer @AutoKoin constructor(
    private val articleRepository: ArticleRepository,
    private val userDataRepository: IUserDataRepository,
) {

    @Suppress("LongParameterList")
    fun renderSpotlight(
        articleEntity: ArticleEntity,
        authorEntities: List<InsiderEntity>,
        title: String,
        description: String,
        moduleIndex: Int,
        index: Int
    ) = FeedSpotlightModel(
        id = articleEntity.articleId,
        imageUrl = articleEntity.articleHeaderImg.orEmpty(),
        title = title,
        excerpt = description,
        commentNumber = CommentsCountNumberFormat.format(articleEntity.commentsCount),
        showComment = articleEntity.commentsCount > 0,
        authorsNames = articleEntity.authorName.orEmpty(),
        avatarModel = AuthorImageStackModel(
            authorEntities.firstOrNull()?.imageUrl.orEmpty(),
            authorEntities.getOrNull(1)?.imageUrl.orEmpty(),
            authorEntities.getOrNull(2)?.imageUrl.orEmpty(),
            displayImageCount = if (authorEntities.size > 3) 0 else authorEntities.size
        ),
        isBookmarked = articleRepository.isArticleBookmarked(articleEntity.articleId),
        isRead = userDataRepository.isItemRead(articleEntity.articleId),
        date = DateUtilityImpl.formatGMTDateString(
            articleEntity.scheduledDate ?: articleEntity.articlePublishDate.orEmpty(),
            DisplayFormat.MONTH_DATE_SHORT
        ),
        analyticsPayload = FeedArticleAnalyticsPayload(
            moduleIndex = moduleIndex,
            container = "a1",
            vIndex = 0,
            hIndex = index
        ),
        impressionPayload = ImpressionPayload(
            element = "a1",
            container = "a1",
            objectType = "article_id",
            objectId = articleEntity.id,
            vIndex = 0,
            hIndex = index.toLong(),
            pageOrder = moduleIndex
        )
    )
}