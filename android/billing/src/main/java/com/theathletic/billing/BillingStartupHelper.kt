package com.theathletic.billing

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.billing.data.local.BillingPurchaseState
import com.theathletic.utility.BillingPreferences
import com.theathletic.utility.coroutines.DispatcherProvider
import com.theathletic.utility.logging.ICrashLogHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class BillingStartupHelper @AutoKoin constructor(
    private val billingManager: BillingManager,
    private val billingPreferences: BillingPreferences,
    private val crashHandler: ICrashLogHandler,
    dispatcherProvider: DispatcherProvider
) {
    private companion object {
        const val TIMEOUT_MS = 2500L
    }

    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + dispatcherProvider.main)

    fun updateBillingInfo(onSuccessfulGiftPurchase: (() -> Unit)?) {
        coroutineScope.launch {
            billingManager.billingState.collect { handleBillingStateUpdates(it, onSuccessfulGiftPurchase) }
        }
        billingManager.onCreate()
    }

    private fun handleBillingStateUpdates(
        billingState: BillingState?,
        onSuccessfulGiftPurchase: (() -> Unit)?
    ) {
        when (billingState) {
            is SetupComplete -> onSuccessfulBillingStartup(onSuccessfulGiftPurchase)
            is SetupFailed -> onFailedBillingStartup()
            else -> {}
        }
    }

    private fun onSuccessfulBillingStartup(onSuccessfulGiftPurchase: (() -> Unit)?) = coroutineScope.launch {
        // This runs during startup and so we don't want to block it for too long
        try {
            withTimeout(TIMEOUT_MS) {
                updateBillingHistory()
                updateSubscriptionAcknowledgement(onSuccessfulGiftPurchase)
            }
        } finally {
            destroy()
        }
    }

    private fun onFailedBillingStartup() {
        crashHandler.logException(OnBillingStartupFailException())
        destroy()
    }

    private suspend fun updateBillingHistory() {
        billingPreferences.hasPurchaseHistory = billingManager.hasSubscriptionPurchaseHistory()
    }

    private suspend fun updateSubscriptionAcknowledgement(onSuccessfulGiftPurchase: (() -> Unit)?) {
        val subscriptionResult = billingManager.getSubscriptionPurchases()
        if (subscriptionResult.isError.not()) {
            val subscriptionList = subscriptionResult.purchases ?: emptyList()
            if (subscriptionList.isEmpty()) {
                billingPreferences.setSubscriptionData(null, null)
            } else {
                subscriptionList.forEach { purchase ->
                    billingManager.registerSubPurchaseIfNeeded(purchase, null)
                    val productId = purchase.skus.lastOrNull()
                    val purchaseToken: String = purchase.purchaseToken
                    billingPreferences.setSubscriptionData(productId, purchaseToken)
                }
            }
        }

        val inAppResult = billingManager.getInAppPurchases()
        if (inAppResult.isError.not()) {
            inAppResult.purchases?.forEach { purchase ->
                if (purchase.purchaseState == BillingPurchaseState.Purchased) {
                    onSuccessfulGiftPurchase?.invoke()
                }
            }

            billingManager.registerGiftPurchasesStartup()
        }
    }

    private fun destroy() {
        billingManager.onDestroy()
        coroutineScope.cancel()
    }

    private class OnBillingStartupFailException : Exception("${this::class.simpleName}: billing client startup failed")
}