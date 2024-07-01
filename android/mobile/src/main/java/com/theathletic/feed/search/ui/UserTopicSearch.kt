package com.theathletic.feed.search.ui

import androidx.annotation.StringRes
import com.theathletic.feed.search.SearchFollowableItem
import com.theathletic.ui.UiModel

interface UserTopicSearch {
    interface Interactor :
        com.theathletic.presenter.Interactor,
        IUserTopicListItemView,
        UserSearchFollowableItem.Interactor {
        fun onCloseClick()
        fun onSearchClearClick()
        fun onQueryChanged(query: String)
        fun onEditClick()
    }

    class ViewState(
        val uiModels: List<UiModel>,
        val showSearchClearButton: Boolean,
        val showClearSelected: Boolean,
        @StringRes val searchEntryHint: Int
    ) : com.theathletic.ui.ViewState

    sealed class Event : com.theathletic.utility.Event() {
        object ClearSearch : Event()
        object CloseDialog : Event()

        class ItemSelected(val topic: SearchFollowableItem?) : Event()
    }
}