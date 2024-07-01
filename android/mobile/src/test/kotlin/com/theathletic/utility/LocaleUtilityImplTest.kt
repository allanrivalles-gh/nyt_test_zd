package com.theathletic.utility

import android.content.res.Configuration
import android.content.res.Resources
import com.google.common.truth.Truth.assertThat
import com.theathletic.AthleticApplication
import java.util.Locale
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class LocaleUtilityImplTest {
    private lateinit var locale: Locale

    @Before
    fun setUp() {
        val application: AthleticApplication = mock()
        val resources: Resources = mock()
        val configuration: Configuration = mock()
        locale = mock()
        whenever(application.resources).thenReturn(resources)
        whenever(resources.configuration).thenReturn(configuration)
        configuration.locale = locale
        AthleticApplication.setInstance(application)
    }

    @Test
    fun `isGDPRCountry true if CZ`() {
        whenever(locale.country).thenReturn("CZ")
        assertThat(LocaleUtilityImpl.isGDPRCountry()).isTrue()
    }

    @Test
    fun `isGDPRCountry false if US`() {
        whenever(locale.country).thenReturn("US")
        assertThat(LocaleUtilityImpl.isGDPRCountry()).isFalse()
    }

    @Test
    fun `isGDPRCountry true if GB`() {
        whenever(locale.country).thenReturn("GB")
        assertThat(LocaleUtilityImpl.isGDPRCountry()).isTrue()
    }

    @Test
    fun `privacyRegion is UK if GB`() {
        whenever(locale.country).thenReturn("GB")
        assertThat(LocaleUtilityImpl.privacyRegion).isEqualTo(PrivacyRegion.UK)
    }

    @Test
    fun `privacyRegion is Australia if AU`() {
        whenever(locale.country).thenReturn("AU")
        assertThat(LocaleUtilityImpl.privacyRegion).isEqualTo(PrivacyRegion.Australia)
    }

    @Test
    fun `privacyRegion is Canada if CA`() {
        whenever(locale.country).thenReturn("CA")
        assertThat(LocaleUtilityImpl.privacyRegion).isEqualTo(PrivacyRegion.Canada)
    }

    @Test
    fun `privacyRegion is Default if US`() {
        whenever(locale.country).thenReturn("US")
        assertThat(LocaleUtilityImpl.privacyRegion).isEqualTo(PrivacyRegion.Default)
    }
}