package com.theathletic.ads

import com.theathletic.ads.data.local.AdPrivacy
import com.theathletic.ads.data.local.ContentType
import com.theathletic.ads.data.local.GeoKeys
import com.theathletic.ads.data.local.ViewPort
import com.theathletic.ads.data.local.ViewPortSize
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.extension.extLogError
import com.theathletic.utility.AdPreferences
import kotlin.collections.HashMap
import kotlin.random.Random

class AdConfig internal constructor(
    val adRequirements: Map<String, String?>,
    val adPrivacy: AdPrivacy,
    val viewport: ViewPort,
    private val adUnitPathEndpoint: String? = null
) {

    val adUnitPath: String
        get() {
            return if (adUnitPathEndpoint?.startsWith(BASE_AD_UNIT_PATH) == true) {
                adUnitPathEndpoint
            } else {
                BASE_AD_UNIT_PATH +
                    when (ContentType.findByType(adRequirements[ConfigKeys.CONTENT_TYPE.key])) {
                        ContentType.HOME_PAGE -> HOME_PAGE_UNIT_PATH
                        else -> adUnitPathEndpoint ?: ""
                    }
            }
        }

    val isPrivacyEnabled: Boolean = adPrivacy.isEnabled()

    val viewportSize get() = adRequirements[ConfigKeys.VIEWPORT.key]?.let {
        try {
            ViewPortSize.valueOf(it.uppercase())
        } catch (e: NoSuchElementException) {
            e.extLogError()
            ViewPortSize.SMALL
        }
    } ?: ViewPortSize.SMALL

    val position: String? = adRequirements[ConfigKeys.POSITION.key]

    override fun equals(other: Any?): Boolean {
        if (other is AdConfig) {
            val filteredRequirements = adRequirements.filterKeys { it != ConfigKeys.SHARE_OF_VOICE.key }
            val filteredOther = other.adRequirements.filterKeys { it != ConfigKeys.SHARE_OF_VOICE.key }
            return filteredRequirements == filteredOther &&
                adPrivacy == other.adPrivacy &&
                viewport == other.viewport &&
                adUnitPathEndpoint == other.adUnitPathEndpoint
        }
        return false
    }

    override fun hashCode(): Int {
        return adRequirements.filterKeys { it != ConfigKeys.SHARE_OF_VOICE.key }.hashCode()
    }

    @Suppress("TooManyFunctions")
    class Builder @AutoKoin(scope = Scope.FACTORY) constructor(
        private val adPreferences: AdPreferences,
        private val adConfigClient: AdConfigClient
    ) {
        private var adTargeting = HashMap<String, String?>()
        private var viewportWidth: Int = 0
        private var viewportHeight: Int = 0
        private var adUnitPathEndpoint: String? = null
        private var privacy: AdPrivacy = AdPrivacy(adPreferences)

        fun subscriber(isSubscribed: Boolean) = apply {
            adTargeting[ConfigKeys.SUBSCRIBER.key] = "$isSubscribed"
        }
        fun contentType(type: String?) = apply {
            adTargeting[ConfigKeys.CONTENT_TYPE.key] = type?.normalizeKvp()
        }
        fun viewport(width: Int, height: Int) = apply {
            val viewport = width.fromViewPortSize()
            this.viewportWidth = width
            this.viewportHeight = height
            adTargeting[ConfigKeys.VIEWPORT.key] = viewport.value.normalizeKvp()
        }
        fun setAdTargeting(targeting: Map<String, String?>?) = apply {
            if (targeting.isNullOrEmpty()) {
                return@apply
            }
            // Make sure to normalize any values coming in from ad targeting
            val normalizedTargeting = hashMapOf<String, String?>()
            for ((key, target) in targeting) {
                normalizedTargeting[key] = target?.normalizeKvp()
            }
            adTargeting.putAll(normalizedTargeting)
        }
        fun setGeo(country: String?, state: String? = null) = apply {
            val countryCode = if (adPreferences.privacyCountryCode.isNullOrEmpty()) {
                country
            } else {
                adPreferences.privacyCountryCode
            }
            if (countryCode.isNullOrEmpty()) {
                return@apply
            }
            privacy[GeoKeys.COUNTRY_CODE.key] = countryCode

            if (countryCode == "US") {
                val stateAbbr = if (adPreferences.privacyStateAbbr.isNullOrEmpty()) {
                    state
                } else {
                    adPreferences.privacyStateAbbr
                }
                stateAbbr?.let { privacy[GeoKeys.STATE_ABBR.key] = it }
            }
        }
        fun appVersion(versionName: String) = apply {
            adTargeting[ConfigKeys.VERSION.key] = versionName.normalizeKvp()
        }
        fun setCompassExperiments(adExperiments: List<String>) = apply {
            var experimentString = ""
            adExperiments.forEachIndexed { index, adExperiment ->
                if (index > 0) experimentString += ","
                experimentString += adExperiment
            }
            adTargeting[ConfigKeys.EXPERIMENT.key] = experimentString.normalizeKvp()
        }
        fun setPosition(pos: String?) = apply { adTargeting[ConfigKeys.POSITION.key] = pos }
        fun setAdUnitPath(adUnitPath: String?) = apply { this.adUnitPathEndpoint = adUnitPath?.normalizeKvp() }
        fun setGDPRCountries(countries: List<String>) = apply {
            privacy.gdprCountries = countries
        }
        fun setCCPAStates(states: List<String>) = apply {
            privacy.ccpaStates = states
        }

        fun build(pageViewId: String, shouldSetUserAccessPoint: Boolean = false): AdConfig {
            adTargeting[ConfigKeys.PAGE_VIEW_ID.key] = pageViewId
            adTargeting[ConfigKeys.PLATFORM.key] = adConfigClient.platform
            adTargeting[ConfigKeys.PROPERTY.key] = adConfigClient.property
            adTargeting[ConfigKeys.SHARE_OF_VOICE.key] = "${Random.nextInt(1, 4)}"
            adTargeting[ConfigKeys.AD_KEYWORD.key] = adPreferences.adKeyword
            if (shouldSetUserAccessPoint) {
                adTargeting[ConfigKeys.USER_ACCESS_POINT.key] = UAP_VALUE
            }
            return AdConfig(
                adTargeting,
                privacy,
                ViewPort(viewportWidth, viewportHeight),
                adUnitPathEndpoint
            )
        }
    }

    private enum class ConfigKeys(val key: String) {
        PAGE_VIEW_ID("ta_page_view_id"),
        AD_KEYWORD("adv"),
        PLATFORM("plat"),
        PROPERTY("prop"),
        USER_ACCESS_POINT("uap"),
        SHARE_OF_VOICE("sov"),
        SUBSCRIBER("sub"),
        CONTENT_TYPE("typ"),
        VIEWPORT("vp"),
        VERSION("ver"),
        EXPERIMENT("abra_dfp"),
        POSITION("pos")
    }

    companion object {
        private const val UAP_VALUE = "android"
        private const val BASE_AD_UNIT_PATH = "/29390238/theathletic"
        private const val HOME_PAGE_UNIT_PATH = "/homepage/feed"

        private const val VIEWPORT_MAX_SMALL_WIDTH = 728
        private const val VIEWPORT_MAX_MEDIUM_WIDTH = 969

        fun Int.fromViewPortSize(): ViewPortSize {
            return if (this < VIEWPORT_MAX_SMALL_WIDTH) {
                ViewPortSize.SMALL
            } else if (this in VIEWPORT_MAX_SMALL_WIDTH..VIEWPORT_MAX_MEDIUM_WIDTH) {
                ViewPortSize.MEDIUM
            } else {
                ViewPortSize.LARGE
            }
        }

        fun String.normalizeKvp(): String = this.lowercase().replace(" ", "").replace("-", "")
    }
}