package com.theathletic.followables.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.theathletic.RecommendedTeamsQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.network.apollo.FetchPolicy
import com.theathletic.network.apollo.httpFetchPolicy

class TeamApi @AutoKoin constructor(private val client: ApolloClient) {

    suspend fun getRecommendedTeams(): ApolloResponse<RecommendedTeamsQuery.Data> {
        return client
            .query(RecommendedTeamsQuery())
            .httpFetchPolicy(FetchPolicy.CacheFirst)
            .execute()
    }
}