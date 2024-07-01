package com.theathletic.comments.game.remote

import com.apollographql.apollo3.ApolloClient
import com.theathletic.TeamSpecificThreadsQuery
import com.theathletic.UpdateCurrentSpecificThreadMutation
import com.theathletic.annotation.autokoin.AutoKoin

class TeamThreadsApi @AutoKoin constructor(private val apolloClient: ApolloClient) {

    suspend fun getTeamThreads(gameId: String): TeamSpecificThreadsQuery.TeamSpecificThreads {
        val response = apolloClient.query(TeamSpecificThreadsQuery(gameId)).execute()
        if (response.hasErrors()) {
            throw Exception("Error on loading team threads for game: $gameId - ${response.errors?.joinToString { "-" }}")
        }

        return response.data?.teamSpecificThreads ?: throw Exception("Null team threads response for game: $gameId")
    }

    suspend fun updateTeamThread(gameId: String, teamId: String): TeamSpecificThreadsQuery.TeamSpecificThreads {
        val result = apolloClient.mutation(UpdateCurrentSpecificThreadMutation(gameId, teamId)).execute()

        return if (result.data?.updateCurrentSpecificThread == true) {
            getTeamThreads(gameId)
        } else {
            throw Exception("Can't select the team thread for game: $gameId | team: $teamId")
        }
    }
}