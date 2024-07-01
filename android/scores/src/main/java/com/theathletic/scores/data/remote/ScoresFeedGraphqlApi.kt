package com.theathletic.scores.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.theathletic.ScoresFeedDayQuery
import com.theathletic.ScoresFeedQuery
import com.theathletic.ScoresFeedUpdatesSubscription
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.network.apollo.notPersistedSubscription
import com.theathletic.utility.LocaleUtility
import com.theathletic.utility.coroutines.retryWithBackoff
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class ScoresFeedGraphqlApi @AutoKoin(Scope.SINGLE) constructor(
    private val client: ApolloClient,
    private val localeUtility: LocaleUtility,
) {
    suspend fun getScoresFeed(): ApolloResponse<ScoresFeedQuery.Data> {
        return client.query(
            ScoresFeedQuery(timeZone = localeUtility.deviceTimeZone.id)
        ).execute()
    }

    suspend fun getScoresFeedForDay(day: String): ApolloResponse<ScoresFeedDayQuery.Data> {
        return client.query(
            ScoresFeedDayQuery(
                timeZone = localeUtility.deviceTimeZone.id,
                day = day
            )
        ).execute()
    }

    fun getScoreFeedUpdates(blockIds: List<String>): Flow<ScoresFeedUpdatesSubscription.Data> {
        return client.notPersistedSubscription(ScoresFeedUpdatesSubscription(blockIds))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }
}