package com.theathletic.ads.articles

import android.webkit.WebView
import java.lang.ref.WeakReference

class AdsDisabledScrollBehaviorImpl : AdsScrollBehavior {
    override var webView: WeakReference<WebView>? = null
        set(_) {
            field = null
        }
    override fun requestInitialAd() {
        // Do nothing
    }

    override fun onScrolled(scrollY: Int) {
        // Do nothing
    }
}