package com.theathletic.debugtools.logs.ui

import androidx.compose.runtime.Composable
import com.theathletic.fragment.AthleticComposeFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class AnalyticsLogFragment : AthleticComposeFragment<AnalyticsLogViewModel, AnalyticsLogContract.ViewState>() {

    companion object {
        fun newInstance() = AnalyticsLogFragment()
    }

    override fun setupViewModel() = getViewModel<AnalyticsLogViewModel> {
        parametersOf(navigator)
    }

    @Composable
    override fun Compose(state: AnalyticsLogContract.ViewState) {
        AnalyticsLogScreen(state.analyticsLogList, viewModel)
    }
}