package com.theathletic.gamedetail.boxscore.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.podcast.download.PodcastDownloadStateStore

class IsPodcastEpisodeDownloadingUseCase @AutoKoin constructor(
    private val podcastDownloadStateStore: PodcastDownloadStateStore
) {
    operator fun invoke(podcastEpisodeId: Long) =
        podcastDownloadStateStore.latestState.get(podcastEpisodeId)?.isDownloading() ?: false
}