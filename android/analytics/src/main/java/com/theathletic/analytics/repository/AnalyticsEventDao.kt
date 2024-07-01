package com.theathletic.analytics.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.theathletic.analytics.data.local.AnalyticsEvent

@Dao
abstract class AnalyticsEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertEvents(events: List<AnalyticsEvent>)

    @Query("SELECT * FROM analytics_events LIMIT :batchSize")
    abstract suspend fun getEvents(batchSize: Int): List<AnalyticsEvent>

    @Query("SELECT COUNT(*) FROM analytics_events")
    abstract suspend fun getEventCount(): Long

    @Query("DELETE FROM analytics_events WHERE dateTime <= datetime('now','utc',:timeSpan)")
    abstract suspend fun deleteEventsBefore(timeSpan: String)

    @Delete
    abstract suspend fun deleteEvents(events: List<AnalyticsEvent>)
}