package com.theathletic.rooms.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

sealed class LiveAudioEvent {
    data class LeaveRoom(val roomEnded: Boolean = false) : LiveAudioEvent()
    data class ChangeMute(val isMuted: Boolean) : LiveAudioEvent()
    data class SwapStageStatus(
        val onStage: Boolean,
        val fromHost: Boolean = false
    ) : LiveAudioEvent()
    object AutoPushSent : LiveAudioEvent()
}

class LiveAudioEventProducer(
    private val mutableSharedFlow: MutableSharedFlow<LiveAudioEvent> = MutableSharedFlow()
) : MutableSharedFlow<LiveAudioEvent> by mutableSharedFlow

class LiveAudioEventConsumer(
    private val producer: LiveAudioEventProducer
) : Flow<LiveAudioEvent> by producer {

    inline fun <reified T : LiveAudioEvent> observe(
        coroutineScope: CoroutineScope,
        crossinline collector: suspend (value: T) -> Unit
    ) {
        coroutineScope.launch {
            filterIsInstance<T>().collect { collector(it) }
        }
    }
}