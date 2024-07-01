package com.theathletic.extension

import androidx.work.PeriodicWorkRequest
import java.util.concurrent.TimeUnit

const val SECONDS_IN_2_HOURS = 3_600 * 2

/**
 * Delays the first run of a periodic worker by X seconds where X is a randomly distributed int
 * between 0 and 7,200 (the number of seconds in a 2 hour period). Use this to ensure any periodic work that
 * sends traffic to our servers is evenly distributed throughout the day.
 *
 * PeriodicWork gets scheduled to repeat, at best, every 15 minutes. This function just future
 * proofs us for any changes to that time window.
 */
fun PeriodicWorkRequest.Builder.applyEvenDistributionDelay(
    shouldScheduleImmediately: Boolean = false
): PeriodicWorkRequest.Builder {
    return if (shouldScheduleImmediately) {
        setInitialDelay(0L, TimeUnit.SECONDS)
    } else {
        setInitialDelay((0..SECONDS_IN_2_HOURS).random().toLong(), TimeUnit.SECONDS)
    }
}