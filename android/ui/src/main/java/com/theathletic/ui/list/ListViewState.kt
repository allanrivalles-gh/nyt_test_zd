package com.theathletic.ui.list

import com.theathletic.ui.UiModel
import com.theathletic.ui.ViewState
import com.theathletic.ui.binding.ParameterizedString

interface ListViewState : ViewState {
    val showSpinner: Boolean
    val uiModels: List<UiModel>
    val refreshable: Boolean
    val showToolbar: Boolean
    val showListUpdateNotification: Boolean
    val listUpdateLabel: ParameterizedString?
    val backgroundColorRes: Int
}

data class SimpleListViewState(
    override val showSpinner: Boolean,
    override val uiModels: List<UiModel>,
    override val refreshable: Boolean = false,
    override val showToolbar: Boolean = true,
    override val showListUpdateNotification: Boolean = false,
    override val listUpdateLabel: ParameterizedString? = null,
    override val backgroundColorRes: Int
) : ListViewState