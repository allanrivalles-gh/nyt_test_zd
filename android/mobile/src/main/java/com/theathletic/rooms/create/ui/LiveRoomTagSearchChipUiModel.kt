package com.theathletic.rooms.create.ui

import com.theathletic.ui.UiModel

data class LiveRoomTagSearchChipUiModel(
    val id: String,
    val type: LiveRoomTagType,
    val title: String,
) : UiModel {
    override val stableId = id

    interface Interactor {
        fun onSearchChipClicked(id: String, type: LiveRoomTagType)
    }
}