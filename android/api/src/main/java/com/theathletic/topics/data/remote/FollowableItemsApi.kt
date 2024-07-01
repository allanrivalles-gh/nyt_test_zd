package com.theathletic.topics.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.theathletic.FollowTopicMutation
import com.theathletic.FollowableItemsQuery
import com.theathletic.UnfollowTopicMutation
import com.theathletic.UserFollowingQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.type.UserFollow
import timber.log.Timber

class FollowableItemsApi @AutoKoin(Scope.SINGLE) constructor(
    private val client: ApolloClient
) {

    suspend fun getFollowableItems(): ApolloResponse<FollowableItemsQuery.Data>? = try {
        client.query(FollowableItemsQuery()).execute()
    } catch (e: Exception) {
        Timber.e(e)
        null
    }

    suspend fun getUserFollowingItems() = client.query(UserFollowingQuery()).execute()

    suspend fun followItem(input: UserFollow): ApolloResponse<FollowTopicMutation.Data> =
        client.mutation(FollowTopicMutation(input)).execute()

    suspend fun unfollowItem(input: UserFollow): ApolloResponse<UnfollowTopicMutation.Data> =
        client.mutation(UnfollowTopicMutation(input)).execute()
}