package com.theathletic.analytics.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flexible_analytics_events")
data class FlexibleAnalyticsEvent(
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0L,
    var timestampMs: Long = 0L,

    var kafkaTopicName: String,
    var schemaJsonBlob: String,
    var extrasJsonBlob: String
)