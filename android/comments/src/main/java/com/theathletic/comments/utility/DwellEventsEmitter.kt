package com.theathletic.comments.utility

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import kotlinx.coroutines.delay

class DwellEventsEmitter @AutoKoin(Scope.SINGLE) constructor() {

    suspend fun start(emitEvent: (seconds: Int) -> Unit) {
        var seconds = 0
        while (seconds <= 3600) {
            delay(1000)
            seconds += 1
            if (shouldEmit(seconds)) emitEvent(seconds)
        }
    }

    private fun shouldEmit(seconds: Int): Boolean {
        if (seconds == 3) return true
        if (seconds == 5) return true
        if (seconds % 20 == 0) return true
        return false
    }
}