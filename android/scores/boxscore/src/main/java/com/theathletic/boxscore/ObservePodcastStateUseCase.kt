package com.theathletic.boxscore

import android.support.v4.media.session.PlaybackStateCompat
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.data.local.BoxScoreLocalDataSource
import com.theathletic.boxscore.data.local.BoxScorePodcastState
import com.theathletic.boxscore.data.local.PodcastEpisode
import com.theathletic.podcast.state.PodcastPlayerStateBus
import com.theathletic.podcast.state.configurableProgressChangeFlow
import com.theathletic.podcast.ui.PlaybackState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val PODCAST_UPDATE_FREQUENCY_MS = 5000

class ObservePodcastStateUseCase @AutoKoin constructor(
    private val boxScoreLocalDataSource: BoxScoreLocalDataSource,
    private val podcastPlayerStateBus: PodcastPlayerStateBus
) {
    operator fun invoke(gameId: String): Flow<BoxScorePodcastState?> {
        return podcastPlayerStateBus
            .configurableProgressChangeFlow(PODCAST_UPDATE_FREQUENCY_MS)
            .map { podcastState ->
                val activeTrack = podcastState.activeTrack ?: return@map null
                val timeElapsedSeconds = podcastState.currentProgressMs / 1000
                val durationSeconds = activeTrack.duration
                val isFinished = timeElapsedSeconds.toLong() == durationSeconds
                val playbackState = when {
                    isFinished -> PlaybackState.Completed
                    podcastState.playbackState == PlaybackStateCompat.STATE_PLAYING -> PlaybackState.Playing
                    podcastState.playbackState == PlaybackStateCompat.STATE_CONNECTING -> PlaybackState.Loading
                    else -> PlaybackState.None
                }

                val boxScore = boxScoreLocalDataSource.getItem(gameId)
                boxScore?.sections?.flatMap { it.modules }?.flatMap { it.blocks }
                    ?.filterIsInstance<PodcastEpisode>()
                    ?.find { it.episodeId.toLong() == activeTrack.episodeId }
                    ?.apply {
                        this.timeElapsed = timeElapsedSeconds
                        this.playbackState = playbackState
                        this.finished = isFinished
                    }
                BoxScorePodcastState(
                    boxScore,
                    activeTrack.episodeId.toString(),
                    playbackState
                )
            }
    }
}