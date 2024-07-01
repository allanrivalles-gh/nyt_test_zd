package com.theathletic.billing

import com.theathletic.billing.data.local.BillingPurchase

sealed class BillingState

/**
 * Emitted when BillingManager has finished its setup and is ready to be used.
 */
object SetupComplete : BillingState()

/**
 * Emitted when BillingManager has finished its setup and has failed.
 */
object SetupFailed : BillingState()

/**
 * Emitted when the user manually exits the purchase flow before completing a purchase.
 */
object PurchaseCancelled : BillingState()

/**
 * Emitted when the user has completed a purchase, but has not yet been charged for it.
 * The user should be notified of this and told to check back later. The user should not be
 * granted privileges for their purchase yet.
 */
object PurchasePending : BillingState()

/**
 * Emitted when an in-app purchase is successfully consumed. This means the user can purchase
 * the same item again if wanted.
 */
data class PurchaseConsumed(val purchase: BillingPurchase) : BillingState()

/**
 * Emitted when BillingManager encounters an error unrelated to making a purchase
 */
object GeneralBillingFailure : BillingState()

/**
 * Emitted when BillingManager encounters an error while attempting to consume a purchase
 */
object ConsumeFailure : BillingState()

/**
 * Emitted when BillingManager encounters an error while processing a purchase.
 * The user should be notified of this and not granted privileges for their purchase.
 */
object PurchaseFailure : BillingState()

/**
 * Emitted when the user has successfully completed a purchase. The user should be granted
 * the privileges associated with their purchase.
 */
data class PurchaseSuccess(val purchase: BillingPurchase) : BillingState()