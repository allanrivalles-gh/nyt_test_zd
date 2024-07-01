package com.theathletic.feed.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.theathletic.entity.main.FeedItem
import com.theathletic.entity.main.FeedResponse
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FeedDao {
    @Query("SELECT DISTINCT * FROM feed_response WHERE feed_response.feedId = :feedId ")
    abstract fun getFeedResponse(feedId: String): Flow<FeedResponse?>

    @Query(
        "SELECT DISTINCT feed_item.* FROM feed_item " +
            "WHERE feed_item.feedId = :feedId "
    )
    abstract suspend fun getFeedItems(feedId: String): List<FeedItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertFeedResponse(feedResponse: FeedResponse)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertItems(items: List<FeedItem>)

    @Suppress("LongParameterList")
    @Transaction
    open suspend fun insertFullFeedResponse(
        feedResponse: FeedResponse,
        items: List<FeedItem>,
        forceRefresh: Boolean
    ) {
        if (forceRefresh) {
            clearOldFeedData(feedResponse.feedId)
        } else {
            feedResponse.feed.maxOfOrNull { it.page }?.let { page ->
                clearItemsOnCurrentAndSubsequentPages(feedResponse.feedId, page)
            }
        }
        insertFeedResponse(feedResponse)
        insertItems(items)
    }

    @Query("DELETE FROM feed_response WHERE feedId = :feedId ")
    abstract suspend fun clearFeedResponses(feedId: String)

    @Query("DELETE FROM feed_item WHERE feedId = :feedId ")
    abstract suspend fun clearItems(feedId: String)

    @Query("DELETE FROM feed_item WHERE feedId = :feedId AND page >= :currentPage")
    abstract suspend fun clearItemsOnCurrentAndSubsequentPages(feedId: String, currentPage: Int)

    @Transaction
    open suspend fun clearOldFeedData(feedId: String) {
        clearFeedResponses(feedId)
        clearItems(feedId)
    }
}