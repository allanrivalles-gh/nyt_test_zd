package com.theathletic.boxscore.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Error
import com.theathletic.GetBoxScoreFeedQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope

class BoxScoreApi @AutoKoin(Scope.SINGLE) constructor(private val client: ApolloClient) {

    suspend fun getBoxScoreFeed(gameId: String): GetBoxScoreFeedQuery.Data? {
        val result = client.query(
            GetBoxScoreFeedQuery(gameId = gameId)
        ).execute()

        if (result.hasErrors()) throw BoxScoreException(
            gameId,
            result.errors ?: emptyList()
        )
        return result.data
    }

    class BoxScoreException(gameId: String, errors: List<Error> = emptyList()) :
        Exception("\"Error fetching box score for game Id: $gameId with message ${errors.joinToString("\n")}")
}