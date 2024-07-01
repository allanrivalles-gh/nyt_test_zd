package com.theathletic.gamedetail.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.theathletic.AmericanFootballPlayerGradesUpdatesSubscription
import com.theathletic.BaseballPlayerGradesUpdatesSubscription
import com.theathletic.BasketballPlayerGradesUpdatesSubscription
import com.theathletic.GetAmericanFootballPlayerGradesQuery
import com.theathletic.GetBaseballPlayerGradesQuery
import com.theathletic.GetBasketballPlayerGradesQuery
import com.theathletic.GetHockeyPlayerGradesQuery
import com.theathletic.GetSoccerPlayerGradesQuery
import com.theathletic.GradePlayerMutation
import com.theathletic.HockeyPlayerGradesUpdatesSubscription
import com.theathletic.SoccerPlayerGradesUpdatesSubscription
import com.theathletic.UngradePlayerMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.network.apollo.notPersistedSubscription
import com.theathletic.utility.coroutines.retryWithBackoff
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class PlayerGradesGraphqlApi @AutoKoin(Scope.SINGLE) constructor(
    private val client: ApolloClient
) {

    suspend fun getPlayerGradesForAmericanFootballGame(
        gameId: String
    ): ApolloResponse<GetAmericanFootballPlayerGradesQuery.Data> {
        return client.query(GetAmericanFootballPlayerGradesQuery(gameId)).execute()
    }

    fun getAmericanFootballPlayerGradesUpdatesSubscription(gameId: String): Flow<AmericanFootballPlayerGradesUpdatesSubscription.Data> {
        return client.notPersistedSubscription(AmericanFootballPlayerGradesUpdatesSubscription(gameId))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }

    suspend fun getPlayerGradesForSoccerGame(
        gameId: String
    ): ApolloResponse<GetSoccerPlayerGradesQuery.Data> {
        return client.query(GetSoccerPlayerGradesQuery(gameId)).execute()
    }

    fun getSoccerPlayerGradesUpdatesSubscription(gameId: String): Flow<SoccerPlayerGradesUpdatesSubscription.Data> {
        return client.notPersistedSubscription(SoccerPlayerGradesUpdatesSubscription(gameId))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }

    suspend fun getPlayerGradesForBasketballGame(
        gameId: String
    ): ApolloResponse<GetBasketballPlayerGradesQuery.Data> {
        return client.query(GetBasketballPlayerGradesQuery(gameId)).execute()
    }

    fun getBasketballPlayerGradesUpdatesSubscription(gameId: String): Flow<BasketballPlayerGradesUpdatesSubscription.Data> {
        return client.notPersistedSubscription(BasketballPlayerGradesUpdatesSubscription(gameId))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }

    suspend fun getPlayerGradesForHockeyGame(
        gameId: String
    ): ApolloResponse<GetHockeyPlayerGradesQuery.Data> {
        return client.query(GetHockeyPlayerGradesQuery(gameId)).execute()
    }

    fun getHockeyPlayerGradesUpdatesSubscription(gameId: String): Flow<HockeyPlayerGradesUpdatesSubscription.Data> {
        return client.notPersistedSubscription(HockeyPlayerGradesUpdatesSubscription(gameId))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }

    suspend fun getPlayerGradesForBaseballGame(
        gameId: String
    ): ApolloResponse<GetBaseballPlayerGradesQuery.Data> {
        return client.query(GetBaseballPlayerGradesQuery(gameId)).execute()
    }

    fun getBaseballPlayerGradesUpdatesSubscription(gameId: String): Flow<BaseballPlayerGradesUpdatesSubscription.Data> {
        return client.notPersistedSubscription(BaseballPlayerGradesUpdatesSubscription(gameId))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }

    suspend fun gradePlayer(
        gameId: String,
        playerId: String,
        grade: Int
    ): ApolloResponse<GradePlayerMutation.Data> {
        return client.mutation(
            GradePlayerMutation(
                gameId = gameId,
                playerId = playerId,
                grade = grade
            )
        ).execute()
    }

    suspend fun ungradePlayer(
        gameId: String,
        playerId: String
    ): ApolloResponse<UngradePlayerMutation.Data> {
        return client.mutation(
            UngradePlayerMutation(
                gameId = gameId,
                playerId = playerId
            )
        ).execute()
    }
}