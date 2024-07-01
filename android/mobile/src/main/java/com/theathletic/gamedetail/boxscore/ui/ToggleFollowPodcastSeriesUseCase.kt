package com.theathletic.gamedetail.boxscore.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.audio.data.remote.AudioApi
import com.theathletic.extension.extLogError
import com.theathletic.podcast.data.PodcastRepository
import com.theathletic.repository.safeApiRequest

class ToggleFollowPodcastSeriesUseCase @AutoKoin constructor(
    private val podcastRepository: PodcastRepository,
    private val audioApi: AudioApi
) {
    suspend operator fun invoke(podcastId: Long) {
        val isFollowing = podcastRepository.isPodcastSeriesFollowed(podcastId)

        if (isFollowing) {
            podcastRepository.setPodcastFollowStatus(podcastId, false)
            safeApiRequest {
                audioApi.unfollowPodcast(podcastId.toString())
            }.onSuccess {
                // Temporary until things are moved into new podcast repository
                podcastRepository.refreshFollowed()
            }.onError {
                it.extLogError()
                podcastRepository.setPodcastFollowStatus(podcastId, true)
            }
        } else {
            podcastRepository.setPodcastFollowStatus(podcastId, true)
            safeApiRequest {
                audioApi.followPodcast(podcastId.toString())
            }.onSuccess {
                // Temporary until things are moved into new podcast repository
                podcastRepository.refreshFollowed()
            }.onError {
                it.extLogError()
                podcastRepository.setPodcastFollowStatus(podcastId, false)
            }
        }
        podcastRepository.refreshFollowed()
    }
}