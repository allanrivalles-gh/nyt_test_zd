package com.theathletic.analytics.impressions

/**
 * An interface that defines a pipeline from [ViewVisibilityTracker] to [ImpressionCalculator].
 * Usually this is a [Presenter] which takes in data from the tracker and forwards it to the
 * calculator.
 */
interface ImpressionVisibilityListener {
    fun onViewVisibilityChanged(payload: ImpressionPayload, pctVisible: Float)
}