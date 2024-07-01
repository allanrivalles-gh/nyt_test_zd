package com.theathletic.links

import android.net.Uri
import com.theathletic.analytics.newarch.context.DeepLinkParams
import com.theathletic.annotation.autokoin.AutoKoin
import timber.log.Timber

private const val NYT_HOST = "nytimes.com"
private const val NYT_HOST_2 = "www.nytimes.com"
private const val TA_HOST = "theathletic.com"

class LinkHelper @AutoKoin constructor() {

    companion object {
        const val BASE_DEEPLINK_SCHEME = "theathletic://"
        const val BASE_IMAGE_SCHEME = "athleticimage://"

        const val KEY_SOURCE = "source"

        const val ATHLETIC_SCHEME = "theathletic"
    }

    enum class AthleticLinkType {
        ITERABLE,
        EXTERNAL,
        ATHLETIC_INTERNAL,
        ATHLETIC_UNIVERSAL,
        NONE
    }

    fun isAthleticLink(url: String): Boolean {
        if (url.startsWith(BASE_IMAGE_SCHEME)) {
            return false
        }
        if (url.startsWith(BASE_DEEPLINK_SCHEME)) {
            return true
        }

        return matchesAthleticLink(url) || matchesAthleticNytLink(url)
    }

    private fun matchesAthleticLink(url: String): Boolean {
        val regex = Regex("https?://(www\\.)?(staging2\\.)?([-a-zA-Z0-9@:%._+~#=]*?)\\.")
        return regex.find(url)?.groups?.get(3)?.value?.let { value ->
            value == ATHLETIC_SCHEME
        } ?: false
    }

    private fun matchesAthleticNytLink(url: String): Boolean {
        val regex = Regex("^(https?://)?(www\\.)?nytimes\\.com/athletic.*")
        return url.matches(regex)
    }

    fun parseUriToDeepLinkParams(uri: Uri): DeepLinkParams? {
        val deeplinkUrlParams = uri.queryParameterNames.associateWith {
            uri.getQueryParameter(it) ?: ""
        }.toMutableMap()

        val deeplinkExternalSource = deeplinkUrlParams.remove(KEY_SOURCE)
        return deeplinkExternalSource?.let { source ->
            DeepLinkParams(source, deeplinkUrlParams)
        }
    }

    fun uriToAthleticLinkType(data: Uri): AthleticLinkType {
        return if (data.host?.startsWith("links") == true) {
            AthleticLinkType.ITERABLE
        } else if (isAthleticLink(data.toString()).not()) {
            AthleticLinkType.EXTERNAL
        } else if (data.toString().isNotBlank()) {
            if (data.scheme == ATHLETIC_SCHEME) {
                val host = data.host
                val path = data.path?.replace("/", "")
                Timber.d("[DeepLink] Handle deepLink host: $host and path: $path")
                AthleticLinkType.ATHLETIC_INTERNAL
            } else {
                AthleticLinkType.ATHLETIC_UNIVERSAL
            }
        } else {
            AthleticLinkType.NONE
        }
    }

    fun parseAthleticUri(weblinkUri: Uri): Uri {
        val isNytHost = weblinkUri.host == NYT_HOST || weblinkUri.host == NYT_HOST_2
        val finalUri = if (isNytHost) {
            Uri.parse(weblinkUri.toString().replace("${weblinkUri.host}/athletic", TA_HOST))
        } else {
            weblinkUri
        }
        return finalUri
    }
}