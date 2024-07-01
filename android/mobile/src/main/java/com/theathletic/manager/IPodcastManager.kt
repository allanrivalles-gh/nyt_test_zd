package com.theathletic.manager

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableFloat
import androidx.databinding.ObservableInt
import com.theathletic.entity.main.PodcastTrack

interface IPodcastManager {
    var activeTrack: ObservableField<PodcastTrack?>
    val playbackState: ObservableInt
    val currentBufferProgress: ObservableInt
    val currentProgress: ObservableInt
    var currentPlayBackSpeed: ObservableFloat
    var playBackSpeedEnabled: ObservableBoolean

    fun trackPodcastListenedState(onComplete: Boolean = false)
    fun trackPodcastListenedState(
        episodeId: Long,
        progress: Long,
        isFinished: Boolean
    )
}