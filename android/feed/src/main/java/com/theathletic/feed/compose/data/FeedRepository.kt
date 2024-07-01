package com.theathletic.feed.compose.data

import com.theathletic.FeedLiveGamesSubscription
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.feed.LocalFeaturedGame
import com.theathletic.entity.feed.LocalFeed
import com.theathletic.entity.feed.LocalLayout
import com.theathletic.entity.feed.LocalScoresGame
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.feed.BuildConfig
import com.theathletic.feed.compose.data.local.toLocal
import com.theathletic.feed.compose.data.local.toLocalModel
import com.theathletic.feed.compose.data.remote.FeedApi
import com.theathletic.user.IUserManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import java.lang.IllegalStateException

internal class FeedRepository @AutoKoin constructor(
    private val feedApi: FeedApi,
    private val entityDataSource: EntityDataSource,
    private val userManager: IUserManager
) {
    fun observeFeed(feedRequest: FeedRequest): Flow<Feed> = observeFeed(feedRequest.key)
        .filterNotNull()
        .map { it.toDomain(userManager.isUserSubscribed()) }

    suspend fun fetchFeed(feedRequest: FeedRequest, page: Int) {
        val feedResult = feedApi.fetchFeed(feedRequest, page = page).toLocal(feedRequest)

        val feed = mergeWithCache(key = feedRequest.key, feedResult)
        val items = feedResult.layouts.flatMap { it.items }
        // We had to divide the data into `items` and `feed` because otherwise it wouldn't fit in a single json.
        // Because we are now calling `entityDataSource.insert` twice, `entityDataSource.updateFlow` would emit twice
        // for a single fetch result. But this data separation should be transparent for users of the `FeedRepository`
        // and it should only emit once for a fetch result. We achieve that by filtering in the method `observeFeed`
        // by `AthleticEntity.Type.FEED`. That means that we will only emit when inserting the `feed` and
        // not the `items`. For that to work, we need to make sure we keep the insertion of the `feed` last.
        entityDataSource.insert(items)
        entityDataSource.insert(feed)
    }

    private suspend fun mergeWithCache(key: String, feedResult: LocalFeed): LocalFeed {
        if (feedResult.pageInfo.currentPage == 0) return feedResult
        val cachedFeed = getCachedFeed(key) ?: return feedResult
        if (cachedFeed.pageInfo.currentPage != feedResult.pageInfo.currentPage - 1) {
            if (BuildConfig.DEBUG) {
                throw IllegalStateException("New page is not a direct follow up of the one in the cache.")
            }
        }
        return feedResult.copy(layouts = cachedFeed.layouts + feedResult.layouts)
    }

    private fun observeFeed(id: String) = entityDataSource.updateFlow
        .filter { it.contains(AthleticEntity.Type.FEED) }
        .map { getCachedFeed(id) }

    private suspend fun getCachedFeed(id: String): LocalFeed? {
        val cachedFeed = entityDataSource.get<LocalFeed>(id) ?: return null
        val entityIds = cachedFeed.layouts.flatMap { it.entityIds }
        val items = entityDataSource.getEntities(entityIds).associateBy { it.entityId }
        return cachedFeed.copy(layouts = cachedFeed.layouts.populatedWith(items))
    }

    private fun List<LocalLayout>.populatedWith(
        items: Map<AthleticEntity.Id, AthleticEntity>
    ) = map { layout ->
        layout.copy(items = layout.entityIds.mapNotNull(items::get))
    }

    suspend fun subscribeToLiveGameUpdates(key: String, gameIds: List<String>) {
        try {
            feedApi.getLiveGameUpdates(gameIds).collect { data -> mergeGameData(key, data) }
        } catch (error: Throwable) {
            throw Exception(
                "Subscribing to live game updates failed for ids: $gameIds with error: ${error.message}"
            )
        }
    }

    private suspend fun mergeGameData(key: String, data: FeedLiveGamesSubscription.Data) {
        val localGame = data.liveScoreUpdates?.toLocalModel() ?: return
        entityDataSource.update<LocalFeaturedGame>(localGame.id) { copy(game = localGame) }
        entityDataSource.update<LocalScoresGame>(localGame.id) { copy(game = localGame) }
        getCachedFeed(key)?.let { feed -> entityDataSource.insert(feed) }
    }
}