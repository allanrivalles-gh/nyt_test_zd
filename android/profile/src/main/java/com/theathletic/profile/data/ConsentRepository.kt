package com.theathletic.profile.data

import android.webkit.WebView
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.profile.data.remote.TranscendConsentWrapper
import timber.log.Timber

class ConsentRepository @AutoKoin constructor(
    private val transcendConsentWrapper: TranscendConsentWrapper,
) {

    suspend fun getIsConsentConfirmed(): Boolean {
        return try {
            transcendConsentWrapper.getConsent().isConfirmed
        } catch (e: Exception) {
            Timber.e(e, "Failed to get consent from Transcend.")
            false
        }
    }

    suspend fun getIsUserInGDPR(): Boolean {
        return try {
            val regimes = transcendConsentWrapper.getRegimes()
            regimes.contains("gdpr")
        } catch (e: Exception) {
            Timber.e(e, "Failed to get regimes from Transcend.")
            false
        }
    }

    suspend fun setConsent(webView: WebView) {
        try {
            transcendConsentWrapper.setConsent(webView)
        } catch (e: Exception) {
            Timber.e(e, "Failed to set consent on webview.")
        }
    }
}