package com.theathletic.utility

import com.theathletic.AthleticApplication
import java.util.TimeZone

object LocaleUtilityImpl : LocaleUtility {
    @Suppress("DEPRECATION")
    private val localeCountry: String
        get() = AthleticApplication.getContext().resources.configuration.locale.country

    private val euCountries = arrayListOf(
        "AT", "BE", "BG", "CY", "CZ", "DE", "DK", "EE", "ES", "FI", "FR", "GB",
        "GR", "HR", "HU", "IE", "IT", "LT", "LU", "LV", "MT", "NL", "PO", "PT", "RO", "SE", "SI", "SK"
    )

    private val usOrCA = listOf("US", "CA")

    override val acceptLanguage: String
        get() {
            @Suppress("DEPRECATION")
            val locale = AthleticApplication.getContext().resources.configuration.locale
            return String.format("%s-%s", locale.language, locale.country)
        }

    fun isGDPRCountry() = euCountries.contains(localeCountry)

    override fun isUnitedStatesOrCanada() = usOrCA.contains(localeCountry)

    override fun isUnitedStates() = localeCountry == "US"

    override val privacyRegion: PrivacyRegion
        get() = when (localeCountry) {
            "GB" -> PrivacyRegion.UK
            "AU" -> PrivacyRegion.Australia
            "CA" -> PrivacyRegion.Canada
            else -> PrivacyRegion.Default
        }

    override val deviceTimeZone: TimeZone
        get() = TimeZone.getDefault()
}