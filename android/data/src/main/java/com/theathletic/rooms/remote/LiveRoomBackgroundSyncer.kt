package com.theathletic.rooms.remote

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.rooms.RoomsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

class LiveRoomBackgroundSyncer @AutoKoin(Scope.SINGLE) constructor(
    private val roomsRepository: RoomsRepository
) {
    companion object {
        const val TIME_BETWEEN_POLLS_MS = 3000L
    }

    private var currentJob: Job? = null

    fun startSync(
        roomId: String,
        scope: CoroutineScope,
    ) {
        if (currentJob != null) {
            Timber.e("Starting monitoring for room $roomId while previous job not cancelled")
            currentJob?.cancel()
        }
        currentJob = subscribe(roomId, scope)
    }

    private fun subscribe(roomId: String, scope: CoroutineScope) = scope.launch {
        try {
            Timber.v("Starting live audio room subscription")
            roomsRepository.subscribeLiveRoom(roomId)

            Timber.v("Subscription failed, falling back to polling")
            while (isActive) {
                delay(TIME_BETWEEN_POLLS_MS)

                Timber.v("Sending polling request")
                roomsRepository.getLiveAudioRoom(roomId, forceFetch = true)
            }
        } catch (e: CancellationException) {
            Timber.v("Background sync coroutine cancelled")
        }
    }

    fun cancel() {
        currentJob?.cancel()
        currentJob = null
    }
}