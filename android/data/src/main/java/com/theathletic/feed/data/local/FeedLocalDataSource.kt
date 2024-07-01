package com.theathletic.feed.data.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.entity.main.FeedItem
import com.theathletic.entity.main.FeedResponse
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext

class FeedLocalDataSource @AutoKoin(Scope.SINGLE) constructor(
    private val feedDao: FeedDao,
    private val entityDataSource: EntityDataSource,
    private val dispatcherProvider: DispatcherProvider
) {
    @Suppress("LongParameterList")
    suspend fun insertFullFeedResponse(
        feedResponse: FeedResponse,
        items: List<FeedItem>,
        forceRefresh: Boolean
    ) {
        feedDao.insertFullFeedResponse(
            feedResponse,
            items,
            forceRefresh
        )
    }

    fun getFeedResponseDistinct(feedId: String) =
        feedDao.getFeedResponse(feedId).distinctUntilChanged()

    suspend fun getFeed(feedId: String): List<FeedItem> {
        return withContext(dispatcherProvider.io) {
            val feedItems = feedDao.getFeedItems(feedId)

            val allEntityIds = feedItems.flatMap { it.entityIds } +
                feedItems.flatMap { it.compoundEntityIds.flatten() }
            val entityModels = entityDataSource.getEntities(allEntityIds)
                .associateBy { it.entityId }

            val populatedFeedItems = feedItems.map {
                it.apply {
                    // Keep the order of entities based on the entitiyIds list
                    entities = entityIds.mapNotNull(entityModels::get)
                    compoundEntities = compoundEntityIds.map { list -> list.mapNotNull(entityModels::get) }
                }
            }

            populatedFeedItems
        }
    }
}