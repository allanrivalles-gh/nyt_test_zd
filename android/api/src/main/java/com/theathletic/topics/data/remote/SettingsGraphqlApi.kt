package com.theathletic.topics.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.theathletic.FollowTopicMutation
import com.theathletic.UnfollowTopicMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.profile.manage.UserTopicId
import com.theathletic.type.UserFollow
import com.theathletic.type.UserFollowType

class SettingsGraphqlApi @AutoKoin(Scope.SINGLE) constructor(
    private val client: ApolloClient
) {

    suspend fun followTopic(
        id: UserTopicId
    ): ApolloResponse<FollowTopicMutation.Data> {
        val input = UserFollow(
            id = id.id.toString(),
            type = id.asGraphqlFollowType
        )

        return client.mutation(
            FollowTopicMutation(input)
        ).execute()
    }

    suspend fun unfollowTopic(
        id: UserTopicId
    ): ApolloResponse<UnfollowTopicMutation.Data> {
        val input = UserFollow(
            id = id.id.toString(),
            type = id.asGraphqlFollowType
        )

        return client.mutation(
            UnfollowTopicMutation(input)
        ).execute()
    }

    private val UserTopicId.asGraphqlFollowType get() = when (this) {
        is UserTopicId.League -> UserFollowType.league
        is UserTopicId.Team -> UserFollowType.team
        is UserTopicId.Author -> UserFollowType.author
    }
}