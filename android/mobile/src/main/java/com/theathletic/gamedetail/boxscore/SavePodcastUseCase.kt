package com.theathletic.gamedetail.boxscore

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.data.local.PodcastEpisode
import com.theathletic.datetime.asGMTString
import com.theathletic.podcast.data.PodcastRepository
import java.util.Date

class SavePodcastUseCase @AutoKoin constructor(
    private val podcastRepository: PodcastRepository,
) {
    suspend operator fun invoke(podcast: PodcastEpisode) {
        if (podcastRepository.podcastEpisodeById(podcast.episodeId.toLong()) == null) {
            podcastRepository.savePodcast(
                episodeId = podcast.episodeId,
                podcastId = podcast.podcastId,
                episodeTitle = podcast.title,
                description = podcast.description.orEmpty(),
                duration = podcast.duration?.toLong() ?: 0L,
                dateGmt = Date(podcast.publishedAt.timeMillis).asGMTString(),
                mp3Url = podcast.mp3Url.orEmpty(),
                imageUrl = podcast.imageUrl.orEmpty(),
                permalinkUrl = podcast.permalink,
                isDownloaded = false
            )
        }
    }
}