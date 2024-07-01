package com.theathletic.podcast.state

import android.support.v4.media.session.PlaybackStateCompat
import com.theathletic.entity.main.PodcastTrack

data class PodcastPlayerState(
    val activeTrack: PodcastTrack? = null,
    @PlaybackStateCompat.State val playbackState: Int = PlaybackStateCompat.STATE_NONE,
    val currentProgressMs: Int = -1
) {
    fun isConnecting() = playbackState == PlaybackStateCompat.STATE_CONNECTING
    fun isPlaying() = playbackState == PlaybackStateCompat.STATE_PLAYING
}