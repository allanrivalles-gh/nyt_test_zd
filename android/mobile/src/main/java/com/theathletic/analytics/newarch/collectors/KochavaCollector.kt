package com.theathletic.analytics.newarch.collectors

import com.theathletic.analytics.KochavaWrapper
import com.theathletic.analytics.newarch.AnalyticsCollector
import com.theathletic.analytics.newarch.Event

class KochavaCollector : AnalyticsCollector {
    override fun trackEvent(
        event: Event,
        properties: Map<String, String>,
        deeplinkParams: Map<String, String>
    ) {
        val finalEvent = when (event) {
            is Event.Payments.ProductPurchase -> {
                createTrackPurchaseEvent(event)
            }
            is Event.Payments.NativePurchaseDialogDisplayed -> {
                KochavaWrapper.KochavaEvent.checkoutStart
            }
            is Event.Onboarding.Finished -> {
                KochavaWrapper.KochavaEvent.tutorialComplete
            }
            is Event.Onboarding.AccountCreated -> {
                KochavaWrapper.KochavaEvent.registrationComplete.setUserId(properties["userId"] ?: "")
            }
            else -> {
                KochavaWrapper.KochavaEvent.named(event.eventName)
            }
        }

        finalEvent.send()
    }

    private fun createTrackPurchaseEvent(event: Event.Payments.ProductPurchase) =
        if (event.isTrialPurchase.toBoolean()) {
            KochavaWrapper.KochavaEvent.startTrial
        } else {
            KochavaWrapper.KochavaEvent.purchase
        }.apply {
            setName(event.eventName)
            setContentId(event.productId)
            setCustomStringValue("isTrialPurchase", event.isTrialPurchase)
        }
}