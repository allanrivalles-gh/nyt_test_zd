package com.theathletic.utility.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration

inline fun <reified T> Flow<T>.collectIn(
    scope: CoroutineScope,
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline action: suspend (T) -> Unit
) = scope.launch(context = context) { collect { action(it) } }

/**
 * Emits the first value, then ignores any value sent within the [windowTime] after a successful
 * emission.
 */
fun <T> Flow<T>.throttle(windowTime: Long): Flow<T> = flow {
    var lastEmissionTime = 0L
    collect { value ->
        val currentTime = System.currentTimeMillis()
        val emit = currentTime - lastEmissionTime > windowTime

        if (emit) {
            lastEmissionTime = currentTime
            emit(value)
        }
    }
}

/**
 * Retries a Flow which has encountered an exception with a backoff of 1 second per extra attempt.
 * So the first retry comes immediately, second retry after 1 second, third after 2 seconds, etc.
 */
fun <T> Flow<T>.retryWithBackoff(maxRetryAttempts: Int = 3) = retryWhen { _, attempt ->
    delay(attempt * 1000)
    attempt < maxRetryAttempts - 1
}

/**
 * A utility function that allows a side-effect check of a Flow value against the previous value
 * in the Flow. It keeps the same value flowing through the Flow so you cannot modify it. You can
 * only use the two to compare with each other and cause some other effects.
 */
fun <T> Flow<T>.doWithPrevious(block: suspend (T?, T) -> Unit): Flow<T> = flow {
    var old: T? = null
    collect { new ->
        block(old, new)
        old = new
        emit(new)
    }
}

fun tickerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
    delay(initialDelay)
    while (true) {
        emit(Unit)
        delay(period)
    }
}