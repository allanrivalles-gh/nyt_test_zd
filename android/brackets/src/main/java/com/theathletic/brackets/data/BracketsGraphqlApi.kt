package com.theathletic.brackets.data

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Input
import com.theathletic.GetTournamentQuery
import com.theathletic.ReplayGameMutation
import com.theathletic.TournamentGamesSubscription
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.network.apollo.notPersistedSubscription
import com.theathletic.type.LeagueCode
import com.theathletic.utility.coroutines.retryWithBackoff
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class BracketsGraphqlApi @AutoKoin(Scope.SINGLE) constructor(
    private val client: ApolloClient
) {
    suspend fun getTournament(leagueCode: LeagueCode, seasonId: String?): ApolloResponse<GetTournamentQuery.Data> {
        return client.query(GetTournamentQuery(leagueCode, Input.optional(seasonId))).execute()
    }

    fun getTournamentGamesSubscription(gameIds: List<String>): Flow<TournamentGamesSubscription.Data> {
        return client.notPersistedSubscription(TournamentGamesSubscription(gameIds))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }

    // Mutation call to test the replay of a game
    suspend fun gameReplay(gameId: String) {
        client.mutation(ReplayGameMutation(gameId)).execute()
    }
}