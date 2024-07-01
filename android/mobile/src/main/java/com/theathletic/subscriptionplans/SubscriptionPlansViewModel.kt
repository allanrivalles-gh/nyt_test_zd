package com.theathletic.subscriptionplans

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.theathletic.R
import com.theathletic.analytics.data.ClickSource
import com.theathletic.analytics.data.ContentType
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
import com.theathletic.event.SnackbarEventRes
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.Transformer
import com.theathletic.user.IUserManager
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.launch

class SubscriptionPlansViewModel @AutoKoin constructor(
    @Assisted private val screenNavigator: ScreenNavigator,
    @Assisted private val initialData: SubscriptionPlansInitialData,
    transformer: SubscriptionPlansTransformer,
    private val billingManager: BillingManager,
    private val userManager: IUserManager,
    private val surveyRouter: SurveyRouter,
    private val analytics: Analytics
) : AthleticViewModel<SubscriptionPlansState, SubscriptionPlansContract.SubscriptionPlansViewState>(),
    SubscriptionPlansContract.Presenter,
    Transformer<SubscriptionPlansState, SubscriptionPlansContract.SubscriptionPlansViewState> by transformer {

    companion object {
        private val DEFAULT_ANNUAL_SKU = BillingProducts.IAB_PRODUCT_ANNUAL.planId
        private val DEFAULT_MONTHLY_SKU = BillingProducts.IAB_PRODUCT_MONTHLY.planId
    }

    override val initialState = SubscriptionPlansState(
        isSetupComplete = false,
        isTrialEligible = userManager.isUserFreeTrialEligible(),
        isSpecialOffer = isSpecialOffer()
    )

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        billingManager.onCreate()
        billingManager.billingState.collectIn(viewModelScope) { handleBillingState(it) }
        trackInitialViewAnalytics()
        viewModelScope.launch { surveyRouter.fetchSurveyIfQualified() }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        billingManager.onDestroy()
    }

    fun isSpecialOffer() = initialData.specialOffer != null

    override fun onAnnualPlanClick() {
        updateState {
            copy(isAnnualPlanSelected = true)
        }
    }

    override fun onMonthlyPlanClick() {
        updateState {
            copy(isAnnualPlanSelected = false)
        }
    }

    override fun onContinueClick() {
        val nullablePlan = if (state.isAnnualPlanSelected) state.annualPlan else state.monthlyPlan
        nullablePlan?.let { plan ->
            screenNavigator.launchBillingFlow(billingManager, plan)
            analytics.track(Event.Payments.NativePurchaseDialogDisplayed)

            analytics.track(
                Event.Payments.Click(
                    object_id = plan.sku,
                    element = "continue",
                    article_id = initialData.articleId.toString(),
                    room_id = initialData.liveRoomId.orEmpty(),
                    action = initialData.liveRoomAction.orEmpty(),
                )
            )
        }
    }

    override fun onCloseClick() {
        screenNavigator.finishActivity()

        initialData.specialOffer?.let {
            // Special Offers are only through push notifications so cannot have an article
            // or live room source
            analytics.track(
                Event.Payments.Click(
                    object_id = it.planId,
                    element = "close",
                    article_id = "",
                    room_id = "",
                    action = "",
                )
            )
        } ?: run {
            // Intentionally tracking this twice to capture both plans the user is shown
            analytics.track(
                Event.Payments.Click(
                    object_id = DEFAULT_ANNUAL_SKU,
                    element = "close",
                    article_id = initialData.articleId.toString(),
                    room_id = initialData.liveRoomId.orEmpty(),
                    action = initialData.liveRoomAction.orEmpty(),
                )
            )
            analytics.track(
                Event.Payments.Click(
                    object_id = DEFAULT_MONTHLY_SKU,
                    element = "close",
                    article_id = initialData.articleId.toString(),
                    room_id = initialData.liveRoomId.orEmpty(),
                    action = initialData.liveRoomAction.orEmpty(),
                )
            )
        }
    }

    override fun onTermsOfServiceClick() {
        screenNavigator.showTermsOfService()
    }

    override fun onPrivacyPolicyClick() {
        screenNavigator.showPrivacyPolicy()
    }

    private suspend fun handleBillingState(billingState: BillingState?) {
        when (billingState) {
            is SetupComplete -> onBillingSetupComplete()
            is PurchaseSuccess -> handleSuccessfulPurchase(billingState)
            is PurchaseFailure -> sendEvent(SnackbarEventRes(R.string.global_billing_error_internal))
            is GeneralBillingFailure -> sendEvent(SnackbarEventRes(R.string.global_error))
            else -> { /* Do nothing */
            }
        }
    }

    private fun onBillingSetupComplete() {
        if (isSpecialOffer()) {
            getSpecialOffer()
        } else {
            getDefaultOffer()
        }
    }

    private fun getSpecialOffer() = viewModelScope.launch {
        val product = initialData.specialOffer?.let {
            billingManager.getSubSkus(listOf(it.planId))
        }
        updateState {
            copy(
                isSetupComplete = true,
                annualPlan = product?.firstOrNull()
            )
        }
    }

    private fun getDefaultOffer() = viewModelScope.launch {
        val products = billingManager.getSubSkus(
            listOf(
                DEFAULT_ANNUAL_SKU,
                DEFAULT_MONTHLY_SKU
            )
        )

        updateState {
            copy(
                isSetupComplete = true,
                annualPlan = products.firstOrNull { it.sku == DEFAULT_ANNUAL_SKU },
                monthlyPlan = products.firstOrNull { it.sku == DEFAULT_MONTHLY_SKU }
            )
        }
    }

    private suspend fun handleSuccessfulPurchase(purchase: PurchaseSuccess) {
        billingManager.registerSubPurchaseIfNeeded(purchase.purchase, getSourceName())
        analytics.track(billingManager.getPurchaseAnalytics(purchase = purchase.purchase, isSubSku = true))
        surveyRouter.hasMadeSuccessfulPurchase()

        sendEvent(SnackbarEventRes(R.string.plans_thanks_for_subscribe))
        launchNextScreen()
    }

    private fun getSourceName(): String {
        return when (initialData.source) {
            ClickSource.FEED -> "feed"
            ClickSource.SETTINGS -> "profile"
            else -> "paywall"
        }
    }

    private fun getAnalyticsElement() = when {
        initialData.articleId != -1L -> "article"
        initialData.liveRoomAction != null -> initialData.liveRoomAction
        else -> ""
    }

    fun launchNextScreen() {
        when {
            userManager.isAnonymous -> {
                screenNavigator.startAuthenticationActivityOnRegistrationScreenPostPurchase(
                    AuthenticationNavigationSource.PAYWALL
                )
            }
            surveyRouter.shouldPresentSurvey() -> screenNavigator.startSurveyActivity(
                analyticsSource = "plans_view",
                analyticsObjectType = "article",
                analyticsObjectId = initialData.articleId
            )
            else -> screenNavigator.finishActivity()
        }
    }

    private fun trackInitialViewAnalytics() {
        analytics.track(
            Event.Global.View(
                ContentType.PLANS.toString(),
                ""
            )
        )

        initialData.specialOffer?.let {
            // Special Offers are only through push notifications so cannot have an article
            // or live room source
            analytics.track(
                Event.Payments.PlanScreenView(
                    object_id = it.planId,
                    element = "",
                    article_id = "",
                    room_id = "",
                )
            )
            analytics.track(Event.Payments.KochavaDiscountedPlanScreenView)
        } ?: run {
            // Intentionally tracking this twice to capture both plans the user is shown
            analytics.track(
                Event.Payments.PlanScreenView(
                    object_id = DEFAULT_ANNUAL_SKU,
                    element = getAnalyticsElement(),
                    article_id = initialData.articleId.toString(),
                    room_id = initialData.liveRoomId.orEmpty(),
                )
            )
            analytics.track(
                Event.Payments.PlanScreenView(
                    object_id = DEFAULT_MONTHLY_SKU,
                    element = getAnalyticsElement(),
                    article_id = initialData.articleId.toString(),
                    room_id = initialData.liveRoomId.orEmpty(),
                )
            )
            analytics.track(Event.Payments.PlanScreenViewKochava)
        }
    }
}

data class SubscriptionPlansState(
    val isSetupComplete: Boolean,
    val isTrialEligible: Boolean,
    val isSpecialOffer: Boolean,
    val isAnnualPlanSelected: Boolean = true,
    val annualPlan: BillingSku? = null,
    val monthlyPlan: BillingSku? = null
) : DataState