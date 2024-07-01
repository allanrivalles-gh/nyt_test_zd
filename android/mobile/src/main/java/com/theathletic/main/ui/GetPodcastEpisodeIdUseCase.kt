package com.theathletic.main.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.podcast.data.PodcastRepository
import timber.log.Timber

class GetPodcastEpisodeIdUseCase @AutoKoin constructor(
    val podcastRepository: PodcastRepository
) {
    suspend operator fun invoke(
        podcastId: String,
        episodeNumber: Int
    ) = try {
        val id = podcastRepository.getPodcastEpisodeByNumber(podcastId, episodeNumber).id
        if (id.isBlank()) {
            throw Exception("Id is null or blank for $podcastId episode $episodeNumber")
        }
        Result.success(id.toLong())
    } catch (e: Throwable) {
        Timber.e(e, "Error on fetching podcast episode id for $podcastId episode $episodeNumber")
        Result.failure(e)
    }
}