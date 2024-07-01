package com.theathletic.gamedetail.boxscore

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.data.local.BoxScore
import com.theathletic.boxscore.data.local.BoxScoreLocalDataSource
import com.theathletic.boxscore.data.local.BoxScorePodcastSync
import com.theathletic.boxscore.data.local.PodcastEpisode
import com.theathletic.datetime.asGMTString
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.podcast.data.PodcastRepository
import com.theathletic.podcast.ui.DownloadState
import com.theathletic.podcast.ui.PlaybackState
import java.util.Date

class SyncBoxScorePodcastUseCase @AutoKoin constructor(
    private val podcastRepository: PodcastRepository,
    private val boxScoreLocalDataSource: BoxScoreLocalDataSource
) {
    suspend operator fun invoke(
        gameId: String,
        boxScore: BoxScore,
    ): BoxScorePodcastSync {
        var refreshLocalDataSource = false
        var hasPodcastEpisodes: Boolean = false
        getEpisodesForGame(boxScore).forEach { episode ->
            hasPodcastEpisodes = true
            val storedEpisode = podcastRepository.podcastEpisodeById(episode.episodeId.toLong())
            if (storedEpisode == null) {
                // Fresh episode for the device so save it
                saveEpisode(episode)
            } else {
                // Update our local box score copy with stored values
                saveEpisodeToLocalSource(boxScore, storedEpisode)
                refreshLocalDataSource = true
            }
        }
        if (refreshLocalDataSource) boxScoreLocalDataSource.update(gameId, boxScore)
        return BoxScorePodcastSync(boxScore, hasPodcastEpisodes)
    }

    private suspend fun saveEpisode(episode: PodcastEpisode) {
        podcastRepository.savePodcast(
            episodeId = episode.episodeId,
            podcastId = episode.podcastId,
            episodeTitle = episode.title,
            description = episode.description.orEmpty(),
            duration = episode.duration?.toLong() ?: 0L,
            dateGmt = Date(episode.publishedAt.timeMillis).asGMTString(),
            mp3Url = episode.mp3Url.orEmpty(),
            imageUrl = episode.imageUrl.orEmpty(),
            permalinkUrl = episode.permalink,
            isDownloaded = false // todo: add proper value when implementing download functionality
        )
    }

    private fun saveEpisodeToLocalSource(boxScore: BoxScore, episode: PodcastEpisodeItem) {
        val isFinished = episode.duration == episode.timeElapsed.toLong()
        boxScore.sections.flatMap { it.modules }.flatMap { it.blocks }
            .filterIsInstance<PodcastEpisode>()
            .find { it.episodeId == episode.id.toString() }
            ?.apply {
                this.timeElapsed = episode.timeElapsed
                this.downloadState = if (episode.isDownloaded) DownloadState.DOWNLOADED else DownloadState.NOT_DOWNLOADED
                this.playbackState = if (isFinished) {
                    PlaybackState.Completed
                } else {
                    PlaybackState.None
                }
                this.finished = isFinished
            }
    }

    private fun getEpisodesForGame(boxScore: BoxScore): List<PodcastEpisode> {
        return mutableListOf<PodcastEpisode>().apply {
            addAll(
                boxScore.sections
                    .flatMap { it.modules }
                    .flatMap { it.blocks }
                    .filterIsInstance<PodcastEpisode>()
                    .map { it }
            )
        }
    }
}