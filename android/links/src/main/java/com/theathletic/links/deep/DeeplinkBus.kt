package com.theathletic.links.deep

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class DeeplinkEventProducer(
    private val mutableSharedFlow: MutableSharedFlow<String> = MutableSharedFlow(0, extraBufferCapacity = 1)
) : MutableSharedFlow<String> by mutableSharedFlow

class DeeplinkEventConsumer(
    private val deeplinkEventProducer: DeeplinkEventProducer
) : Flow<String> by deeplinkEventProducer {

    fun observe(
        coroutineScope: CoroutineScope,
        collector: suspend (value: String) -> Unit
    ) {
        coroutineScope.launch {
            collect { collector(it) }
        }
    }
}