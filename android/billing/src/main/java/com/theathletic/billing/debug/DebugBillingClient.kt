package com.theathletic.billing.debug

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.InAppMessageParams
import com.android.billingclient.api.InAppMessageResponseListener
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.PurchaseHistoryResponseListener
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchaseHistoryParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.SkuDetailsResponseListener
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.billing.R
import com.theathletic.ui.widgets.dialog.menuSheet

class DebugBillingClient @AutoKoin constructor(
    private val skuDetailsFactory: SkuDetailsFactory,
    private val purchaseGenerator: DebugPurchaseGenerator
) : BillingClient() {

    private val okResult = BillingResult
        .newBuilder()
        .setResponseCode(BillingResponseCode.OK)
        .build()

    private var stateListener: BillingClientStateListener? = null
    private var purchaseListener: PurchasesUpdatedListener? = null

    override fun getConnectionState(): Int {
        return ConnectionState.CONNECTED
    }

    override fun isFeatureSupported(feature: String): BillingResult {
        return okResult
    }

    override fun isReady(): Boolean {
        return true
    }

    override fun startConnection(listener: BillingClientStateListener) {
        stateListener = listener
        stateListener?.onBillingSetupFinished(okResult)
    }

    override fun endConnection() {
        stateListener = null
        purchaseListener?.let { purchaseGenerator.removePurchaseListener(it) }
        purchaseListener = null
    }

    override fun launchBillingFlow(
        activity: Activity,
        params: BillingFlowParams
    ): BillingResult {
        val skuDetails = params.zzg().lastOrNull() as SkuDetails?
        menuSheet {
            skuDetails?.let {
                addEntry(
                    R.drawable.ic_arrow_right,
                    R.string.debug_billing_start_successful_purchase
                ) {
                    purchaseGenerator.createSuccessfulPurchase(it.sku)
                }
            }
            addEntry(R.drawable.ic_arrow_right, R.string.debug_billing_start_failed_purchase) {
                purchaseGenerator.createFailedPurchase()
            }
            if (skuDetails?.type == SkuType.INAPP) {
                addEntry(R.drawable.ic_arrow_right, R.string.debug_billing_start_pending_purchase) {
                    purchaseGenerator.createPendingPurchase(skuDetails.sku)
                }
            }
            addOnCancelListener {
                purchaseGenerator.createUserCancelledEvent()
            }
        }.show((activity as FragmentActivity).supportFragmentManager, null)
        return okResult
    }

    override fun showInAppMessages(
        p0: Activity,
        p1: InAppMessageParams,
        p2: InAppMessageResponseListener
    ): BillingResult {
        return okResult
    }

    override fun queryProductDetailsAsync(
        params: QueryProductDetailsParams,
        listener: ProductDetailsResponseListener
    ) {
        // Do nothing - currently unused
    }

    override fun querySkuDetailsAsync(
        params: SkuDetailsParams,
        listener: SkuDetailsResponseListener
    ) {
        val skus = when (params.skuType) {
            SkuType.SUBS -> params.skusList.map(skuDetailsFactory::createSubscriptionSku)
            SkuType.INAPP -> params.skusList.map(skuDetailsFactory::createInAppSku)
            else -> emptyList()
        }

        listener.onSkuDetailsResponse(
            okResult,
            skus
        )
    }

    override fun consumeAsync(
        consumeParams: ConsumeParams,
        listener: ConsumeResponseListener
    ) {
        // TODO: return different results?
        listener.onConsumeResponse(okResult, "")
    }

    override fun queryPurchaseHistoryAsync(
        skuType: String,
        listener: PurchaseHistoryResponseListener
    ) {
        // Do nothing - currently unused
    }

    override fun queryPurchaseHistoryAsync(
        params: QueryPurchaseHistoryParams,
        listener: PurchaseHistoryResponseListener
    ) {
        listener.onPurchaseHistoryResponse(
            okResult,
            emptyList()
        )
    }

    @Deprecated("Deprecated in Java")
    override fun queryPurchasesAsync(skuType: String, listener: PurchasesResponseListener) {
        listener.onQueryPurchasesResponse(okResult, emptyList())
    }

    override fun queryPurchasesAsync(
        params: QueryPurchasesParams,
        listener: PurchasesResponseListener
    ) {
        listener.onQueryPurchasesResponse(okResult, emptyList())
    }

    override fun acknowledgePurchase(
        params: AcknowledgePurchaseParams,
        listener: AcknowledgePurchaseResponseListener
    ) {
        // Do nothing - currently unused
    }

    fun setPurchaseUpdatedListener(listener: PurchasesUpdatedListener) {
        purchaseGenerator.addPurchaseListener(listener)
        purchaseListener = listener
    }
}