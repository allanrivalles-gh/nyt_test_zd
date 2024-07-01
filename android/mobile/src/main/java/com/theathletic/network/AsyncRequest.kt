package com.theathletic.network

import com.theathletic.repository.safeApiRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

inline fun <reified T> CoroutineScope.safeAsyncRequest(
    crossinline block: suspend CoroutineScope.() -> T
) = async {
    safeApiRequest { block() }
}