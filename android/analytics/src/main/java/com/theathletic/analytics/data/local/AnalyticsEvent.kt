package com.theathletic.analytics.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.theathletic.data.LocalModel

@Entity(tableName = "analytics_events")
data class AnalyticsEvent(
    val verb: String,
    val previousView: String? = null,
    val view: String? = null,
    val element: String? = null,
    val objectType: String? = null,
    val objectId: String? = null,
    val source: String? = null,
    val metaBlob: Map<String, String>? = null,
    @Deprecated("Use timestamp instead")
    var dateTime: String? = null,
    var timestampMs: Long = 0L,
    @PrimaryKey(autoGenerate = true) var uid: Long = 0L
) : LocalModel