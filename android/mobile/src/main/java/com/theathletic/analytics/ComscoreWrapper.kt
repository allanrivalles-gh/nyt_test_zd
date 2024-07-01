package com.theathletic.analytics

import android.content.Context
import com.comscore.Analytics
import com.comscore.PublisherConfiguration
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.featureswitch.Features

private const val PUBLISHER_ID = "36100123"

class ComscoreWrapper @AutoKoin(Scope.SINGLE) constructor(
    private val features: Features
) {
    private var initialized: Boolean = false

    fun initializeAndStart(context: Context) {
        if (features.isComscoreEnabled.not()) return
        val publisherConfig = PublisherConfiguration.Builder().publisherId(PUBLISHER_ID).build()
        Analytics.getConfiguration().addClient(publisherConfig)
        Analytics.start(context)
        initialized = true
        notifyUxActive()
    }

    fun notifyUxActive() {
        if (initialized && features.isComscoreEnabled) {
            Analytics.notifyUxActive()
        }
    }

    fun notifyUxInactive() {
        if (initialized && features.isComscoreEnabled) {
            Analytics.notifyUxInactive()
        }
    }
}