package com.theathletic.network

sealed class ResponseStatus<T> {
    class Success<T>(val body: T) : ResponseStatus<T>()
    class Error<T>(val throwable: Throwable) : ResponseStatus<T>()

    suspend fun onSuccess(callback: suspend (T) -> Unit): ResponseStatus<T> {
        if (this is Success) {
            callback(body)
        }
        return this
    }

    suspend fun onError(callback: suspend (Throwable) -> Unit): ResponseStatus<T> {
        if (this is Error) {
            callback(throwable)
        }
        return this
    }
}