package com.theathletic.podcast.ui

interface IPodcastEpisodeItemView {
    fun onPodcastEpisodeItemClick(item: PodcastEpisodeListItem)
    fun onPodcastDownloadClick(item: PodcastEpisodeListItem)
    fun onPodcastPlayClick(item: PodcastEpisodeListItem)
}