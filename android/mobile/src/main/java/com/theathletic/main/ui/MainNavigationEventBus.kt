package com.theathletic.main.ui

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

sealed class MainNavigationEvent {
    object ScrollToTopOfFeed : MainNavigationEvent()
    object ScrollToTopHeadlines : MainNavigationEvent()
    data class ShowActionTextSnackbar(val actionSnackbarData: ActionSnackbarData) : MainNavigationEvent()
}

class MainNavigationEventProducer(
    private val mutableSharedFlow: MutableSharedFlow<MainNavigationEvent> = MutableSharedFlow()
) : MutableSharedFlow<MainNavigationEvent> by mutableSharedFlow

class MainNavigationEventConsumer(
    private val producer: MainNavigationEventProducer
) : Flow<MainNavigationEvent> by producer