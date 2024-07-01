package com.theathletic.feed.ui

import androidx.annotation.DimenRes
import com.theathletic.R
import com.theathletic.ads.shouldDisplayAds
import com.theathletic.ads.ui.AdWrapperUiModel
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.article.data.local.InsiderEntity
import com.theathletic.datetime.formatter.LastActivityDateFormatter
import com.theathletic.device.IsTabletProvider
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.article.TrendingTopicsEntity
import com.theathletic.entity.main.FeedItem
import com.theathletic.entity.main.FeedItemStyle
import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.extension.addIf
import com.theathletic.extension.flatMapIndexed
import com.theathletic.extension.nullIfEmpty
import com.theathletic.featureswitch.Features
import com.theathletic.feed.FeedType
import com.theathletic.feed.data.local.AnnouncementEntity
import com.theathletic.feed.data.local.AuthorDetails
import com.theathletic.feed.ui.models.BasicSectionHeader
import com.theathletic.feed.ui.models.FeedAnnouncement
import com.theathletic.feed.ui.models.FeedAnnouncementAnalyticsPayload
import com.theathletic.feed.ui.models.FeedAuthorHeader
import com.theathletic.feed.ui.models.FeedCarousel
import com.theathletic.feed.ui.models.FeedCuratedTopperHero
import com.theathletic.feed.ui.models.FeedEndOfFeed
import com.theathletic.feed.ui.models.FeedFourItemHeroCarousel
import com.theathletic.feed.ui.models.FeedHeroCarousel
import com.theathletic.feed.ui.models.FeedLiveBlogCarousel
import com.theathletic.feed.ui.models.FeedLoadingMore
import com.theathletic.feed.ui.models.FeedMostPopularCarousel
import com.theathletic.feed.ui.models.FeedPodcastShowAnalyticsPayload
import com.theathletic.feed.ui.models.FeedSeeAllButton
import com.theathletic.feed.ui.models.FeedSideBySideCarousel
import com.theathletic.feed.ui.models.FeedThreeFourContentCarousel
import com.theathletic.feed.ui.models.FeedTopperHeadlines
import com.theathletic.feed.ui.models.FeedTopperModule
import com.theathletic.feed.ui.models.LiveBlogAnalyticsPayload
import com.theathletic.feed.ui.models.LiveBlogCarouselItem
import com.theathletic.feed.ui.models.LiveRoomAnalyticsPayload
import com.theathletic.feed.ui.models.LiveRoomUiModel
import com.theathletic.feed.ui.models.RecommendedPodcastSeriesGridItem
import com.theathletic.feed.ui.models.RecommendedPodcastsGrid
import com.theathletic.feed.ui.models.SectionHeaderWithDescription
import com.theathletic.feed.ui.models.SeeAllAnalyticsPayload
import com.theathletic.feed.ui.models.StandaloneFeedHeader
import com.theathletic.feed.ui.renderers.ArticleRenderers
import com.theathletic.feed.ui.renderers.FeedCuratedRenderers
import com.theathletic.feed.ui.renderers.FeedItemRenderers
import com.theathletic.feed.ui.renderers.FeedScoresRenderer
import com.theathletic.feed.ui.renderers.FeedSpotlightRenderer
import com.theathletic.feed.ui.renderers.HeadlineListRenderer
import com.theathletic.feed.ui.renderers.ThreeFourContentRenderer
import com.theathletic.feed.ui.renderers.TopperRenderer
import com.theathletic.frontpage.ui.trendingtopics.TrendingTopicAnalyticsPayload
import com.theathletic.frontpage.ui.trendingtopics.TrendingTopicGridItem
import com.theathletic.headline.data.local.HeadlineEntity
import com.theathletic.liveblog.data.local.LiveBlogEntity
import com.theathletic.news.FrontpagePodcastRenderers
import com.theathletic.podcast.data.local.PodcastEpisodeEntity
import com.theathletic.podcast.data.local.PodcastSeriesEntity
import com.theathletic.scores.data.local.BoxScoreEntity
import com.theathletic.scores.data.local.GameCoverageType
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.FeedItemDivider
import com.theathletic.ui.list.FeedItemVerticalPadding
import com.theathletic.ui.list.ListRoot
import com.theathletic.ui.list.ListVerticalPadding
import com.theathletic.ui.list.ensureDistinct
import com.theathletic.ui.widgets.AuthorImageStackModel
import com.theathletic.user.IUserManager
import com.theathletic.utility.RecyclerLayout
import com.theathletic.utility.extensions.filterPaddingAroundAds
import com.theathletic.utility.logging.ICrashLogHandler
import com.theathletic.utility.safeLet
import kotlin.math.min

@Suppress("LargeClass", "LongParameterList")
class FeedTransformer @AutoKoin constructor(
    private val features: Features,
    private val podcastRenderers: FrontpagePodcastRenderers,
    private val articleRenderers: ArticleRenderers,
    private val feedItemRenderers: FeedItemRenderers,
    private val feedCuratedRenderers: FeedCuratedRenderers,
    private val scoresRenderer: FeedScoresRenderer,
    private val crashHandler: ICrashLogHandler,
    private val feedSpotlightRenderer: FeedSpotlightRenderer,
    private val headlineListRender: HeadlineListRenderer,
    private val topperRenderer: TopperRenderer,
    private val threeFourContentRenderer: ThreeFourContentRenderer,
    private val isTabletProvider: IsTabletProvider,
    private val lastActivityDateFormatter: LastActivityDateFormatter,
    userManager: IUserManager
) : Transformer<CompleteFeedState, FeedContract.ViewState> {
    private var dividerSeed: Int = 0

    companion object {
        const val RECOMMENDED_PODCAST_MAX_GRID = 6
        const val TOPPER_CONTAINER = "topper"
    }

    override fun transform(data: CompleteFeedState): FeedContract.ViewState {
        dividerSeed = 0
        val uiModels = mutableListOf<UiModel>(ListRoot)

        data.authorDetails?.let { authorDetails ->
            uiModels.add(authorDetails.getHeader(data))
        }

        uiModels.addAll(createUiModels(data))
        uiModels.pruneSpacingForLiveBlogCarousel()

        when (data.loadingState) {
            LoadingState.LOADING_MORE -> uiModels.add(FeedLoadingMore())
            LoadingState.FINISHED -> uiModels.addIf(FeedEndOfFeed()) { data.isEndOfFeed }
            else -> { /* Do nothing */
            }
        }

        return FeedContract.ViewState(
            showSpinner = data.loadingState == LoadingState.RELOADING,
            uiModels = uiModels.filterPaddingAroundAds().ensureDistinct(),
            followHeader = getFollowHeader(data)
        )
    }

    private fun MutableList<UiModel>.pruneSpacingForLiveBlogCarousel() {
        val liveBlogCarouselIndex = indexOfFirst { it is FeedLiveBlogCarousel }
        val previousLayoutIndex = liveBlogCarouselIndex - 1
        if (liveBlogCarouselIndex > 0 && this[previousLayoutIndex] is ListVerticalPadding) {
            removeAt(previousLayoutIndex)
        }
    }

    private fun createUiModels(data: CompleteFeedState): List<UiModel> {
        var isTopperAdded = false
        return data.feedItems.flatMapIndexed { index, item ->
            if (isTabletProvider.isTablet && item.container == TOPPER_CONTAINER) {
                if (!isTopperAdded) {
                    isTopperAdded = true
                    return@flatMapIndexed createTopperSection(data)
                }
                return@flatMapIndexed emptyList()
            }
            when (item.style) {
                FeedItemStyle.IPM_ANNOUNCEMENT -> item.toAnnouncement(data, index)
                FeedItemStyle.PODCAST_EPISODE -> item.toPodcastEpisodeGroup(data, index)
                FeedItemStyle.CAROUSEL_RECOMMENDED_PODCASTS -> item.toRecommendedPodcastGrid(index)
                FeedItemStyle.CAROUSEL_TOPICS -> item.toTrendingTopicCarousel(index)
                FeedItemStyle.ARTICLE -> item.toThreeFourContent(data, index)
                FeedItemStyle.HEADLINE -> item.toHeadline(index)
                FeedItemStyle.HEADLINE_LIST -> item.toHeadlineList(isInTopper = false, index)
                FeedItemStyle.FRONTPAGE_MOST_POPULAR_ARTICLES -> item.toMostPopular(index)
                FeedItemStyle.FEED_THREE_FOUR_CONTENT -> item.toThreeFourContent(data, index)
                FeedItemStyle.FRONTPAGE_INSIDERS_CAROUSEL -> item.toInsidersCarousel(index)
                FeedItemStyle.TOPPER -> item.toTopper(index)
                FeedItemStyle.ONE_HERO -> item.toOneHeroItem(index, data)
                FeedItemStyle.TWO_HERO -> item.toTwoHeroItem(index, data)
                FeedItemStyle.THREE_HERO -> item.toThreeHeroItem(index, data)
                FeedItemStyle.FOUR_HERO -> item.toFourHeroItem(index, data)
                FeedItemStyle.FIVE_HERO -> item.toFiveSixHeroItem(index, data)
                FeedItemStyle.SIX_HERO -> item.toFiveSixHeroItem(index, data)
                FeedItemStyle.SEVEN_PLUS_HERO -> item.toSevenPlusHeroItem(index, data)
                FeedItemStyle.CAROUSEL_SCORES -> item.toScoresCarousel(index)
                FeedItemStyle.CAROUSEL_LIVE_BLOGS -> item.toLiveBlogsCarousel(index)
                FeedItemStyle.FOUR_FIVE_GALLERY -> item.toFourFiveGallery(index, data)
                FeedItemStyle.SIX_PLUS_GALLERY -> item.toSixPlusGallery(index, data)
                FeedItemStyle.SPOTLIGHT -> item.toSpotlight(index)
                FeedItemStyle.LIVE_ROOM -> item.toLiveRoom(index)
                FeedItemStyle.DROPZONE -> item.toAdWrapper(data)
                else -> emptyList()
            }
        }
    }

    private fun getFollowHeader(data: CompleteFeedState) = StandaloneFeedHeader(
        title = data.feedTitle,
        isVisible = data.isStandaloneFeed && data.feedType !is FeedType.Author
    )

    private fun createTopperSection(data: CompleteFeedState): List<UiModel> {
        val newItems = data.feedItems.filter { it.container == TOPPER_CONTAINER }

        val hero = newItems.find {
            it.style == FeedItemStyle.TOPPER
        }?.let { item ->
            item.toTabletTopper(item.pageIndex.toInt())
        }

        val headlines = newItems.find {
            it.style == FeedItemStyle.HEADLINE_LIST
        }?.let { item ->
            item.toHeadlineList(isInTopper = true, item.pageIndex.toInt())
        }

        val articles = newItems.find {
            it.style == FeedItemStyle.FEED_THREE_FOUR_CONTENT
        }?.let { item -> item.toThreeFourContentV2(item.pageIndex.toInt()) }

        val ads = newItems.find {
            it.style == FeedItemStyle.DROPZONE
        }?.toAdWrapper(data)

        return createTopperSectionUiModels(hero, headlines, articles, ads)
    }

    private fun createTopperSectionUiModels(
        hero: FeedCuratedTopperHero?,
        headlines: List<UiModel>?,
        articles: List<UiModel>?,
        ads: List<UiModel>?
    ): List<UiModel> {
        val topperSection = safeLet(hero, headlines) { safeHero, safeHeadlines ->
            FeedTopperModule(
                safeHero,
                FeedTopperHeadlines(safeHeadlines)
            )
        }

        return mutableListOf<UiModel>().apply {
            topperSection?.let { add(it) } ?: run {
                hero?.let { add(it) }
                headlines?.let { addAll(it) }
            }
            articles?.let { addAll(it) }
            ads?.let { addAll(it) }
        }
    }

    private fun FeedItem.toAnnouncement(data: CompleteFeedState, moduleIndex: Int): List<UiModel> {
        if (data.isAnnouncementDismissed) return emptyList()

        val announcement = entities.filterIsInstance<AnnouncementEntity>()
            .firstOrNull() ?: return emptyList()

        return listOf(
            FeedAnnouncement(
                id = announcement.id,
                backgroundImageUrl = announcement.imageUrl,
                title = announcement.title,
                subtext = announcement.subtitle,
                ctaText = announcement.ctaText,
                analyticsPayload = FeedAnnouncementAnalyticsPayload(moduleIndex),
                impressionPayload = ImpressionPayload(
                    element = "announcement",
                    container = "announcement",
                    objectType = "announcement_id",
                    objectId = announcement.id,
                    pageOrder = moduleIndex
                )
            ),
            createItemSeparator()
        )
    }

    private fun FeedItem.toPodcastEpisodeGroup(
        state: CompleteFeedState,
        moduleIndex: Int
    ): List<UiModel> {
        val podcasts = entities.filterIsInstance<PodcastEpisodeEntity>()
        if (podcasts.isEmpty()) return emptyList()

        val uiModels = mutableListOf<UiModel>()
        podcasts.forEachIndexed { index, podcast ->
            uiModels.add(
                podcastRenderers.feedPodcastEpisodeGrouped(
                    podcast = podcast,
                    playerState = state.podcastPlayerState,
                    moduleIndex = moduleIndex,
                    hIndex = index,
                    isDownloaded = state.downloadedPodcasts.firstOrNull {
                        it.id == podcast.id.toLong()
                    }?.isDownloaded == true,
                    downloads = state.podcastDownloadData.downloads
                )
            )
            if (index != podcasts.lastIndex) {
                uiModels.add(
                    FeedItemDivider(
                        seed = ++dividerSeed,
                        horizontalPadding = R.dimen.global_spacing_16,
                        verticalPadding = R.dimen.global_spacing_8
                    )
                )
            }
        }

        return mutableListOf<UiModel>().apply {
            add(getBasicSectionHeader(moduleIndex))
            addAll(uiModels)
            addAll(
                getSeeAllWithSpacing(
                    moduleIndex,
                    SeeAllAnalyticsPayload(
                        container = "latest_podcasts_curation",
                        moduleIndex = moduleIndex
                    ),
                    verticalPadding = R.dimen.global_spacing_8
                )
            )
            addItemSeparator()
        }
    }

    private fun FeedItem.toRecommendedPodcastGrid(moduleIndex: Int): List<UiModel> {
        val podcastSeries = entities.filterIsInstance<PodcastSeriesEntity>()
        if (podcastSeries.isEmpty()) return emptyList()

        val uiModels = podcastSeries
            .take(RECOMMENDED_PODCAST_MAX_GRID)
            .mapIndexed { index, series ->
                val vIndex = index / 3
                val hIndex = index % 3

                RecommendedPodcastSeriesGridItem(
                    id = series.id.toInt(),
                    title = series.title,
                    category = series.category,
                    imageUrl = series.imageUrl,
                    analyticsPayload = FeedPodcastShowAnalyticsPayload(
                        moduleIndex = moduleIndex,
                        container = "recommended_podcasts",
                        vIndex = vIndex,
                        hIndex = hIndex
                    ),
                    impressionPayload = ImpressionPayload(
                        objectType = "podcast_id",
                        objectId = series.id,
                        element = "recommended_podcasts",
                        pageOrder = moduleIndex,
                        container = "recommended_podcasts",
                        vIndex = vIndex.toLong(),
                        hIndex = hIndex.toLong()
                    )
                )
            }

        return mutableListOf<UiModel>().apply {
            add(getBasicSectionHeader(moduleIndex))
            add(RecommendedPodcastsGrid(moduleIndex, isTabletProvider.isTablet, uiModels))
            addAll(
                getSeeAllWithSpacing(
                    moduleIndex,
                    SeeAllAnalyticsPayload(
                        container = "recommended_podcasts",
                        moduleIndex = moduleIndex
                    ),
                    verticalPadding = R.dimen.global_spacing_24
                )
            )
            addItemSeparator()
        }
    }

    private fun FeedItem.toTrendingTopicCarousel(moduleIndex: Int): List<UiModel> {
        val itemModels = entities.filterIsInstance<TrendingTopicsEntity>().mapIndexed { carouselIndex, topic ->
            TrendingTopicGridItem(
                id = topic.id.toLongOrNull() ?: 0,
                title = topic.name,
                storyCount = topic.articleCount.toIntOrNull() ?: 0,
                imageUrl = topic.imageUrl.orEmpty(),
                analyticsPayload = TrendingTopicAnalyticsPayload(
                    moduleIndex = moduleIndex, hIndex = carouselIndex, container = "topic"
                ),
                impressionPayload = ImpressionPayload(
                    element = "topic",
                    objectType = "topic_id",
                    objectId = topic.id,
                    pageOrder = moduleIndex,
                    container = "topic",
                    hIndex = carouselIndex.toLong()
                )
            )
        }
        return if (itemModels.isEmpty()) {
            crashHandler.logException(ICrashLogHandler.FeedEmptyCarouselException("Topics"))
            emptyList()
        } else {
            listOf(
                FeedCarousel(
                    title = title,
                    carouselItemModels = itemModels,
                    recyclerLayout = RecyclerLayout.STAGGERED_GRID_HORIZONTAL
                ),
                createItemSeparator()
            )
        }
    }

    private fun FeedItem.toThreeFourContent(
        data: CompleteFeedState,
        moduleIndex: Int
    ): List<UiModel> {
        return when {
            container == TOPPER_CONTAINER -> toTopperThreeFourContent(
                moduleIndex,
                data
            )
            else -> toThreeFourContentV2(moduleIndex)
        }
    }

    private fun FeedItem.toThreeFourContentV2(moduleIndex: Int): List<UiModel> {
        val analyticsContainer = when (entities.size) {
            1 -> "article_single"
            3 -> "three_content"
            else -> "four_content"
        }
        val numColumns = if (isTabletProvider.isTablet) 2 else 1
        val content = entities.mapIndexedNotNull { index, athleticEntity ->
            threeFourContentRenderer.toFeedGroupedItemV2(
                entity = athleticEntity,
                curatedTitle = entityCuratedTitles[athleticEntity.entityId],
                curatedImageUrl = entityCuratedImageUrls[athleticEntity.entityId],
                moduleIndex = moduleIndex,
                vIndex = index / numColumns,
                hIndex = index % numColumns,
                parentId = id,
                analyticsContainer = analyticsContainer,
                isLastItem = index == entities.lastIndex || isTabletProvider.isTablet
            )
        }

        if (content.isEmpty()) return emptyList()

        return mutableListOf<UiModel>().apply {
            if (title.isNotBlank()) {
                add(getBasicSectionHeader(moduleIndex))
            }
            if (isTabletProvider.isTablet) {
                add(FeedThreeFourContentCarousel(moduleIndex, content))
            } else {
                addAll(content)
            }
            addItemSeparator()
        }
    }

    private fun FeedItem.toTopperThreeFourContent(
        moduleIndex: Int,
        data: CompleteFeedState
    ): List<UiModel> {
        val analyticsContainer = when (entities.size) {
            1 -> "article_single"
            3 -> "three_content"
            else -> "four_content"
        }
        val topperArticles = entities.mapIndexedNotNull { index, athleticEntity ->
            threeFourContentRenderer.toFeedTopperGroupedItem(
                entity = athleticEntity,
                curatedTitle = entityCuratedTitles[athleticEntity.entityId],
                curatedImageUrl = entityCuratedImageUrls[athleticEntity.entityId],
                moduleIndex = moduleIndex,
                index = index,
                parentId = id,
                analyticsContainer = analyticsContainer,
                isLastItem = index == entities.lastIndex,
                podcastPlayerState = data.podcastPlayerState
            )
        }
        return topperArticles + listOf(createItemSeparator())
    }

    private fun FeedItem.toHeadline(moduleIndex: Int): List<UiModel> {
        val headline = entities.filterIsInstance<HeadlineEntity>().firstOrNull()
            ?: return emptyList()

        return listOf(
            threeFourContentRenderer.toFeedCuratedGroupedItem(
                entity = headline,
                curatedTitle = entityCuratedTitles[headline.entityId],
                curatedImageUrl = entityCuratedImageUrls[headline.entityId],
                moduleIndex = moduleIndex,
                vIndex = 0,
                hIndex = 0,
                parentId = id,
                analyticsContainer = "headline_single",
                isLastItem = true,
                verticalPadding = 0
            ),
            createItemSeparator()
        )
    }

    private fun FeedItem.toHeadlineList(isInTopper: Boolean, moduleIndex: Int): List<UiModel> {
        if (entities.isEmpty()) return emptyList()
        val spacing = if (isInTopper) R.dimen.global_spacing_10 else R.dimen.global_spacing_14
        val items = mutableListOf(
            getBasicSectionHeader(moduleIndex),
            FeedItemVerticalPadding(++dividerSeed, spacing)
        )

        items.addAll(
            entities.mapIndexedNotNull { index, entity ->
                headlineListRender.renderHeadlineListItem(
                    entity,
                    index,
                    moduleIndex,
                    id
                )
            }
        )

        items.add(FeedItemVerticalPadding(++dividerSeed, spacing))
        items.add(createItemSeparator())

        return items
    }

    private fun FeedItem.toMostPopular(
        moduleIndex: Int
    ): List<UiModel> {
        val articles = entities.filterIsInstance<ArticleEntity>()
        if (articles.isEmpty()) return emptyList()

        return when {
            isTabletProvider.isTablet -> listOf(
                getBasicSectionHeader(moduleIndex),
                feedItemRenderers.mostPopularArticlesCarousel(
                    articles.take(FeedMostPopularCarousel.MOST_POPULAR_MAX_ITEMS_SINGLE_COLUMN),
                    moduleIndex,
                    isTabletProvider.isTablet
                ),
                createItemSeparator()
            )
            else -> mutableListOf<UiModel>().apply {
                add(getBasicSectionHeader(moduleIndex))
                addAll(
                    feedItemRenderers.mostPopularArticles(
                        articles.take(FeedMostPopularCarousel.MOST_POPULAR_MAX_ITEMS_SINGLE_COLUMN),
                        moduleIndex,
                        isTabletProvider.isTablet
                    )
                )
                add(createItemSeparator())
            }
        }
    }

    private fun FeedItem.toInsidersCarousel(moduleIndex: Int): List<UiModel> {
        val carousel = articleRenderers.renderInsidersCarousel(compoundEntities, moduleIndex)
        return if (carousel.carouselItemModels.isEmpty()) {
            crashHandler.logException(ICrashLogHandler.FeedEmptyCarouselException("Insiders"))
            emptyList()
        } else {
            listOf(
                getBasicSectionHeader(moduleIndex),
                articleRenderers.renderInsidersCarousel(compoundEntities, moduleIndex),
                FeedItemVerticalPadding(++dividerSeed, R.dimen.global_spacing_32),
                createItemSeparator()
            )
        }
    }

    private fun FeedItem.toTopper(moduleIndex: Int): List<UiModel> {
        val entity = entities.firstOrNull() ?: return emptyList()
        val item = topperRenderer.renderTopper(
            entity,
            entityCuratedTitles[entity.entityId],
            entityCuratedDescriptions[entity.entityId],
            entityCuratedImageUrls[entity.entityId],
            isTablet = false,
            moduleIndex,
            id
        ) ?: return emptyList()
        return listOf(item)
    }

    private fun FeedItem.toTabletTopper(moduleIndex: Int): FeedCuratedTopperHero? {
        return entities.firstOrNull()?.let { entity ->
            topperRenderer.renderTopper(
                entity,
                entityCuratedTitles[entity.entityId],
                entityCuratedDescriptions[entity.entityId],
                entityCuratedImageUrls[entity.entityId],
                isTablet = true,
                moduleIndex,
                id
            )
        }
    }

    private fun FeedItem.toLiveRoom(moduleIndex: Int): List<UiModel> {
        val entity = entities.filterIsInstance<LiveAudioRoomEntity>().firstOrNull()
            ?: return emptyList()
        val authorImages = entity.hosts.map { it.imageUrl }.filterNot { it.isEmpty() }

        return listOf(
            LiveRoomUiModel(
                id = entity.id,
                title = entity.title,
                subtitle = entity.subtitle,
                hostImageUrls = AuthorImageStackModel(
                    authorImage1 = authorImages.getOrNull(0),
                    authorImage2 = authorImages.getOrNull(1),
                    authorImage3 = authorImages.getOrNull(2),
                    displayImageCount = min(authorImages.size, 3),
                ),
                topicLogo1 = entity.topicImages.getOrNull(0).nullIfEmpty(),
                topicLogo2 = entity.topicImages.getOrNull(1).nullIfEmpty(),
                analyticsPayload = LiveRoomAnalyticsPayload(moduleIndex = moduleIndex),
                impressionPayload = ImpressionPayload(
                    element = "live_room",
                    container = "live_room",
                    objectType = "room_id",
                    objectId = entity.id,
                    pageOrder = moduleIndex
                )
            ),
            createItemSeparator(),
        )
    }

    private fun FeedItem.toOneHeroItem(moduleIndex: Int, data: CompleteFeedState): List<UiModel> {
        val entity = entities.firstOrNull() ?: return emptyList()

        val item = feedCuratedRenderers.renderHeroItem(
            userData = data.userData,
            entity = entity,
            curatedTitles = entityCuratedTitles,
            curatedDescriptions = entityCuratedDescriptions,
            curatedImageUrls = entityCuratedImageUrls,
            podcastPlayerState = data.podcastPlayerState,
            moduleIndex = moduleIndex,
            analyticsContainer = "single_hero",
            parentId = id,
            isTablet = isTabletProvider.isTablet
        ) ?: return emptyList()

        return mutableListOf<UiModel>().apply {
            addAll(getHeroTitleWithSpacing(moduleIndex))
            add(item)
            addAll(
                getSeeAllWithSpacing(
                    moduleIndex,
                    SeeAllAnalyticsPayload(
                        container = "single_hero",
                        moduleIndex = moduleIndex,
                        parentObjectType = "curated_module_id",
                        parentObjectId = id
                    )
                )
            )
            add(FeedItemVerticalPadding(++dividerSeed, R.dimen.global_spacing_12))
            addItemSeparator()
        }
    }

    @Suppress("LongMethod")
    private fun FeedItem.toTwoHeroItem(moduleIndex: Int, data: CompleteFeedState): List<UiModel> {
        if (isTabletProvider.isTablet) return toTwoHeroItemTablet(moduleIndex, data)
        if (entities.size != 2) return emptyList()

        val hero = feedCuratedRenderers.renderHeroItem(
            userData = data.userData,
            entity = entities[0],
            curatedTitles = entityCuratedTitles,
            curatedDescriptions = entityCuratedDescriptions,
            curatedImageUrls = entityCuratedImageUrls,
            podcastPlayerState = data.podcastPlayerState,
            moduleIndex = moduleIndex,
            analyticsContainer = "two_hero",
            parentId = id,
            isTablet = isTabletProvider.isTablet
        ) ?: return emptyList()

        val athleticEntity = entities[1]
        val leftItem = threeFourContentRenderer.toFeedTopperGroupedItem(
            entity = athleticEntity,
            curatedTitle = entityCuratedTitles[athleticEntity.entityId],
            curatedImageUrl = entityCuratedImageUrls[athleticEntity.entityId],
            moduleIndex = moduleIndex,
            index = 1,
            parentId = id,
            analyticsContainer = "two_hero",
            isLastItem = true,
            podcastPlayerState = data.podcastPlayerState
        ) ?: return emptyList()

        return mutableListOf<UiModel>().apply {
            addAll(getHeroTitleWithSpacing(moduleIndex))
            add(hero)
            add(FeedItemVerticalPadding(++dividerSeed, R.dimen.global_spacing_8))
            add(FeedItemDivider(++dividerSeed, horizontalPadding = R.dimen.global_spacing_16))
            add(leftItem)
            addAll(
                getSeeAllWithSpacing(
                    moduleIndex,
                    SeeAllAnalyticsPayload(
                        container = "two_hero",
                        moduleIndex = moduleIndex,
                        parentObjectType = "curated_module_id",
                        parentObjectId = id
                    )
                )
            )
            addItemSeparator()
        }
    }

    private fun FeedItem.toTwoHeroItemTablet(
        moduleIndex: Int,
        data: CompleteFeedState
    ): List<UiModel> {
        if (entities.size != 2) return emptyList()

        val left = feedCuratedRenderers.renderHeroPhoneItem(
            userData = data.userData,
            entity = entities[0],
            curatedTitles = entityCuratedTitles,
            curatedDescriptions = entityCuratedDescriptions,
            curatedImageUrls = entityCuratedImageUrls,
            podcastPlayerState = data.podcastPlayerState,
            moduleIndex = moduleIndex,
            analyticsContainer = "two_hero",
            parentId = id,
            hIndex = 0
        ) ?: return emptyList()

        val right = feedCuratedRenderers.renderHeroPhoneItem(
            userData = data.userData,
            entity = entities[1],
            curatedTitles = entityCuratedTitles,
            curatedDescriptions = entityCuratedDescriptions,
            curatedImageUrls = entityCuratedImageUrls,
            podcastPlayerState = data.podcastPlayerState,
            moduleIndex = moduleIndex,
            analyticsContainer = "two_hero",
            parentId = id,
            hIndex = 1
        ) ?: return emptyList()

        val seeAll = getSeeAllWithSpacing(
            moduleIndex,
            SeeAllAnalyticsPayload(
                container = "two_hero",
                moduleIndex = moduleIndex,
                parentObjectType = "curated_module_id",
                parentObjectId = id
            )
        )

        return mutableListOf<UiModel>(
            FeedSideBySideCarousel(moduleIndex, listOf(left, right))
        ).apply {
            addAll(0, getHeroTitleWithSpacing(moduleIndex))
            addAll(
                if (seeAll.isEmpty()) {
                    listOf(FeedItemVerticalPadding(++dividerSeed, R.dimen.global_spacing_16))
                } else {
                    seeAll
                }
            )
            addItemSeparator()
        }
    }

    private fun FeedItem.toThreeHeroItem(moduleIndex: Int, data: CompleteFeedState): List<UiModel> {
        if (entities.size != 3) return emptyList()

        val hero = feedCuratedRenderers.renderHeroItem(
            userData = data.userData,
            entity = entities.first(),
            curatedTitles = entityCuratedTitles,
            curatedDescriptions = entityCuratedDescriptions,
            curatedImageUrls = entityCuratedImageUrls,
            podcastPlayerState = data.podcastPlayerState,
            moduleIndex = moduleIndex,
            analyticsContainer = "three_hero",
            parentId = id,
            isTablet = isTabletProvider.isTablet
        ) ?: return emptyList()

        val sideBySide = feedCuratedRenderers.renderSideBySideItem(
            userData = data.userData,
            entities = entities.drop(1),
            curatedTitles = entityCuratedTitles,
            curatedImageUrls = entityCuratedImageUrls,
            playerState = data.podcastPlayerState,
            moduleIndex = moduleIndex,
            analyticsContainer = "three_hero",
            parentId = id,
            isTablet = isTabletProvider.isTablet
        ) ?: return emptyList()

        val seeAll = getSeeAllWithSpacing(
            moduleIndex,
            SeeAllAnalyticsPayload(
                container = "three_hero",
                moduleIndex = moduleIndex,
                parentObjectType = "curated_module_id",
                parentObjectId = id
            )
        )

        return mutableListOf<UiModel>().apply {
            addAll(getHeroTitleWithSpacing(moduleIndex))
            add(hero)

            add(FeedItemVerticalPadding(++dividerSeed, R.dimen.global_spacing_8))
            add(FeedItemDivider(++dividerSeed, horizontalPadding = R.dimen.global_spacing_16))

            add(sideBySide)
            addAll(seeAll)
            addItemSeparator()
        }
    }

    @Suppress("LongMethod")
    private fun FeedItem.toFourHeroItem(moduleIndex: Int, data: CompleteFeedState): List<UiModel> {
        if (isTabletProvider.isTablet) return toFourHeroItemTablet(moduleIndex, data)
        return toFourPlusModule(moduleIndex, data)
    }

    private fun FeedItem.toFourHeroItemTablet(
        moduleIndex: Int,
        data: CompleteFeedState
    ): List<UiModel> {
        if (entities.size != 4) return emptyList()

        val hero = feedCuratedRenderers.renderHeroItem(
            userData = data.userData,
            entity = entities.first(),
            curatedTitles = entityCuratedTitles,
            curatedDescriptions = entityCuratedDescriptions,
            curatedImageUrls = entityCuratedImageUrls,
            podcastPlayerState = data.podcastPlayerState,
            moduleIndex = moduleIndex,
            analyticsContainer = "four_hero",
            parentId = id,
            isTablet = isTabletProvider.isTablet
        ) ?: return emptyList()

        val articles = entities.drop(1).mapIndexedNotNull { index, entity ->
            feedCuratedRenderers.renderSingleSideBySideItem(
                userData = data.userData,
                entity = entity,
                curatedTitles = entityCuratedTitles,
                curatedImageUrls = entityCuratedImageUrls,
                playerState = data.podcastPlayerState,
                moduleIndex = moduleIndex,
                hIndex = index,
                vIndex = 1,
                analyticsContainer = "four_hero",
                parentId = id,
                titleMaxLines = 4,
                useTopPadding = false
            )
        }

        val seeAll = getSeeAllWithSpacing(
            moduleIndex,
            SeeAllAnalyticsPayload(
                container = "four_hero",
                moduleIndex = moduleIndex,
                parentObjectType = "curated_module_id",
                parentObjectId = id
            )
        )

        return mutableListOf<UiModel>().apply {
            addAll(getHeroTitleWithSpacing(moduleIndex))
            add(hero)

            add(FeedItemDivider(++dividerSeed, horizontalPadding = R.dimen.global_spacing_20))

            add(FeedFourItemHeroCarousel(moduleIndex, articles))
            addAll(
                if (seeAll.isEmpty()) {
                    listOf(FeedItemVerticalPadding(++dividerSeed, R.dimen.global_spacing_12))
                } else {
                    seeAll
                }
            )
            addItemSeparator()
        }
    }

    private fun FeedItem.toFiveSixHeroItem(
        moduleIndex: Int,
        data: CompleteFeedState
    ): List<UiModel> = toFourPlusModule(moduleIndex, data)

    @Suppress("LongMethod")
    private fun FeedItem.toSevenPlusHeroItem(
        moduleIndex: Int,
        data: CompleteFeedState
    ): List<UiModel> = toFourPlusModule(moduleIndex, data)

    private fun FeedItem.toSpotlight(moduleIndex: Int): List<UiModel> {
        return mutableListOf<UiModel>().apply {
            add(
                getBasicSectionHeader(
                    moduleIndex,
                    SeeAllAnalyticsPayload(
                        container = "a1", moduleIndex = moduleIndex
                    )
                )
            )

            val spotlight = toSpotlightCarousel(moduleIndex)

            if (spotlight.isNotEmpty()) {
                addAll(spotlight)
            } else {
                return emptyList()
            }

            addItemSeparator()
        }
    }

    private fun FeedItem.toSpotlightCarousel(moduleIndex: Int): List<UiModel> {
        val models = compoundEntities.mapIndexedNotNull { index, entities ->
            val article = entities.filterIsInstance<ArticleEntity>()
                .firstOrNull() ?: return@mapIndexedNotNull null
            val authors = entities.filterIsInstance<InsiderEntity>()

            feedSpotlightRenderer.renderSpotlight(
                article,
                authors,
                entityCuratedTitles[article.entityId] ?: article.articleTitle.orEmpty(),
                entityCuratedDescriptions[article.entityId] ?: article.excerpt.orEmpty(),
                moduleIndex,
                index
            )
        }
        return if (models.isNotEmpty()) {
            listOf(FeedCarousel(carouselItemModels = models))
        } else {
            emptyList()
        }
    }

    private fun FeedItem.toLiveBlogsCarousel(pageIndex: Int): List<UiModel> {
        val liveBlogs = entities
            .filterIsInstance<LiveBlogEntity>()
            .filter { it.isLive }
            .sortedByDescending { it.lastActivityAt }

        val items = liveBlogs.mapIndexed { rowIndex, liveBlog ->
            val lastActivity = lastActivityDateFormatter.format(liveBlog.lastActivityAt)
            val analyticsPayload = LiveBlogAnalyticsPayload(
                pageOrder = pageIndex.toString(),
                horizontalIndex = rowIndex.toString()
            )

            LiveBlogCarouselItem(
                id = liveBlog.id,
                title = liveBlog.title,
                lastActivity = lastActivity,
                analyticsPayload = analyticsPayload
            )
        }

        val impressionPayload = ImpressionPayload(
            element = "live_blogs",
            container = "live_blogs",
            objectType = "live_blog_id",
            objectId = items.firstOrNull()?.id.orEmpty(),
            hIndex = 0,
            pageOrder = pageIndex
        )

        return listOf(FeedLiveBlogCarousel(pageIndex, items, impressionPayload))
    }

    private fun FeedItem.toScoresCarousel(moduleIndex: Int): List<UiModel> {
        val games = entities.filterIsInstance<BoxScoreEntity>()
        val showDiscussButtonMap = games.associate { it.id to shouldShowDiscuss(it) }.takeIf {
            isDiscussButtonEnabled
        }.orEmpty()
        val carousel = scoresRenderer.renderFeedBoxScoreCarousel(
            games,
            moduleIndex,
            entityCuratedDisplayOrder,
            showDiscussButtonMap
        )
        return if (carousel.carouselItemModels.isEmpty()) {
            crashHandler.logException(ICrashLogHandler.FeedEmptyCarouselException("Scores"))
            emptyList()
        } else {
            listOf(
                carousel,
                ListVerticalPadding(R.dimen.feed_item_spacing_normal)
            )
        }
    }

    private fun shouldShowDiscuss(game: BoxScoreEntity): Boolean {
        // Discovery areas start showing discovery items 5 hours before start time && stops 6 hours after
        val hasLiveDiscussion = game.availableGameCoverage.contains(GameCoverageType.DISCOVERABLE_COMMENTS)
        val hasTeamSpecificNavigation = features.areTeamSpecificCommentsEnabled && game.availableGameCoverage.contains(
            GameCoverageType.COMMENTS_NAVIGATION
        )
        return hasLiveDiscussion && hasTeamSpecificNavigation
    }

    private val isDiscussButtonEnabled = features.isBoxScoresDiscussTabEnabled && userManager.isUserSubscribed()

    private fun FeedItem.toFourFiveGallery(
        moduleIndex: Int,
        data: CompleteFeedState
    ): List<UiModel> {
        if (entities.size < 4) return emptyList()
        return toGalleryModule(moduleIndex, data)
    }

    private fun FeedItem.toSixPlusGallery(
        moduleIndex: Int,
        data: CompleteFeedState
    ): List<UiModel> {
        if (entities.size < 6) return emptyList()
        return toGalleryModule(moduleIndex, data)
    }

    private fun FeedItem.toFourPlusModule(
        moduleIndex: Int,
        data: CompleteFeedState
    ): List<UiModel> {
        if (entities.size < 4) return emptyList()
        val container = "fourplus_hero"

        val topper = feedCuratedRenderers.renderHeroItem(
            userData = data.userData,
            entity = entities.first(),
            curatedTitles = entityCuratedTitles,
            curatedDescriptions = entityCuratedDescriptions,
            curatedImageUrls = entityCuratedImageUrls,
            podcastPlayerState = data.podcastPlayerState,
            moduleIndex = moduleIndex,
            analyticsContainer = container,
            parentId = id,
            isTablet = isTabletProvider.isTablet
        ) ?: return emptyList()

        val carouselItems = entities.drop(1).mapIndexedNotNull { index, athleticEntity ->
            feedCuratedRenderers.renderCuratedCarouselItem(
                athleticEntity = athleticEntity,
                userData = data.userData,
                curatedTitles = entityCuratedTitles,
                curatedImageUrls = entityCuratedImageUrls,
                podcastPlayerState = data.podcastPlayerState,
                moduleIndex = moduleIndex,
                vIndex = 1,
                hIndex = index,
                analyticsContainer = container,
                parentId = id
            )
        }

        return getHeroTitleWithSpacing(moduleIndex) + listOf(
            topper,
            FeedItemVerticalPadding(++dividerSeed, R.dimen.global_spacing_12),
            FeedItemDivider(++dividerSeed, horizontalPadding = R.dimen.global_spacing_16),
            FeedItemVerticalPadding(++dividerSeed, R.dimen.global_spacing_24),
            FeedHeroCarousel(
                id = moduleIndex,
                carouselItemModels = carouselItems,
                recyclerLayout = RecyclerLayout.LINEAR_HORIZONTAL
            ),
            FeedItemVerticalPadding(++dividerSeed, R.dimen.global_spacing_24),
            createItemSeparator()
        )
    }

    private fun FeedItem.toGalleryModule(
        moduleIndex: Int,
        data: CompleteFeedState
    ): List<UiModel> {
        val carouselItems = entities.mapIndexedNotNull { index, athleticEntity ->
            feedCuratedRenderers.renderCuratedCarouselItem(
                athleticEntity = athleticEntity,
                userData = data.userData,
                curatedTitles = entityCuratedTitles,
                curatedImageUrls = entityCuratedImageUrls,
                podcastPlayerState = data.podcastPlayerState,
                moduleIndex = moduleIndex,
                vIndex = 1,
                hIndex = index,
                analyticsContainer = "fourplus_gallery",
                parentId = id
            )
        }

        return getHeroTitleWithSpacing(moduleIndex) + listOf(
            FeedHeroCarousel(
                id = moduleIndex,
                carouselItemModels = carouselItems,
                recyclerLayout = RecyclerLayout.LINEAR_HORIZONTAL
            ),
            FeedItemVerticalPadding(++dividerSeed, R.dimen.global_spacing_24),
            createItemSeparator()
        )
    }

    private fun createItemSeparator() = ListVerticalPadding(R.dimen.feed_item_divider_height)

    private fun MutableList<UiModel>.addItemSeparator() {
        add(createItemSeparator())
    }

    private fun FeedItem.getSeeAllWithSpacing(
        moduleIndex: Int,
        analyticsPayload: SeeAllAnalyticsPayload,
        @DimenRes verticalPadding: Int = R.dimen.global_spacing_16,
        @DimenRes horizontalPadding: Int = R.dimen.feed_horizontal_padding
    ) = action?.let {
        listOf(
            FeedItemVerticalPadding(++dividerSeed, verticalPadding),
            FeedItemDivider(++dividerSeed, horizontalPadding = horizontalPadding),
            FeedSeeAllButton(
                moduleIndex.toLong(),
                it.actionText,
                it.deeplink,
                analyticsPayload
            )
        )
    } ?: emptyList()

    private fun FeedItem.getHeroTitleWithSpacing(
        moduleIndex: Int
    ) = if (description.isNotBlank()) {
        listOf(getSectionHeaderWithDescription(moduleIndex))
    } else {
        listOf(
            getBasicSectionHeader(moduleIndex),
            FeedItemVerticalPadding(++dividerSeed, R.dimen.global_spacing_24)
        )
    }

    private fun FeedItem.getSectionHeaderWithDescription(moduleIndex: Int) =
        SectionHeaderWithDescription(
            moduleIndex,
            title,
            description,
            titleImageUrl,
            titleImageUrl.isNotEmpty()
        )

    private fun FeedItem.getBasicSectionHeader(
        moduleIndex: Int,
        analyticsPayload: SeeAllAnalyticsPayload?
    ) = action?.let {
        BasicSectionHeader(
            id = moduleIndex,
            title = title,
            actionText = it.actionText,
            deeplink = it.deeplink,
            imageUrl = titleImageUrl,
            showImage = titleImageUrl.isNotEmpty(),
            analyticsPayload = analyticsPayload
        )
    } ?: getBasicSectionHeader(moduleIndex)

    private fun FeedItem.getBasicSectionHeader(moduleIndex: Int) = BasicSectionHeader(
        moduleIndex,
        title,
        imageUrl = titleImageUrl,
        showImage = titleImageUrl.isNotEmpty()
    )

    private fun FeedItem.toAdWrapper(data: CompleteFeedState): List<UiModel> {
        if (!data.feedType.shouldDisplayAds(features)) {
            return emptyList()
        }

        if (data.hideFeedItemIds.contains(id)) {
            return emptyList()
        }
        val model = AdWrapperUiModel(
            id = id,
            page = page,
            adView = data.adList[id]?.adView
        )
        return listOf(model)
    }

    private fun AuthorDetails.getHeader(data: CompleteFeedState) = FeedAuthorHeader(
        id = id,
        name = name,
        imageUrl = imageUrl,
        description = description,
        twitterHandle = twitterHandle,
        isUserFollowing = data.isFollowingTopic
    )
}