package com.theathletic.audio.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.theathletic.FollowPodcastMutation
import com.theathletic.ListenFeedDataQuery
import com.theathletic.LiveRoomAvailableQuery
import com.theathletic.UnfollowPodcastMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope

class AudioApi @AutoKoin(Scope.SINGLE) constructor(
    val apolloClient: ApolloClient
) {

    suspend fun getListenFeedData(): ApolloResponse<ListenFeedDataQuery.Data> {
        return apolloClient.query(ListenFeedDataQuery()).execute()
    }

    suspend fun followPodcast(id: String): ApolloResponse<FollowPodcastMutation.Data> {
        return apolloClient.mutation(FollowPodcastMutation(id)).execute()
    }

    suspend fun unfollowPodcast(id: String): ApolloResponse<UnfollowPodcastMutation.Data> {
        return apolloClient.mutation(UnfollowPodcastMutation(id)).execute()
    }

    suspend fun currentLiveRooms(): ApolloResponse<LiveRoomAvailableQuery.Data> {
        return apolloClient.query(LiveRoomAvailableQuery()).execute()
    }
}