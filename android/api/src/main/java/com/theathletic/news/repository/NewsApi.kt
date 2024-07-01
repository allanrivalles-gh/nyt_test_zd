package com.theathletic.news.repository

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.theathletic.HeadlineCommentCountQuery
import com.theathletic.NewsByIdQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.network.apollo.FetchPolicy
import com.theathletic.network.apollo.httpFetchPolicy

class NewsApi @AutoKoin(Scope.SINGLE) constructor(private val client: ApolloClient) {

    suspend fun getNewsByIdAsync(id: String, isRefreshing: Boolean): ApolloResponse<NewsByIdQuery.Data> {
        val fetchPolicy = if (isRefreshing) {
            FetchPolicy.NetworkFirst
        } else {
            FetchPolicy.CacheFirst
        }

        val query = NewsByIdQuery(id = id)

        return client.query(query)
            .httpFetchPolicy(fetchPolicy)
            .execute()
    }

    suspend fun getHeadlinesCommentCount(id: String): ApolloResponse<HeadlineCommentCountQuery.Data> {
        val query = HeadlineCommentCountQuery(id = id)
        return client.query(query).execute()
    }
}