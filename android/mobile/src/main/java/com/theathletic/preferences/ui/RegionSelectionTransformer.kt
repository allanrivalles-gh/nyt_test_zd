package com.theathletic.preferences.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.ui.Transformer

class RegionSelectionTransformer @AutoKoin constructor() :
    Transformer<RegionSelectionState, RegionSelectionContract.RegionSelectionViewState> {

    override fun transform(data: RegionSelectionState): RegionSelectionContract.RegionSelectionViewState {
        return RegionSelectionContract.RegionSelectionViewState(
            region = data.regions,
            selectedRegionIndex = data.selectedIndex()
        )
    }
}

private fun RegionSelectionState.selectedIndex(): Int {
    val index = this.regions.indexOf(this.currentRegion)
    return if (index > -1) index else 0
}