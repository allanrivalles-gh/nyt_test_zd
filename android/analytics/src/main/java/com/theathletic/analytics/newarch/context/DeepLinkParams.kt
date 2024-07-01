package com.theathletic.analytics.newarch.context

import com.google.gson.annotations.SerializedName

const val KEY_DEEPLINKS_PARAMS = "deeplink_params"

data class DeepLinkParams(
    @SerializedName("source")
    val source: String,
    @SerializedName("meta_blob")
    val metablob: Map<String, String>
) {
    // Tt this mimics encoding of iOS, which is accepted on backend
    fun convertToRequestParameterFormat(): Map<String, String> {
        val deepLinkParameters = mutableMapOf<String, String>()
        deepLinkParameters["$KEY_DEEPLINKS_PARAMS[source]"] = source
        metablob.forEach {
            deepLinkParameters["$KEY_DEEPLINKS_PARAMS[meta_blob][${it.key}]"] = it.value
        }

        return deepLinkParameters
    }
}