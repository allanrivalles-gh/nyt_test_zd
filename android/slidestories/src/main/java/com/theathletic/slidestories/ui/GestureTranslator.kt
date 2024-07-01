package com.theathletic.slidestories.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class GestureTranslator @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider
) {

    private val coroutineScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    private val _gestureEvents = MutableSharedFlow<Event>()
    val gestureEvents = _gestureEvents.asSharedFlow()

    // Used for the initial delay before it starts to pause to ensure its not just a standard tap/click
    private var pressToPauseTimerJob: Job? = null

    private var isPressToPauseInProgress = false

    fun onGestureEvent(event: RawGestureEvent) {
        when (event) {
            RawGestureEvent.RAW_PRESS -> startPressToPauseTimer()
            RawGestureEvent.RAW_TAP_LEFT,
            RawGestureEvent.RAW_TAP_RIGHT,
            RawGestureEvent.RAW_TAP_CENTER -> handleTap(event)
        }
    }

    private fun handleTap(event: RawGestureEvent) {
        cancelPressToPauseTimer()
        coroutineScope.launch {
            when {
                isPressToPauseInProgress -> {
                    _gestureEvents.emit(Event.PRESS_TO_PAUSE_END)
                    isPressToPauseInProgress = false
                }
                event == RawGestureEvent.RAW_TAP_LEFT -> _gestureEvents.emit(Event.LEFT_TAP)
                event == RawGestureEvent.RAW_TAP_RIGHT -> _gestureEvents.emit(Event.RIGHT_TAP)
            }
        }
    }

    // Wait for a bit before setting the start of a 'press to pause' in case its just a tap/click
    private fun startPressToPauseTimer() {
        pressToPauseTimerJob = coroutineScope.launch {
            delay(500)
            _gestureEvents.emit(Event.PRESS_TO_PAUSE_START)
            isPressToPauseInProgress = true
        }
    }

    private fun cancelPressToPauseTimer() {
        if (pressToPauseTimerJob != null) pressToPauseTimerJob?.cancel()
    }

    enum class Event {
        LEFT_TAP,
        RIGHT_TAP,
        PRESS_TO_PAUSE_START,
        PRESS_TO_PAUSE_END,
        CLOSE
    }
}

enum class RawGestureEvent {
    RAW_PRESS,
    RAW_TAP_LEFT,
    RAW_TAP_RIGHT,
    RAW_TAP_CENTER
}