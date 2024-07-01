package com.theathletic.audio

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.audio.data.ListenFeedRepository
import com.theathletic.audio.data.local.ListenFeedData
import com.theathletic.podcast.data.PodcastRepository
import kotlinx.coroutines.flow.map

class ListenFeedDataCacheUseCase @AutoKoin(Scope.SINGLE) constructor(
    private val listenFeedRepository: ListenFeedRepository,
    private val podcastRepository: PodcastRepository
) {
    operator fun invoke() = listenFeedRepository.listenFeed.map { listenFeedData ->
        if (listenFeedData == null) {
            val downloadedEpisodes = podcastRepository.downloadedEpisodesImmediate().map {
                it.id.toString()
            }
            val diskCache = listenFeedRepository.listenFeedDisk()
            diskCache.copy(
                podcastEpisodes = filterLatestEpisodes(diskCache, downloadedEpisodes),
                followingPodcasts = diskCache.followingPodcasts.filter { it.isFollowing }
            )
        } else {
            listenFeedData
        }
    }

    private fun filterLatestEpisodes(
        diskCache: ListenFeedData.WithEntities,
        downloadedEpisodes: List<String>
    ) = diskCache.podcastEpisodes.filter {
        downloadedEpisodes.contains(it.id)
    }.groupBy {
        it.seriesId
    }.mapNotNull { entry ->
        entry.value.lastOrNull()
    }
}