package com.theathletic.gamedetail.boxscore.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.data.local.BoxScoreLocalDataSource
import com.theathletic.boxscore.data.local.PodcastEpisode
import com.theathletic.extension.firstOrNull
import com.theathletic.podcast.data.PodcastRepository
import com.theathletic.podcast.download.PodcastDownloadStateStore
import com.theathletic.podcast.ui.DownloadState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.rx2.asFlow

class ObservePodcastDownloadStateChangeUseCase @AutoKoin constructor(
    private val podcastRepository: PodcastRepository,
    private val boxScoreLocalDataSource: BoxScoreLocalDataSource,
    private val podcastDownloadStateStore: PodcastDownloadStateStore
) {
    operator fun invoke(gameId: String) = flow {
        podcastDownloadStateStore.downloadStates.asFlow().combine(podcastRepository.downloadedEpisodes) { downloadStates, downloadedEpisodes ->
            val boxScore = boxScoreLocalDataSource.getItem(gameId)

            boxScore?.let {
                it.sections.flatMap { it.modules }.flatMap { it.blocks }
                    .filterIsInstance<PodcastEpisode>()
                    .forEach { episode ->
                        val episodeBeingDownloaded = downloadStates.firstOrNull { it.podcastEpisodeId == episode.episodeId.toLong() }
                            ?.isDownloading() == true
                        val episodeDownloaded = downloadedEpisodes.firstOrNull { it.id == episode.episodeId.toLong() }
                            ?.isDownloaded == true

                        episode.downloadState =
                            when {
                                episodeBeingDownloaded -> DownloadState.DOWNLOADING
                                episodeDownloaded -> DownloadState.DOWNLOADED
                                else -> DownloadState.NOT_DOWNLOADED
                            }
                    }
            }
            emit(boxScore)
        }.collect()
    }
}