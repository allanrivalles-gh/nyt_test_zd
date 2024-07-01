package com.theathletic.gamedetail.boxscore.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.podcast.data.PodcastRepository

class DeleteDownloadedPodcastEpisodeUseCase @AutoKoin constructor(
    private val podcastRepository: PodcastRepository
) {
    operator fun invoke(episodeId: Long) {
        podcastRepository.deleteDownloadedPodcastEpisode(episodeId)
    }
}