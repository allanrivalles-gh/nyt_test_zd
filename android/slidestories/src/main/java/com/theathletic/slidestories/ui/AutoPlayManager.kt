package com.theathletic.slidestories.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

const val DEFAULT_DELAY_BETWEEN_SLIDES = 5_000L // 5 secs
private const val DELAY_BETWEEN_UPDATES = 100L // 0.1 secs

class AutoPlayManager @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider
) {

    private val coroutineScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    private val _progressUpdates = MutableSharedFlow<Float>()
    val progressUpdates = _progressUpdates.asSharedFlow()

    private var totalDelayLapsed: Long = 0L

    private var playTimerJob: Job? = null

    private var isPaused: Boolean = false

    fun start(slideDuration: Long = DEFAULT_DELAY_BETWEEN_SLIDES) {
        cancelPlayTimer()
        startPlayTimer(slideDuration)
    }

    fun stop() {
        cancelPlayTimer()
    }

    fun pause() {
        isPaused = true
    }

    fun resume() {
        isPaused = false
    }

    private fun startPlayTimer(slideDuration: Long) {
        totalDelayLapsed = 0L
        playTimerJob = coroutineScope.launch {
            while ((totalDelayLapsed <= slideDuration) && isActive) {
                if (isPaused.not()) {
                    _progressUpdates.emit(getSlideProgress(totalDelayLapsed, slideDuration))
                    delay(DELAY_BETWEEN_UPDATES)
                    totalDelayLapsed += DELAY_BETWEEN_UPDATES
                }
            }
            _progressUpdates.emit(getSlideProgress(totalDelayLapsed, slideDuration))
        }
    }

    private fun cancelPlayTimer() {
        playTimerJob?.takeIf { it.isActive }?.cancel()
    }

    private fun getSlideProgress(totalDelayLapsed: Long, slideDuration: Long): Float {
        return totalDelayLapsed.toFloat() / slideDuration.toFloat()
    }
}