package com.theathletic.feed.compose.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Error
import com.theathletic.FeedLiveGamesSubscription
import com.theathletic.NewFeedQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.feed.compose.data.FeedRequest
import com.theathletic.network.apollo.notPersistedSubscription
import com.theathletic.utility.coroutines.retryWithBackoff
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

internal class FeedApi @AutoKoin(Scope.SINGLE) constructor(
    private val client: ApolloClient
) {

    suspend fun fetchFeed(
        feedRequest: FeedRequest,
        page: Int,
    ): NewFeedQuery.FeedMulligan {
        val result = client.query(feedRequest.toRemote(page = page)).execute()

        if (result.hasErrors()) throw FeedFetchException(result.errors ?: emptyList())
        return result.data?.feedMulligan ?: throw FeedFetchException()
    }

    fun getLiveGameUpdates(gameIds: List<String>): Flow<FeedLiveGamesSubscription.Data> {
        return client.notPersistedSubscription(FeedLiveGamesSubscription(gameIds))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }
}

class FeedFetchException(errors: List<Error> = emptyList()) :
    Exception("Unable to fetch feed ${errors.joinToString(" - ")}")