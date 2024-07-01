package com.theathletic.links

import android.net.Uri
import timber.log.Timber

fun Uri.paramsMap(): Map<String, String> {
    val parametersMap = mutableMapOf<String, String>()

    parametersMap.putAll(queryParameters)
    parametersMap.putAll(fragmentParameters(separator = "-"))
    parametersMap.putAll(fragmentParameters(separator = "="))
    return parametersMap
}

private val Uri.queryParameters: Map<String, String>
    get() = queryParameterNames.associateWith { getQueryParameter(it).orEmpty() }

private fun Uri.fragmentParameters(separator: String): Map<String, String> {
    val uriFragment = fragment ?: return emptyMap()

    return try {
        val (key, value) = uriFragment.split(separator)
        mapOf(key to value)
    } catch (error: Throwable) {
        Timber.e(error)
        emptyMap()
    }
}