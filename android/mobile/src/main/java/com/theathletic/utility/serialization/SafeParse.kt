package com.theathletic.utility.serialization

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend inline fun <reified T> safeParse(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    crossinline block: suspend () -> T
): ParseResult<T> {
    return try {
        val result = withContext(dispatcher) {
            block()
        }
        ParseResult.Success(result)
    } catch (e: Exception) {
        ParseResult.Error(e)
    }
}

sealed class ParseResult<T> {
    class Success<T>(val body: T) : ParseResult<T>()
    class Error<T>(val throwable: Throwable) : ParseResult<T>()
}