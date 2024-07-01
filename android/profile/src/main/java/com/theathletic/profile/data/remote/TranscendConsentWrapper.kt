package com.theathletic.profile.data.remote

import android.content.Context
import android.webkit.WebView
import androidx.preference.PreferenceManager
import io.transcend.webview.IABConstants
import io.transcend.webview.TranscendAPI
import io.transcend.webview.models.TrackingConsentDetails
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TranscendConsentWrapper(
    private val applicationContext: Context,
    val transcendConsentUrl: String,
    val siteUrl: String
) {
    private var initialized: Boolean = false

    fun initialize() {
        TranscendAPI.init(applicationContext, transcendConsentUrl, listOf(siteUrl)) {
            initialized = true
        }
    }

    suspend fun getConsent(): ConsentDetails = suspendCoroutine {
        if (initialized) {
            TranscendAPI.getConsent(applicationContext) { consentDetails ->
                it.resume(consentDetails.toConsentDetails())
            }
        } else {
            throw Exception("Transcend Consent API not initialized")
        }
    }

    suspend fun getRegimes(): Set<String> = suspendCoroutine {
        if (initialized) {
            TranscendAPI.getRegimes(applicationContext) { regimes ->
                it.resume(regimes)
            }
        } else {
            Timber.e("Transcend Consent API not initialized")
            it.resume(emptySet())
        }
    }

    suspend fun setConsent(webView: WebView): Unit = suspendCoroutine {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val tcString = sharedPreferences.getString(IABConstants.IAB_TCF_TC_STRING, null)
        if (tcString.isNullOrEmpty()) {
            it.resume(Unit)
        }
        if (initialized) {
            TranscendAPI.getConsent(applicationContext) { consentDetails ->
                TranscendAPI.setConsent(webView, consentDetails, tcString) { isSuccess, errorDetails ->
                    if (isSuccess.not() && errorDetails.isNotEmpty()) {
                        Timber.e("TranscendAPI.setConsent failed: $errorDetails")
                    }
                    it.resume(Unit)
                }
            }
        } else {
            throw Exception("Transcend Consent API not initialized")
        }
    }

    private fun TrackingConsentDetails.toConsentDetails() = ConsentDetails(
        isConfirmed = isConfirmed
    )
}