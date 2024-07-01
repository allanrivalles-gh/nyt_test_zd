package com.theathletic.network.apollo

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Query

// Use this query if you want to make call for cache first
fun <D : Query.Data> ApolloClient.cachedQuery(
    query: Query<D>,
    fetchPolicy: FetchPolicy = FetchPolicy.CacheFirst
): ApolloCall<D> {
    return query(query).httpFetchPolicy(fetchPolicy)
}