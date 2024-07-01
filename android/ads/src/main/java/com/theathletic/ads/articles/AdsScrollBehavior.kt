package com.theathletic.ads.articles

import android.webkit.WebView
import java.lang.ref.WeakReference

interface AdsScrollBehavior {
    var webView: WeakReference<WebView>?

    fun requestInitialAd()
    fun onScrolled(scrollY: Int)
}