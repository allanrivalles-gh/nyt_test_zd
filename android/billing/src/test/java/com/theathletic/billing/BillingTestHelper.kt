package com.theathletic.billing

import com.android.billingclient.api.BillingClient
import com.theathletic.billing.data.local.BillingPurchase

val emptyQuery = BillingManager.QueryPurchasesResult(
    purchases = emptyList(),
    isError = false,
    responseCode = BillingClient.BillingResponseCode.OK
)

val errorQuery = BillingManager.QueryPurchasesResult(
    purchases = null,
    isError = true,
    responseCode = BillingClient.BillingResponseCode.ERROR
)

val okQuery: (List<BillingPurchase>) -> BillingManager.QueryPurchasesResult = {
    BillingManager.QueryPurchasesResult(
        purchases = it,
        isError = false,
        responseCode = BillingClient.BillingResponseCode.OK
    )
}