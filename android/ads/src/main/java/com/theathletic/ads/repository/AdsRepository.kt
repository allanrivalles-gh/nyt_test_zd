package com.theathletic.ads.repository

import com.google.android.gms.ads.admanager.AdManagerAdView
import com.theathletic.ads.AdConfig
import com.theathletic.ads.AdViewFactory
import com.theathletic.ads.AdViewImpl
import com.theathletic.ads.bridge.data.local.AdEvent
import com.theathletic.ads.data.local.AdErrorReason
import com.theathletic.ads.data.local.AdLocalModel
import com.theathletic.ads.data.local.AdsLocalDataStore
import com.theathletic.ads.data.local.AdsLocalLastEventDataStore
import com.theathletic.ads.data.remote.AdFetcher
import com.theathletic.annotation.autokoin.AutoKoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class AdsRepository @AutoKoin constructor(
    private val localDataSource: AdsLocalDataStore,
    private val localLastEventDataStore: AdsLocalLastEventDataStore,
    private val adFetcher: AdFetcher,
    private val adViewFactory: AdViewFactory
) : AdFetcher.AdFetchListener {
    private val inflightAdRequests = mutableMapOf<AdsLocalDataStore.AdKey, AdManagerAdView?>()

    var shouldAllowDiscardingAds: Boolean = false
    fun observeAdEvents(pageViewId: String) = localLastEventDataStore.observeItem(pageViewId).mapNotNull { it ?: AdEvent.NotInitialized }

    fun getAd(pageViewId: String, adId: String): Flow<AdLocalModel?> {
        val key = AdsLocalDataStore.AdKey(pageViewId = pageViewId, adId = adId)
        return localDataSource.observeItem(key)
    }

    fun clearCache(pageViewId: String) {
        localDataSource.unset { it.pageViewId == pageViewId }
        localLastEventDataStore.unset(pageViewId)
    }

    fun fetchAd(pageViewId: String, adId: String, adConfig: AdConfig, shouldReplaceDiscarded: Boolean) {
        val key = AdsLocalDataStore.AdKey(pageViewId = pageViewId, adId = adId)
        if (localDataSource.isLocalAdAvailable(key, shouldReplaceDiscarded) || inflightAdRequests[key] != null) {
            return
        }
        val adView = adViewFactory.newAdViewInstance()
        inflightAdRequests[key] = adView
        adFetcher.fetchAd(key, adView, this, adConfig)

        localLastEventDataStore.setEvent(pageViewId) { AdEvent.AdRequest(it, adConfig.position) }
    }

    override fun onAdLoaded(key: AdsLocalDataStore.AdKey, adConfig: AdConfig, ad: AdManagerAdView) {
        val localAd = AdLocalModel(
            id = key.adId,
            adConfig = adConfig,
            adView = AdViewImpl(ad),
            discard = false,
            collapsed = false
        )
        localDataSource.update(key, localAd)
        inflightAdRequests.remove(key)

        localLastEventDataStore.setEvent(key.pageViewId) { AdEvent.AdResponseSuccess(it, adConfig.position) }
    }

    override fun onAdImpression(key: AdsLocalDataStore.AdKey, adConfig: AdConfig, ad: AdManagerAdView) {
        if (shouldAllowDiscardingAds) {
            val localAd = AdLocalModel(
                id = key.adId,
                adConfig = adConfig,
                adView = AdViewImpl(ad),
                discard = true,
                collapsed = false
            )
            localDataSource.update(key, localAd)
        }

        localLastEventDataStore.setEvent(key.pageViewId) { AdEvent.AdImpression(it, adConfig.position) }
    }

    override fun onAdFailed(key: AdsLocalDataStore.AdKey, adConfig: AdConfig, errorReason: AdErrorReason, ad: AdManagerAdView) {
        localLastEventDataStore.setEvent(key.pageViewId) { eventId ->
            when (errorReason) {
                AdErrorReason.NO_FILL_ERROR -> AdEvent.AdNoFill(eventId, adConfig.position)
                else -> AdEvent.AdResponseFail(
                    eventId,
                    adConfig.position,
                    errorReason.reason
                )
            }
        }
        val localAd = AdLocalModel(
            id = key.adId,
            adConfig = adConfig,
            adView = null,
            discard = false,
            collapsed = true
        )
        localDataSource.update(key, localAd)
        inflightAdRequests.remove(key)
    }
}