package com.theathletic.podcast.downloaded.ui

import com.theathletic.entity.main.PodcastTrack
import com.theathletic.viewmodel.LiveViewModelState

data class LiveState(
    val activeTrack: PodcastTrack? = null,
    val currentProgressMs: Int = -1
) : LiveViewModelState