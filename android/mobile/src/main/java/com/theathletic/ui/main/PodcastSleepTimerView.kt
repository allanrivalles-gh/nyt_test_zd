package com.theathletic.ui.main

import com.theathletic.ui.BaseView
import com.theathletic.viewmodel.main.PodcastBigPlayerViewModel

interface PodcastSleepTimerView : BaseView {
    fun onCloseClick()
    fun onSleepDelayClick(chosenDelay: PodcastBigPlayerViewModel.SleepTimerOptions)
    fun onTurnTimerOffClick()
    fun onSleepAfterEpisodeClick()
}