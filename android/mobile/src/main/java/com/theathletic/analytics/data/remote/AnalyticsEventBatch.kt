package com.theathletic.analytics.data.remote

import com.google.gson.annotations.SerializedName
import com.theathletic.data.RemoteModel

data class AnalyticsEventBatch(
    val platform: String = "android",
    val version: String,
    val topic: String,
    @SerializedName("schema_id") val schemaId: Int,
    val records: List<RemoteAnalyticsRecord>
) : RemoteModel

interface RemoteAnalyticsRecord : RemoteModel

@Suppress("ConstructorParameterNaming")
data class AnalyticsEventRemote(
    // Metadata
    val event_timestamp: Long,
    val user_id: Long,
    val device_id: String?,
    val is_subscriber: Boolean,
    val platform: String,
    val browser: String,
    val browser_version: String?,
    val locale: String?,
    val user_agent: String,
    val session_id: String?,
    val ip_address: String?,

    // Event data
    val verb: String,
    val view: String,
    val element: String?,
    val object_type: String?,
    val object_id: String?,
    val meta_blob: String,
    val source: String?,
    val previous_view: String?
) : RemoteAnalyticsRecord