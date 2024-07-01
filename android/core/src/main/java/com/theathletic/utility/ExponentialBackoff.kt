package com.theathletic.utility

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

interface BackoffState {
    fun resetBackoff()
    fun runBlockingBackoff(condition: Boolean, action: () -> Unit)
}

class ExponentialBackoff @AutoKoin constructor(
    private val backoffInterval: Duration = 500.milliseconds,
    private val backoffIntervalLimit: Duration = 4.seconds,
    private val retryCount: Int = 3,
    dispatcherProvider: DispatcherProvider
) : BackoffState {
    private var currentBackoffTime = backoffInterval
    private var currentRetries = retryCount

    private val coroutineScope = CoroutineScope(dispatcherProvider.io + Job())
    private var job: Job? = null

    override fun resetBackoff() {
        currentBackoffTime = backoffInterval
        currentRetries = retryCount
    }

    override fun runBlockingBackoff(condition: Boolean, action: () -> Unit) {
        if (condition ||
            currentRetries == 0 ||
            job?.isActive == true
        ) {
            return
        }

        job = coroutineScope.launch {
            delay(minOf(currentBackoffTime, backoffIntervalLimit))
            currentBackoffTime *= 2
            currentRetries--
            action()
        }
    }
}