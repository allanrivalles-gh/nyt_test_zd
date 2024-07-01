package com.theathletic.rooms.ui

import com.theathletic.ui.UiModel

interface LiveAudioRoomMiniPlayerInteractor {
    fun onRoomMiniPlayerClicked(id: String)
    fun onRoomCloseClicked(id: String)
}

data class LiveAudioRoomMiniPlayerUiModel(
    val id: String,
    val title: String,
    val subtitle: String
) : UiModel {
    override val stableId = "LiveAudioRoomMiniPlayer:$id"
}