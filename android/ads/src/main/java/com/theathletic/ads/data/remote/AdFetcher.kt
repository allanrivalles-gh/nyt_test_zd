package com.theathletic.ads.data.remote

import android.content.Context
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.theathletic.ads.AdConfig
import com.theathletic.ads.constructAdTargetingBundle
import com.theathletic.ads.data.local.AdErrorReason
import com.theathletic.ads.data.local.AdsLocalDataStore
import com.theathletic.ads.getAdSizes
import com.theathletic.annotation.autokoin.AutoKoin

open class AdFetcher @AutoKoin constructor() {

    interface AdFetchListener {
        fun onAdLoaded(key: AdsLocalDataStore.AdKey, adConfig: AdConfig, ad: AdManagerAdView)
        fun onAdImpression(key: AdsLocalDataStore.AdKey, adConfig: AdConfig, ad: AdManagerAdView)
        fun onAdFailed(key: AdsLocalDataStore.AdKey, adConfig: AdConfig, errorReason: AdErrorReason, ad: AdManagerAdView)
    }

    @Suppress("SpreadOperator")
    fun fetchAd(key: AdsLocalDataStore.AdKey, adView: AdManagerAdView, listener: AdFetchListener, adConfig: AdConfig) {
        if (!adConfig.isPrivacyEnabled) {
            listener.onAdFailed(key, adConfig, AdErrorReason.AD_PRIVACY_DATA_MISSING, adView)
            return
        }
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                listener.onAdLoaded(key, adConfig, adView)
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                super.onAdFailedToLoad(error)
                val errorReason = AdErrorReason.findByCode(error.code)
                listener.onAdFailed(key, adConfig, errorReason, adView)
            }

            override fun onAdImpression() {
                super.onAdImpression()
                listener.onAdImpression(key, adConfig, adView)
            }
        }
        if (adConfig.adUnitPath.isNullOrEmpty()) {
            listener.onAdFailed(key, adConfig, AdErrorReason.INVALID_AD_UNIT_PATH, adView)
            return
        }
        adView.adUnitId = adConfig.adUnitPath
        adView.setAdSizes(*adConfig.viewportSize.getAdSizes())

        val builder = AdManagerAdRequest.Builder()
        val networkBundle = adConfig.constructAdTargetingBundle()
        networkBundle.putString(GMA_SDK_KVP, MobileAds.getVersion().toString())
        setPrivacy(adView.context.applicationContext, adConfig, networkBundle)
        builder.addNetworkExtrasBundle(AdMobAdapter::class.java, networkBundle)
        adView.loadAd(builder.build())
    }

    private fun setPrivacy(context: Context, adConfig: AdConfig, networkBundle: Bundle) {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPrefs.edit().remove(GAD_HAS_COOKIE_CONSENT_KEY).apply()
        sharedPrefs.edit().remove(GAD_RDP_KEY).apply()
        when {
            adConfig.adPrivacy.isGdpr() -> {
                sharedPrefs.edit().putInt(GAD_HAS_COOKIE_CONSENT_KEY, 0).apply()
                networkBundle.putInt(LTD_KEY, 1)
            }
            adConfig.adPrivacy.isCcpa() -> {
                sharedPrefs.edit().putInt(GAD_RDP_KEY, 1).apply()
                networkBundle.putInt(RDP_KEY, 1)
            }
            else -> {
                sharedPrefs.edit().apply {
                    remove(GAD_HAS_COOKIE_CONSENT_KEY)
                    remove(GAD_RDP_KEY)
                }.apply()
            }
        }
    }

    companion object {
        private const val LTD_KEY = "ltd"
        private const val GAD_HAS_COOKIE_CONSENT_KEY = "gad_has_consent_for_cookies"
        private const val GAD_RDP_KEY = "gad_rdp"
        private const val RDP_KEY = "rdp"
        private const val GMA_SDK_KVP = "gma_sdk"
    }
}