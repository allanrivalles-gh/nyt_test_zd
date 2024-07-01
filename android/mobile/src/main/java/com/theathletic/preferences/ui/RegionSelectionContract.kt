package com.theathletic.preferences.ui

import com.theathletic.presenter.Interactor
import com.theathletic.profile.ui.RegionOptions
import com.theathletic.profile.ui.RegionSettingsUiModel
import com.theathletic.ui.ViewState

interface RegionSelectionContract {

    interface ViewModelInteractor : Interactor, RegionSettingsUiModel.Interactor

    sealed class Event : com.theathletic.utility.Event() {
        object NavigateBackToProfile : Event()
    }

    data class RegionSelectionViewState(
        val selectedRegionIndex: Int,
        val region: List<RegionOptions>
    ) : ViewState
}