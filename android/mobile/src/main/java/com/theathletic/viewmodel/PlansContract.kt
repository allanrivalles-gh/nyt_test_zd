package com.theathletic.viewmodel

interface PlansContract {
    sealed class Event : com.theathletic.utility.Event() {
        object PurchaseFinishedEvent : Event()
        object PurchasePendingEvent : Event()
    }
}