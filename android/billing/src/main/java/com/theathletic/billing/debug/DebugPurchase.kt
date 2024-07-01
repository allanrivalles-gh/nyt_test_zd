package com.theathletic.billing.debug

import com.android.billingclient.api.Purchase
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.TimeProvider

@Suppress("LongParameterList")
@JsonClass(generateAdapter = true)
class DebugPurchase(
    val orderId: String = "testing_id",
    val packageName: String = "com.theathletic",
    val productId: String,
    val purchaseTime: Long,
    val purchaseState: Int,
    val purchaseToken: String = "testing_token",
    val autoRenewing: Boolean = true,
    val acknowledged: Boolean = false
)

class DebugPurchaseFactory @AutoKoin constructor(
    moshi: Moshi,
    private val timeProvider: TimeProvider
) {

    private val adapter = moshi.adapter(DebugPurchase::class.java)

    fun createSuccessfulPurchase(
        sku: String
    ): Purchase {
        val purchase = DebugPurchase(
            productId = sku,
            purchaseTime = timeProvider.currentTimeMs,
            purchaseState = Purchase.PurchaseState.PURCHASED
        )
        return Purchase(adapter.toJson(purchase), "signature")
    }

    fun createPendingPurchase(
        sku: String
    ): Purchase {
        val purchase = DebugPurchase(
            productId = sku,
            purchaseTime = timeProvider.currentTimeMs,
            purchaseState = 4
        )
        return Purchase(adapter.toJson(purchase), "signature")
    }
}