package com.theathletic.debugtools.billingconfig

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.billing.debug.DebugPurchaseFactory
import com.theathletic.debugtools.DebugPreferences
import com.theathletic.debugtools.billingconfig.models.BillingConfigToggleType
import com.theathletic.ui.DataState
import com.theathletic.ui.Transformer
import com.theathletic.ui.list.AthleticListViewModel
import com.theathletic.user.IUserManager
import com.theathletic.utility.BillingPreferences

class BillingConfigViewModel @AutoKoin constructor(
    transformer: BillingConfigTransformer,
    private val userManager: IUserManager,
    private val billingPreferences: BillingPreferences,
    private val debugPreferences: DebugPreferences,
    private val debugPurchaseFactory: DebugPurchaseFactory
) : AthleticListViewModel<
    BillingConfigState,
    BillingConfigContract.BillingConfigViewState
    >(),
    BillingConfigContract.Presenter,
    Transformer<
        BillingConfigState,
        BillingConfigContract.BillingConfigViewState> by transformer {

    override val initialState: BillingConfigState
        get() {
            val currencies = listOf("USD", "CAD", "GBP")
            return BillingConfigState(
                isSubscribed = userManager.isUserSubscribed(),
                isTrialEligible = userManager.isUserFreeTrialEligible(),
                isGiftsResponseSuccessful = debugPreferences.isGiftsResponseSuccessful,
                selectedCurrencyIndex = currencies.indexOf(debugPreferences.debugBillingCurrency),
                currencies = currencies
            )
        }

    override fun onToggleSelected(type: BillingConfigToggleType) {
        when (type) {
            BillingConfigToggleType.IS_SUBSCRIBED -> {
                val purchase = if (!state.isSubscribed) {
                    debugPurchaseFactory.createSuccessfulPurchase("debug_sku")
                } else {
                    null
                }
                val productId = purchase?.skus?.lastOrNull()
                val purchaseToken = purchase?.purchaseToken
                billingPreferences.setSubscriptionData(productId, purchaseToken)
                updateState { copy(isSubscribed = userManager.isUserSubscribed()) }
            }
            BillingConfigToggleType.IS_TRIAL_ELIGIBLE -> {
                billingPreferences.hasPurchaseHistory = state.isTrialEligible
                updateState { copy(isTrialEligible = userManager.isUserFreeTrialEligible()) }
            }
            BillingConfigToggleType.IS_GIFTS_RESPONSE_SUCCESS -> {
                debugPreferences.isGiftsResponseSuccessful = !state.isGiftsResponseSuccessful
                updateState { copy(isGiftsResponseSuccessful = debugPreferences.isGiftsResponseSuccessful) }
            }
        }
    }

    override fun onSpinnerClicked(newSelection: Int) {
        debugPreferences.debugBillingCurrency = state.currencies[newSelection]
        updateState { copy(selectedCurrencyIndex = newSelection) }
    }
}

data class BillingConfigState(
    val isSubscribed: Boolean,
    val isTrialEligible: Boolean,
    val isGiftsResponseSuccessful: Boolean,
    val selectedCurrencyIndex: Int,
    val currencies: List<String>
) : DataState