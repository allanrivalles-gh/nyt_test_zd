package com.theathletic.viewmodel.main

import android.os.Bundle
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LifecycleObserver
import com.theathletic.fragment.main.PodcastSleepTimerSheetDialogFragment
import com.theathletic.rxbus.RxBus
import com.theathletic.viewmodel.BaseViewModel
import org.koin.core.component.KoinComponent

class PodcastSleepTimerViewModel(extras: Bundle? = null) : BaseViewModel(), LifecycleObserver, KoinComponent {
    val sleepTimerRunning = ObservableBoolean(false)

    init {
        handleExtras(extras)
    }

    private fun handleExtras(extras: Bundle?) {
        sleepTimerRunning.set(extras?.getBoolean(PodcastSleepTimerSheetDialogFragment.EXTRA_SLEEP_TIMER_ACTIVATED) ?: false)
    }

    fun onSleepDelayClick(delayInMinutes: Int) {
        RxBus.instance.post(SleepDelayEvent(delayInMinutes))
    }

    fun onTurnTimerOffClick() {
        RxBus.instance.post(SleepCancelEvent())
    }

    fun onSleepAfterEpisodeClick() {
        RxBus.instance.post(SleepAfterEpisodeEvent())
    }

    internal class SleepDelayEvent(val delayInMinutes: Int)
    internal class SleepCancelEvent
    internal class SleepAfterEpisodeEvent
}