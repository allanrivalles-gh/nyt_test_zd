package com.theathletic.ads.data.local

import com.google.common.truth.Truth.assertThat
import com.theathletic.utility.AdPreferences
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class AdPrivacyTest {
    @Mock lateinit var mockAdPreferences: AdPreferences

    private lateinit var adPrivacy: AdPrivacy

    private val gdprCountries = listOf("DE", "FR", "GB")
    private val ccpaStates = listOf("CA", "CO", "VA")

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        whenever(mockAdPreferences.privacyEnabled).thenReturn(true)
        adPrivacy = AdPrivacy(mockAdPreferences)
        adPrivacy.gdprCountries = gdprCountries
        adPrivacy.ccpaStates = ccpaStates
    }

    @Test
    fun `verify gdpr check`() {
        assertThat(adPrivacy.isGdpr()).isFalse()

        adPrivacy[GeoKeys.COUNTRY_CODE.key] = "FR"
        assertThat(adPrivacy.isGdpr()).isTrue()
    }

    @Test
    fun `verify ccpa check`() {
        assertThat(adPrivacy.isCcpa()).isFalse()

        adPrivacy[GeoKeys.COUNTRY_CODE.key] = "US"
        adPrivacy[GeoKeys.STATE_ABBR.key] = "CA"
        assertThat(adPrivacy.isCcpa()).isTrue()
    }

    @Test
    fun `verify ccpa check fails if country not US`() {
        assertThat(adPrivacy.isCcpa()).isFalse()

        adPrivacy[GeoKeys.COUNTRY_CODE.key] = "FR"
        adPrivacy[GeoKeys.STATE_ABBR.key] = "CA"
        assertThat(adPrivacy.isCcpa()).isFalse()
    }

    @Test
    fun `verify privacy enabled with geo set`() {
        assertThat(adPrivacy.isEnabled()).isFalse()

        adPrivacy[GeoKeys.COUNTRY_CODE.key] = "FR"
        assertThat(adPrivacy.isEnabled()).isTrue()
    }

    @Test
    fun `verify privacy is disabled when turned off in ad preferences`() {
        whenever(mockAdPreferences.privacyEnabled).thenReturn(false)
        assertThat(adPrivacy.isEnabled()).isFalse()

        adPrivacy[GeoKeys.COUNTRY_CODE.key] = "FR"
        assertThat(adPrivacy.isEnabled()).isFalse()
    }
}