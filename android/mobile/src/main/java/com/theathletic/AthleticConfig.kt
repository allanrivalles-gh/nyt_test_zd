package com.theathletic

import com.theathletic.debugtools.DebugPreferences
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object AthleticConfig : KoinComponent {
    private val debugPreferences by lazy { inject<DebugPreferences>() }

    @JvmStatic
    val VERSION_NAME = BuildConfig.VERSION_NAME
    val DEBUG = BuildConfig.DEBUG
    const val BASE_URL_US = "theathletic.com"
    val REST_BASE_URL: String
        get() {
            return if (BuildConfig.DEV_ENVIRONMENT) {
                debugPreferences.value.baseUrlOverride ?: "https://api-staging.$BASE_URL_US/"
            } else {
                "https://api.$BASE_URL_US/"
            }
        }

    val BASE_TWITTER_URL = "https://publish.twitter.com/"
    val BASE_NYTIMES_LOCATION_URL = "https://content.api.nytimes.com/"
    const val LOGS = BuildConfig.LOGS
    const val RETROFIT_LOGS = BuildConfig.LOGS
    val RETROFIT_LOG_LEVEL = HttpLoggingInterceptor.Level.BODY
    const val IS_EXCEPTION_TRACKING_ENABLED = BuildConfig.IS_EXCEPTION_TRACKING_ENABLED
    val DEBUG_TOOLS_ENABLED = BuildConfig.DEBUG_TOOLS_ENABLED
    const val REST_ACCESS_TOKEN = "b68021e1c57be2fc4e66c48937a1a79b"
    const val PREFS_NAME = "TheAthleticsPrefs"
    const val FEED_REFRESH_PREFS_NAME = "FeedRefreshPrefs"
    const val BACKED_UP_PREFS_NAME = "CloudBackedUpPrefs"
    const val HEADER_ATH_PLATFORM_VALUE = "android"

    val GRAPHQL_SERVER_BASE_URL: String
        get() {
            return if (BuildConfig.DEV_ENVIRONMENT) {
                debugPreferences.value.baseGraphQLUrlOverride ?: "https://graphql-staging.$BASE_URL_US/graphql"
            } else {
                "https://graphql.$BASE_URL_US/graphql"
            }
        }
    val GRAPHQL_SUBSCRIPTION_WEBSOCKET_URL: String
        get() {
            return if (BuildConfig.DEV_ENVIRONMENT) {
                "wss://graphql-staging.theathletic.com/gqlsubscriptions"
            } else {
                "wss://graphql.theathletic.com/gqlsubscriptions"
            }
        }

    const val ATHLETIC_SETTINGS = "https://$BASE_URL_US/settings"
    const val PRIVACY_POLICY_URL = "https://privacy.theathletic.com"
    const val CODE_OF_CONDUCT = "https://$BASE_URL_US/code-of-conduct/"
    const val FORGOT_PASSWORD_URL = "https://$BASE_URL_US/forgot-password/?from=mobile"
    const val GOOGLE_SERVICES_PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.google.android.gms&hl=en"
    const val FAQ_URL = "https://theathletic.zendesk.com/hc/en-us"
    const val CONTACT_SUPPORT_URL = "https://theathletic.zendesk.com/hc/en-us/requests/new"
    const val EMAIL_EDITOR_URI = "mailto:editor@theathletic.com"

    // TT Content Configuration
    const val GOOGLE_SERVICES_MIN_VERSION = 1130200 // 11.3.02

    val SITE_URL: String = if (BuildConfig.DEV_ENVIRONMENT) {
        "https://staging2.theathletic.com"
    } else {
        "https://theathletic.com"
    }

    val GOOGLE_AUTHORIZE_URL: String = "$SITE_URL/google-client-login"

    val FB_AUTHORIZE_URL: String = "$SITE_URL/fb-client-login"

    val APPLE_AUTHORIZE_URL: String = "$SITE_URL/apple-client-login"

    val NYT_AUTHORIZE_URL: String = "$SITE_URL/nyt-client-login"

    // TT Constants
    const val ARTICLE_CACHE_DAYS = 3L

    val TRANSCEND_CONSENT_URL: String = if (BuildConfig.DEV_ENVIRONMENT) {
        "https://cdn.transcend.io/cm-test/ee571c7f-030a-41b2-affa-70df8a47b57b/airgap.js"
    } else {
        "https://cdn.transcend.io/cm/ee571c7f-030a-41b2-affa-70df8a47b57b/airgap.js"
    }
}