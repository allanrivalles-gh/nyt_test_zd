package com.theathletic.ui.list

import com.theathletic.presenter.Interactor
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState

interface AthleticListInteractor : Interactor {
    fun onRefresh() {
        // Optional override
    }
    fun onFeedNotificationClick() {
        // Also optional override
    }
}

abstract class AthleticListViewModel<D : DataState, V : ListViewState> :
    AthleticViewModel<D, V>(),
    AthleticListInteractor