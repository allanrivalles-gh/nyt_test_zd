package com.theathletic.service

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat

class LiveRoomMediaSession(context: Context) {

    companion object {
        const val MEDIA_SESSION_TAG = "TheAthletic"
    }

    val sessionToken: MediaSessionCompat.Token get() = mediaSession.sessionToken

    private var mediaSession = MediaSessionCompat(context, MEDIA_SESSION_TAG).also {
        it.isActive = true
        it.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0L, 1f)
                .build()
        )
    }

    fun release() {
        mediaSession.apply {
            isActive = false
            setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_STOPPED, 0L, 1f)
                    .build()
            )
            release()
        }
    }
}