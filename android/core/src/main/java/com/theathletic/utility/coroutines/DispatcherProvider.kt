package com.theathletic.utility.coroutines

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {
    val default: CoroutineDispatcher
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}