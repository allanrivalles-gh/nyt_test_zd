package com.theathletic.ads.data.local

import com.theathletic.utility.AdPreferences

class AdPrivacy(private val adPreferences: AdPreferences) {
    var gdprCountries: List<String> = emptyList()
    var ccpaStates: List<String> = emptyList()
    private val _geo: MutableMap<String, String?> = mutableMapOf()

    val geo: Map<String, String?>
        get() {
            return _geo.toMap()
        }

    fun isGdpr(): Boolean =
        gdprCountries.contains(_geo[GeoKeys.COUNTRY_CODE.key])

    fun isCcpa(): Boolean {
        if (_geo[GeoKeys.COUNTRY_CODE.key] == "US") {
            return ccpaStates.contains(_geo[GeoKeys.STATE_ABBR.key])
        }
        return false
    }

    fun get(key: String): String? = this._geo[key]

    fun isEnabled(): Boolean = adPreferences.privacyEnabled && _geo.isNotEmpty()

    operator fun set(key: String, value: String) {
        _geo[key] = value
    }

    override fun equals(other: Any?): Boolean {
        if (other is AdPrivacy) {
            return this._geo == other._geo
        }
        return false
    }

    override fun hashCode(): Int {
        return _geo.hashCode()
    }
}