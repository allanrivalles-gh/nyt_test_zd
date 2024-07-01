package com.theathletic.feed.ui.renderers

import androidx.annotation.DimenRes
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.formatter.TimeAgoShortDateFormatter
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.feed.ui.models.CuratedItemType
import com.theathletic.feed.ui.models.FeedCuratedGroupedItem
import com.theathletic.feed.ui.models.FeedCuratedItemAnalyticsPayload
import com.theathletic.feed.ui.models.FeedTopperGroupedItem
import com.theathletic.headline.data.local.HeadlineEntity
import com.theathletic.liveblog.data.local.LiveBlogEntity
import com.theathletic.podcast.data.local.PodcastEpisodeEntity
import com.theathletic.podcast.state.PodcastPlayerState
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.ui.binding.ParameterizedString
import com.theathletic.ui.binding.asParameterized
import com.theathletic.utility.PodcastPlayerStateUtility

class ThreeFourContentRenderer @AutoKoin constructor(
    private val userDataRepository: IUserDataRepository,
    private val timeAgoShortDateFormatter: TimeAgoShortDateFormatter,
    private val podcastPlayerStateUtility: PodcastPlayerStateUtility
) {

    @Suppress("LongParameterList")
    fun toFeedCuratedGroupedItem(
        entity: LiveBlogEntity,
        curatedTitle: String?,
        curatedImageUrl: String?,
        moduleIndex: Int,
        @DimenRes verticalPadding: Int,
        vIndex: Int,
        hIndex: Int,
        parentId: String,
        analyticsContainer: String,
        isLastItem: Boolean
    ) = FeedCuratedGroupedItem(
        id = entity.id,
        title = curatedTitle ?: entity.title,
        imageUrl = curatedImageUrl ?: entity.imageUrl.orEmpty(),
        isLive = entity.isLive,
        byline = timeAgoShortDateFormatter.format(
            entity.lastActivityAt,
            TimeAgoShortDateFormatter.Params(showUpdated = true)
        ),
        type = CuratedItemType.LIVE_BLOG,
        verticalPadding = verticalPadding,
        isRead = false,
        showDivider = !isLastItem,
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "blog_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            parentId = parentId,
            vIndex = vIndex,
            hIndex = hIndex
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "blog_id",
            objectId = entity.id,
            pageOrder = moduleIndex,
            container = analyticsContainer
        )
    )

    @Suppress("LongParameterList")
    fun toFeedCuratedGroupedItem(
        entity: HeadlineEntity,
        curatedTitle: String?,
        curatedImageUrl: String?,
        moduleIndex: Int,
        @DimenRes verticalPadding: Int,
        vIndex: Int,
        hIndex: Int,
        parentId: String,
        analyticsContainer: String,
        isLastItem: Boolean
    ) = FeedCuratedGroupedItem(
        id = entity.id,
        title = curatedTitle ?: entity.headline,
        imageUrl = curatedImageUrl ?: entity.imageUrls.firstOrNull().orEmpty(),
        isLive = false,
        byline = ParameterizedString(entity.byline),
        commentCount = entity.commentsCount.toString(),
        showCommentCount = entity.commentsCount > 0 && !entity.commentsDisabled,
        type = CuratedItemType.HEADLINE,
        verticalPadding = verticalPadding,
        isRead = false,
        showDivider = !isLastItem,
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "headline_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            parentId = parentId,
            vIndex = vIndex,
            hIndex = hIndex
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "headline_id",
            objectId = entity.id,
            pageOrder = moduleIndex,
            container = analyticsContainer
        )
    )

    @Suppress("LongParameterList")
    private fun ArticleEntity.toFeedCuratedGroupItem(
        curatedTitle: String?,
        curatedImageUrl: String?,
        moduleIndex: Int,
        @DimenRes verticalPadding: Int,
        vIndex: Int,
        hIndex: Int,
        parentId: String,
        analyticsContainer: String,
        isLastItem: Boolean
    ) = FeedCuratedGroupedItem(
        id = articleId.toString(),
        title = curatedTitle ?: articleTitle.orEmpty(),
        imageUrl = curatedImageUrl ?: articleHeaderImg.orEmpty(),
        isLive = false,
        byline = ParameterizedString(authorName.orEmpty()),
        commentCount = commentsCount.toString(),
        showCommentCount = commentsCount > 0 && !commentsDisabled,
        isBookmarked = userDataRepository.isItemBookmarked(articleId),
        type = getArticleType(),
        verticalPadding = verticalPadding,
        isRead = userDataRepository.isItemRead(articleId),
        showDivider = !isLastItem,
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "article_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            parentId = parentId,
            vIndex = vIndex,
            hIndex = hIndex
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "article_id",
            objectId = articleId.toString(),
            pageOrder = moduleIndex,
            container = analyticsContainer
        )
    )

    @Suppress("LongParameterList")
    fun toFeedGroupedItemV2(
        entity: AthleticEntity,
        curatedTitle: String?,
        curatedImageUrl: String?,
        moduleIndex: Int,
        vIndex: Int,
        hIndex: Int,
        parentId: String,
        analyticsContainer: String,
        isLastItem: Boolean
    ): FeedCuratedGroupedItem? {
        return when (entity) {
            is ArticleEntity -> entity.toFeedCuratedGroupItem(
                curatedTitle = curatedTitle,
                curatedImageUrl = curatedImageUrl,
                moduleIndex = moduleIndex,
                vIndex = vIndex,
                hIndex = hIndex,
                parentId = parentId,
                analyticsContainer = analyticsContainer,
                isLastItem = isLastItem,
                verticalPadding = 0
            )
            is LiveBlogEntity -> toFeedCuratedGroupedItem(
                entity = entity,
                curatedTitle = curatedTitle,
                curatedImageUrl = curatedImageUrl,
                moduleIndex = moduleIndex,
                vIndex = vIndex,
                hIndex = hIndex,
                parentId = parentId,
                analyticsContainer = analyticsContainer,
                isLastItem = isLastItem,
                verticalPadding = 0
            )
            is HeadlineEntity -> toFeedCuratedGroupedItem(
                entity = entity,
                curatedTitle = curatedTitle,
                curatedImageUrl = curatedImageUrl,
                moduleIndex = moduleIndex,
                vIndex = vIndex,
                hIndex = hIndex,
                parentId = parentId,
                analyticsContainer = analyticsContainer,
                isLastItem = isLastItem,
                verticalPadding = 0
            )
            else -> null
        }
    }

    @Suppress("LongParameterList")
    fun toFeedTopperGroupedItem(
        entity: AthleticEntity,
        curatedTitle: String?,
        curatedImageUrl: String?,
        moduleIndex: Int,
        index: Int,
        parentId: String,
        analyticsContainer: String,
        isLastItem: Boolean,
        podcastPlayerState: PodcastPlayerState
    ): FeedTopperGroupedItem? {
        return when (entity) {
            is ArticleEntity -> entity.toFeedTopperGroupedItem(
                title = curatedTitle ?: entity.articleTitle.orEmpty(),
                imageUrl = curatedImageUrl ?: entity.articleHeaderImg.orEmpty(),
                moduleIndex = moduleIndex,
                index = index,
                parentId = parentId,
                analyticsContainer = analyticsContainer,
                isLastItem = isLastItem
            )
            is LiveBlogEntity -> entity.toFeedTopperGroupedItem(
                title = curatedTitle ?: entity.title,
                imageUrl = curatedImageUrl ?: entity.imageUrl.orEmpty(),
                moduleIndex = moduleIndex,
                index = index,
                parentId = parentId,
                analyticsContainer = analyticsContainer,
                isLastItem = isLastItem
            )
            is HeadlineEntity -> entity.toFeedTopperGroupedItem(
                title = curatedTitle ?: entity.headline,
                imageUrl = curatedImageUrl ?: entity.imageUrls.firstOrNull().orEmpty(),
                moduleIndex = moduleIndex,
                index = index,
                parentId = parentId,
                analyticsContainer = analyticsContainer,
                isLastItem = isLastItem
            )
            is PodcastEpisodeEntity -> entity.toFeedTopperGroupedItem(
                title = curatedTitle ?: entity.title,
                imageUrl = curatedImageUrl ?: entity.imageUrl,
                moduleIndex = moduleIndex,
                index = index,
                parentId = parentId,
                analyticsContainer = analyticsContainer,
                isLastItem = isLastItem,
                podcastPlayerState = podcastPlayerState
            )
            else -> null
        }
    }

    @Suppress("LongParameterList")
    private fun ArticleEntity.toFeedTopperGroupedItem(
        title: String,
        imageUrl: String,
        moduleIndex: Int,
        index: Int,
        parentId: String,
        analyticsContainer: String,
        isLastItem: Boolean
    ) = FeedTopperGroupedItem(
        id = articleId.toString(),
        title = title,
        imageUrl = imageUrl,
        isLive = false,
        byline = ParameterizedString(authorName.orEmpty()),
        commentCount = commentsCount.toString(),
        showCommentCount = commentsCount > 0 && !commentsDisabled,
        isBookmarked = userDataRepository.isItemBookmarked(articleId),
        isRead = userDataRepository.isItemRead(articleId),
        showDivider = !isLastItem,
        type = getArticleType(),
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "article_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            parentId = parentId,
            vIndex = index
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "article_id",
            objectId = articleId.toString(),
            pageOrder = moduleIndex,
            container = analyticsContainer
        )
    )

    @Suppress("LongParameterList")
    private fun LiveBlogEntity.toFeedTopperGroupedItem(
        title: String,
        imageUrl: String,
        moduleIndex: Int,
        index: Int,
        parentId: String,
        analyticsContainer: String,
        isLastItem: Boolean
    ) = FeedTopperGroupedItem(
        id = id,
        title = title,
        imageUrl = imageUrl,
        isLive = isLive,
        commentCount = "",
        showCommentCount = false,
        isBookmarked = false,
        showDivider = !isLastItem,
        byline = timeAgoShortDateFormatter.format(
            lastActivityAt,
            TimeAgoShortDateFormatter.Params(showUpdated = true)
        ),
        type = CuratedItemType.LIVE_BLOG,
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "blog_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            parentId = parentId,
            vIndex = index
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "blog_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = analyticsContainer
        )
    )

    @Suppress("LongParameterList")
    private fun HeadlineEntity.toFeedTopperGroupedItem(
        title: String,
        imageUrl: String,
        moduleIndex: Int,
        index: Int,
        parentId: String,
        analyticsContainer: String,
        isLastItem: Boolean
    ) = FeedTopperGroupedItem(
        id = id,
        title = title,
        imageUrl = imageUrl,
        isLive = false,
        commentCount = commentsCount.toString(),
        showCommentCount = commentsCount > 0 && !commentsDisabled,
        isBookmarked = false,
        showDivider = !isLastItem,
        byline = byline.asParameterized(),
        type = CuratedItemType.HEADLINE,
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "headline_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            parentId = parentId,
            vIndex = index
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "headline_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = analyticsContainer
        )
    )

    @Suppress("LongParameterList")
    private fun PodcastEpisodeEntity.toFeedTopperGroupedItem(
        title: String,
        imageUrl: String,
        moduleIndex: Int,
        index: Int,
        parentId: String,
        analyticsContainer: String,
        isLastItem: Boolean,
        podcastPlayerState: PodcastPlayerState
    ) = FeedTopperGroupedItem(
        id = id,
        title = title,
        imageUrl = imageUrl,
        isLive = false,
        commentCount = "",
        showCommentCount = false,
        isBookmarked = false,
        showDivider = !isLastItem,
        byline = "".asParameterized(),
        type = CuratedItemType.PODCAST,
        podcastPlayerState = podcastPlayerStateUtility.getPlayerState(this, podcastPlayerState),
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "podcast_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            parentId = parentId,
            vIndex = index
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "podcast_episode_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = analyticsContainer
        )
    )
}