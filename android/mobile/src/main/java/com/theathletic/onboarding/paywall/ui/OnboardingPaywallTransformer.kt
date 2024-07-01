package com.theathletic.onboarding.paywall.ui

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.Transformer

class OnboardingPaywallTransformer @AutoKoin constructor() :
    Transformer<PaywallDataState, OnboardingPaywallContract.PaywallViewState> {

    override fun transform(data: PaywallDataState): OnboardingPaywallContract.PaywallViewState {
        return OnboardingPaywallContract.PaywallViewState(
            titleRes = if (data.isTrialEligible) {
                R.string.onboarding_subscribe_title_trial
            } else {
                R.string.onboarding_subscribe_title
            },
            ctaRes = if (data.isTrialEligible) {
                R.string.onboarding_subscribe_button_text_trial
            } else {
                R.string.onboarding_subscribe_button_text
            },
            finePrint = when {
                !data.isPlayStoreAvailable -> StringWithParams(R.string.global_billing_error_init_failed)
                data.isTrialEligible -> StringWithParams(
                    R.string.onboarding_subscribe_price_text_trial,
                    data.sku?.price.orEmpty()
                )
                else -> StringWithParams(
                    R.string.onboarding_subscribe_price_text,
                    data.sku?.price.orEmpty()
                )
            },
            showVATInfo = data.sku?.isShowVAT ?: false,
            isCTAEnabled = data.isPurchasingEnabled
        )
    }
}