package com.theathletic.profile.ui

object ProfilePreviewData {

    val regionInteractor = object : RegionSettingsUiModel.Interactor {
        override fun onBackClick() {}
        override fun regionOptionSelected(region: RegionOptions) {}
    }

    val regionSettingsUiModel = RegionSettingsUiModel(
        id = "001",
        regions = listOf(RegionOptions.NORTH_AMERICA, RegionOptions.INTERNATIONAL),
        selectedIndex = 1
    )
}