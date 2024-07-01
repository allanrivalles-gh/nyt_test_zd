package com.theathletic.ads

import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.AdapterStatus
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import timber.log.Timber
import java.util.UUID

class AdsManager @AutoKoin(Scope.SINGLE) constructor() {
    // Create a unique cache identifier we can use each time application starts
    val adCacheUniqueIdentifier = UUID.randomUUID().toString()

    private var isInitialized: Boolean = false

    fun initAds(context: Context) {
        try {
            MobileAds.initialize(context) { initStatus ->
                // Validate initialization status is ready
                for (status in initStatus.adapterStatusMap) {
                    if (status.value.initializationState != AdapterStatus.State.READY) {
                        isInitialized = false
                        return@initialize
                    }
                }
                isInitialized = true
            }
        } catch (e: ClassNotFoundException) {
            Timber.e(e)
        }
    }
}