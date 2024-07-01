package com.theathletic.auth

import android.webkit.CookieManager
import android.webkit.WebView
import com.theathletic.utility.Preferences
import java.util.concurrent.TimeUnit

open class AthleticCookieManager(val domain: String) {
    fun setCookie(webView: WebView) {
        val cookieString = "ath_access_token=${Preferences.accessToken};" +
            "\$Path=\"/\";\$Domain=\"$domain\";" +
            "\$Secure=true;\$Max-Age=${TimeUnit.DAYS.toSeconds(30)}"

        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setCookie(domain, cookieString)
            flush()
            setAcceptThirdPartyCookies(webView, true)
        }
    }

    fun removeCookies() {
        CookieManager.getInstance().apply {
            removeAllCookies(null)
            flush()
        }
    }
}