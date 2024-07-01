package com.theathletic.auth

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.LifecycleObserver
import com.theathletic.AthleticConfig
import com.theathletic.R
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.event.SnackbarEvent
import com.theathletic.extension.extGetString
import com.theathletic.onboarding.BeginOnboardingUseCase
import com.theathletic.ui.LegacyAthleticViewModel
import com.theathletic.utility.NetworkManager
import com.theathletic.widget.StatefulLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AuthenticationViewModel : LegacyAthleticViewModel(), LifecycleObserver, KoinComponent {
    val state = ObservableInt(StatefulLayout.CONTENT)
    val debugToolsButtonVisible = ObservableBoolean(AthleticConfig.DEBUG_TOOLS_ENABLED)
    private val beginOnboardingUseCase by inject<BeginOnboardingUseCase>()
    private val analytics by inject<Analytics>()

    init {
        analytics.track(Event.Authentication.StartScreenView())
    }

    fun onResume() {
        state.set(StatefulLayout.CONTENT)
    }

    fun beginOnboarding() {
        analytics.track(Event.Authentication.GetStartedClick())

        if (NetworkManager.getInstance().isOffline()) {
            sendEvent(SnackbarEvent(R.string.global_network_offline.extGetString()))
        } else {
            state.set(StatefulLayout.PROGRESS)
            beginOnboardingUseCase()
        }
    }
}