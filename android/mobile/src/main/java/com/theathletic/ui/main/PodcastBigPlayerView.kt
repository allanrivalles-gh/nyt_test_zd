package com.theathletic.ui.main

import com.theathletic.ui.BaseView
import org.alfonz.adapter.AdapterView

interface PodcastBigPlayerView : BaseView, AdapterView, PodcastTrackItemView, PodcastStoryItemView {
    fun onOpenDetailOverlayClick()
    fun onPlayPauseClick()
    fun onBackwardClick()
    fun onForwardClick()
    fun onChangeSpeedClick()
    fun onShareClick()
    fun onCloseClick()
    fun onOpenQueueClick()
    fun onSleepTimerClick()
}