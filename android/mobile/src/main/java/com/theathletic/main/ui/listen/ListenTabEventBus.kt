package com.theathletic.main.ui.listen

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

sealed class ListenTabEvent {
    object SwitchToDiscoverTab : ListenTabEvent()
}

class ListenTabEventProducer(
    private val mutableSharedFlow: MutableSharedFlow<ListenTabEvent> = MutableSharedFlow()
) : MutableSharedFlow<ListenTabEvent> by mutableSharedFlow

class ListenTabEventConsumer(
    private val listenTabEventProducer: ListenTabEventProducer
) : Flow<ListenTabEvent> by listenTabEventProducer {

    inline fun <reified T : ListenTabEvent> observe(
        coroutineScope: CoroutineScope,
        crossinline collector: suspend (value: T) -> Unit
    ) {
        coroutineScope.launch {
            filterIsInstance<T>().collect { collector(it) }
        }
    }
}