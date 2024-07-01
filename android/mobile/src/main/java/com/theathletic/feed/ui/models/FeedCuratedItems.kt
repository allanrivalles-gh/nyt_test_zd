package com.theathletic.feed.ui.models

import androidx.annotation.DimenRes
import com.theathletic.R
import com.theathletic.analytics.AnalyticsPayload
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.podcast.ui.widget.TinyPodcastPlayer
import com.theathletic.ui.CarouselUiModel
import com.theathletic.ui.UiModel
import com.theathletic.ui.binding.ParameterizedString
import com.theathletic.ui.binding.asParameterized
import com.theathletic.utility.RecyclerLayout

data class FeedHeroItem(
    val id: String,
    val moduleIndex: Int,
    val type: CuratedItemType,
    val imageUrl: String = "",
    val title: String = "",
    val byline: String = "",
    val excerpt: String = "",
    val showExpert: Boolean = false,
    val commentCount: String = "",
    val showComments: Boolean = false,
    val isBookmarked: Boolean = false,
    val isRead: Boolean = false,
    val isLive: Boolean = false,
    val updatedAt: ParameterizedString = ParameterizedString(""),
    val podcastImageUrl: String = "",
    val podcastPlayerState: TinyPodcastPlayer.ViewState = defaultPlayerState,
    val analyticsPayload: FeedCuratedItemAnalyticsPayload,
    override val impressionPayload: ImpressionPayload
) : UiModel {
    override val stableId get() = "FeedHeroItem:$id:$moduleIndex"

    val isArticle = type == CuratedItemType.ARTICLE
    val isQandA = type == CuratedItemType.QANDA
    val isDiscussion = type == CuratedItemType.DISCUSSION
    val isLiveBlog = type == CuratedItemType.LIVE_BLOG
    val isPodcast = type == CuratedItemType.PODCAST
    val isHeadline = type == CuratedItemType.HEADLINE

    interface Interactor : FeedCuratedItemInteractor
}

data class FeedHeroTabletItem(
    val id: String,
    val moduleIndex: Int,
    val type: CuratedItemType,
    val imageUrl: String = "",
    val title: String = "",
    val byline: String = "",
    val excerpt: String = "",
    val showExpert: Boolean = false,
    val commentCount: String = "",
    val showComments: Boolean = false,
    val isBookmarked: Boolean = false,
    val isRead: Boolean = false,
    val isLive: Boolean = false,
    val isBylineVisible: Boolean = true,
    val isLiveBlogVisible: Boolean = false,
    val isPodcastVisible: Boolean = false,
    val updatedAt: ParameterizedString = ParameterizedString(""),
    val podcastImageUrl: String = "",
    val podcastPlayerState: TinyPodcastPlayer.ViewState = defaultPlayerState,
    val analyticsPayload: FeedCuratedItemAnalyticsPayload,
    override val impressionPayload: ImpressionPayload
) : UiModel {
    override val stableId get() = "FeedHeroItem:$id:$moduleIndex"

    interface Interactor : FeedCuratedItemInteractor
}

data class FeedLeftImageItem(
    val id: String,
    val moduleIndex: Int,
    val type: CuratedItemType,
    val imageUrl: String = "",
    val title: String = "",
    val byline: String = "",
    val commentCount: String = "",
    val showComments: Boolean = false,
    val isBookmarked: Boolean = false,
    val isRead: Boolean = false,
    val isLive: Boolean = false,
    val updatedAt: ParameterizedString = ParameterizedString(""),
    val podcastImageUrl: String = "",
    val podcastPlayerState: TinyPodcastPlayer.ViewState = defaultPlayerState,
    val showListDivider: Boolean = false,
    val isSquareImage: Boolean = false,
    val analyticsPayload: FeedCuratedItemAnalyticsPayload,
    override val impressionPayload: ImpressionPayload
) : UiModel {
    override val stableId get() = "FeedLeftImageItem:$id:$moduleIndex"

    val isArticle = type == CuratedItemType.ARTICLE
    val isQandA = type == CuratedItemType.QANDA
    val isDiscussion = type == CuratedItemType.DISCUSSION
    val isLiveBlog = type == CuratedItemType.LIVE_BLOG
    val isPodcast = type == CuratedItemType.PODCAST
    val isHeadline = type == CuratedItemType.HEADLINE

    val imageViewRatio = if (isSquareImage) "1:1" else "3:2"

    interface Interactor : FeedCuratedItemInteractor
}

data class FeedSideBySideLeftItemCarousel(
    val id: Int,
    override val carouselItemModels: List<UiModel>
) : CarouselUiModel {
    override val stableId = "FeedSideBySideLeftItemCarousel:$id"
}

data class FeedHeroCarousel(
    val id: Int,
    override val carouselItemModels: List<UiModel>,
    val recyclerLayout: RecyclerLayout
) : CarouselUiModel {
    override val stableId = "FeedHeroCarousel:$id-${carouselItemModels.firstOrNull()?.stableId}"
}

data class FeedSideBySideItem(
    val id: String,
    val moduleIndex: Int,
    val type: CuratedItemType,
    val titleMaxLines: Int,
    val imageUrl: String = "",
    val title: String = "",
    val byline: String = "",
    val commentCount: String = "",
    val showComments: Boolean = false,
    val isBookmarked: Boolean = false,
    val isRead: Boolean = false,
    val isLive: Boolean = false,
    @DimenRes val topPaddingRes: Int,
    val updatedAt: ParameterizedString = ParameterizedString(""),
    val podcastImageUrl: String = "",
    val podcastPlayerState: TinyPodcastPlayer.ViewState = defaultPlayerState,
    val analyticsPayload: FeedCuratedItemAnalyticsPayload,
    override val impressionPayload: ImpressionPayload
) : UiModel {
    override val stableId get() = "FeedSideBySideItem:$id:$moduleIndex"

    val isArticle = type == CuratedItemType.ARTICLE
    val isQandA = type == CuratedItemType.QANDA
    val isDiscussion = type == CuratedItemType.DISCUSSION
    val isLiveBlog = type == CuratedItemType.LIVE_BLOG
    val isPodcast = type == CuratedItemType.PODCAST
    val isHeadline = type == CuratedItemType.HEADLINE

    interface Interactor : FeedCuratedItemInteractor
}

data class FeedCuratedCarouselItem(
    val id: String,
    val moduleIndex: Int,
    val type: CuratedItemType,
    val imageUrl: String = "",
    val title: String = "",
    val byline: String = "",
    val commentCount: String = "",
    val showComments: Boolean = false,
    val isBookmarked: Boolean = false,
    val isRead: Boolean = false,
    val isLive: Boolean = false,
    val updatedAt: ParameterizedString = "".asParameterized(),
    val podcastImageUrl: String = "",
    val podcastPlayerState: TinyPodcastPlayer.ViewState = defaultPlayerState,
    val analyticsPayload: FeedCuratedItemAnalyticsPayload,
    override val impressionPayload: ImpressionPayload
) : UiModel {
    override val stableId get() = "FeedCuratedCarouselItem:$id:$moduleIndex"

    val isArticle = type == CuratedItemType.ARTICLE
    val isQandA = type == CuratedItemType.QANDA
    val isDiscussion = type == CuratedItemType.DISCUSSION
    val isLiveBlog = type == CuratedItemType.LIVE_BLOG
    val isPodcast = type == CuratedItemType.PODCAST
    val isHeadline = type == CuratedItemType.HEADLINE

    val titleMaxLines = if (isPodcast) 3 else 4

    interface Interactor : FeedCuratedItemInteractor
}

data class FeedSideBySideCarousel(
    val id: Int,
    override val carouselItemModels: List<UiModel>
) : CarouselUiModel {
    override val stableId = "FeedSideBySideCarousel:$id"
}

data class FeedCuratedTopperHero(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val isLive: Boolean,
    val byline: ParameterizedString,
    val commentCount: String = "",
    val showCommentCount: Boolean = false,
    val isBookmarked: Boolean = false,
    val isRead: Boolean = false,
    val showSubtitle: Boolean,
    val type: CuratedItemType,
    val isTablet: Boolean,
    val analyticsPayload: FeedCuratedItemAnalyticsPayload,
    override val impressionPayload: ImpressionPayload
) : UiModel {
    override val stableId = "FeedCuratedTopperHero:$id"

    val imageMargin = when {
        isTablet -> R.dimen.feed_topper_hero_margin
        type == CuratedItemType.DISCUSSION -> R.dimen.feed_topper_hero_margin
        type == CuratedItemType.QANDA -> R.dimen.feed_topper_hero_margin
        else -> R.dimen.global_spacing_0
    }

    val isTopAlignImage = when (type) {
        CuratedItemType.DISCUSSION -> false
        CuratedItemType.QANDA -> false
        else -> true
    }

    interface Interactor : FeedCuratedItemInteractor
}

data class FeedCuratedGroupedItem(
    val id: String,
    val title: String,
    val imageUrl: String,
    val isLive: Boolean,
    val byline: ParameterizedString,
    val commentCount: String = "",
    val showCommentCount: Boolean = false,
    val isBookmarked: Boolean = false,
    val isRead: Boolean,
    val showDivider: Boolean,
    val type: CuratedItemType,
    @DimenRes val verticalPadding: Int,
    val analyticsPayload: FeedCuratedItemAnalyticsPayload,
    override val impressionPayload: ImpressionPayload
) : UiModel {
    override val stableId = "FeedCuratedGroupedItem:$id:${analyticsPayload.moduleIndex}"

    interface Interactor : FeedCuratedItemInteractor
}

data class FeedCuratedGroupedItemRead(
    val id: String,
    val title: String,
    val imageUrl: String,
    val isLive: Boolean,
    val byline: ParameterizedString,
    val commentCount: String = "",
    val showCommentCount: Boolean = false,
    val isBookmarked: Boolean = false,
    val type: CuratedItemType,
    @DimenRes val verticalPadding: Int,
    val analyticsPayload: FeedCuratedItemAnalyticsPayload,
    override val impressionPayload: ImpressionPayload
) : UiModel {
    override val stableId = "FeedCuratedGroupedItemRead:$id"

    interface Interactor : FeedCuratedItemInteractor
}

data class FeedTopperGroupedItem(
    val id: String,
    val title: String,
    val imageUrl: String,
    val isLive: Boolean,
    val byline: ParameterizedString,
    val commentCount: String = "",
    val showCommentCount: Boolean = false,
    val isBookmarked: Boolean = false,
    val isRead: Boolean = false,
    val showDivider: Boolean,
    val type: CuratedItemType,
    val podcastPlayerState: TinyPodcastPlayer.ViewState = defaultPlayerState,
    val analyticsPayload: FeedCuratedItemAnalyticsPayload,
    override val impressionPayload: ImpressionPayload
) : UiModel {
    override val stableId = "FeedTopperGroupedItemRead:$id"

    val isPodcast = type == CuratedItemType.PODCAST

    interface Interactor : FeedCuratedItemInteractor
}

data class FeedCuratedItemAnalyticsPayload(
    val objectType: String,
    val moduleIndex: Int,
    val container: String,
    val vIndex: Int? = null,
    val hIndex: Int = -1,
    val parentType: String = "curated_module_id",
    val parentId: String
) : AnalyticsPayload

interface FeedCuratedItemInteractor {
    fun onCuratedItemClicked(
        id: String,
        type: CuratedItemType,
        payload: FeedCuratedItemAnalyticsPayload,
        title: String
    )
    fun onCuratedItemLongClicked(id: String, type: CuratedItemType): Boolean
    fun onPodcastControlClicked(
        id: String,
        analyticsPayload: FeedCuratedItemAnalyticsPayload
    )
}

enum class CuratedItemType {
    ARTICLE,
    QANDA,
    DISCUSSION,
    LIVE_BLOG,
    PODCAST,
    HEADLINE
}

private val defaultPlayerState = TinyPodcastPlayer.ViewState(
    "",
    R.drawable.ic_play_2
)

data class FeedTopperModule(
    val topperModel: FeedCuratedTopperHero,
    val headlines: FeedTopperHeadlines
) : UiModel {
    override val stableId = "FeedTopperModule"
}

data class FeedTopperHeadlines(
    override val carouselItemModels: List<UiModel>,
) : CarouselUiModel {
    override val stableId = "FeedTopperHeadlines"
}

data class FeedThreeFourContentCarousel(
    val id: Int,
    override val carouselItemModels: List<UiModel>
) : CarouselUiModel {
    override val stableId = "FeedThreeFourContentCarouse:$id"

    val recyclerLayout = RecyclerLayout.SIDE_BY_SIDE_GRID
}

data class FeedFourItemHeroCarousel(
    val id: Int,
    override val carouselItemModels: List<UiModel>
) : CarouselUiModel {
    override val stableId = "FeedFourItemHeroCarousel:$id"
}