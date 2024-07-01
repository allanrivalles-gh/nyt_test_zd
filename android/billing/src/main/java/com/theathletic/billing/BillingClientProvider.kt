package com.theathletic.billing

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener
import com.theathletic.billing.debug.DebugBillingClient

class BillingClientProvider(
    private val debugClient: DebugBillingClient,
    private val productionClientBuilder: BillingClient.Builder,
    private val areDebugToolsEnabled: Boolean,
    private val areDebugBillingToolsEnabled: () -> Boolean
) {

    fun getBillingClient(listener: PurchasesUpdatedListener): BillingClient {
        return if (areDebugToolsEnabled && areDebugBillingToolsEnabled()) {
            debugClient.apply { setPurchaseUpdatedListener(listener) }
        } else {
            productionClientBuilder
                .setListener(listener)
                .enablePendingPurchases()
                .build()
        }
    }
}