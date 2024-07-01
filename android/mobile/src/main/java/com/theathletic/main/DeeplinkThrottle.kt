package com.theathletic.main

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.datetime.DateUtility
import com.theathletic.datetime.TimeProvider
import java.util.concurrent.TimeUnit

/**
 * This is a simple, injectable instance that tracks the last timestamp of a deeplink or push
 * that triggers an app open event. This allows us to prevent load intense actions from happening
 * on a massive scale if we kick off a large communications campaign.
 */
class DeeplinkThrottle @AutoKoin(Scope.SINGLE) constructor(
    private val timeProvider: TimeProvider,
    private val dateUtility: DateUtility
) {
    private var _lastDeeplinkTimestampMillis: Long? = null
    companion object {
        const val THROTTLE_TIME_SECONDS = 10L
    }

    fun startThrottle() {
        _lastDeeplinkTimestampMillis = timeProvider.currentTimeMs
    }

    fun releaseThrottle() {
        _lastDeeplinkTimestampMillis = 0
    }

    fun isThrottled(): Boolean {
        return !dateUtility.isInPastMoreThan(
            _lastDeeplinkTimestampMillis ?: 0,
            TimeUnit.SECONDS.toMillis(THROTTLE_TIME_SECONDS)
        )
    }

    override fun toString(): String {
        return "DeeplinkTimestamp(_lastDeeplinkTimestampMillis=$_lastDeeplinkTimestampMillis)"
    }
}