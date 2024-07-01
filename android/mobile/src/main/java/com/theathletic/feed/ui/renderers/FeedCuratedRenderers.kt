package com.theathletic.feed.ui.renderers

import androidx.annotation.DimenRes
import com.theathletic.R
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.formatter.TimeAgoShortDateFormatter
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.authentication.UserData
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.entity.main.FeedItemEntryType
import com.theathletic.feed.ui.models.CuratedItemType
import com.theathletic.feed.ui.models.FeedCuratedCarouselItem
import com.theathletic.feed.ui.models.FeedCuratedItemAnalyticsPayload
import com.theathletic.feed.ui.models.FeedHeroCarousel
import com.theathletic.feed.ui.models.FeedHeroItem
import com.theathletic.feed.ui.models.FeedHeroTabletItem
import com.theathletic.feed.ui.models.FeedLeftImageItem
import com.theathletic.feed.ui.models.FeedSideBySideCarousel
import com.theathletic.feed.ui.models.FeedSideBySideItem
import com.theathletic.feed.ui.models.FeedSideBySideLeftItemCarousel
import com.theathletic.headline.data.local.HeadlineEntity
import com.theathletic.liveblog.data.local.LiveBlogEntity
import com.theathletic.podcast.data.local.PodcastEpisodeEntity
import com.theathletic.podcast.state.PodcastPlayerState
import com.theathletic.ui.CarouselUiModel
import com.theathletic.ui.UiModel
import com.theathletic.utility.PodcastPlayerStateUtility
import com.theathletic.utility.RecyclerLayout
import com.theathletic.utility.formatters.CommentsCountNumberFormat

@Suppress("LargeClass")
class FeedCuratedRenderers @AutoKoin constructor(
    private val podcastPlayerStateUtility: PodcastPlayerStateUtility,
    private val timeAgoShortDateFormatter: TimeAgoShortDateFormatter
) {

    @Suppress("LongParameterList")
    fun renderTwoRowCarousel(
        userData: UserData?,
        entities: List<AthleticEntity>,
        curatedTitles: Map<AthleticEntity.Id, String?>,
        curatedImageUrls: Map<AthleticEntity.Id, String?>,
        podcastPlayerState: PodcastPlayerState,
        moduleIndex: Int,
        analyticsContainer: String,
        vIndex: Int,
        parentId: String
    ): FeedHeroCarousel {
        val carouselModels = entities.mapIndexedNotNull { index, entity ->
            renderLeftImageItem(
                userData = userData,
                entity = entity,
                curatedTitles = curatedTitles,
                curatedImageUrls = curatedImageUrls,
                podcastPlayerState = podcastPlayerState,
                moduleIndex = moduleIndex,
                analyticsContainer = analyticsContainer,
                vIndex = vIndex + index % 2,
                hIndex = index / 2,
                parentId = parentId,
                isSquareImage = true,
                showListDivider = index % 2 == 0 && index != entities.size - 1
            )
        }

        return FeedHeroCarousel(
            id = moduleIndex,
            carouselItemModels = carouselModels,
            recyclerLayout = RecyclerLayout.TWO_ROW_CAROUSEL
        )
    }

    @Suppress("LongParameterList")
    fun renderThreeRowCarousel(
        userData: UserData?,
        entities: List<AthleticEntity>,
        curatedTitles: Map<AthleticEntity.Id, String?>,
        curatedImageUrls: Map<AthleticEntity.Id, String?>,
        podcastPlayerState: PodcastPlayerState,
        moduleIndex: Int,
        analyticsContainer: String,
        vIndex: Int,
        parentId: String
    ): FeedHeroCarousel {
        val carouselModels = entities.mapIndexedNotNull { index, entity ->
            renderLeftImageItem(
                userData = userData,
                entity = entity,
                curatedTitles = curatedTitles,
                curatedImageUrls = curatedImageUrls,
                podcastPlayerState = podcastPlayerState,
                moduleIndex = moduleIndex,
                analyticsContainer = analyticsContainer,
                vIndex = vIndex + index % 3,
                hIndex = index / 3,
                parentId = parentId,
                isSquareImage = true,
                showListDivider = index % 3 != 2 && index != entities.size - 1
            )
        }

        return FeedHeroCarousel(
            id = moduleIndex,
            carouselItemModels = carouselModels,
            recyclerLayout = RecyclerLayout.THREE_ROW_CAROUSEL
        )
    }

    @Suppress("LongParameterList")
    fun renderHeroItem(
        userData: UserData?,
        entity: AthleticEntity,
        curatedTitles: Map<AthleticEntity.Id, String?>,
        curatedDescriptions: Map<AthleticEntity.Id, String?>,
        curatedImageUrls: Map<AthleticEntity.Id, String?>,
        podcastPlayerState: PodcastPlayerState,
        moduleIndex: Int,
        analyticsContainer: String,
        parentId: String,
        isTablet: Boolean
    ): UiModel? {
        return if (isTablet) {
            renderHeroTabletItem(
                userData,
                entity,
                curatedTitles,
                curatedDescriptions,
                curatedImageUrls,
                podcastPlayerState,
                moduleIndex,
                analyticsContainer,
                parentId
            )
        } else {
            renderHeroPhoneItem(
                userData,
                entity,
                curatedTitles,
                curatedDescriptions,
                curatedImageUrls,
                podcastPlayerState,
                moduleIndex,
                analyticsContainer,
                parentId
            )
        }
    }

    @Suppress("LongParameterList")
    fun renderHeroPhoneItem(
        userData: UserData?,
        entity: AthleticEntity,
        curatedTitles: Map<AthleticEntity.Id, String?>,
        curatedDescriptions: Map<AthleticEntity.Id, String?>,
        curatedImageUrls: Map<AthleticEntity.Id, String?>,
        podcastPlayerState: PodcastPlayerState,
        moduleIndex: Int,
        analyticsContainer: String,
        parentId: String,
        vIndex: Int = 0,
        hIndex: Int = -1
    ): FeedHeroItem? {
        return when (entity) {
            is ArticleEntity -> entity.toHeroItem(
                userData = userData,
                title = curatedTitles[entity.entityId] ?: entity.articleTitle.orEmpty(),
                description = curatedDescriptions[entity.entityId] ?: entity.excerpt.orEmpty(),
                imageUrl = curatedImageUrls[entity.entityId] ?: entity.articleHeaderImg.orEmpty(),
                moduleIndex = moduleIndex,
                analyticsContainer = analyticsContainer,
                parentId = parentId,
                vIndex = vIndex,
                hIndex = hIndex
            )
            is PodcastEpisodeEntity -> entity.toHeroItem(
                curatedTitles[entity.entityId] ?: entity.title,
                curatedDescriptions[entity.entityId] ?: entity.description,
                curatedImageUrls[entity.entityId] ?: entity.imageUrl,
                podcastPlayerState,
                moduleIndex,
                analyticsContainer,
                parentId,
                vIndex,
                hIndex
            )
            is HeadlineEntity -> entity.toHeroItem(
                curatedTitles[entity.entityId] ?: entity.headline,
                moduleIndex,
                analyticsContainer,
                parentId,
                vIndex,
                hIndex
            )
            is LiveBlogEntity -> entity.toHeroItem(
                curatedTitles[entity.entityId] ?: entity.title,
                curatedDescriptions[entity.entityId] ?: entity.description,
                curatedImageUrls[entity.entityId] ?: entity.imageUrl.orEmpty(),
                moduleIndex,
                analyticsContainer,
                parentId,
                vIndex,
                hIndex
            )
            else -> null
        }
    }

    @Suppress("LongParameterList")
    private fun ArticleEntity.toHeroItem(
        userData: UserData?,
        title: String,
        description: String,
        imageUrl: String,
        moduleIndex: Int,
        analyticsContainer: String,
        parentId: String,
        vIndex: Int,
        hIndex: Int
    ) = FeedHeroItem(
        id = articleId.toString(),
        moduleIndex = moduleIndex,
        type = getArticleType(),
        imageUrl = imageUrl,
        title = title,
        byline = authorName.orEmpty(),
        excerpt = description,
        showExpert = description.isNotBlank(),
        commentCount = CommentsCountNumberFormat.format(commentsCount),
        showComments = commentsCount > 0,
        isBookmarked = userData.isArticleBookmarked(articleId),
        isRead = userData.isArticleRead(articleId),
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "article_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex,
            hIndex = hIndex,
            parentId = parentId
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "article_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex.toLong(),
            hIndex = hIndex.toLong(),
            parentObjectType = "curated_module_id",
            parentObjectId = parentId
        )
    )

    @Suppress("LongParameterList")
    private fun PodcastEpisodeEntity.toHeroItem(
        title: String,
        description: String,
        imageUrl: String,
        playerState: PodcastPlayerState,
        moduleIndex: Int,
        analyticsContainer: String,
        parentId: String,
        vIndex: Int,
        hIndex: Int
    ) = FeedHeroItem(
        id = id,
        moduleIndex = moduleIndex,
        type = CuratedItemType.PODCAST,
        title = title,
        excerpt = description,
        showExpert = description.isNotBlank(),
        podcastImageUrl = imageUrl,
        podcastPlayerState = podcastPlayerStateUtility.getPlayerState(this, playerState),
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "podcast_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex,
            hIndex = hIndex,
            parentId = parentId
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "podcast_episode_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex.toLong(),
            hIndex = hIndex.toLong(),
            parentObjectType = "curated_module_id",
            parentObjectId = parentId
        )
    )

    @Suppress("LongParameterList")
    private fun HeadlineEntity.toHeroItem(
        title: String,
        moduleIndex: Int,
        analyticsContainer: String,
        parentId: String,
        vIndex: Int,
        hIndex: Int
    ) = FeedHeroItem(
        id = id,
        moduleIndex = moduleIndex,
        type = CuratedItemType.HEADLINE,
        title = title,
        byline = byline,
        commentCount = CommentsCountNumberFormat.format(commentsCount),
        showComments = commentsCount > 0,
        imageUrl = imageUrls.firstOrNull().orEmpty(),
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "headline_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex,
            hIndex = hIndex,
            parentId = parentId
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "headline_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex.toLong(),
            hIndex = hIndex.toLong(),
            parentObjectType = "curated_module_id",
            parentObjectId = parentId
        )
    )

    @Suppress("LongParameterList")
    private fun LiveBlogEntity.toHeroItem(
        title: String,
        description: String,
        imageUrl: String,
        moduleIndex: Int,
        analyticsContainer: String,
        parentId: String,
        vIndex: Int,
        hIndex: Int
    ) = FeedHeroItem(
        id = id,
        moduleIndex = moduleIndex,
        type = CuratedItemType.LIVE_BLOG,
        title = title,
        excerpt = description,
        showExpert = description.isNotBlank(),
        imageUrl = imageUrl,
        updatedAt = timeAgoShortDateFormatter.format(
            lastActivityAt,
            TimeAgoShortDateFormatter.Params(showUpdated = true)
        ),
        isLive = isLive,
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "blog_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex,
            hIndex = hIndex,
            parentId = parentId
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "blog_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex.toLong(),
            hIndex = hIndex.toLong(),
            parentObjectType = "curated_module_id",
            parentObjectId = parentId
        )
    )

    @Suppress("LongParameterList")
    fun renderHeroTabletItem(
        userData: UserData?,
        entity: AthleticEntity,
        curatedTitles: Map<AthleticEntity.Id, String?>,
        curatedDescriptions: Map<AthleticEntity.Id, String?>,
        curatedImageUrls: Map<AthleticEntity.Id, String?>,
        podcastPlayerState: PodcastPlayerState,
        moduleIndex: Int,
        analyticsContainer: String,
        parentId: String
    ): FeedHeroTabletItem? {
        return when (entity) {
            is ArticleEntity -> entity.toHeroTabletItem(
                userData = userData,
                title = curatedTitles[entity.entityId] ?: entity.articleTitle.orEmpty(),
                description = curatedDescriptions[entity.entityId] ?: entity.excerpt.orEmpty(),
                imageUrl = curatedImageUrls[entity.entityId] ?: entity.articleHeaderImg.orEmpty(),
                moduleIndex = moduleIndex,
                analyticsContainer = analyticsContainer,
                parentId = parentId
            )
            is PodcastEpisodeEntity -> entity.toHeroTabletItem(
                curatedTitles[entity.entityId] ?: entity.title,
                curatedDescriptions[entity.entityId] ?: entity.description,
                curatedImageUrls[entity.entityId] ?: entity.imageUrl,
                podcastPlayerState,
                moduleIndex,
                analyticsContainer,
                parentId
            )
            is HeadlineEntity -> entity.toHeroTabletItem(
                curatedTitles[entity.entityId] ?: entity.headline,
                moduleIndex,
                analyticsContainer,
                parentId
            )
            is LiveBlogEntity -> entity.toHeroTabletItem(
                curatedTitles[entity.entityId] ?: entity.title,
                curatedDescriptions[entity.entityId] ?: entity.description,
                curatedImageUrls[entity.entityId] ?: entity.imageUrl.orEmpty(),
                moduleIndex,
                analyticsContainer,
                parentId
            )
            else -> null
        }
    }

    @Suppress("LongParameterList")
    private fun ArticleEntity.toHeroTabletItem(
        userData: UserData?,
        title: String,
        description: String,
        imageUrl: String,
        moduleIndex: Int,
        analyticsContainer: String,
        parentId: String
    ) = FeedHeroTabletItem(
        id = articleId.toString(),
        moduleIndex = moduleIndex,
        type = getArticleType(),
        imageUrl = imageUrl,
        title = title,
        byline = authorName.orEmpty(),
        excerpt = description,
        showExpert = description.isNotBlank(),
        commentCount = CommentsCountNumberFormat.format(commentsCount),
        showComments = commentsCount > 0,
        isBookmarked = userData.isArticleBookmarked(articleId),
        isRead = userData.isArticleRead(articleId),
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "article_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            vIndex = 0,
            parentId = parentId
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "article_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = analyticsContainer,
            vIndex = 0L,
            parentObjectType = "curated_module_id",
            parentObjectId = parentId
        )
    )

    @Suppress("LongParameterList")
    private fun PodcastEpisodeEntity.toHeroTabletItem(
        title: String,
        description: String,
        imageUrl: String,
        playerState: PodcastPlayerState,
        moduleIndex: Int,
        analyticsContainer: String,
        parentId: String
    ) = FeedHeroTabletItem(
        id = id,
        moduleIndex = moduleIndex,
        type = CuratedItemType.PODCAST,
        title = title,
        excerpt = description,
        showExpert = description.isNotBlank(),
        podcastImageUrl = imageUrl,
        isPodcastVisible = true,
        isBylineVisible = false,
        podcastPlayerState = podcastPlayerStateUtility.getPlayerState(this, playerState),
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "podcast_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            vIndex = 0,
            parentId = parentId
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "podcast_episode_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = analyticsContainer,
            vIndex = 0L,
            parentObjectType = "curated_module_id",
            parentObjectId = parentId
        )
    )

    private fun HeadlineEntity.toHeroTabletItem(
        title: String,
        moduleIndex: Int,
        analyticsContainer: String,
        parentId: String
    ) = FeedHeroTabletItem(
        id = id,
        moduleIndex = moduleIndex,
        type = CuratedItemType.HEADLINE,
        title = title,
        byline = byline,
        commentCount = CommentsCountNumberFormat.format(commentsCount),
        showComments = commentsCount > 0,
        imageUrl = imageUrls.firstOrNull().orEmpty(),
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "headline_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            vIndex = 0,
            parentId = parentId
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "headline_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = analyticsContainer,
            vIndex = 0,
            parentObjectType = "curated_module_id",
            parentObjectId = parentId
        )
    )

    @Suppress("LongParameterList")
    private fun LiveBlogEntity.toHeroTabletItem(
        title: String,
        description: String,
        imageUrl: String,
        moduleIndex: Int,
        analyticsContainer: String,
        parentId: String
    ) = FeedHeroTabletItem(
        id = id,
        moduleIndex = moduleIndex,
        type = CuratedItemType.LIVE_BLOG,
        title = title,
        excerpt = description,
        showExpert = description.isNotBlank(),
        imageUrl = imageUrl,
        updatedAt = timeAgoShortDateFormatter.format(
            lastActivityAt,
            TimeAgoShortDateFormatter.Params(showUpdated = true)
        ),
        isLive = isLive,
        isLiveBlogVisible = true,
        isBylineVisible = false,
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "blog_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            vIndex = 0,
            parentId = parentId
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "blog_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = analyticsContainer,
            vIndex = 0,
            parentObjectType = "curated_module_id",
            parentObjectId = parentId
        )
    )

    @Suppress("LongParameterList")
    fun renderLeftImageItem(
        userData: UserData?,
        entity: AthleticEntity,
        curatedTitles: Map<AthleticEntity.Id, String?>,
        curatedImageUrls: Map<AthleticEntity.Id, String?>,
        podcastPlayerState: PodcastPlayerState,
        moduleIndex: Int,
        analyticsContainer: String,
        vIndex: Int,
        hIndex: Int,
        parentId: String,
        showListDivider: Boolean = false,
        isSquareImage: Boolean = false
    ): FeedLeftImageItem? {
        return when (entity) {
            is ArticleEntity -> entity.toLeftImageItem(
                userData = userData,
                title = curatedTitles[entity.entityId] ?: entity.articleTitle.orEmpty(),
                imageUrl = curatedImageUrls[entity.entityId] ?: entity.articleHeaderImg.orEmpty(),
                isSquareImage = isSquareImage,
                showListDivider = showListDivider,
                moduleIndex = moduleIndex,
                analyticsContainer = analyticsContainer,
                vIndex = vIndex,
                hIndex = hIndex,
                parentId = parentId
            )
            is PodcastEpisodeEntity -> entity.toLeftImageItem(
                curatedTitles[entity.entityId] ?: entity.title,
                curatedImageUrls[entity.entityId] ?: entity.imageUrl,
                podcastPlayerState,
                isSquareImage,
                showListDivider,
                moduleIndex,
                analyticsContainer,
                vIndex,
                hIndex,
                parentId
            )
            is HeadlineEntity -> entity.toLeftImageItem(
                curatedTitles[entity.entityId] ?: entity.headline,
                isSquareImage,
                showListDivider,
                moduleIndex,
                analyticsContainer,
                vIndex,
                hIndex,
                parentId
            )
            is LiveBlogEntity -> entity.toLeftImageItem(
                curatedTitles[entity.entityId] ?: entity.title,
                curatedImageUrls[entity.entityId] ?: entity.imageUrl.orEmpty(),
                isSquareImage,
                showListDivider,
                moduleIndex,
                analyticsContainer,
                vIndex,
                hIndex,
                parentId
            )
            else -> null
        }
    }

    @Suppress("LongParameterList")
    private fun ArticleEntity.toLeftImageItem(
        userData: UserData?,
        title: String,
        imageUrl: String,
        isSquareImage: Boolean,
        showListDivider: Boolean,
        moduleIndex: Int,
        analyticsContainer: String,
        vIndex: Int,
        hIndex: Int,
        parentId: String
    ) = FeedLeftImageItem(
        id = articleId.toString(),
        moduleIndex = moduleIndex,
        type = getArticleType(),
        imageUrl = imageUrl,
        title = title,
        byline = authorName.orEmpty(),
        commentCount = CommentsCountNumberFormat.format(commentsCount),
        showComments = commentsCount > 0,
        isBookmarked = userData.isArticleBookmarked(articleId),
        isRead = userData.isArticleRead(articleId),
        showListDivider = showListDivider,
        isSquareImage = isSquareImage,
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "article_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex,
            hIndex = hIndex,
            parentId = parentId
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "article_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = analyticsContainer,
            hIndex = hIndex.toLong(),
            vIndex = vIndex.toLong(),
            parentObjectType = "curated_module_id",
            parentObjectId = parentId
        )
    )

    @Suppress("LongParameterList")
    private fun PodcastEpisodeEntity.toLeftImageItem(
        title: String,
        imageUrl: String,
        playerState: PodcastPlayerState,
        isSquareImage: Boolean,
        showListDivider: Boolean,
        moduleIndex: Int,
        analyticsContainer: String,
        vIndex: Int,
        hIndex: Int,
        parentId: String
    ) = FeedLeftImageItem(
        id = id,
        moduleIndex = moduleIndex,
        type = CuratedItemType.PODCAST,
        title = title,
        imageUrl = if (isSquareImage) imageUrl else "",
        podcastImageUrl = if (isSquareImage) "" else imageUrl,
        podcastPlayerState = podcastPlayerStateUtility.getPlayerState(this, playerState),
        showListDivider = showListDivider,
        isSquareImage = isSquareImage,
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "podcast_episode_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex,
            hIndex = hIndex,
            parentId = parentId
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "podcast_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = analyticsContainer,
            hIndex = hIndex.toLong(),
            vIndex = vIndex.toLong(),
            parentObjectType = "curated_module_id",
            parentObjectId = parentId
        )
    )

    @Suppress("LongParameterList")
    private fun HeadlineEntity.toLeftImageItem(
        title: String,
        isSquareImage: Boolean,
        showListDivider: Boolean,
        moduleIndex: Int,
        analyticsContainer: String,
        vIndex: Int,
        hIndex: Int,
        parentId: String
    ) = FeedLeftImageItem(
        id = id,
        moduleIndex = moduleIndex,
        type = CuratedItemType.HEADLINE,
        title = title,
        byline = byline,
        commentCount = CommentsCountNumberFormat.format(commentsCount),
        showComments = commentsCount > 0,
        imageUrl = imageUrls.firstOrNull().orEmpty(),
        isSquareImage = isSquareImage,
        showListDivider = showListDivider,
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "headline_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex,
            hIndex = hIndex,
            parentId = parentId
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "headline_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex.toLong(),
            hIndex = hIndex.toLong(),
            parentObjectType = "curated_module_id",
            parentObjectId = parentId
        )
    )

    @Suppress("LongParameterList")
    private fun LiveBlogEntity.toLeftImageItem(
        title: String,
        imageUrl: String,
        isSquareImage: Boolean,
        showListDivider: Boolean,
        moduleIndex: Int,
        analyticsContainer: String,
        vIndex: Int,
        hIndex: Int,
        parentId: String
    ) = FeedLeftImageItem(
        id = id,
        moduleIndex = moduleIndex,
        type = CuratedItemType.LIVE_BLOG,
        title = title,
        imageUrl = imageUrl,
        updatedAt = timeAgoShortDateFormatter.format(
            lastActivityAt,
            TimeAgoShortDateFormatter.Params(showUpdated = true)
        ),
        isLive = isLive,
        isSquareImage = isSquareImage,
        showListDivider = showListDivider,
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "blog_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex,
            hIndex = hIndex,
            parentId = parentId
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "blog_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex.toLong(),
            hIndex = hIndex.toLong(),
            parentObjectType = "curated_module_id",
            parentObjectId = parentId
        )
    )

    @Suppress("LongParameterList")
    fun renderSideBySideItem(
        userData: UserData?,
        entities: List<AthleticEntity>,
        curatedTitles: Map<AthleticEntity.Id, String?>,
        curatedImageUrls: Map<AthleticEntity.Id, String?>,
        playerState: PodcastPlayerState,
        moduleIndex: Int,
        analyticsContainer: String,
        parentId: String,
        isTablet: Boolean
    ): CarouselUiModel? {
        return if (isTablet) {
            renderSideBySideItemTablet(
                userData,
                entities,
                curatedTitles,
                curatedImageUrls,
                playerState,
                moduleIndex,
                analyticsContainer,
                parentId,
            )
        } else {
            renderSideBySideItemPhone(
                userData,
                entities,
                curatedTitles,
                curatedImageUrls,
                playerState,
                moduleIndex,
                analyticsContainer,
                parentId
            )
        }
    }

    @Suppress("LongParameterList")
    fun renderSideBySideItemPhone(
        userData: UserData?,
        entities: List<AthleticEntity>,
        curatedTitles: Map<AthleticEntity.Id, String?>,
        curatedImageUrls: Map<AthleticEntity.Id, String?>,
        playerState: PodcastPlayerState,
        moduleIndex: Int,
        analyticsContainer: String,
        parentId: String
    ): FeedSideBySideCarousel? {
        val left = entities.first().toSideBySideItem(
            userData,
            curatedTitles,
            curatedImageUrls,
            playerState,
            moduleIndex,
            analyticsContainer,
            hIndex = 0,
            vIndex = 1,
            parentId
        ) ?: return null
        val right = entities.last().toSideBySideItem(
            userData,
            curatedTitles,
            curatedImageUrls,
            playerState,
            moduleIndex,
            analyticsContainer,
            hIndex = 1,
            vIndex = 1,
            parentId
        ) ?: return null
        return FeedSideBySideCarousel(
            moduleIndex,
            listOf(left, right)
        )
    }

    @Suppress("LongParameterList")
    fun renderSideBySideItemTablet(
        userData: UserData?,
        entities: List<AthleticEntity>,
        curatedTitles: Map<AthleticEntity.Id, String?>,
        curatedImageUrls: Map<AthleticEntity.Id, String?>,
        playerState: PodcastPlayerState,
        moduleIndex: Int,
        analyticsContainer: String,
        parentId: String
    ): FeedSideBySideLeftItemCarousel? {
        val left = renderLeftImageItem(
            userData = userData,
            entity = entities.first(),
            curatedTitles = curatedTitles,
            curatedImageUrls = curatedImageUrls,
            podcastPlayerState = playerState,
            moduleIndex = moduleIndex,
            analyticsContainer = analyticsContainer,
            vIndex = 1,
            hIndex = 0,
            parentId = parentId,
            isSquareImage = true
        ) ?: return null
        val right = renderLeftImageItem(
            userData = userData,
            entity = entities.last(),
            curatedTitles = curatedTitles,
            curatedImageUrls = curatedImageUrls,
            podcastPlayerState = playerState,
            moduleIndex = moduleIndex,
            analyticsContainer = analyticsContainer,
            vIndex = 1,
            hIndex = 0,
            parentId = parentId,
            isSquareImage = true
        ) ?: return null
        return FeedSideBySideLeftItemCarousel(
            moduleIndex,
            listOf(left, right)
        )
    }

    @Suppress("LongParameterList")
    fun renderSingleSideBySideItem(
        userData: UserData?,
        entity: AthleticEntity,
        curatedTitles: Map<AthleticEntity.Id, String?>,
        curatedImageUrls: Map<AthleticEntity.Id, String?>,
        playerState: PodcastPlayerState,
        moduleIndex: Int,
        vIndex: Int,
        hIndex: Int,
        analyticsContainer: String,
        parentId: String,
        titleMaxLines: Int,
        useTopPadding: Boolean
    ) = entity.toSideBySideItem(
        userData,
        curatedTitles,
        curatedImageUrls,
        playerState,
        moduleIndex,
        analyticsContainer,
        hIndex,
        vIndex,
        parentId,
        titleMaxLines,
        if (useTopPadding) R.dimen.global_spacing_12 else R.dimen.global_spacing_0
    )

    @Suppress("LongParameterList")
    private fun AthleticEntity.toSideBySideItem(
        userData: UserData?,
        curatedTitles: Map<AthleticEntity.Id, String?>,
        curatedImageUrls: Map<AthleticEntity.Id, String?>,
        podcastPlayerState: PodcastPlayerState,
        moduleIndex: Int,
        analyticsContainer: String,
        hIndex: Int,
        vIndex: Int,
        parentId: String,
        titleMaxLines: Int = 3,
        @DimenRes topPaddingRes: Int = R.dimen.global_spacing_12
    ) = when (this) {
        is ArticleEntity -> toSideBySideItem(
            userData = userData,
            title = curatedTitles[entityId] ?: articleTitle.orEmpty(),
            imageUrl = curatedImageUrls[entityId] ?: articleHeaderImg.orEmpty(),
            moduleIndex = moduleIndex,
            analyticsContainer = analyticsContainer,
            hIndex = hIndex,
            vIndex = vIndex,
            parentId = parentId,
            titleMaxLines = titleMaxLines,
            topPaddingRes = topPaddingRes
        )
        is PodcastEpisodeEntity -> toSideBySideItem(
            curatedTitles[entityId] ?: title,
            curatedImageUrls[entityId] ?: imageUrl,
            podcastPlayerState,
            moduleIndex,
            analyticsContainer,
            hIndex,
            vIndex,
            parentId,
            titleMaxLines,
            topPaddingRes
        )
        is HeadlineEntity -> toSideBySideItem(
            curatedTitles[entityId] ?: headline,
            moduleIndex,
            analyticsContainer,
            hIndex,
            vIndex,
            parentId,
            titleMaxLines,
            topPaddingRes
        )
        is LiveBlogEntity -> toSideBySideItem(
            curatedTitles[entityId] ?: title,
            curatedImageUrls[entityId] ?: imageUrl.orEmpty(),
            moduleIndex,
            analyticsContainer,
            hIndex,
            vIndex,
            parentId,
            titleMaxLines,
            topPaddingRes
        )
        else -> null
    }

    @Suppress("LongParameterList")
    private fun ArticleEntity.toSideBySideItem(
        userData: UserData?,
        title: String,
        imageUrl: String,
        moduleIndex: Int,
        analyticsContainer: String,
        hIndex: Int,
        vIndex: Int,
        parentId: String,
        titleMaxLines: Int,
        @DimenRes topPaddingRes: Int
    ) = FeedSideBySideItem(
        id = articleId.toString(),
        moduleIndex = moduleIndex,
        type = getArticleType(),
        imageUrl = imageUrl,
        title = title,
        byline = authorName.orEmpty(),
        commentCount = CommentsCountNumberFormat.format(commentsCount),
        showComments = commentsCount > 0,
        isBookmarked = userData.isArticleBookmarked(articleId),
        isRead = userData.isArticleRead(articleId),
        titleMaxLines = titleMaxLines,
        topPaddingRes = topPaddingRes,
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "article_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex,
            hIndex = hIndex,
            parentId = parentId
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "article_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = analyticsContainer,
            hIndex = hIndex.toLong(),
            vIndex = vIndex.toLong(),
            parentObjectType = "curated_module_id",
            parentObjectId = parentId
        )
    )

    @Suppress("LongParameterList")
    private fun PodcastEpisodeEntity.toSideBySideItem(
        title: String,
        imageUrl: String,
        playerState: PodcastPlayerState,
        moduleIndex: Int,
        analyticsContainer: String,
        hIndex: Int,
        vIndex: Int,
        parentId: String,
        titleMaxLines: Int,
        @DimenRes topPaddingRes: Int
    ) = FeedSideBySideItem(
        id = id,
        moduleIndex = moduleIndex,
        type = CuratedItemType.PODCAST,
        title = title,
        podcastImageUrl = imageUrl,
        podcastPlayerState = podcastPlayerStateUtility.getPlayerState(this, playerState),
        titleMaxLines = titleMaxLines,
        topPaddingRes = topPaddingRes,
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "podcast_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex,
            hIndex = hIndex,
            parentId = parentId
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "podcast_episode_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = analyticsContainer,
            hIndex = hIndex.toLong(),
            vIndex = vIndex.toLong(),
            parentObjectType = "curated_module_id",
            parentObjectId = parentId
        )
    )

    @Suppress("LongParameterList")
    private fun HeadlineEntity.toSideBySideItem(
        title: String,
        moduleIndex: Int,
        analyticsContainer: String,
        hIndex: Int,
        vIndex: Int,
        parentId: String,
        titleMaxLines: Int,
        @DimenRes topPaddingRes: Int
    ) = FeedSideBySideItem(
        id = id,
        moduleIndex = moduleIndex,
        type = CuratedItemType.HEADLINE,
        title = title,
        byline = byline,
        commentCount = CommentsCountNumberFormat.format(commentsCount),
        showComments = commentsCount > 0,
        imageUrl = imageUrls.firstOrNull().orEmpty(),
        titleMaxLines = titleMaxLines,
        topPaddingRes = topPaddingRes,
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "headline_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex,
            hIndex = hIndex,
            parentId = parentId
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "headline_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex.toLong(),
            hIndex = hIndex.toLong(),
            parentObjectType = "curated_module_id",
            parentObjectId = parentId
        )
    )

    @Suppress("LongParameterList")
    private fun LiveBlogEntity.toSideBySideItem(
        title: String,
        imageUrl: String,
        moduleIndex: Int,
        analyticsContainer: String,
        hIndex: Int,
        vIndex: Int,
        parentId: String,
        titleMaxLines: Int,
        @DimenRes topPaddingRes: Int
    ) = FeedSideBySideItem(
        id = id,
        moduleIndex = moduleIndex,
        type = CuratedItemType.LIVE_BLOG,
        title = title,
        imageUrl = imageUrl,
        updatedAt = timeAgoShortDateFormatter.format(
            lastActivityAt,
            TimeAgoShortDateFormatter.Params(showUpdated = true)
        ),
        isLive = isLive,
        titleMaxLines = titleMaxLines,
        topPaddingRes = topPaddingRes,
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "blog_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex,
            hIndex = hIndex,
            parentId = parentId
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "blog_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex.toLong(),
            hIndex = hIndex.toLong(),
            parentObjectType = "curated_module_id",
            parentObjectId = parentId
        )
    )

    @Suppress("LongParameterList")
    fun renderCuratedCarouselItem(
        athleticEntity: AthleticEntity,
        userData: UserData?,
        curatedTitles: Map<AthleticEntity.Id, String?>,
        curatedImageUrls: Map<AthleticEntity.Id, String?>,
        podcastPlayerState: PodcastPlayerState,
        moduleIndex: Int,
        analyticsContainer: String,
        hIndex: Int,
        vIndex: Int,
        parentId: String
    ) = when (athleticEntity) {
        is ArticleEntity -> athleticEntity.toCuratedCarouselItem(
            userData = userData,
            title = curatedTitles[athleticEntity.entityId] ?: athleticEntity.articleTitle.orEmpty(),
            imageUrl = curatedImageUrls[athleticEntity.entityId] ?: athleticEntity.articleHeaderImg.orEmpty(),
            moduleIndex = moduleIndex,
            analyticsContainer = analyticsContainer,
            hIndex = hIndex,
            vIndex = vIndex,
            parentId = parentId
        )
        is PodcastEpisodeEntity -> athleticEntity.toCuratedCarouselItem(
            curatedTitles[athleticEntity.entityId] ?: athleticEntity.title,
            curatedImageUrls[athleticEntity.entityId] ?: athleticEntity.imageUrl,
            podcastPlayerState,
            moduleIndex,
            analyticsContainer,
            hIndex,
            vIndex,
            parentId
        )
        is HeadlineEntity -> athleticEntity.toCuratedCarouselItem(
            curatedTitles[athleticEntity.entityId] ?: athleticEntity.headline,
            moduleIndex,
            analyticsContainer,
            hIndex,
            vIndex,
            parentId
        )
        is LiveBlogEntity -> athleticEntity.toCuratedCarouselItem(
            curatedTitles[athleticEntity.entityId] ?: athleticEntity.title,
            curatedImageUrls[athleticEntity.entityId] ?: athleticEntity.imageUrl.orEmpty(),
            moduleIndex,
            analyticsContainer,
            hIndex,
            vIndex,
            parentId
        )
        else -> null
    }

    @Suppress("LongParameterList")
    private fun ArticleEntity.toCuratedCarouselItem(
        userData: UserData?,
        title: String,
        imageUrl: String,
        moduleIndex: Int,
        analyticsContainer: String,
        hIndex: Int,
        vIndex: Int,
        parentId: String
    ) = FeedCuratedCarouselItem(
        id = articleId.toString(),
        moduleIndex = moduleIndex,
        type = getArticleType(),
        imageUrl = imageUrl,
        title = title,
        byline = authorName.orEmpty(),
        commentCount = CommentsCountNumberFormat.format(commentsCount),
        showComments = commentsCount > 0,
        isBookmarked = userData.isArticleBookmarked(articleId),
        isRead = userData.isArticleRead(articleId),
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "article_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex,
            hIndex = hIndex,
            parentId = parentId
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "article_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = analyticsContainer,
            hIndex = hIndex.toLong(),
            vIndex = vIndex.toLong(),
            parentObjectType = "curated_module_id",
            parentObjectId = parentId
        )
    )

    @Suppress("LongParameterList")
    private fun PodcastEpisodeEntity.toCuratedCarouselItem(
        title: String,
        imageUrl: String,
        playerState: PodcastPlayerState,
        moduleIndex: Int,
        analyticsContainer: String,
        hIndex: Int,
        vIndex: Int,
        parentId: String,
    ) = FeedCuratedCarouselItem(
        id = id,
        moduleIndex = moduleIndex,
        type = CuratedItemType.PODCAST,
        title = title,
        podcastImageUrl = imageUrl,
        podcastPlayerState = podcastPlayerStateUtility.getPlayerState(this, playerState),
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "podcast_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex,
            hIndex = hIndex,
            parentId = parentId
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "podcast_episode_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = analyticsContainer,
            hIndex = hIndex.toLong(),
            vIndex = vIndex.toLong(),
            parentObjectType = "curated_module_id",
            parentObjectId = parentId
        )
    )

    @Suppress("LongParameterList")
    private fun HeadlineEntity.toCuratedCarouselItem(
        title: String,
        moduleIndex: Int,
        analyticsContainer: String,
        hIndex: Int,
        vIndex: Int,
        parentId: String
    ) = FeedCuratedCarouselItem(
        id = id,
        moduleIndex = moduleIndex,
        type = CuratedItemType.HEADLINE,
        title = title,
        byline = byline,
        commentCount = CommentsCountNumberFormat.format(commentsCount),
        showComments = commentsCount > 0,
        imageUrl = imageUrls.firstOrNull().orEmpty(),
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "headline_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex,
            hIndex = hIndex,
            parentId = parentId
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "headline_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex.toLong(),
            hIndex = hIndex.toLong(),
            parentObjectType = "curated_module_id",
            parentObjectId = parentId
        )
    )

    @Suppress("LongParameterList")
    private fun LiveBlogEntity.toCuratedCarouselItem(
        title: String,
        imageUrl: String,
        moduleIndex: Int,
        analyticsContainer: String,
        hIndex: Int,
        vIndex: Int,
        parentId: String
    ) = FeedCuratedCarouselItem(
        id = id,
        moduleIndex = moduleIndex,
        type = CuratedItemType.LIVE_BLOG,
        title = title,
        imageUrl = imageUrl,
        updatedAt = timeAgoShortDateFormatter.format(
            lastActivityAt,
            TimeAgoShortDateFormatter.Params(showUpdated = true)
        ),
        isLive = isLive,
        analyticsPayload = FeedCuratedItemAnalyticsPayload(
            objectType = "blog_id",
            moduleIndex = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex,
            hIndex = hIndex,
            parentId = parentId
        ),
        impressionPayload = ImpressionPayload(
            element = analyticsContainer,
            objectType = "blog_id",
            objectId = id,
            pageOrder = moduleIndex,
            container = analyticsContainer,
            vIndex = vIndex.toLong(),
            hIndex = hIndex.toLong(),
            parentObjectType = "curated_module_id",
            parentObjectId = parentId
        )
    )

    private fun UserData?.isArticleBookmarked(articleId: Long) = this?.articlesSaved?.contains(articleId) == true

    private fun UserData?.isArticleRead(articleId: Long) = this?.articlesRead?.contains(articleId) == true
}

fun ArticleEntity.getArticleType() = when (entryType) {
    FeedItemEntryType.COMMENTS -> CuratedItemType.DISCUSSION
    FeedItemEntryType.USER_DISCUSSION -> CuratedItemType.DISCUSSION
    FeedItemEntryType.LIVE_DISCUSSION -> CuratedItemType.QANDA
    else -> CuratedItemType.ARTICLE
}