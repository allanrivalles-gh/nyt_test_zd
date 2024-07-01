package com.theathletic.profile

import android.webkit.WebView
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.profile.data.ConsentRepository

class SetConsentUseCase @AutoKoin constructor(
    private val consentRepository: ConsentRepository,
) {
    suspend operator fun invoke(webView: WebView) {
        consentRepository.setConsent(webView)
    }
}