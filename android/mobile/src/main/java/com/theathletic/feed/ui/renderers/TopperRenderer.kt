package com.theathletic.feed.ui.renderers

import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.formatter.TimeAgoShortDateFormatter
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.feed.ui.models.CuratedItemType
import com.theathletic.feed.ui.models.FeedCuratedItemAnalyticsPayload
import com.theathletic.feed.ui.models.FeedCuratedTopperHero
import com.theathletic.headline.data.local.HeadlineEntity
import com.theathletic.liveblog.data.local.LiveBlogEntity
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.ui.binding.ParameterizedString

class TopperRenderer @AutoKoin constructor(
    private val userDataRepository: IUserDataRepository,
    private val timeAgoShortDateFormatter: TimeAgoShortDateFormatter
) {
    @Suppress("LongParameterList")
    fun renderTopper(
        entity: AthleticEntity,
        curatedTitle: String?,
        curatedSubTitle: String?,
        curatedImageUrl: String?,
        isTablet: Boolean,
        moduleIndex: Int,
        parentId: String
    ) = when (entity) {
        is ArticleEntity -> entity.toFeedTopperHero(
            curatedTitle,
            curatedSubTitle,
            curatedImageUrl,
            isTablet,
            moduleIndex,
            parentId
        )
        is LiveBlogEntity -> entity.toFeedTopperHero(
            curatedTitle,
            curatedSubTitle,
            curatedImageUrl,
            isTablet,
            moduleIndex,
            parentId
        )
        is HeadlineEntity -> entity.toFeedTopperHero(
            curatedTitle,
            curatedSubTitle,
            curatedImageUrl,
            isTablet,
            moduleIndex,
            parentId
        )
        else -> null
    }

    @Suppress("LongParameterList")
    private fun ArticleEntity.toFeedTopperHero(
        curatedTitle: String?,
        curatedSubTitle: String?,
        curatedImageUrl: String?,
        isTablet: Boolean,
        moduleIndex: Int,
        parentId: String
    ) = FeedCuratedTopperHero(
        id = articleId.toString(),
        title = curatedTitle ?: articleTitle.orEmpty(),
        subtitle = curatedSubTitle ?: excerpt.orEmpty(),
        imageUrl = curatedImageUrl ?: articleHeaderImg.orEmpty(),
        isLive = false,
        byline = ParameterizedString(authorName.orEmpty()),
        commentCount = commentsCount.toString(),
        showCommentCount = commentsCount > 0 && !commentsDisabled,
        isBookmarked = userDataRepository.isItemBookmarked(articleId),
        isRead = userDataRepository.isItemRead(articleId),
        type = getArticleType(),
        isTablet = isTablet,
        showSubtitle = !(curatedSubTitle ?: excerpt).isNullOrEmpty(),
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "article_id",
            moduleIndex = moduleIndex,
            container = "content_topper_hero",
            parentId = parentId
        ),
        impressionPayload = ImpressionPayload(
            element = "content_topper_hero",
            objectType = "article_id",
            objectId = articleId.toString(),
            pageOrder = moduleIndex,
            container = "content_topper_hero"
        )
    )

    @Suppress("LongParameterList")
    private fun LiveBlogEntity.toFeedTopperHero(
        curatedTitle: String?,
        curatedSubTitle: String?,
        curatedImageUrl: String?,
        isTablet: Boolean,
        moduleIndex: Int,
        parentId: String
    ) = FeedCuratedTopperHero(
        id = id,
        title = curatedTitle ?: this.title,
        subtitle = curatedSubTitle ?: description,
        imageUrl = curatedImageUrl ?: this.imageUrl.orEmpty(),
        isLive = isLive,
        byline = timeAgoShortDateFormatter.format(
            lastActivityAt,
            TimeAgoShortDateFormatter.Params(showUpdated = true)
        ),
        isTablet = isTablet,
        showSubtitle = (curatedSubTitle ?: description).isNotEmpty(),
        type = CuratedItemType.LIVE_BLOG,
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "blog_id",
            moduleIndex = moduleIndex,
            container = "content_topper_hero",
            parentId = parentId
        ),
        impressionPayload = ImpressionPayload(
            element = "content_topper_hero",
            objectType = "blog_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = "content_topper_hero"
        )
    )

    @Suppress("LongParameterList")
    private fun HeadlineEntity.toFeedTopperHero(
        curatedTitle: String?,
        curatedSubTitle: String?,
        curatedImageUrl: String?,
        isTablet: Boolean,
        moduleIndex: Int,
        parentId: String
    ) = FeedCuratedTopperHero(
        id = id,
        title = curatedTitle ?: headline,
        subtitle = curatedSubTitle ?: "",
        imageUrl = curatedImageUrl ?: this.imageUrls.firstOrNull().orEmpty(),
        isLive = false,
        byline = ParameterizedString(byline),
        commentCount = commentsCount.toString(),
        showCommentCount = commentsCount > 0 && !commentsDisabled,
        isTablet = isTablet,
        showSubtitle = !curatedTitle.isNullOrEmpty(),
        type = CuratedItemType.HEADLINE,
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "headline_id",
            moduleIndex = moduleIndex,
            container = "content_topper_hero",
            parentId = parentId
        ),
        impressionPayload = ImpressionPayload(
            element = "content_topper_hero",
            objectType = "headline_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = "content_topper_hero"
        )
    )
}