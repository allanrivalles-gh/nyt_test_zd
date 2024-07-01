package com.theathletic.analytics.impressions

data class ImpressionPayload(
    val objectType: String,
    val objectId: String,
    val element: String,
    val pageOrder: Int,
    val container: String? = null,
    val hIndex: Long = -1,
    val vIndex: Long = -1,
    val parentObjectType: String? = null,
    val parentObjectId: String? = null
)