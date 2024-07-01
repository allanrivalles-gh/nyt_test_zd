package com.theathletic.onboarding.paywall.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.R
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.attributionsurvey.SurveyRouter
import com.theathletic.auth.analytics.AuthenticationNavigationSource
import com.theathletic.billing.BillingManager
import com.theathletic.billing.BillingProducts
import com.theathletic.billing.BillingSku
import com.theathletic.billing.BillingState
import com.theathletic.billing.GeneralBillingFailure
import com.theathletic.billing.PurchaseFailure
import com.theathletic.billing.PurchaseSuccess
import com.theathletic.billing.SetupComplete
import com.theathletic.billing.data.local.BillingPurchase
import com.theathletic.event.SnackbarEventRes
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.onboarding.data.OnboardingRepository
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.Transformer
import com.theathletic.user.IUserManager
import com.theathletic.utility.BillingPreferences
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.launch

class OnboardingPaywallViewModel @AutoKoin constructor(
    @Assisted private val navigator: ScreenNavigator,
    private val billingManager: BillingManager,
    private val userManager: IUserManager,
    private val onboardingRepository: OnboardingRepository,
    private val surveyRouter: SurveyRouter,
    private val billingPreferences: BillingPreferences,
    private val analytics: Analytics,
    transformer: OnboardingPaywallTransformer
) : AthleticViewModel<PaywallDataState, OnboardingPaywallContract.PaywallViewState>(),
    Transformer<PaywallDataState, OnboardingPaywallContract.PaywallViewState> by transformer,
    OnboardingPaywallContract.Interactor,
    DefaultLifecycleObserver {

    companion object {
        private val SUBSCRIPTION_PRODUCT = BillingProducts.IAB_PRODUCT_ANNUAL
        private const val SOURCE = "onboarding-interstitial"
    }

    override val initialState = PaywallDataState()

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        initialize()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        viewModelScope.launch {
            surveyRouter.fetchSurveyIfQualified()

            // If we are returning to this activity after having successfully created an account, move forward automatically
            if (userManager.isUserSubscribed() && userManager.isUserLoggedIn() && onboardingRepository.isOnboarding.not()) {
                navigateToNextScreen(true)
            }
        }
    }

    private fun initialize() {
        trackView()
        setupBillingManager()
    }

    override fun onCleared() {
        super.onCleared()
        billingManager.onDestroy()
    }

    private fun trackView() {
        if (billingPreferences.hasPurchaseHistory) {
            analytics.track(
                Event.Onboarding.NoTrialDisplayed(object_id = SUBSCRIPTION_PRODUCT.planId)
            )
        } else {
            analytics.track(
                Event.Onboarding.FreeTrialDisplayed(object_id = SUBSCRIPTION_PRODUCT.planId)
            )
        }
    }

    private fun setupBillingManager() {
        if (userManager.isUserSubscribed()) {
            sendEvent(SnackbarEventRes(R.string.global_already_subscribed))
            navigateToNextScreen(hasSuccessfulPurchase = false)
            return
        }
        billingManager.onCreate()
        billingManager.billingState.collectIn(viewModelScope) { handleBillingStateUpdate(it) }
    }

    override fun onSkipClick() {
        analytics.track(Event.Onboarding.TrialButtonPressSkip())
        navigateToNextScreen(hasSuccessfulPurchase = false)
    }

    override fun onPurchaseClick() {
        analytics.track(Event.Onboarding.StartFreeTrialPress(object_id = SUBSCRIPTION_PRODUCT.planId))
        state.sku?.let {
            navigator.launchBillingFlow(billingManager, it)
            analytics.track(Event.Payments.NativePurchaseDialogDisplayed)
        }
    }

    override fun onTermsClick() {
        navigator.showTermsOfService()
    }

    override fun onPrivacyClick() {
        navigator.showPrivacyPolicy()
    }

    private suspend fun handleBillingStateUpdate(billingState: BillingState?) {
        when (billingState) {
            is SetupComplete -> onBillingSetupComplete()
            is PurchaseSuccess -> onSuccessfulPurchase(billingState.purchase)
            is PurchaseFailure -> onBillingError()
            is GeneralBillingFailure -> onBillingError()
            else -> { /* No-op */ }
        }
    }

    private suspend fun onBillingSetupComplete() {
        val results = billingManager.getSubSkus(listOf(SUBSCRIPTION_PRODUCT.planId))
        results.firstOrNull()?.let { sku ->
            updateState {
                copy(
                    isTrialEligible = !billingPreferences.hasPurchaseHistory,
                    isPurchasingEnabled = true,
                    sku = sku
                )
            }
        } ?: run {
            sendEvent(SnackbarEventRes(R.string.global_billing_error_init_failed))
            updateState {
                copy(
                    isPurchasingEnabled = false,
                    isPlayStoreAvailable = false
                )
            }
        }
    }

    private suspend fun onSuccessfulPurchase(purchase: BillingPurchase) {
        sendEvent(SnackbarEventRes(R.string.plans_thanks_for_subscribe))
        billingManager.registerSubPurchaseIfNeeded(purchase, SOURCE)
        analytics.track(billingManager.getPurchaseAnalytics(purchase = purchase, isSubSku = true))
        surveyRouter.hasMadeSuccessfulPurchase()
        navigateToNextScreen(hasSuccessfulPurchase = true)
    }

    private fun onBillingError() {
        sendEvent(SnackbarEventRes(R.string.global_billing_error_internal))
        updateState { copy(isPurchasingEnabled = false) }
    }

    private fun navigateToNextScreen(hasSuccessfulPurchase: Boolean) {
        // In theory, these checks for isOnboarding and isAnonymous are redundant; I chose to keep the
        // isAnonymous check in place in case there's a scenario I haven't imagined that would bypass onboarding
        if (onboardingRepository.isOnboarding || userManager.isAnonymous) {
            if (hasSuccessfulPurchase) {
                navigator.startAuthenticationActivityOnRegistrationScreenPostPurchase(
                    AuthenticationNavigationSource.ONBOARDING_PURCHASE
                )
            } else {
                navigator.startAuthenticationActivityOnRegistrationScreen(
                    AuthenticationNavigationSource.ONBOARDING_NO_PURCHASE
                )
            }
        } else {
            navigator.startMainActivity()
            navigator.finishActivity()
        }
    }
}

data class PaywallDataState(
    val isTrialEligible: Boolean = false,
    val isPurchasingEnabled: Boolean = false,
    val isPlayStoreAvailable: Boolean = true,
    val sku: BillingSku? = null
) : DataState