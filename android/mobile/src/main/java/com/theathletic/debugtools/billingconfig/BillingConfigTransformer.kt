package com.theathletic.debugtools.billingconfig

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.debugtools.billingconfig.models.BillingConfigSpinner
import com.theathletic.debugtools.billingconfig.models.BillingConfigToggle
import com.theathletic.debugtools.billingconfig.models.BillingConfigToggleType
import com.theathletic.ui.Transformer

class BillingConfigTransformer @AutoKoin constructor() :
    Transformer<BillingConfigState, BillingConfigContract.BillingConfigViewState> {

    override fun transform(data: BillingConfigState): BillingConfigContract.BillingConfigViewState {
        return BillingConfigContract.BillingConfigViewState(
            false,
            listOf(
                BillingConfigToggle(
                    id = 1,
                    textRes = R.string.debug_billing_config_is_subscribed,
                    type = BillingConfigToggleType.IS_SUBSCRIBED,
                    isActive = data.isSubscribed
                ),
                BillingConfigToggle(
                    id = 2,
                    textRes = R.string.debug_billing_config_is_trial_eligible,
                    type = BillingConfigToggleType.IS_TRIAL_ELIGIBLE,
                    isActive = data.isTrialEligible
                ),
                BillingConfigToggle(
                    id = 3,
                    textRes = R.string.debug_billing_config_is_gifts_response_success,
                    type = BillingConfigToggleType.IS_GIFTS_RESPONSE_SUCCESS,
                    isActive = data.isGiftsResponseSuccessful
                ),
                BillingConfigSpinner(
                    id = 4,
                    selectedIndex = data.selectedCurrencyIndex,
                    options = data.currencies
                )
            )
        )
    }
}