package com.theathletic.ads.repository

import com.google.android.gms.ads.admanager.AdManagerAdView
import com.google.common.truth.Truth.assertThat
import com.theathletic.ads.AdConfig
import com.theathletic.ads.AdViewFactory
import com.theathletic.ads.bridge.data.local.AdEvent
import com.theathletic.ads.data.local.AdErrorReason
import com.theathletic.ads.data.local.AdLocalModel
import com.theathletic.ads.data.local.AdsLocalDataStore
import com.theathletic.ads.data.local.AdsLocalLastEventDataStore
import com.theathletic.ads.data.remote.AdFetcher
import com.theathletic.test.runTest
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AdsRepositoryTest {

    @Mock lateinit var mockLocalDatastore: AdsLocalDataStore
    @Mock lateinit var mockAdFetcher: AdFetcher
    @Mock lateinit var mockAdViewFactory: AdViewFactory
    @Mock lateinit var mockAdManagerAdView: AdManagerAdView
    @Mock lateinit var mockAdConfig: AdConfig
    lateinit var localLastEventDataStore: AdsLocalLastEventDataStore
    lateinit var adRepository: AdsRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        whenever(mockAdViewFactory.newAdViewInstance()).thenReturn(mockAdManagerAdView)
        whenever(mockAdConfig.position).thenReturn("mid1")
        localLastEventDataStore = spy(AdsLocalLastEventDataStore())
        adRepository = AdsRepository(mockLocalDatastore, localLastEventDataStore, mockAdFetcher, mockAdViewFactory)
    }

    @Test
    fun `verify repository can clear local ad cache`() {
        adRepository.clearCache(pageViewId = "d94b9d61-eb89-4bdb-b431-d732a323d160")
        verify(mockLocalDatastore).unset(any<(AdsLocalDataStore.AdKey) -> Boolean>())
        verify(localLastEventDataStore).unset(any<String>())
    }

    @Test
    fun `verify fetch ad called if local ad doesnt exist`() {
        val key = AdsLocalDataStore.AdKey(pageViewId = "d94b9d61-eb89-4bdb-b431-d732a323d160", adId = "mid1")
        whenever(mockLocalDatastore.isLocalAdAvailable(key, true)).thenReturn(false)
        adRepository.fetchAd(pageViewId = key.pageViewId, adId = key.adId, mockAdConfig, true)
        verify(mockAdFetcher).fetchAd(
            eq(key),
            eq(mockAdManagerAdView),
            any(),
            eq(mockAdConfig)
        )
    }

    @Test
    fun `verify fetch ad not called if local ad exists`() {
        val key = AdsLocalDataStore.AdKey(pageViewId = "d94b9d61-eb89-4bdb-b431-d732a323d160", adId = "mid1")
        whenever(mockLocalDatastore.isLocalAdAvailable(key, true)).thenReturn(true)
        adRepository.fetchAd(pageViewId = key.pageViewId, adId = key.adId, mockAdConfig, true)
        verify(mockAdFetcher, times(0)).fetchAd(any(), any(), any(), any())
    }

    @Test
    fun `verify ad request event triggered if ad fetched`() = runTest {
        val key = AdsLocalDataStore.AdKey(pageViewId = "d94b9d61-eb89-4bdb-b431-d732a323d160", adId = "mid1")
        whenever(mockLocalDatastore.isLocalAdAvailable(key, true)).thenReturn(false)
        whenever(mockAdConfig.position).thenReturn(key.adId)
        adRepository.fetchAd(pageViewId = key.pageViewId, adId = key.adId, mockAdConfig, true)
        val event = adRepository.observeAdEvents(pageViewId = key.pageViewId).first()
        assertThat(event).isEqualTo(AdEvent.AdRequest(1, "mid1"))
    }

    @Test
    fun `verify ad request event not triggered when trying to ad fetch with an existing local ad`() = runTest {
        val key = AdsLocalDataStore.AdKey(pageViewId = "d94b9d61-eb89-4bdb-b431-d732a323d160", adId = "mid1")
        whenever(mockLocalDatastore.isLocalAdAvailable(key, true)).thenReturn(true)
        adRepository.fetchAd(pageViewId = key.pageViewId, adId = key.adId, mockAdConfig, true)
        val event = adRepository.observeAdEvents(pageViewId = key.pageViewId).first()
        assertThat(event).isEqualTo(AdEvent.NotInitialized)
    }

    @Test
    fun `verify ad loaded updates local cache`() = runTest {
        val key = AdsLocalDataStore.AdKey(pageViewId = "d94b9d61-eb89-4bdb-b431-d732a323d160", adId = "mid1")
        adRepository.onAdLoaded(key, mockAdConfig, mockAdManagerAdView)
        verify(mockLocalDatastore).update(eq(key), any())
    }

    @Test
    fun `verify ad loaded triggers ad response success event`() = runTest {
        val key = AdsLocalDataStore.AdKey(pageViewId = "d94b9d61-eb89-4bdb-b431-d732a323d160", adId = "mid1")
        adRepository.onAdLoaded(key, mockAdConfig, mockAdManagerAdView)
        val event = adRepository.observeAdEvents(pageViewId = key.pageViewId).first()
        assertThat(event).isEqualTo(AdEvent.AdResponseSuccess(1, key.adId))
    }

    @Test
    fun `verify ad impression updates a local ad`() {
        val key = AdsLocalDataStore.AdKey(pageViewId = "d94b9d61-eb89-4bdb-b431-d732a323d160", adId = "mid1")
        adRepository.shouldAllowDiscardingAds = true
        adRepository.onAdImpression(key, mockAdConfig, mockAdManagerAdView)
        verify(mockLocalDatastore).update(eq(key), any())
    }

    @Test
    fun `verify ad impression does not update local ad if allow discard not enabled`() {
        val key = AdsLocalDataStore.AdKey(pageViewId = "d94b9d61-eb89-4bdb-b431-d732a323d160", adId = "mid1")
        adRepository.onAdImpression(key, mockAdConfig, mockAdManagerAdView)
        verify(mockLocalDatastore, times(0)).update(any(), any())
    }

    @Test
    fun `verify ad impression triggers ad impression event`() = runTest {
        val key = AdsLocalDataStore.AdKey(pageViewId = "d94b9d61-eb89-4bdb-b431-d732a323d160", adId = "mid1")
        adRepository.onAdImpression(key, mockAdConfig, mockAdManagerAdView)
        val event = adRepository.observeAdEvents(pageViewId = key.pageViewId).first()
        assertThat(event).isEqualTo(AdEvent.AdImpression(1, "mid1"))
    }

    @Test
    fun `verify ad failed stores an empty ad in local cache`() {
        val key = AdsLocalDataStore.AdKey(pageViewId = "d94b9d61-eb89-4bdb-b431-d732a323d160", adId = "mid1")
        adRepository.onAdFailed(key, mockAdConfig, AdErrorReason.UNKNOWN_ERROR, mockAdManagerAdView)
        verify(mockLocalDatastore).update(
            key,
            AdLocalModel(key.adId, mockAdConfig, null, false, true)
        )
    }

    @Test
    fun `verify ad failed due to no fill triggers an ad no fill event`() = runTest {
        val key = AdsLocalDataStore.AdKey(pageViewId = "d94b9d61-eb89-4bdb-b431-d732a323d160", adId = "mid1")
        adRepository.onAdFailed(key, mockAdConfig, AdErrorReason.NO_FILL_ERROR, mockAdManagerAdView)
        val event = adRepository.observeAdEvents(pageViewId = key.pageViewId).first()
        assertThat(event).isEqualTo(AdEvent.AdNoFill(1, key.adId))
    }

    @Test
    fun `verify ad failed triggers an ad response fail event`() = runTest {
        val key = AdsLocalDataStore.AdKey(pageViewId = "d94b9d61-eb89-4bdb-b431-d732a323d160", adId = "mid1")
        adRepository.onAdFailed(key, mockAdConfig, AdErrorReason.NETWORK_ERROR, mockAdManagerAdView)
        val event = adRepository.observeAdEvents(pageViewId = key.pageViewId).first()
        assertThat(event).isEqualTo(AdEvent.AdResponseFail(1, "mid1", AdErrorReason.NETWORK_ERROR.reason))
    }

    @Test
    fun `verify event is not sent to unrelated page`() = runTest {
        val key = AdsLocalDataStore.AdKey(pageViewId = "1", adId = "mid1")
        val unrelatedPageViewId = "2"
        adRepository.onAdFailed(key, mockAdConfig, AdErrorReason.NETWORK_ERROR, mockAdManagerAdView)
        val event = adRepository.observeAdEvents(pageViewId = unrelatedPageViewId).first()
        assertThat(event).isEqualTo(AdEvent.NotInitialized)
    }
}