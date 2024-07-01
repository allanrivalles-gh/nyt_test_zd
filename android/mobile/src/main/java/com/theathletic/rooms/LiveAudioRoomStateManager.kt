package com.theathletic.rooms

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.audio.AudioEngineState
import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.rooms.local.LiveAudioRoomUserDetailsDataSource
import com.theathletic.rooms.ui.LiveAudioRoomMiniPlayerUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class LiveAudioRoomStateManager @AutoKoin(Scope.SINGLE) constructor(
    private val liveAudioRoomUserDetails: LiveAudioRoomUserDetailsDataSource
) {

    val currentRoom = MutableStateFlow<LiveAudioRoomEntity?>(null)
    val currentRoomViewState get() = currentRoom.map {
        it?.let {
            LiveAudioRoomMiniPlayerUiModel(
                id = it.id,
                title = it.title,
                subtitle = it.subtitle
            )
        }
    }

    val audioEngineState = MutableStateFlow<AudioEngineState?>(null)
    val userIdToVolume = MutableStateFlow<Map<String, Int>>(emptyMap())

    fun userDetailsForRoom(roomId: String) = liveAudioRoomUserDetails.observeItem(roomId)
        .map { userDetailsList -> userDetailsList?.associateBy { it.id } ?: emptyMap() }
}