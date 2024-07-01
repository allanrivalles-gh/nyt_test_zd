package com.theathletic.analytics.data.remote

import com.theathletic.analytics.newarch.schemas.AnalyticsSchema

@Suppress("ConstructorParameterNaming")
data class ImpressionEventRecordRemote(
    // Metadata
    val event_timestamp: Long,
    val user_id: Long,
    val device_id: String?,
    val platform: String,
    val browser: String,
    val browser_version: String?,
    val locale: String?,
    val user_agent: String,
    val session_id: String?,

    // Event data
    override val verb: String,
    override val view: String,
    override val object_type: String,
    override val object_id: String,
    override val impress_start_time: Long,
    override val impress_end_time: Long,
    override val filter_type: String?,
    override val filter_id: Long?,
    override val v_index: Long,
    override val h_index: Long,
    override val element: String,
    override val container: String?,
    override val page_order: Long?,
    override val parent_object_type: String?,
    override val parent_object_id: String?
) : RemoteAnalyticsRecord, AnalyticsSchema.Contract.Impression