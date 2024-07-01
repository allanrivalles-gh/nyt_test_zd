package com.theathletic.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.theathletic.ui.LegacyAthleticViewModel
import com.theathletic.ui.LoadingState
import com.theathletic.ui.UiModel

/**
 * Works with the [AthleticListFragment] in order to quickly build list screens without much
 * boilerplate and plumbing code.
 *
 * In order to enable refreshing on the screen, override [refreshable] and set it to true, then
 * override [onRefresh] in order to start the refresh. Remember to call
 * [setLoadingState] with [LoadingState.FINISHED] when the refresh is completed.
 */
@Deprecated("Use AthleticViewModel and Compose LazyColumns instead")
abstract class LegacyAthleticListViewModel : LegacyAthleticViewModel() {
    companion object {
        val LOADING_LIST = listOf(ListLoadingItem)
    }

    abstract val uiModels: LiveData<List<UiModel>>

    open val refreshable: Boolean get() = false
    open fun onRefresh() {}

    private val _loadingState = MutableLiveData(LoadingState.INITIAL_LOADING)
    val loadingState: LiveData<LoadingState> = _loadingState

    protected fun setLoadingState(state: LoadingState) {
        _loadingState.value = state
    }
}