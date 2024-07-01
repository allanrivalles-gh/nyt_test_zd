package com.theathletic.billing.data.local

import com.android.billingclient.api.Purchase
import com.android.billingclient.api.Purchase.PurchaseState

data class BillingPurchase(
    val skus: List<String>,
    val purchaseToken: String,
    val orderId: String?,
    val signature: String,
    val originalJson: String,
    val purchaseState: BillingPurchaseState,
    val isAcknowledged: Boolean
) {
    constructor(purchase: Purchase) : this(
        skus = purchase.skus,
        purchaseToken = purchase.purchaseToken,
        orderId = purchase.orderId,
        signature = purchase.signature,
        originalJson = purchase.originalJson,
        purchaseState = getPurchaseState(purchase),
        isAcknowledged = purchase.isAcknowledged
    )

    companion object {
        private fun getPurchaseState(purchase: Purchase): BillingPurchaseState {
            return when (purchase.purchaseState) {
                PurchaseState.PENDING -> BillingPurchaseState.Pending
                PurchaseState.PURCHASED -> BillingPurchaseState.Purchased
                PurchaseState.UNSPECIFIED_STATE -> BillingPurchaseState.Unspecified
                else -> BillingPurchaseState.Unset
            }
        }
    }
}

enum class BillingPurchaseState {
    Pending,
    Purchased,
    Unspecified,
    Unset
}