package com.theathletic.debugtools

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.theathletic.extension.get
import com.theathletic.extension.set
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DebugPreferences(appContext: Context) : KoinComponent {
    companion object {
        const val PREF_FILE = "developer_prefs"
        const val KEY_ENABLE_TOASTS = "key_analytics_toasts_enabled"
        const val KEY_COMPASS_SELECTED_VARIANT_MAP = "key_compass_selected_variant_map"
        const val KEY_FORCE_UNSUBSCRIBED_STATUS = "key_force_unsubscribed_status"
        const val KEY_TEMP_BAN_END_TIME = "key_temp_ban_end_time"
        const val KEY_BASE_URL_OVERRIDE = "key_base_url_override"
        const val KEY_BASE_GRAPHQL_URL_OVERRIDE = "key_base_graphql_url_override"
        const val KEY_SHOW_NOISY_EVENTS = "key_show_noisy_events"
        const val KEY_FEEDV2_DATA_MOCKING = "key_feedv2_data_mocking"
        const val KEY_DISABLE_ARTICLE_CACHING = "key_disable_article_caching"
        const val KEY_ENABLE_DEBUG_BILLING_TOOLS = "key_enable_debug_billing_tools"
        const val KEY_IS_GIFTS_RESPONSE_SUCCESSFUL = "key_is_gifts_response_successful"
        const val KEY_DEBUG_BILLING_CURRENCY = "key_debug_billing_currency"
        const val KEY_DEBUG_CODE_OF_CONDUCT_OVERRIDE = "key_debug_code_of_conduct"
    }

    private val sharedPreferences = appContext.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
    private val gson by inject<Gson>()

    var forceUnsubscribedStatus: Boolean
        get() = sharedPreferences.getBoolean(KEY_FORCE_UNSUBSCRIBED_STATUS, false)
        set(value) {
            sharedPreferences.edit { putBoolean(KEY_FORCE_UNSUBSCRIBED_STATUS, value) }
        }

    var tempBanEndTime: Long
        get() = sharedPreferences.getLong(KEY_TEMP_BAN_END_TIME, 0)
        set(value) {
            sharedPreferences.edit { putLong(KEY_TEMP_BAN_END_TIME, value) }
        }

    var areToastsEnabled: Boolean
        get() = sharedPreferences.getBoolean(KEY_ENABLE_TOASTS, false)
        set(value) {
            sharedPreferences.edit { putBoolean(KEY_ENABLE_TOASTS, value) }
        }

    var showNoisyEvents: Boolean
        get() = sharedPreferences.getBoolean(KEY_SHOW_NOISY_EVENTS, false)
        set(value) {
            sharedPreferences.edit { putBoolean(KEY_SHOW_NOISY_EVENTS, value) }
        }

    var useFeedV2MockedData: Boolean
        get() = sharedPreferences.getBoolean(KEY_FEEDV2_DATA_MOCKING, false)
        set(value) {
            sharedPreferences.edit { putBoolean(KEY_FEEDV2_DATA_MOCKING, value) }
        }

    var disableArticleCaching: Boolean
        get() = sharedPreferences.getBoolean(KEY_DISABLE_ARTICLE_CACHING, false)
        set(value) {
            sharedPreferences.edit { putBoolean(KEY_DISABLE_ARTICLE_CACHING, value) }
        }

    var enableDebugBillingTools: Boolean
        get() = sharedPreferences.getBoolean(KEY_ENABLE_DEBUG_BILLING_TOOLS, false)
        set(value) {
            sharedPreferences.edit { putBoolean(KEY_ENABLE_DEBUG_BILLING_TOOLS, value) }
        }

    var isGiftsResponseSuccessful: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_GIFTS_RESPONSE_SUCCESSFUL, true)
        set(value) {
            sharedPreferences.edit { putBoolean(KEY_IS_GIFTS_RESPONSE_SUCCESSFUL, value) }
        }

    var debugBillingCurrency: String
        get() = sharedPreferences.getString(KEY_DEBUG_BILLING_CURRENCY, "USD") ?: "USD"
        set(value) {
            sharedPreferences.edit { putString(KEY_DEBUG_BILLING_CURRENCY, value) }
        }

    var forceNotAcceptedCodeOfConduct: Boolean
        get() = sharedPreferences.getBoolean(KEY_DEBUG_CODE_OF_CONDUCT_OVERRIDE, false)
        set(value) {
            sharedPreferences.edit { putBoolean(KEY_DEBUG_CODE_OF_CONDUCT_OVERRIDE, value) }
        }

    // TT Map<"Experiment name","Variant name">
    var compassSelectedVariantMap: Map<String, String>
        get() {
            val typeToken = TypeToken.getParameterized(
                HashMap::class.java,
                String::class.java,
                String::class.java
            ).type
            val emptyJSONObjectString = JSONObject().toString()
            val jsonString =
                sharedPreferences[KEY_COMPASS_SELECTED_VARIANT_MAP, emptyJSONObjectString]
                    ?: return HashMap()
            return gson.fromJson(jsonString, typeToken) as HashMap<String, String>
        }
        set(value) {
            sharedPreferences[KEY_COMPASS_SELECTED_VARIANT_MAP] =
                JSONObject(value.toMap()).toString()
        }

    var baseUrlOverride: String?
        get() = sharedPreferences[KEY_BASE_URL_OVERRIDE, null as String?]
        set(value) {
            sharedPreferences[KEY_BASE_URL_OVERRIDE] = value
        }

    var baseGraphQLUrlOverride: String?
        get() = sharedPreferences[KEY_BASE_GRAPHQL_URL_OVERRIDE, null as String?]
        set(value) {
            sharedPreferences[KEY_BASE_GRAPHQL_URL_OVERRIDE] = value
        }
}