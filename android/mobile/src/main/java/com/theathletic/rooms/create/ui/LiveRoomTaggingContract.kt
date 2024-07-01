package com.theathletic.rooms.create.ui

import com.theathletic.presenter.Interactor
import com.theathletic.ui.UiModel

interface LiveRoomTaggingContract {

    interface Presenter :
        Interactor,
        LiveRoomTagSearchChipUiModel.Interactor,
        LiveRoomHostSearchResultUiModel.Interactor,
        LiveRoomTagSearchResultUiModel.Interactor {
        fun onQueryChanged(query: String)
        fun onClearSearch()
    }

    data class ViewState(
        val searchText: String,
        val resultsUiModels: List<UiModel> = emptyList(),
        val selectedChipModels: List<LiveRoomTagSearchChipUiModel> = emptyList(),
        val showClearButton: Boolean = false,
    ) : com.theathletic.ui.ViewState
}