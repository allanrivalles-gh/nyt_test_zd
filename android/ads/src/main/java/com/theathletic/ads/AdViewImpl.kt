package com.theathletic.ads

import com.google.android.gms.ads.admanager.AdManagerAdView
import com.theathletic.ads.ui.setColorThemeForAd

class AdViewImpl(
    override val view: AdManagerAdView
) : AdView {
    override fun pause() = view.pause()
    override fun resume() = view.resume()
    override fun setLightMode(lightMode: Boolean) = view.setColorThemeForAd(lightMode)
}