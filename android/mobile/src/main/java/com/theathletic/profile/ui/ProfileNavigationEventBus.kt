package com.theathletic.profile.ui

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

sealed class ProfileNavigationEvent {
    object ScrollToTopOfFeed : ProfileNavigationEvent()
}

class ProfileNavigationEventProducer(
    private val mutableSharedFlow: MutableSharedFlow<ProfileNavigationEvent> = MutableSharedFlow()
) : MutableSharedFlow<ProfileNavigationEvent> by mutableSharedFlow

class ProfileNavigationEventConsumer(
    private val producer: ProfileNavigationEventProducer
) : Flow<ProfileNavigationEvent> by producer