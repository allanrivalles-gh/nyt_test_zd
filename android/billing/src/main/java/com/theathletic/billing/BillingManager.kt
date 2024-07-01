package com.theathletic.billing

import com.theathletic.analytics.newarch.Event
import com.theathletic.billing.data.local.BillingPurchase
import kotlinx.coroutines.flow.StateFlow

interface BillingManager {
    /**
     * Read only flow that contains the most recent event encountered in BillingManager
     */
    val billingState: StateFlow<BillingState?>

    /**
     * Begins setting up the BillingManager. Should be called before any other function
     */
    fun onCreate()

    /**
     * Closes any open connections. Should be called once done with the BillingManager
     */
    fun onDestroy()

    /**
     * Opens the bottom sheet that allows the user to purchase the selected sku
     *
     * @param sku the sku the user wants to purchase
     */
    fun startPurchaseFlow(sku: BillingSku)

    /**
     * Opens the bottom sheet that allows the user to change their subscription plan
     *
     * @param newSku the sku the user wishes to switch to
     */
    suspend fun startSubscriptionChangeFlow(newSku: BillingSku)

    /**
     * @return all active subscriptions of the user
     */
    suspend fun getSubscriptionPurchases(): QueryPurchasesResult

    /**
     * @return all unconsumed in app purchases of the user
     */
    suspend fun getInAppPurchases(): QueryPurchasesResult

    /**
     * Checks that the purchase is not pending and is not already acknowledged. If true,
     * the purchase is sent to the backend to be acknowledged and the user's subscription data
     * is updated.
     *
     * @param purchase the purchase to be registered. This should always be a subscription
     * @param source the source of the purchase for analytics purposes
     */
    suspend fun registerSubPurchaseIfNeeded(purchase: BillingPurchase, source: String?)

    /**
     * Checks for gift purchases that have been consumed, but not logged through v5/log_google_sub.
     * If there are gift purchases that need to be logged, this will start the
     * RegisterGoogleSubscriptionWorker to log each purchase.
     */
    suspend fun registerGiftPurchasesStartup()

    /**
     * Saves gift purchase data and starts worker to log data with backend
     * @param purchase the purchase to be registered. This should always be an in-app purchase
     */
    suspend fun registerGiftPurchase(purchase: BillingPurchase)

    /**
     * Consumes an in app purchase
     *
     * @param purchase the purchase that should be consumed
     */
    fun consumePurchase(purchase: BillingPurchase)

    /**
     * @return true is the user has previously purchased a subscription, false otherwise
     */
    suspend fun hasSubscriptionPurchaseHistory(): Boolean

    /**
     * Query for more info about available in app purchases
     *
     * @param skus List of skus to query for more info about
     * @return list of more info on the requested skus
     */
    suspend fun getInAppSkus(skus: List<String>): List<BillingSku>

    /**
     * Query for more info about available subscriptions
     *
     * @param skus List of skus to query for more info about
     * @return list of more info on the requested skus
     */
    suspend fun getSubSkus(skus: List<String>): List<BillingSku>

    /**
     * Returns the product purchase event from a Purchase to be tracked by the Analytics
     * @param purchase the purchase that should be tracked
     * @param isSubSku true if the purchase is a subscription or false if it is a consumable
     */
    suspend fun getPurchaseAnalytics(purchase: BillingPurchase, isSubSku: Boolean):
        Event.Payments.ProductPurchase

    data class QueryPurchasesResult(
        val purchases: List<BillingPurchase>?,
        val isError: Boolean,
        val responseCode: Int
    )
}