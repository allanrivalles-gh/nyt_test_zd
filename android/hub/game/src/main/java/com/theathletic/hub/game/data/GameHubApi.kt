package com.theathletic.hub.game.data

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.theathletic.GetGameSummaryQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope

class GameHubApi @AutoKoin(Scope.SINGLE) constructor(
    private val client: ApolloClient
) {
    suspend fun getGameSummary(gameId: String): ApolloResponse<GetGameSummaryQuery.Data> {
        return client.query(GetGameSummaryQuery(gameId)).execute()
    }
}