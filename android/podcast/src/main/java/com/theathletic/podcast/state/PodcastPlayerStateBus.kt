package com.theathletic.podcast.state

import android.support.v4.media.session.PlaybackStateCompat
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.entity.main.PodcastTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.concurrent.TimeUnit

class PodcastPlayerStateBus @AutoKoin(Scope.SINGLE) constructor() {

    val currentState get() = state.value

    private val state = MutableStateFlow(PodcastPlayerState())

    /**
     * Use to listen to all changes, including playback progress. While a podcast is playing,
     * this will update many times per second so it's best to avoid doing heavy UI changes when
     * observing this.
     */
    val progressChangeFlow: Flow<PodcastPlayerState> = state

    /**
     * Use to listen to state changes, including when the track or the playback state changes. This
     * updates much less frequently than [progressChangeFlow] so it is more safe to do larger
     * UI operations while observing this.
     */
    val stateChangeFlow: Flow<PodcastPlayerState> =
        state.distinctUntilChanged { new, old ->
            new.activeTrack?.id == old.activeTrack?.id && new.playbackState == old.playbackState
        }

    fun updateActiveTrack(track: PodcastTrack?) {
        if (track?.id == currentState.activeTrack?.id) return

        if (currentState.activeTrack != null) {
            // If there was a previous playing track, mark that it has been paused.
            state.value = currentState.copy(playbackState = PlaybackStateCompat.STATE_PAUSED)
        }

        state.value = currentState.copy(
            activeTrack = track,
            playbackState = when (track) {
                null -> PlaybackStateCompat.STATE_NONE
                else -> PlaybackStateCompat.STATE_CONNECTING
            },
            currentProgressMs = track?.currentProgressMs ?: -1
        )
    }

    fun updatePlaybackState(@PlaybackStateCompat.State playbackState: Int) {
        state.value = currentState.copy(playbackState = playbackState)
    }

    fun updateProgress(progress: Int) {
        state.value = currentState.copy(currentProgressMs = progress)
    }
}

fun PodcastPlayerStateBus.configurableProgressChangeFlow(frequencyMs: Int): Flow<PodcastPlayerState> =
    progressChangeFlow.distinctUntilChanged { old, new ->
        old.playbackState == new.playbackState &&
            ((new.currentProgressMs - old.currentProgressMs) < frequencyMs)
    }

/**
 * [PodcastPlayerStateBus.progressChangeFlow] emits events very frequently. To avoid updating the UI
 * every few milliseconds, we only update the data state if the active track or playback state has
 * changed or if more than a minute has passed wince the last update since the UI only shows minutes
 * remaining in an episode
 */
val PodcastPlayerStateBus.minuteStateChangeFlow: Flow<PodcastPlayerState> get() =
    progressChangeFlow.distinctUntilChanged { old, new ->
        old.activeTrack?.id == new.activeTrack?.id &&
            old.playbackState == new.playbackState &&
            TimeUnit.MILLISECONDS.toMinutes(old.currentProgressMs.toLong()) ==
            TimeUnit.MILLISECONDS.toMinutes(new.currentProgressMs.toLong())
    }