package com.theathletic.analytics.newarch.context

import android.content.Context
import com.google.gson.Gson
import com.theathletic.extension.set

class ContextInfoPreferences(
    appContext: Context,
    val gson: Gson
) {
    companion object {
        const val PREFERENCES_FILE = "analytics_context_info_preferences"
        const val PREF_ANALYTICS_DEEPLINK_PARAMETERS = "pref_analytics_deeplink_parameters"
    }
    private val sharedPreferences = appContext.getSharedPreferences(
        PREFERENCES_FILE,
        Context.MODE_PRIVATE
    )

    var analyticsDeeplinkParameters: DeepLinkParams?
        get() {
            val getPref = sharedPreferences.getString(PREF_ANALYTICS_DEEPLINK_PARAMETERS, null as String?)
            return getPref?.let {
                gson.fromJson(it, DeepLinkParams::class.java)
            }
        }
        set(value) {
            sharedPreferences[PREF_ANALYTICS_DEEPLINK_PARAMETERS] = gson.toJson(value)
        }
}