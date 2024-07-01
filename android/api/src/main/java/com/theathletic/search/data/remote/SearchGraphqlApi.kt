package com.theathletic.search.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Input
import com.theathletic.SearchPopularArticlesQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope

class SearchGraphqlApi @AutoKoin(Scope.SINGLE) constructor(private val client: ApolloClient) {

    companion object {
        const val DEFAULT_MOST_POPULAR_COUNT = 10
    }

    suspend fun getMostPopularArticles(): ApolloResponse<SearchPopularArticlesQuery.Data> {
        return client.query(
            SearchPopularArticlesQuery(Input.optional(DEFAULT_MOST_POPULAR_COUNT))
        ).execute()
    }
}