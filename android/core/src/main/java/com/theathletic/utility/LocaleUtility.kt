package com.theathletic.utility

import java.util.TimeZone

interface LocaleUtility {
    val acceptLanguage: String
    fun isUnitedStatesOrCanada(): Boolean
    fun isUnitedStates(): Boolean
    val privacyRegion: PrivacyRegion
    val deviceTimeZone: TimeZone
}