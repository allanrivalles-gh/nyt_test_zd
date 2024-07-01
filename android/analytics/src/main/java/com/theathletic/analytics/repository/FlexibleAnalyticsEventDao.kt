package com.theathletic.analytics.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.theathletic.analytics.data.local.FlexibleAnalyticsEvent

@Dao
abstract class FlexibleAnalyticsEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertEvents(events: List<FlexibleAnalyticsEvent>)

    @Query("SELECT * FROM flexible_analytics_events WHERE kafkaTopicName = :topicName LIMIT :batchSize")
    abstract suspend fun getEvents(
        topicName: String,
        batchSize: Int
    ): List<FlexibleAnalyticsEvent>

    @Query("SELECT COUNT(*) FROM flexible_analytics_events WHERE kafkaTopicName = :topicName")
    abstract suspend fun getEventCount(topicName: String): Long

    @Delete
    abstract suspend fun deleteEvents(events: List<FlexibleAnalyticsEvent>)

    @Query("DELETE FROM flexible_analytics_events WHERE datetime(timestampMs / 1000, 'unixepoch', 'utc') <= datetime('now','utc',:timeSpan)")
    abstract suspend fun deleteEventsBefore(timeSpan: String)

    @Query("DELETE FROM flexible_analytics_events")
    abstract suspend fun deleteAllEvents()
}