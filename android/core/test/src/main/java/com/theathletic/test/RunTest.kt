package com.theathletic.test

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

const val SIX_SECONDS_MS = 6000L

fun runTest(
    context: CoroutineContext = EmptyCoroutineContext,
    dispatchTimeoutMs: Long = SIX_SECONDS_MS,
    testBody: suspend CoroutineScope.() -> Unit
) {
    kotlinx.coroutines.test.runTest(
        context = context,
        dispatchTimeoutMs = dispatchTimeoutMs,
        testBody = testBody
    )
}