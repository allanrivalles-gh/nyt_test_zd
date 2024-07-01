package com.theathletic.onboarding.paywall.ui

import androidx.annotation.StringRes
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ViewState

interface OnboardingPaywallContract {

    interface Interactor : OnboardingPaywallUi.Interactor

    data class PaywallViewState(
        @StringRes val titleRes: Int,
        @StringRes val ctaRes: Int,
        val finePrint: ResourceString,
        val showVATInfo: Boolean,
        val isCTAEnabled: Boolean
    ) : ViewState
}