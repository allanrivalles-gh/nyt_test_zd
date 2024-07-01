package com.theathletic.podcast.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Error
import com.theathletic.PodcastEpisodeByNumberQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope

class PodcastApi @AutoKoin(Scope.SINGLE) constructor(
    private val client: ApolloClient
) {
    suspend fun getPodcastEpisodeByNumber(podcastEpisodeRequest: PodcastEpisodeRequest): PodcastEpisodeByNumberQuery.PodcastEpisodeByNumber {
        val result = client.query(podcastEpisodeRequest.toRemote()).execute()

        if (result.hasErrors()) throw PodcastEpisodeNotFoundException(result.errors ?: emptyList())
        return result.data?.podcastEpisodeByNumber ?: throw PodcastEpisodeNotFoundException()
    }
}

class PodcastEpisodeNotFoundException(errors: List<Error> = emptyList()) :
    Exception("Unable to fetch podcast ${errors.joinToString(" - ")}")