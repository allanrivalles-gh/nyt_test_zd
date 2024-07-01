package com.theathletic.ads

import android.content.Context
import android.content.res.Resources
import com.theathletic.ads.data.local.AdPrivacy
import com.theathletic.ads.data.local.ContentType
import com.theathletic.ads.data.local.ViewPort
import com.theathletic.ads.models.R
import com.theathletic.utility.AdPreferences
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class AdConfigTest {

    @Mock lateinit var mockContext: Context
    @Mock lateinit var mockResources: Resources
    @Mock lateinit var mockPreferences: AdPreferences
    @Mock lateinit var mockConfigClient: AdConfigClient

    private lateinit var adConfigBuilder: AdConfig.Builder

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        adConfigBuilder = AdConfig.Builder(mockPreferences, mockConfigClient)
        whenever(mockContext.resources).thenReturn(mockResources)
        whenever(mockResources.getBoolean(R.bool.isTablet)).thenReturn(false)
        whenever(mockPreferences.privacyEnabled).thenReturn(true)
        whenever(mockPreferences.privacyCountryCode).thenReturn(null)
        whenever(mockPreferences.privacyStateAbbr).thenReturn(null)
        whenever(mockPreferences.adKeyword).thenReturn(DEFAULT_AD_KEYWORD_VALUE)
        whenever(mockConfigClient.platform).thenReturn("phone")
        whenever(mockConfigClient.property).thenReturn("athdroid")
    }

    @Test
    fun `adconfig with kvps set are equal and ignore sov`() {
        // Equal adconfig with different sov values
        val expectedAdConfig = adConfigFixture(vp = "small", viewportWidth = 700, viewportHeight = 800)
        val invalidAdConfig = adConfigFixture(subscriber = null)
        val equalAdConfig = adConfigBuilder.subscriber(true)
            .contentType(ContentType.ARTICLES.type)
            .viewport(700, 800)
            .build(DEFAULT_PAGE_VIEW_ID)
        assertEquals(expectedAdConfig, equalAdConfig)
        // Equal adconfig with same sov values
        val sameAdConfig = adConfigBuilder.subscriber(true)
            .contentType(ContentType.ARTICLES.type)
            .viewport(700, 800)
            .build(DEFAULT_PAGE_VIEW_ID)
        assertEquals(expectedAdConfig, sameAdConfig)
        // Different adconfigs
        val differentAdConfig = adConfigBuilder.subscriber(false)
            .contentType(ContentType.ARTICLES.type)
            .viewport(980, 800)
            .build(DEFAULT_PAGE_VIEW_ID)
        assertNotEquals(expectedAdConfig, differentAdConfig)
        // Not equal to bad adconfig
        val goodAdConfig = adConfigBuilder.subscriber(true)
            .contentType(ContentType.ARTICLES.type)
            .viewport(700, 800)
            .build(DEFAULT_PAGE_VIEW_ID)
        assertNotEquals(invalidAdConfig, goodAdConfig)
        // Limited ad config not equal to expected config (doesnt contain certain values like adv)
        val limitedAdConfig = adConfigBuilder.subscriber(false)
            .contentType(ContentType.ARTICLES.type)
            .viewport(980, 800)
            .build(DEFAULT_PAGE_VIEW_ID)
        assertNotEquals(expectedAdConfig, limitedAdConfig)
    }

    @Test
    fun `adconfig set geo country code`() {
        adConfigBuilder.setGDPRCountries(adPrivacyGDPRCountryList)
        adConfigBuilder.setCCPAStates(adPrivacyCCPAStateList)
        val config = adConfigBuilder.setGeo("FR", null)
            .build(DEFAULT_PAGE_VIEW_ID)
        assertEquals("FR", config.adPrivacy.get("cc"))
        assertNotNull(config.adPrivacy)
        assertNull(config.adPrivacy.get("state"))
        assertFalse(config.adPrivacy.isCcpa())
        assertTrue(config.adPrivacy.isGdpr())
    }

    @Test
    fun `adconfig set geo country code and state`() {
        adConfigBuilder.setGDPRCountries(adPrivacyGDPRCountryList)
        adConfigBuilder.setCCPAStates(adPrivacyCCPAStateList)
        var config = adConfigBuilder.setGeo("US", "CA")
            .build(DEFAULT_PAGE_VIEW_ID)
        assertEquals("US", config.adPrivacy.get("cc"))
        assertEquals("CA", config.adPrivacy.get("state"))
        assertFalse(config.adPrivacy.isGdpr())
        assertTrue(config.adPrivacy.isCcpa())

        config = adConfigBuilder.setGeo("US", "VA")
            .build(DEFAULT_PAGE_VIEW_ID)
        assertEquals("US", config.adPrivacy.get("cc"))
        assertEquals("VA", config.adPrivacy.get("state"))
        assertFalse(config.adPrivacy.isGdpr())
        assertTrue(config.adPrivacy.isCcpa())

        config = adConfigBuilder.setGeo("US", "NC")
            .build(DEFAULT_PAGE_VIEW_ID)
        assertEquals("US", config.adPrivacy.get("cc"))
        assertEquals("NC", config.adPrivacy.get("state"))
        assertFalse(config.adPrivacy.isGdpr())
        assertFalse(config.adPrivacy.isCcpa())
    }

    @Test
    fun `adconfig set geo country code and state for non US`() {
        adConfigBuilder.setGDPRCountries(adPrivacyGDPRCountryList)
        adConfigBuilder.setCCPAStates(adPrivacyCCPAStateList)
        val config = adConfigBuilder.setGeo("FR", "CA")
            .build(DEFAULT_PAGE_VIEW_ID)
        assertEquals("FR", config.adPrivacy.get("cc"))
        assertNull(config.adPrivacy.get("state"))
        assertTrue(config.adPrivacy.isGdpr())
        assertFalse(config.adPrivacy.isCcpa())
    }

    @Test
    fun `adconfig set empty geo country code`() {
        adConfigBuilder.setGDPRCountries(adPrivacyGDPRCountryList)
        adConfigBuilder.setCCPAStates(adPrivacyCCPAStateList)
        val config = adConfigBuilder.setGeo("", "")
            .build(DEFAULT_PAGE_VIEW_ID)
        assertNull(config.adPrivacy.get("cc"))
        assertNull(config.adPrivacy.get("state"))
        assertFalse(config.adPrivacy.isGdpr())
        assertFalse(config.adPrivacy.isCcpa())
    }

    @Test
    fun `adconfig version name set`() {
        val versionName = "12.28.0"
        val config = adConfigBuilder.appVersion(versionName)
            .build(DEFAULT_PAGE_VIEW_ID)
        assertEquals(versionName, config.adRequirements["ver"])
    }
    @Test
    fun `adconfig with compass experiment`() {
        var config = adConfigBuilder.setCompassExperiments(listOf("adsv1_ctrl"))
            .build(DEFAULT_PAGE_VIEW_ID)
        assertEquals("adsv1_ctrl", config.adRequirements["abra_dfp"])
        config = adConfigBuilder
            .setCompassExperiments(listOf("adsv1_ctrl", "adsv2_a"))
            .build(DEFAULT_PAGE_VIEW_ID)
        assertEquals("adsv1_ctrl,adsv2_a", config.adRequirements["abra_dfp"])
    }

    @Test
    fun `adconfig with us based geo supports CCPA`() {
        adConfigBuilder.setGDPRCountries(adPrivacyGDPRCountryList)
        adConfigBuilder.setCCPAStates(adPrivacyCCPAStateList)
        val ccpaAdConfig = adConfigBuilder.setGeo("US", "CA")
            .build(DEFAULT_PAGE_VIEW_ID)
        assertTrue(ccpaAdConfig.adPrivacy.isCcpa())

        val notCcpaAdConfig = adConfigBuilder.setGeo("US", "NC")
            .build(DEFAULT_PAGE_VIEW_ID)
        assertFalse(notCcpaAdConfig.adPrivacy.isCcpa())
    }

    @Test
    fun `adconfig with eu based geo supports GDPR`() {
        adConfigBuilder.setGDPRCountries(adPrivacyGDPRCountryList)
        adConfigBuilder.setCCPAStates(adPrivacyCCPAStateList)
        val gdprAdConfig = adConfigBuilder.setGeo("FR")
            .build(DEFAULT_PAGE_VIEW_ID)
        assertTrue(gdprAdConfig.adPrivacy.isGdpr())

        val notGdprAdConfig = adConfigBuilder.setGeo("US")
            .build(DEFAULT_PAGE_VIEW_ID)
        assertFalse(notGdprAdConfig.adPrivacy.isGdpr())
    }

    @Test
    fun `adconfig with author name`() {
        val adTargeting = mapOf("auth" to "Test Author")
        val config = adConfigBuilder.setAdTargeting(adTargeting)
            .build(DEFAULT_PAGE_VIEW_ID)
        assertEquals("testauthor", config.adRequirements["auth"])
    }

    @Test
    fun `adconfig with tags`() {
        val adTargeting = mapOf("tags" to "First, second tag, Third Word, FoUr")
        val config = adConfigBuilder.setAdTargeting(adTargeting)
            .build(DEFAULT_PAGE_VIEW_ID)
        assertEquals("first,secondtag,thirdword,four", config.adRequirements["tags"])
    }

    @Test
    fun `adconfig with content taxonomy set`() {
        var adTargeting = mapOf("org" to "mls,premierleague,soccer,eflchampionship", "coll" to "mls,epl,socc,efl")
        val configWithBoth = AdConfig.Builder(mockPreferences, mockConfigClient).setAdTargeting(adTargeting)
            .build(DEFAULT_PAGE_VIEW_ID)
        assertEquals("mls,epl,socc,efl", configWithBoth.adRequirements["coll"])
        assertEquals("mls,premierleague,soccer,eflchampionship", configWithBoth.adRequirements["org"])

        adTargeting = mapOf("org" to "mls,premierleague,soccer,eflchampionship")
        val configWithTeams = AdConfig.Builder(mockPreferences, mockConfigClient).setAdTargeting(adTargeting)
            .build(DEFAULT_PAGE_VIEW_ID)
        assertEquals("mls,premierleague,soccer,eflchampionship", configWithTeams.adRequirements["org"])
        assertNull(configWithTeams.adRequirements["coll"])

        adTargeting = mapOf("coll" to "mls,epl,socc,efl")
        val configWithLeagues = AdConfig.Builder(mockPreferences, mockConfigClient).setAdTargeting(adTargeting)
            .build(DEFAULT_PAGE_VIEW_ID)
        assertEquals("mls,epl,socc,efl", configWithLeagues.adRequirements["coll"])
        assertNull(configWithLeagues.adRequirements["org"])
    }

    @Test
    fun `adconfig with uap set`() {
        // Equal adconfig with different sov values
        val equalAdConfig = adConfigBuilder
            .subscriber(true)
            .contentType(ContentType.ARTICLES.type)
            .build(DEFAULT_PAGE_VIEW_ID, true)
        assertEquals(adConfigFixture(uap = "android"), equalAdConfig)
    }

    private fun adConfigFixture(
        subscriber: Boolean? = true,
        uap: String? = null,
        vp: String? = null,
        viewportWidth: Int = 0,
        viewportHeight: Int = 0
    ) = AdConfig(
        adRequirements = HashMap<String, String?>().apply {
            subscriber?.let {
                put("sub", it.toString())
            }
            put("ta_page_view_id", DEFAULT_PAGE_VIEW_ID)
            put("adv", DEFAULT_AD_KEYWORD_VALUE)
            uap?.let {
                put("uap", it)
            }
            vp?.let {
                put("vp", it)
            }
            put("prop", "athdroid")
            put("typ", "art")
            put("plat", "phone")
            put("sov", "2")
        },
        adPrivacy = AdPrivacy(mockPreferences),
        viewport = ViewPort(viewportWidth, viewportHeight)
    )
    companion object {
        private const val DEFAULT_PAGE_VIEW_ID = "page_view_1"
        private const val DEFAULT_AD_KEYWORD_VALUE = "adtest"
        private val adPrivacyCCPAStateList = listOf("CA", "CO", "VA")
        private val adPrivacyGDPRCountryList = listOf(
            "AT", "BE", "BG", "HR", "CY", "CZ", "DK", "EE", "FI", "FR", "DE", "GR", "HU", "IE", "IT", "LV", "LT", "LU",
            "MT", "NL", "PL", "PT", "RO", "SK", "SI", "ES", "SE", "GB", "IS", "LI", "NO", "CH", "BB", "BR", "AE", "BV",
            "GF", "GI", "GP", "MQ", "YT", "RE", "SJ"
        )
    }
}