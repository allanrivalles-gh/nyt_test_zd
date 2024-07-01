package com.theathletic.main

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

sealed class MainEvent {
    data class ScrollToTop(val route: String) : MainEvent()
}

class MainEventProducer(
    private val mutableSharedFlow: MutableSharedFlow<MainEvent> = MutableSharedFlow(extraBufferCapacity = 1)
) : MutableSharedFlow<MainEvent> by mutableSharedFlow

class MainEventConsumer(
    private val eventProducer: MainEventProducer
) : Flow<MainEvent> by eventProducer {

    fun observe(
        coroutineScope: CoroutineScope,
        collector: suspend (value: MainEvent) -> Unit
    ) {
        coroutineScope.launch {
            collect { collector(it) }
        }
    }
}