package com.theathletic.podcast

import android.content.Context
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import timber.log.Timber

class ExoPlayerPodcastPlayer {

    lateinit var player: ExoPlayer

    val currentPositionMs: Int get() = player.currentPosition.toInt()
    val durationMs: Int get() = player.duration.toInt()
    val isPlaying: Boolean get() = player.playWhenReady && player.playbackState == Player.STATE_READY

    val bufferProgressPct: Int get() = player.bufferedPercentage

    var onPreparedListener: (() -> Unit)? = null
    var onPausedListener: (() -> Unit)? = null
    var onCompletionListener: (() -> Unit)? = null
    var onErrorListener: (() -> Unit)? = null

    var prepared = false

    val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> play()
                Player.STATE_ENDED -> onCompletionListener?.invoke()
                else -> {}
            }
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            super.onPlayWhenReadyChanged(playWhenReady, reason)
            if (!playWhenReady) {
                onPausedListener?.invoke()
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Timber.e(error, "ExoPlayerPodcastPlayer error")
            onErrorListener?.invoke()
        }

        private fun play() {
            if (!prepared) {
                prepared = true
                onPreparedListener?.invoke()
            }
        }
    }

    fun init(context: Context) {
        player = ExoPlayer.Builder(context).apply {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
                .build()

            val loadControl = DefaultLoadControl.Builder().setBufferDurationsMs(
                MIN_BUFFER_MS,
                MAX_BUFFER_MS,
                MIN_BUFFER_FOR_PLAYBACK_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
            ).build()

            setTrackSelector(DefaultTrackSelector(context))
            setLoadControl(loadControl)
            setAudioAttributes(audioAttributes, true)
        }.build()
        player.addListener(listener)
    }

    fun release() {
        player.removeListener(listener)
        player.release()
    }

    fun prepare(context: Context, uri: String) {
        val userAgent = Util.getUserAgent(
            context,
            context.getString(com.theathletic.R.string.app_name)
        )
        val mediaSource = ProgressiveMediaSource.Factory(
            DefaultDataSource.Factory(
                context,
                DefaultHttpDataSource.Factory().apply {
                    setUserAgent(userAgent)
                }
            ),
            DefaultExtractorsFactory()
        ).createMediaSource(MediaItem.fromUri(uri))

        prepared = false
        player.setMediaSource(mediaSource)
        player.prepare()
    }

    fun play() {
        player.playWhenReady = true
    }

    fun pause() {
        player.playWhenReady = false
    }

    fun stop() {
        player.stop()
    }

    fun seekTo(timeMs: Int) {
        player.seekTo(timeMs.toLong())
    }

    fun setVolume(volume: Float) {
        player.volume = volume
    }

    fun setPlaybackSpeed(playbackSpeed: Float) {
        player.playbackParameters = PlaybackParameters(playbackSpeed)
    }

    companion object {
        private const val MIN_BUFFER_MS = 30000
        private const val MAX_BUFFER_MS = 60000
        private const val MIN_BUFFER_FOR_PLAYBACK_MS = 1500
    }
}