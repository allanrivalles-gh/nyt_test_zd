package com.theathletic.ads.data.local

import com.theathletic.ads.bridge.data.local.AdEvent
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.local.InMemoryLocalDataSource
import java.util.concurrent.atomic.AtomicInteger

class AdsLocalDataStore @AutoKoin(Scope.SINGLE) constructor() : InMemoryLocalDataSource<AdsLocalDataStore.AdKey, AdLocalModel>() {
    data class AdKey(
        val pageViewId: String,
        val adId: String,
    )

    fun isLocalAdAvailable(key: AdKey, shouldReplaceDiscarded: Boolean): Boolean {
        val ad = getStateFlow(key).value
        if (ad == null || (shouldReplaceDiscarded && ad.discard)) {
            return false
        }
        return true
    }
}

class AdsLocalLastEventDataStore @AutoKoin(Scope.SINGLE) constructor() : InMemoryLocalDataSource<String, AdEvent>() {
    private val eventId = AtomicInteger()

    fun setEvent(key: String, createEvent: (Int) -> AdEvent) {
        update(key, createEvent(eventId.incrementAndGet()))
    }
}