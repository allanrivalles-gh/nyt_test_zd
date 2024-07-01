package com.theathletic.ads

import android.content.Context
import android.content.res.Resources
import com.google.android.gms.ads.AdSize
import com.theathletic.ads.data.local.ContentType
import com.theathletic.ads.data.local.ViewPortSize
import com.theathletic.ads.models.R
import com.theathletic.utility.AdPreferences
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import kotlin.test.assertContains
import kotlin.test.assertEquals

class AdExtensionsTest {

    @Mock lateinit var mockContext: Context
    @Mock lateinit var mockResources: Resources
    @Mock lateinit var mockPreferences: AdPreferences
    @Mock lateinit var mockConfigClient: AdConfigClient

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        whenever(mockContext.resources).thenReturn(mockResources)
        whenever(mockResources.getBoolean(R.bool.isTablet)).thenReturn(false)
        whenever(mockPreferences.adKeyword).thenReturn(DEFAULT_AD_KEYWORD_VALUE)
        whenever(mockPreferences.privacyEnabled).thenReturn(true)
        whenever(mockPreferences.privacyCountryCode).thenReturn(null)
        whenever(mockPreferences.privacyStateAbbr).thenReturn(null)
        whenever(mockConfigClient.property).thenReturn("athdroid")
        whenever(mockConfigClient.platform).thenReturn("phone")
    }

    @Test
    fun `adconfig json with privacy set`() {
        val adConfig = AdConfig.Builder(mockPreferences, mockConfigClient)
            .subscriber(true)
            .contentType(ContentType.ARTICLES.type)
            .viewport(700, 800)
            .setGeo("US", "CA")
            .build(DEFAULT_PAGE_VIEW_ID)

        val sov = adConfig.adRequirements["sov"]

        val adConfigString = adConfig.getAdKvpsAsJson()
        assertEquals(String.format(EXPECTED_JSON_WITH_PRIVACY_KVPS, sov), adConfigString)
    }

    @Test
    fun `adconfig json without privacy set`() {
        val adConfig = AdConfig.Builder(mockPreferences, mockConfigClient)
            .subscriber(true)
            .contentType(ContentType.ARTICLES.type)
            .viewport(700, 800)
            .setGeo(null, null)
            .build(DEFAULT_PAGE_VIEW_ID)

        val sov = adConfig.adRequirements["sov"]

        val adConfigString = adConfig.getAdKvpsAsJson()
        assertEquals(String.format(EXPECTED_JSON_WITHOUT_PRIVACY_KVPS, sov), adConfigString)
    }

    @Test
    fun `get ad position from id`() {
        var pos = "0-1".adPosition
        assertEquals("mid0-1", pos)
        pos = "0-2".adPosition
        assertEquals("mid0-2", pos)
    }

    @Test
    fun `get ad sizes for small viewport`() {
        val adSizes = ViewPortSize.SMALL.getAdSizes()
        assertEquals(2, adSizes.size)
        assertContains(adSizes, AdSize.FLUID)
        assertContains(adSizes, AdSize(300, 250))
    }

    @Test
    fun `get ad sizes for medium viewport`() {
        val adSizes = ViewPortSize.MEDIUM.getAdSizes()
        assertEquals(3, adSizes.size)
        assertContains(adSizes, AdSize.FLUID)
        assertContains(adSizes, AdSize(300, 250))
        assertContains(adSizes, AdSize(728, 90))
    }

    @Test
    fun `get ad sizes for large viewport`() {
        val adSizes = ViewPortSize.LARGE.getAdSizes()
        assertEquals(4, adSizes.size)
        assertContains(adSizes, AdSize.FLUID)
        assertContains(adSizes, AdSize(728, 90))
        assertContains(adSizes, AdSize(970, 90))
        assertContains(adSizes, AdSize(970, 250))
    }

    companion object {
        private const val DEFAULT_PAGE_VIEW_ID = "page_view_1"
        private const val DEFAULT_AD_KEYWORD_VALUE = "adtest"

        private const val EXPECTED_JSON_WITHOUT_PRIVACY_KVPS =
            "{\"AdRequirements\":{\"sub\":\"true\",\"adv\":\"$DEFAULT_AD_KEYWORD_VALUE\",\"ta_page_view_id\":\"page_view_1\",\"prop\":\"athdroid\",\"typ\":\"art\",\"vp\":\"small\",\"plat\":\"phone\",\"sov\":\"%s\"}," +
                "\"viewport\":{\"width\":700,\"height\":800},\"adUnitPath\":\"/29390238/theathletic\"}"
        private const val EXPECTED_JSON_WITH_PRIVACY_KVPS =
            "{\"AdRequirements\":{\"sub\":\"true\",\"adv\":\"$DEFAULT_AD_KEYWORD_VALUE\",\"ta_page_view_id\":\"page_view_1\",\"prop\":\"athdroid\",\"typ\":\"art\",\"vp\":\"small\",\"plat\":\"phone\",\"sov\":\"%s\"}," +
                "\"privacy\":{\"geo\":{\"cc\":\"US\",\"state\":\"CA\"}}," +
                "\"viewport\":{\"width\":700,\"height\":800},\"adUnitPath\":\"/29390238/theathletic\"}"
    }
}