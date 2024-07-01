package com.theathletic.billing.debug

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope

class DebugPurchaseGenerator @AutoKoin(Scope.SINGLE) constructor(
    private val purchaseFactory: DebugPurchaseFactory
) {

    private val purchaseListeners = mutableListOf<PurchasesUpdatedListener>()
    private val okResult = BillingResult
        .newBuilder()
        .setResponseCode(BillingClient.BillingResponseCode.OK)
        .build()

    fun addPurchaseListener(newListener: PurchasesUpdatedListener) {
        purchaseListeners.add(newListener)
    }

    fun removePurchaseListener(listener: PurchasesUpdatedListener) {
        purchaseListeners.remove(listener)
    }

    fun createSuccessfulPurchase(sku: String) {
        purchaseListeners.forEach { listener ->
            listener.onPurchasesUpdated(
                okResult,
                listOf(purchaseFactory.createSuccessfulPurchase(sku))
            )
        }
    }

    fun createFailedPurchase() {
        val errorResult = BillingResult
            .newBuilder()
            .setResponseCode(BillingClient.BillingResponseCode.ERROR)
            .build()
        purchaseListeners.forEach { listener ->
            listener.onPurchasesUpdated(
                errorResult,
                listOf()
            )
        }
    }

    fun createPendingPurchase(sku: String) {
        purchaseListeners.forEach { listener ->
            listener.onPurchasesUpdated(
                okResult,
                listOf(purchaseFactory.createPendingPurchase(sku))
            )
        }
    }

    fun createUserCancelledEvent() {
        val result = BillingResult
            .newBuilder()
            .setResponseCode(BillingClient.BillingResponseCode.USER_CANCELED)
            .build()
        purchaseListeners.forEach { listener ->
            listener.onPurchasesUpdated(
                result,
                listOf()
            )
        }
    }
}