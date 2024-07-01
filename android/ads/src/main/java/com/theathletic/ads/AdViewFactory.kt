package com.theathletic.ads

import android.content.Context
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.theathletic.annotation.autokoin.AutoKoin

class AdViewFactory @AutoKoin constructor(private val applicationContext: Context) {
    fun newAdViewInstance(): AdManagerAdView = AdManagerAdView(applicationContext)
}