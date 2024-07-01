package com.theathletic.utility

import androidx.annotation.ColorRes
import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.podcast.data.local.PodcastEpisodeEntity
import com.theathletic.podcast.state.PodcastPlayerState
import com.theathletic.podcast.ui.PodcastStringFormatter
import com.theathletic.podcast.ui.widget.TinyPodcastPlayer

class PodcastPlayerStateUtility @AutoKoin constructor(
    private val podcastStringFormatter: PodcastStringFormatter
) {

    fun getPlayerState(
        episode: PodcastEpisodeEntity,
        playerState: PodcastPlayerState
    ) = TinyPodcastPlayer.ViewState(
        formattedDuration = podcastStringFormatter.formatTinyPlayerDuration(episode.duration),
        controlsDrawable = playDrawable(episode.id.toLong(), playerState)
    )

    fun getPlayerState(
        episode: PodcastEpisodeItem,
        playerState: PodcastPlayerState,
        @ColorRes playButtonTint: Int
    ) = TinyPodcastPlayer.ViewState(
        formattedDuration = podcastStringFormatter.formatTinyPlayerDuration(episode.duration),
        controlsDrawable = playDrawable(episode.id, playerState),
        playButtonTint = playButtonTint
    )

    private fun playDrawable(
        episodeId: Long,
        playerState: PodcastPlayerState
    ) = when {
        playerState.activeTrack?.id == episodeId && playerState.isPlaying() -> R.drawable.ic_pause_2
        playerState.activeTrack?.id == episodeId && playerState.isConnecting() -> R.drawable.anim_podcast_play_connecting
        else -> R.drawable.ic_play_2
    }
}