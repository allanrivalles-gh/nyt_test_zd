package com.theathletic.utility

import android.text.format.DateUtils
import com.theathletic.datetime.TimeProvider

class Throttler<T> constructor(
    private val timeProvider: TimeProvider,
    private val minimumInterval: Long = DateUtils.MINUTE_IN_MILLIS * 5,
) {
    private var lastRunAt: Long = 0L
    private var lastKey: T? = null

    fun willRun(key: T): Boolean {
        return shouldRun(key).also {
            if (it) {
                lastRunAt = timeProvider.currentTimeMs
                lastKey = key
            }
        }
    }

    private fun shouldRun(key: T): Boolean {
        if (lastKey != key) return true
        if (lastRunAt + minimumInterval < timeProvider.currentTimeMs) return true
        return false
    }
}