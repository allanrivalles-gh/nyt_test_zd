package com.theathletic.impressions

import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.Chronos
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class ImpressionsDispatcher @AutoKoin constructor(private val chronos: Chronos) {

    private var dispatchInterval = 500L
    private var onImpression: (ImpressionEvent) -> Unit = {}

    private val dispatchedImpressions = hashSetOf<ImpressionPayload>()
    private val impressionsToDispatch = hashMapOf<ImpressionPayload, Long>()

    fun listenToImpressionEvents(interval: Duration = 500.milliseconds, onImpression: (ImpressionEvent) -> Unit) {
        this.dispatchInterval = interval.inWholeMilliseconds
        this.onImpression = onImpression
    }

    fun registerImpression(visibility: Visibility, payload: ImpressionPayload) {
        if (isPayloadRegistered(visibility, payload)) return

        val currentTime = chronos.currentTimeMs
        if (shouldRegisterPayload(visibility, payload)) {
            impressionsToDispatch[payload] = currentTime
        } else {
            val startTime = impressionsToDispatch.remove(payload) ?: Long.MAX_VALUE
            val shouldDispatchImpression = (currentTime - startTime) >= dispatchInterval
            if (shouldDispatchImpression) {
                onImpression(payload.toImpressionEvent(startTime, currentTime))
                dispatchedImpressions.add(payload)
            }

            if (visibility == Visibility.GONE) dispatchedImpressions.remove(payload)
        }
    }

    private fun isPayloadRegistered(
        visibility: Visibility,
        payload: ImpressionPayload
    ) = visibility == Visibility.VISIBLE && impressionsToDispatch.contains(payload)

    private fun shouldRegisterPayload(
        visibility: Visibility,
        payload: ImpressionPayload
    ) = visibility == Visibility.VISIBLE && dispatchedImpressions.contains(payload).not()
}

fun ImpressionPayload.toImpressionEvent(startTime: Long, endTime: Long) = ImpressionEvent(
    objectType = objectType,
    objectId = objectId,
    element = element,
    pageOrder = pageOrder,
    container = container,
    hIndex = hIndex,
    vIndex = vIndex,
    parentObjectType = parentObjectType,
    parentObjectId = parentObjectId,
    startTime = startTime,
    endTime = endTime
)

data class ImpressionEvent(
    val objectType: String = "",
    val objectId: String = "",
    val element: String = "",
    val pageOrder: Int = 0,
    val container: String? = null,
    val hIndex: Long = -1,
    val vIndex: Long = -1,
    val parentObjectType: String? = null,
    val parentObjectId: String? = null,
    val startTime: Long = Long.MAX_VALUE,
    val endTime: Long = Long.MAX_VALUE,
)