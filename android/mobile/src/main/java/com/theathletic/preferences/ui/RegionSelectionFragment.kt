package com.theathletic.preferences.ui

import androidx.compose.runtime.Composable
import com.theathletic.fragment.AthleticComposeFragment
import com.theathletic.profile.ui.RegionSettings
import com.theathletic.ui.observe
import org.koin.androidx.viewmodel.ext.android.getViewModel

class RegionSelectionFragment :
    AthleticComposeFragment<RegionSelectionViewModel,
        RegionSelectionContract.RegionSelectionViewState>() {

    companion object {
        fun newInstance() = RegionSelectionFragment()
    }

    override fun setupViewModel() = getViewModel<RegionSelectionViewModel>()

    override fun onResume() {
        super.onResume()
        viewModel.observe<RegionSelectionContract.Event.NavigateBackToProfile>(viewLifecycleOwner) {
            navigator.finishActivity()
        }
    }

    @Composable
    override fun Compose(state: RegionSelectionContract.RegionSelectionViewState) {
        activity?.supportFragmentManager?.let {
            RegionSettings(
                regions = state.region,
                selectedIndex = state.selectedRegionIndex,
                interactor = viewModel
            )
        }
    }
}