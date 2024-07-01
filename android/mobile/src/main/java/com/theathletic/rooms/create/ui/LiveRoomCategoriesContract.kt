package com.theathletic.rooms.create.ui

import com.theathletic.presenter.Interactor

interface LiveRoomCategoriesContract {

    interface Presenter :
        Interactor,
        LiveRoomCategoriesUi.Interactor {
        override fun onCloseClicked()
        override fun onCategoryClicked(value: String)
    }

    data class ViewState(
        val categories: List<LiveRoomCategoriesUi.Category>,
    ) : com.theathletic.ui.ViewState
}