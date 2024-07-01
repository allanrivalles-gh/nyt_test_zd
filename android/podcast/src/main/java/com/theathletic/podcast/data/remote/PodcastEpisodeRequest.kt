package com.theathletic.podcast.data.remote

import com.theathletic.PodcastEpisodeByNumberQuery

data class PodcastEpisodeRequest(
    val podcastId: String,
    val episodeNumber: Int
)

fun PodcastEpisodeRequest.toRemote() = PodcastEpisodeByNumberQuery(
    podcastId,
    episodeNumber
)