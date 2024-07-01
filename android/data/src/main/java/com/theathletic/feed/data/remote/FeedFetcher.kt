package com.theathletic.feed.data.remote

import com.theathletic.FeedQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.datetime.TimeProvider
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.entity.main.FeedResponse
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.entity.main.fromEntity
import com.theathletic.entity.settings.UserContentEdition
import com.theathletic.featureswitch.Features
import com.theathletic.feed.FeedType
import com.theathletic.feed.data.local.FeedLocalDataSource
import com.theathletic.podcast.data.local.PodcastDao
import com.theathletic.podcast.data.local.PodcastEpisodeEntity
import com.theathletic.type.FeedConsumableType
import com.theathletic.type.LayoutType
import com.theathletic.user.IUserManager
import com.theathletic.utility.FeedPreferences
import com.theathletic.utility.coroutines.DispatcherProvider
import com.theathletic.utility.logging.ICrashLogHandler

class FeedFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val feedApi: FeedGraphqlApi,
    private val userManager: IUserManager,
    private val feedPreferences: FeedPreferences,
    private val timeProvider: TimeProvider,
    private val feedLocalDataSource: FeedLocalDataSource,
    private val entityDataSource: EntityDataSource,
    private val podcastDao: PodcastDao,
    private val featureSwitch: Features,
    private val crashLogHandler: ICrashLogHandler
) : RemoteToLocalFetcher<
    FeedFetcher.Params,
    FeedQuery.Data,
    FeedResponse>(dispatcherProvider) {

    data class Params(
        val feedType: FeedType,
        val forceRefresh: Boolean,
        val page: Int,
        val isAdsEnabled: Boolean,
        val contentEdition: UserContentEdition?,
    )

    override suspend fun makeRemoteRequest(params: Params): FeedQuery.Data? {
        val feedId = when (params.feedType) {
            FeedType.User -> userManager.getCurrentUserId()
            else -> params.feedType.id
        }

        val remoteResponse = feedApi.getFeed(
            id = feedId,
            type = params.feedType,
            page = params.page,
            contentEdition = params.contentEdition,
            layouts = if (params.isAdsEnabled) layoutsToRequestWithAds else layoutsToRequest,
            adsEnabled = params.isAdsEnabled
        )
        if (remoteResponse == null) {
            crashLogHandler.logException(
                FeedFetcherException("Received empty response when fetching feedType: ${params.feedType.compositeId} with params: $params")
            )
        }
        return remoteResponse?.data
    }

    class FeedFetcherException(message: String) : Throwable(message)

    private val consumablesArticleTypes = listOf(
        FeedConsumableType.article,
        FeedConsumableType.discussion,
        FeedConsumableType.qanda
    )

    private val consumablesAll = listOf(
        FeedConsumableType.article,
        FeedConsumableType.podcast_episode,
        FeedConsumableType.discussion,
        FeedConsumableType.qanda,
        FeedConsumableType.liveBlog,
        FeedConsumableType.news,
    )

    private val consumablesCurated = listOf(
        FeedConsumableType.article,
        FeedConsumableType.discussion,
        FeedConsumableType.qanda,
        FeedConsumableType.news,
        FeedConsumableType.liveBlog
    )

    private val layoutsToRequest = mutableMapOf<LayoutType, List<FeedConsumableType>>(
        LayoutType.recommended_podcasts to listOf(FeedConsumableType.podcast),
        LayoutType.podcast_episodes_list to listOf(FeedConsumableType.podcast_episode),
        LayoutType.topic to listOf(FeedConsumableType.topic),
        LayoutType.announcement to listOf(FeedConsumableType.announcement),
        LayoutType.single_headline to listOf(FeedConsumableType.news),
        LayoutType.headlines_list to listOf(FeedConsumableType.news),
        LayoutType.curated_content_list to consumablesCurated,
        LayoutType.one_content_curated to consumablesCurated,
        LayoutType.three_content_curated to consumablesCurated,
        LayoutType.four_content_curated to consumablesCurated,
        LayoutType.shortforms to listOf(FeedConsumableType.brief),
        LayoutType.single_content to consumablesArticleTypes,
        LayoutType.four_content to consumablesArticleTypes,
        LayoutType.three_content to consumablesArticleTypes,
        LayoutType.highlight_three_content to consumablesArticleTypes,
        LayoutType.more_for_you to consumablesArticleTypes,
        LayoutType.most_popular_articles to listOf(FeedConsumableType.article),
        LayoutType.one_hero_curation to consumablesAll,
        LayoutType.two_hero_curation to consumablesAll,
        LayoutType.three_hero_curation to consumablesAll,
        LayoutType.four_hero_curation to consumablesAll,
        LayoutType.five_hero_curation to consumablesAll,
        LayoutType.six_hero_curation to consumablesAll,
        LayoutType.seven_plus_hero_curation to consumablesAll,
        LayoutType.one_content to consumablesArticleTypes,
        LayoutType.scores to listOf(FeedConsumableType.feed_game),
        LayoutType.four_gallery_curation to consumablesAll,
        LayoutType.five_gallery_curation to consumablesAll,
        LayoutType.six_plus_gallery_curation to consumablesAll,
        LayoutType.spotlight_carousel to listOf(FeedConsumableType.spotlight),
        LayoutType.insiders to listOf(FeedConsumableType.insider),
        LayoutType.live_room to listOf(FeedConsumableType.liveRoom)
    ).apply {
        if (featureSwitch.isLiveBlogRibbonEnabled) {
            put(LayoutType.live_blogs, listOf(FeedConsumableType.liveBlog))
        }
    }

    private val layoutsToRequestWithAds: Map<LayoutType, List<FeedConsumableType>>
        get() {
            val layoutsWithAds = layoutsToRequest.toMutableMap()
            layoutsWithAds[LayoutType.dropzone] = listOf(FeedConsumableType.dropzone)
            return layoutsWithAds
        }

    override fun mapToLocalModel(
        params: Params,
        remoteModel: FeedQuery.Data
    ) = remoteModel.toLocalModel(
        feedId = params.feedType.compositeId,
        page = params.page
    )

    override suspend fun saveLocally(params: Params, dbModel: FeedResponse) {
        saveFeedResponse(params, dbModel)
    }

    private suspend fun saveFeedResponse(
        params: Params,
        feedResponse: FeedResponse
    ) {
        val entities = feedResponse.allEntities

        entities.filterIsInstance<PodcastEpisodeEntity>().forEach {
            podcastDao.insertPodcastEpisodeStandalone(PodcastEpisodeItem.fromEntity(it))
        }

        entityDataSource.insertOrUpdate(entities)

        feedLocalDataSource.insertFullFeedResponse(
            feedResponse,
            feedResponse.feed,
            params.forceRefresh
        )

        feedPreferences.setFeedLastFetchDate(params.feedType, timeProvider.currentTimeMs)
    }
}