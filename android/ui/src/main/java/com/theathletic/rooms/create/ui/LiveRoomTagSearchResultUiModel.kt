package com.theathletic.rooms.create.ui

import com.theathletic.ui.UiModel

data class LiveRoomTagSearchResultUiModel(
    val id: String,
    val type: LiveRoomTagType,
    val name: String,
    val logoUri: String,
    val isChecked: Boolean,
) : UiModel {
    override val stableId = "LiveRoomTagSearchItem:${type.name}:$id"

    interface Interactor {
        fun onTagClicked(id: String, type: LiveRoomTagType)
    }
}

data class LiveRoomHostSearchResultUiModel(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val isChecked: Boolean,
) : UiModel {
    override val stableId = "LiveRoomHostSearchItem:$id"

    interface Interactor {
        fun onHostClicked(id: String)
    }
}