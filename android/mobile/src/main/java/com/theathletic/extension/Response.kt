package com.theathletic.extension

import retrofit2.Response
import timber.log.Timber

// mapResource response to another response using body mapResource function
fun <T, S> Response<T>.map(mapFunction: (T?) -> S?): Any = if (isSuccessful) {
    Response.success(mapFunction(body()), raw())
} else {
    Response.error(errorBody()!!, raw())
}

fun <T> Response<T>.extLogError() {
    Timber.e("[Response Error] ${this.errorBody()?.string()}")
}