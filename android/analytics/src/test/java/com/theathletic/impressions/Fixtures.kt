package com.theathletic.impressions

import com.theathletic.analytics.impressions.ImpressionPayload

internal fun impressionPayloadFixture(
    objectId: String = "objectId",
    objectType: String = "objectType",
    element: String = "element",
    pageOrder: Int = 0
) = ImpressionPayload(
    objectId = objectId,
    objectType = objectType,
    element = element,
    pageOrder = pageOrder
)

internal fun impressionEventFixture(
    objectId: String = "objectId",
    objectType: String = "objectType",
    element: String = "element",
    pageOrder: Int = 0,
    startTime: Long = Long.MAX_VALUE,
    endTime: Long = Long.MAX_VALUE
) = ImpressionEvent(
    objectId = objectId,
    objectType = objectType,
    element = element,
    pageOrder = pageOrder,
    startTime = startTime,
    endTime = endTime
)