package com.theathletic.gamedetail.boxscore.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.podcast.data.PodcastRepository
import com.theathletic.podcast.data.local.PodcastEpisodeDetails

class GetPodcastEpisodeDetailsUseCase @AutoKoin constructor(
    private val podcastRepository: PodcastRepository
) {
    suspend operator fun invoke(podcastId: Long, episodeId: Long): PodcastEpisodeDetails? {

        val podcastEpisode = podcastRepository.podcastEpisodeById(episodeId)
        return podcastEpisode?.let { episode ->
            return PodcastEpisodeDetails(
                isDownloaded = episode.isDownloaded,
                isFollowed = podcastRepository.isPodcastSeriesFollowed(podcastId),
                title = episode.title,
                downloadUrl = episode.mp3Url,
                isTeaser = episode.isTeaser
            )
        }
    }
}