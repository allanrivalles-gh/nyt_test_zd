package com.theathletic.analytics

import com.google.common.truth.Truth.assertThat
import com.kochava.tracker.attribution.InstallAttributionApi
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class KochavaWrapperTest {
    @Mock
    lateinit var kochavaWrapper: KochavaWrapper

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        whenever(kochavaWrapper.extractAttributionInfo(any())).thenCallRealMethod()
    }

    @Test
    fun extractAttributionInfo_IsNullWhenNoAttribution() {
        val attributionApi = attributionInstance("")
        assertThat(kochavaWrapper.extractAttributionInfo(attributionApi)).isNull()
    }

    @Test
    fun extractAttributionInfo_IsNullWhenAttributionIsFalse() {
        val testData = """
        {
        "attribution": "false"
        }
        """.trimIndent()
        val attributionApi = attributionInstance(testData)
        assertThat(kochavaWrapper.extractAttributionInfo(attributionApi)).isNull()
    }

    @Test
    fun extractAttributionInfo_ParsesSiteAndCreativeId() {
        val testData = """
        {
        "site_id": "site",
        "creative_id": "creative",
        "adgroup_name": "US-NATIONAL-FBOOK-CONVERSIONS-RETARGETING-FB-ALL"
        }
        """.trimIndent()
        val attributionApi = attributionInstance(testData)
        assertThat(kochavaWrapper.extractAttributionInfo(attributionApi)).isEqualTo(KochavaWrapper.AttributionInfo("site", "creative"))
    }

    @Test
    fun extractAttributionInfo_ParsesArticleFromSiteId() {
        val testData = """
        {
        "site_id": "1234",
        "creative_id": "creative",
        "adgroup_name": "US-NATIONAL-FBOOK-CONVERSIONS-RETARGETING-FB-ALL"
        }
        """.trimIndent()
        val attributionApi = attributionInstance(testData)
        assertThat(kochavaWrapper.extractAttributionInfo(attributionApi)).isEqualTo(KochavaWrapper.AttributionInfo("1234", "creative", 1234L))
    }

    @Test
    fun extractAttributionInfo_ParsesArticleFromAdgroupName() {
        val testData = """
        {
        "site_id": "site",
        "creative_id": "creative",
        "adgroup_name": "US-NATIONAL-FBOOK-CONVERSIONS-RETARGETING-FB-ALL article_id=1012887"
        }
        """.trimIndent()
        val attributionApi = attributionInstance(testData)
        assertThat(kochavaWrapper.extractAttributionInfo(attributionApi)).isEqualTo(KochavaWrapper.AttributionInfo("site", "creative", 1012887L))
    }

    @Test
    fun extractAttributionInfo_PrefersSiteArticleId() {
        val testData = """
        {
        "site_id": "1234",
        "creative_id": "creative",
        "adgroup_name": "US-NATIONAL-FBOOK-CONVERSIONS-RETARGETING-FB-ALL article_id=1012887"
        }
        """.trimIndent()
        val attributionApi = attributionInstance(testData)
        assertThat(kochavaWrapper.extractAttributionInfo(attributionApi)).isEqualTo(KochavaWrapper.AttributionInfo("1234", "creative", 1234L))
    }

    private fun attributionInstance(json: String?): InstallAttributionApi {
        return object : InstallAttributionApi {
            override fun toJson(): JSONObject = JSONObject(json.orEmpty())
            override fun isRetrieved(): Boolean = true
            override fun getRaw(): JSONObject = JSONObject(json.orEmpty())
            override fun isAttributed(): Boolean = true
            override fun isFirstInstall(): Boolean = true
        }
    }
}