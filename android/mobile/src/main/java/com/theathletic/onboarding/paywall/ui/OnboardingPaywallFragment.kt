package com.theathletic.onboarding.paywall.ui

import androidx.compose.runtime.Composable
import com.theathletic.fragment.AthleticComposeFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class OnboardingPaywallFragment :
    AthleticComposeFragment<OnboardingPaywallViewModel, OnboardingPaywallContract.PaywallViewState>() {

    companion object {
        fun newInstance() = OnboardingPaywallFragment()
    }

    override fun setupViewModel() = getViewModel<OnboardingPaywallViewModel> { parametersOf(navigator) }

    @Composable
    override fun Compose(state: OnboardingPaywallContract.PaywallViewState) {
        OnboardingPaywallScreen(
            titleRes = state.titleRes,
            ctaRes = state.ctaRes,
            finePrint = state.finePrint,
            showVATInfo = state.showVATInfo,
            isCTAEnabled = state.isCTAEnabled,
            interactor = viewModel
        )
    }
}