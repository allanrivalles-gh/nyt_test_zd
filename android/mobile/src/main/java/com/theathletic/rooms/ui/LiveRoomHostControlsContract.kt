package com.theathletic.rooms.ui

import com.theathletic.presenter.Interactor
import com.theathletic.ui.UiModel

interface LiveRoomHostControlsContract {

    interface Presenter :
        Interactor,
        LiveRoomControlsSpeakingRequest.Interactor,
        LiveRoomControlsOnStageUser.Interactor

    data class Params(
        val roomId: String
    )

    data class ViewState(
        val uiModels: List<UiModel> = emptyList()
    ) : com.theathletic.ui.ViewState
}