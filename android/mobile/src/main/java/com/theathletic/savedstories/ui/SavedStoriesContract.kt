package com.theathletic.savedstories.ui

import com.theathletic.R
import com.theathletic.presenter.Interactor
import com.theathletic.savedstories.ui.models.SavedStoryListItem
import com.theathletic.ui.UiModel
import com.theathletic.ui.binding.ParameterizedString
import com.theathletic.ui.list.ListViewState

interface SavedStoriesContract {

    interface Presenter : Interactor, SavedStoryListItem.Interactor

    sealed class Event : com.theathletic.utility.Event() {
        class ShowArticleLongClickSheet(val articleId: Long, val isBookmarked: Boolean) : Event()
    }

    data class SavedStoriesViewState(
        override val showSpinner: Boolean,
        override val uiModels: List<UiModel>,
        override val refreshable: Boolean = false,
        override val showToolbar: Boolean = true,
        override val showListUpdateNotification: Boolean = false,
        override val listUpdateLabel: ParameterizedString? = null,
        override val backgroundColorRes: Int = R.color.ath_grey_70,
        val isDeleteAllEnabled: Boolean = false,
    ) : ListViewState
}