package com.theathletic.repository.resource

@Suppress("unused")
data class Resource<out T> constructor(
    val status: Status,
    val data: T? = null,
    val isCache: Boolean,
    val throwable: Throwable? = null
) {
    enum class Status { SUCCESS, ERROR, LOADING }
    companion object {
        fun <T> loading(data: T? = null, isCache: Boolean = true) = Resource(Status.LOADING, data, isCache, null)
        fun <T> success(data: T?, isCache: Boolean = true) = Resource(Status.SUCCESS, data, isCache, null)
        fun <T> error(throwable: Throwable?, isCache: Boolean = false) = Resource(Status.ERROR, null, isCache, throwable)
    }
}