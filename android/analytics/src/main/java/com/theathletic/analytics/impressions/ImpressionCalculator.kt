package com.theathletic.analytics.impressions

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.TimeProvider

/**
 * This class takes in visibility information then fires impression events depending on the
 * configuration defined when [configure] is called. Pipe in the output from [ViewVisibilityTracker]
 * for best results.
 */
class ImpressionCalculator @AutoKoin constructor(
    private val timeProvider: TimeProvider
) {
    private data class ItemState(val startTimeMs: Long)

    private var onImpression: ((ImpressionPayload, startTime: Long, endTime: Long) -> Unit)? = null
    private var percentVisibleForImpression: Float = 0.8f
    private var timeForImpressionMs: Long = 500L

    private val impressionsFired = mutableSetOf<ImpressionPayload>()
    private val states = mutableMapOf<ImpressionPayload, ItemState>()

    fun configure(
        onImpression: (ImpressionPayload, startTime: Long, endTime: Long) -> Unit,
        percentVisibleForImpression: Float = this.percentVisibleForImpression,
        timeForImpressionMs: Long = this.timeForImpressionMs
    ) {
        this.onImpression = onImpression
        this.percentVisibleForImpression = percentVisibleForImpression
        this.timeForImpressionMs = timeForImpressionMs
    }

    fun onViewVisibilityChanged(
        payload: ImpressionPayload,
        pctVisible: Float
    ) {
        if (impressionsFired.contains(payload)) {
            // If item leave the screen, we want to reset it so it can fire an impression when
            // it returns.
            if (pctVisible <= 0.0f) {
                impressionsFired.remove(payload)
            }
            return
        }

        val currentTimeMs = timeProvider.currentTimeMs

        if (pctVisible >= percentVisibleForImpression) {
            states.getOrPut(payload) {
                ItemState(currentTimeMs)
            }
        } else {
            val payloadState = states.remove(payload) ?: return

            if (currentTimeMs - payloadState.startTimeMs >= timeForImpressionMs) {
                onImpression?.invoke(payload, payloadState.startTimeMs, currentTimeMs)

                // Only remember we fired an impression if the item isn't fully off screen
                if (pctVisible > 0.0f) {
                    impressionsFired.add(payload)
                }
            }
        }
    }

    fun clearImpressionCache() {
        states.clear()
    }
}