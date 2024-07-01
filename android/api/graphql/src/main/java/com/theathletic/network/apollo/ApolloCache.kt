package com.theathletic.network.apollo

import android.content.Context
import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.api.Mutation
import com.apollographql.apollo3.api.Query
import com.apollographql.apollo3.cache.http.HttpFetchPolicy
import com.apollographql.apollo3.cache.http.httpExpireTimeout
import com.apollographql.apollo3.cache.http.httpFetchPolicy
import java.io.File

class ApolloCache(context: Context) {
    val file = File(context.applicationContext.cacheDir, "apolloCache")
    val size = 1024L * 1024L * 2L // 2 MiB
}

sealed class FetchPolicy(val policy: HttpFetchPolicy, val timeout: Long = 0) {
    object CacheOnly : FetchPolicy(HttpFetchPolicy.CacheOnly, ONE_DAY)
    object NetworkOnly : FetchPolicy(HttpFetchPolicy.NetworkOnly)
    object CacheFirst : FetchPolicy(HttpFetchPolicy.CacheFirst, ONE_DAY)
    object NetworkFirst : FetchPolicy(HttpFetchPolicy.NetworkFirst)

    companion object {
        const val ONE_DAY = 60 * 60 * 24000L
    }
}

fun <D : Query.Data> ApolloCall<D>.httpFetchPolicy(httpFetchPolicy: FetchPolicy): ApolloCall<D> {
    return httpFetchPolicy(httpFetchPolicy.policy)
        .httpExpireTimeout(httpFetchPolicy.timeout)
}

@JvmName("mutationHttpFetchPolicy")
fun <D : Mutation.Data> ApolloCall<D>.httpFetchPolicy(httpFetchPolicy: FetchPolicy): ApolloCall<D> {
    return httpFetchPolicy(httpFetchPolicy.policy)
        .httpExpireTimeout(httpFetchPolicy.timeout)
}