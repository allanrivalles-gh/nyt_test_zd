package com.theathletic.podcast.data.local

data class PodcastEpisodeDetails(
    val isFollowed: Boolean,
    val isDownloaded: Boolean,
    val title: String,
    val downloadUrl: String,
    val isTeaser: Boolean,
)