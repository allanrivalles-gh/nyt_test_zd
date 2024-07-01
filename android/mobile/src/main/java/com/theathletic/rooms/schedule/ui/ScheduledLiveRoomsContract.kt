package com.theathletic.rooms.schedule.ui

import com.theathletic.links.deep.Deeplink
import com.theathletic.presenter.Interactor
import com.theathletic.rooms.ui.ScheduledRoomsUi
import com.theathletic.ui.widgets.ModalBottomSheetType

interface ScheduledLiveRoomsContract {

    interface Presenter :
        Interactor,
        ScheduledRoomsUi.Interactor

    data class ViewState(
        val uiModel: ScheduledRoomsUi,
        val currentBottomSheetModal: ModalSheetType? = null,
    ) : com.theathletic.ui.ViewState

    sealed class ModalSheetType : ModalBottomSheetType {
        data class LinksMenu(
            val deeplink: Deeplink,
            val universalLink: String
        ) : ModalSheetType()
    }
}