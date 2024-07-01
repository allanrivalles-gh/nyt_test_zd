package com.theathletic.ads.data.local

import com.google.common.truth.Truth.assertThat
import com.theathletic.ads.AdView
import com.theathletic.test.runTest
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class AdsLocalDataStoreTest {

    @Mock lateinit var mockAdModel: AdLocalModel
    @Mock lateinit var mockReplacementAdModel: AdLocalModel
    @Mock lateinit var mockAdView: AdView

    private lateinit var adLocalDatastore: AdsLocalDataStore

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        whenever(mockAdModel.discard).thenReturn(false)
        whenever(mockAdModel.adView).thenReturn(mockAdView)
        whenever(mockReplacementAdModel.discard).thenReturn(false)
        whenever(mockReplacementAdModel.adView).thenReturn(mockAdView)
        adLocalDatastore = AdsLocalDataStore()
    }

    @Test
    fun `verify local ad available after update`() {
        val key = AdsLocalDataStore.AdKey(pageViewId = "d94b9d61-eb89-4bdb-b431-d732a323d160", adId = "mid1")
        assertThat(adLocalDatastore.isLocalAdAvailable(key, true)).isFalse()
        adLocalDatastore.update(key, mockAdModel)
        assertThat(adLocalDatastore.isLocalAdAvailable(key, true)).isTrue()
    }

    @Test
    fun `verify no local ad if ad in cache is marked for discard`() {
        val key = AdsLocalDataStore.AdKey(pageViewId = "d94b9d61-eb89-4bdb-b431-d732a323d160", adId = "mid1")
        whenever(mockAdModel.discard).thenReturn(true)
        adLocalDatastore.update(key, mockAdModel)
        assertThat(adLocalDatastore.isLocalAdAvailable(key, true)).isFalse()
    }

    @Test
    fun `verify local ad cache can be cleared`() = runTest {
        val key = AdsLocalDataStore.AdKey(pageViewId = "d94b9d61-eb89-4bdb-b431-d732a323d160", adId = "mid1")
        adLocalDatastore.update(key, mockAdModel)
        assertThat(adLocalDatastore.observeItem(key).first()).isEqualTo(mockAdModel)

        adLocalDatastore.clearCache()
        assertThat(adLocalDatastore.observeItem(key).first()).isNull()
        assertThat(adLocalDatastore.isLocalAdAvailable(key, true)).isFalse()
    }

    @Test
    fun `verify local ad still cached if ad marked for discard but shouldReplaceDiscarded is false`() = runTest {
        val key = AdsLocalDataStore.AdKey(pageViewId = "d94b9d61-eb89-4bdb-b431-d732a323d160", adId = "mid1")
        whenever(mockAdModel.discard).thenReturn(true)
        adLocalDatastore.update(key, mockAdModel)
        assertThat(adLocalDatastore.observeItem(key).first()).isEqualTo(mockAdModel)
        assertThat(adLocalDatastore.isLocalAdAvailable(key, false)).isTrue()
    }
}