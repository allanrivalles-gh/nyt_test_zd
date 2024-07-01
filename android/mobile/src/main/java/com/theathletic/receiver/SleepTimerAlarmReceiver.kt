package com.theathletic.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.theathletic.BuildConfig
import com.theathletic.extension.extLogError
import com.theathletic.manager.PodcastManager
import com.theathletic.rxbus.RxBus
import com.theathletic.service.PodcastServicePlaybackAction
import com.theathletic.utility.Preferences

class SleepTimerAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(context, "DEBUG ONLY - ALARM", Toast.LENGTH_LONG).show()
        }
        pausePlayback()
    }

    private fun pausePlayback() {
        PodcastManager.getTransportControlsSingle().subscribe(
            { transportControls ->
                transportControls?.sendCustomAction(PodcastServicePlaybackAction.KILL_PLAYER.value, null)
                RxBus.instance.post(RxBus.SleepTimerPauseEvent())
                Preferences.clearPodcastSleepTimestamp()
            },
            Throwable::extLogError
        )
    }
}