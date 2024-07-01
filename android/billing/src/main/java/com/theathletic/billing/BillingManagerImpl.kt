package com.theathletic.billing

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingFlowParams.ProrationMode.IMMEDIATE_WITHOUT_PRORATION
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetailsParams
import com.theathletic.analytics.IAnalytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Exposes
import com.theathletic.billing.data.local.BillingPurchase
import com.theathletic.billing.data.local.BillingPurchaseState
import com.theathletic.featureswitch.Features
import com.theathletic.user.IUserManager
import com.theathletic.utility.ActivityProvider
import com.theathletic.utility.BackoffState
import com.theathletic.utility.BillingPreferences
import com.theathletic.utility.IPreferences
import com.theathletic.utility.logging.ICrashLogHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val GIFT_3M = "gift_3m"
private const val GIFT_2Y = "gift_2y"
private const val MONTH = "month"
private const val YEAR = "year"

@Exposes(BillingManager::class)
@Suppress("LongParameterList")
class BillingManagerImpl @AutoKoin constructor(
    private val analytics: IAnalytics,
    private val preferences: IPreferences,
    private val billingPreferences: BillingPreferences,
    private val userManager: IUserManager,
    private val registerGooglePurchaseScheduler: RegisterGooglePurchaseScheduler,
    private val billingRepository: BillingRepository,
    private val crashHandler: ICrashLogHandler,
    billingClientProvider: BillingClientProvider,
    private val backoffState: BackoffState,
    private val activityProvider: ActivityProvider,
    private val features: Features
) : BillingManager, PurchasesUpdatedListener, BillingClientStateListener {

    private val billingClient = billingClientProvider.getBillingClient(this)

    private val _billingState = MutableStateFlow<BillingState?>(null)
    override val billingState = _billingState.asStateFlow()

    override fun onCreate() {
        billingClient.startConnection(this)
    }

    override fun onDestroy() {
        billingClient.endConnection()
    }

    override fun startPurchaseFlow(sku: BillingSku) {
        val activity = activityProvider.activeActivity ?: return

        val params = BillingFlowParams.newBuilder()
            .setSkuDetails(sku.skuDetails)
            .build()

        billingClient.launchBillingFlow(activity, params)
        analytics.track(Event.Billing.BillingStartPurchase)
    }

    override suspend fun startSubscriptionChangeFlow(newSku: BillingSku) {
        val activity = activityProvider.activeActivity ?: return
        val lastPurchase = getSubscriptionPurchases().purchases?.lastOrNull() ?: return

        val updateParams = BillingFlowParams.SubscriptionUpdateParams.newBuilder()
            .setOldSkuPurchaseToken(lastPurchase.purchaseToken)
            .setReplaceSkusProrationMode(IMMEDIATE_WITHOUT_PRORATION)
            .build()

        val params = BillingFlowParams.newBuilder()
            .setSubscriptionUpdateParams(updateParams)
            .setSkuDetails(newSku.skuDetails)
            .build()

        billingClient.launchBillingFlow(activity, params)
        analytics.track(Event.Billing.BillingStartChangePlan)
    }

    override suspend fun getSubscriptionPurchases() = suspendCoroutine { continuation ->
        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS) { result, subscriptions ->
            when (result.responseCode) {
                BillingResponseCode.OK -> onQueryPurchasesSuccess(subscriptions, continuation)
                else -> onQueryPurchasesFailure(result.responseCode, continuation)
            }
        }
    }

    override suspend fun getInAppPurchases() = suspendCoroutine { continuation ->
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { result, purchases ->
            when (result.responseCode) {
                BillingResponseCode.OK -> onQueryPurchasesSuccess(purchases, continuation)
                else -> onQueryPurchasesFailure(result.responseCode, continuation)
            }
        }
    }

    private fun onQueryPurchasesSuccess(
        subscriptions: List<Purchase>,
        continuation: Continuation<BillingManager.QueryPurchasesResult>
    ) {
        val result = BillingManager.QueryPurchasesResult(
            purchases = subscriptions.map { BillingPurchase(it) },
            isError = false,
            responseCode = BillingResponseCode.OK
        )
        continuation.resume(result)
    }

    private fun onQueryPurchasesFailure(
        responseCode: Int,
        continuation: Continuation<BillingManager.QueryPurchasesResult>
    ) {
        val result = BillingManager.QueryPurchasesResult(
            purchases = null,
            isError = true,
            responseCode = responseCode
        )
        _billingState.value = GeneralBillingFailure
        crashHandler.logException(QueryPurchasesFailedException())
        continuation.resume(result)
    }

    override fun consumePurchase(purchase: BillingPurchase) {
        val params = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.consumeAsync(params) { billingResult, _ ->
            if (billingResult.responseCode == BillingResponseCode.OK) {
                analytics.track(Event.Billing.BillingConsumePurchase)
                _billingState.value = PurchaseConsumed(purchase)
            } else {
                analytics.track(Event.Billing.BillingConsumeFailure)
                _billingState.value = ConsumeFailure
            }
        }
    }

    override suspend fun hasSubscriptionPurchaseHistory(): Boolean = suspendCoroutine {
        billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS) { _, records ->
            it.resume(!records.isNullOrEmpty())
        }
    }

    override suspend fun getInAppSkus(skus: List<String>): List<BillingSku> {
        return getSkus(skus, BillingClient.SkuType.INAPP)
    }

    override suspend fun getSubSkus(skus: List<String>): List<BillingSku> {
        return getSkus(skus, BillingClient.SkuType.SUBS)
    }

    override suspend fun getPurchaseAnalytics(purchase: BillingPurchase, isSubSku: Boolean):
        Event.Payments.ProductPurchase {
        val billingSku = purchase.getBillingSku(isSubSku)
        val isTrial = billingSku?.skuDetails.toString().contains("freeTrialPeriod") &&
            userManager.isUserFreeTrialEligible()
        return Event.Payments.ProductPurchase(
            productId = purchase.orderId ?: "",
            priceValue = billingSku?.purchasePrice.toString(),
            purchaseSignature = purchase.signature,
            purchaseOriginalJson = purchase.originalJson,
            currency = billingSku?.skuDetails?.priceCurrencyCode.orEmpty(),
            priceLong = billingSku?.skuDetails?.priceAmountMicros.toString(),
            isTrialPurchase = isTrial.toString()
        )
    }

    private suspend fun getSkus(skus: List<String>, type: String): List<BillingSku> =
        suspendCoroutine {
            val params = SkuDetailsParams.newBuilder()
                .setSkusList(skus)
                .setType(type)
                .build()

            billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    it.resume(skuDetailsList?.map { BillingSku(it) } ?: emptyList())
                } else {
                    _billingState.value = GeneralBillingFailure
                    it.resume(emptyList())
                }
            }
        }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        when (billingResult.responseCode) {
            BillingResponseCode.OK -> {
                if (purchases != null) {
                    handleNewPurchases(purchases.map { BillingPurchase(it) })
                } else {
                    _billingState.value = PurchaseFailure
                    analytics.track(Event.Billing.BillingFailedPurchase)
                }
            }

            BillingResponseCode.USER_CANCELED -> {
                _billingState.value = PurchaseCancelled
                analytics.track(Event.Billing.BillingCancelledPurchase)
            }

            else -> {
                _billingState.value = PurchaseFailure
                analytics.track(Event.Billing.BillingFailedPurchase)
            }
        }
    }

    private fun handleNewPurchases(purchases: List<BillingPurchase>) = purchases.forEach { purchase ->
        when (purchase.purchaseState) {
            BillingPurchaseState.Purchased -> {
                _billingState.value = PurchaseSuccess(purchase)
                analytics.track(Event.Billing.BillingSuccessfulPurchase)
            }

            BillingPurchaseState.Pending -> {
                _billingState.value = PurchasePending
                analytics.track(Event.Billing.BillingPendingPurchase)
            }

            BillingPurchaseState.Unspecified,
            BillingPurchaseState.Unset -> {
                // nothing to do
            }
        }
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        when (billingResult.responseCode) {
            BillingResponseCode.OK -> {
                backoffState.resetBackoff()
                _billingState.value = SetupComplete
            }
            else -> {
                Timber.v("Problem setting up in-app billing: $billingResult.debugMessage")
                _billingState.value = SetupFailed
            }
        }
    }

    override fun onBillingServiceDisconnected() {
        backoffState.runBlockingBackoff(billingClient.isReady) {
            billingClient.startConnection(this@BillingManagerImpl)
        }
    }

    override suspend fun registerSubPurchaseIfNeeded(purchase: BillingPurchase, source: String?) {
        if (purchase.purchaseState == BillingPurchaseState.Purchased &&
            (!purchase.isAcknowledged || extraSubLoggingEnabled())
        ) {
            savePurchaseDataEntity(purchase, source, isSubSku = true)

            val productId = purchase.skus.lastOrNull()
            val purchaseToken = purchase.purchaseToken
            billingPreferences.setSubscriptionData(productId, purchaseToken)
            registerGooglePurchaseScheduler.scheduleIfNeeded(purchase.purchaseToken)
            analytics.track(Event.Billing.BillingRegisterSubscription)
        }
    }

    override suspend fun registerGiftPurchase(purchase: BillingPurchase) {
        savePurchaseDataEntity(purchase, source = null, isSubSku = false)
        registerGooglePurchaseScheduler.scheduleIfNeeded(purchase.purchaseToken)
    }

    override suspend fun registerGiftPurchasesStartup() {
        billingRepository.getPurchaseDataByType(isSubPurchase = false).forEach { purchase ->
            registerGooglePurchaseScheduler.scheduleIfNeeded(purchase.googleToken)
        }
    }

    private suspend fun savePurchaseDataEntity(
        purchase: BillingPurchase,
        source: String?,
        isSubSku: Boolean
    ) {
        val firstSku = purchase.getFirstSku()
        val billingSku = purchase.getBillingSku(isSubSku)

        if (billingSku == null) {
            analytics.track(Event.Billing.BillingMissingSku(firstSku))
        }

        billingRepository.savePurchaseData(
            PurchaseDataEntity(
                googleToken = purchase.purchaseToken,
                price = billingSku?.purchasePrice,
                priceCurrency = billingSku?.skuDetails?.priceCurrencyCode,
                productSku = firstSku,
                planTerm = getPlanTerm(firstSku, isSubSku, billingSku),
                planNum = getPlanNum(firstSku, isSubSku),
                lastArticleId = preferences.lastGoogleSubArticleId,
                lastPodcastId = preferences.lastGoogleSubPodcastId,
                source = source,
                isSubPurchase = isSubSku
            )
        )
    }

    private fun getPlanTerm(
        sku: String,
        isSubSku: Boolean,
        billingSku: BillingSku?
    ) = when {
        !isSubSku -> if (sku == GIFT_3M) MONTH else YEAR
        billingSku?.isMonthlySubscription == true -> MONTH
        else -> YEAR
    }

    private fun getPlanNum(
        sku: String,
        isSubSku: Boolean
    ) = when {
        isSubSku -> "1"
        else -> when (sku) {
            GIFT_3M -> "3"
            GIFT_2Y -> "2"
            else -> "1"
        }
    }

    private fun extraSubLoggingEnabled() =
        !userManager.isUserSubscribedOnBackend() && features.isExtraSubLoggingEnabled

    private fun BillingPurchase.getFirstSku() = skus.firstOrNull() ?: ""

    private suspend fun BillingPurchase.getBillingSku(isSubSku: Boolean) = if (isSubSku) {
        getSubSkus(skus).firstOrNull()
    } else {
        getInAppSkus(skus).firstOrNull()
    }
}

private class QueryPurchasesFailedException :
    Exception(
        "The billing operation has failed to fetch " +
            "the user purchases"
    )