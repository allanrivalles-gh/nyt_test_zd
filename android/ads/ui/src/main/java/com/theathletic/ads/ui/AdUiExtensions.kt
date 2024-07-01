package com.theathletic.ads.ui

import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.annotation.VisibleForTesting
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebSettingsCompat.FORCE_DARK_OFF
import androidx.webkit.WebSettingsCompat.FORCE_DARK_ON
import androidx.webkit.WebViewFeature

fun View.setColorThemeForAd(lightMode: Boolean) {
    if (this is ViewGroup) {
        this.findInternalWebView()?.let { webview ->
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                val forceDark = if (lightMode) FORCE_DARK_OFF else FORCE_DARK_ON
                WebSettingsCompat.setForceDark(webview.settings, forceDark)
            }
        }
    }
}

@VisibleForTesting
internal fun ViewGroup.findInternalWebView(): WebView? {
    for (childPosition in 0 until this.childCount) {
        return when (val child = this.getChildAt(childPosition)) {
            is WebView -> child
            is ViewGroup -> child.findInternalWebView()
            else -> null
        }
    }
    return null
}