package com.theathletic.rooms.ui

import com.theathletic.ui.UiModel
import com.theathletic.ui.binding.ParameterizedString

object LiveRoomControlsOnStageTitle : UiModel {
    override val stableId = "LiveRoomControlsOnStageTitle"
}

data class LiveRoomControlsOnStageHost(
    val id: String,
    val name: ParameterizedString,
    val initials: String,
    val imageUrl: String?,
) : UiModel {
    override val stableId = "LiveRoomControlsOnStageHost:$id"
}

data class LiveRoomControlsOnStageUser(
    val id: String,
    val name: ParameterizedString,
    val initials: String,
    val showSpinner: Boolean,
    val imageUrl: String?,
    val showVerifiedCheck: Boolean,
) : UiModel {
    override val stableId = "LiveRoomControlsOnStageUser:$id"

    interface Interactor {
        fun onRemoveClicked(userId: String)
    }
}

data class LiveRoomControlsRequestsTitle(val requestCount: String) : UiModel {
    override val stableId = "LiveRoomControlsRequestsTitle"
}

data class LiveRoomControlsSpeakingRequest(
    val id: String,
    val name: ParameterizedString,
    val initials: String,
    val imageUrl: String?,
    val showVerifiedCheck: Boolean,
) : UiModel {
    override val stableId = "LiveRoomControlsSpeakingRequest:$id"

    interface Interactor {
        fun onRequestResponseClicked(userId: String, approved: Boolean)
    }
}