package com.theathletic.analytics.newarch

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.scores.data.remote.LiveGamesSubscriptionManager
import timber.log.Timber

class LifecycleTracker(
    val analytics: Analytics
) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        analytics.track(
            Event.AppLifecycle.ToForeground
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground() {
        analytics.track(
            Event.AppLifecycle.ToBackground
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onAppDestroyed() {
        Timber.d("app ON_DESTROY")
        analytics.track(
            Event.AppLifecycle.Terminated
        )
    }
}

class LiveScoresSubscriptionLifecycleTracker @AutoKoin(Scope.SINGLE) constructor(
    private val liveGamesSubscriptionManager: LiveGamesSubscriptionManager
) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        liveGamesSubscriptionManager.resume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        liveGamesSubscriptionManager.pause()
    }
}