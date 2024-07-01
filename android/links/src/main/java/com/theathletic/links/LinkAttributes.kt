package com.theathletic.links

import android.net.Uri
import com.theathletic.links.deep.DeeplinkType

sealed interface LinkAttributes {
    val first: String?
    val second: String?
    val third: String?
    val fourth: String?
    val parameters: Map<String, String>
    val slug: String?

    data class BaseLinkAttributes(
        override val first: String?,
        override val second: String?,
        override val third: String?,
        override val fourth: String?,
        override val parameters: Map<String, String>,
        override val slug: String?
    ) : LinkAttributes

    data class DeeplinkAttributes(
        val linkAttributes: LinkAttributes
    ) : LinkAttributes by linkAttributes {
        val linkType: DeeplinkType?
            get() = linkAttributes.first?.let {
                DeeplinkType.fromType(it)
            } ?: linkAttributes.second?.let {
                DeeplinkType.fromType(it)
            }
    }

    data class UniversalLinkAttributes(
        val linkAttributes: LinkAttributes
    ) : LinkAttributes by linkAttributes {
        val linkType: UniversalLinkType?
            get() = linkAttributes.first?.let {
                UniversalLinkType.fromType(it)
            } ?: linkAttributes.second?.let {
                UniversalLinkType.fromType(it)
            }
    }
}

fun parseDeeplink(deeplink: Uri): LinkAttributes.DeeplinkAttributes {
    val pathArray = (deeplink.path ?: "").trimStart('/').trimEnd('/').split("/")
    return LinkAttributes.DeeplinkAttributes(
        LinkAttributes.BaseLinkAttributes(
            deeplink.host,
            pathArray.getOrNullIfEmpty(0),
            pathArray.getOrNullIfEmpty(1),
            pathArray.getOrNullIfEmpty(2),
            deeplink.paramsMap(),
            pathArray.last()
        )
    )
}

fun parseAthleticUniversalLink(universalLink: Uri): LinkAttributes.UniversalLinkAttributes {
    val pathArray = (universalLink.path ?: "").trimStart('/').trimEnd('/').split("/")
    return LinkAttributes.UniversalLinkAttributes(
        LinkAttributes.BaseLinkAttributes(
            pathArray.getOrNull(0) ?: "",
            pathArray.getOrNullIfEmpty(1),
            pathArray.getOrNullIfEmpty(2),
            pathArray.getOrNullIfEmpty(3),
            universalLink.paramsMap(),
            pathArray.last()
        )
    )
}

enum class UniversalLinkType(val type: String) {
    OPEN_APP(""),
    PODCAST_FEED("podcasts"),
    PODCAST("podcast"),
    DISCUSSIONS("discussions"),
    LIVE_DISCUSSIONS("livediscussions"),
    HEADLINE("headline"),
    FRONTPAGE("frontpage"),
    REACTIONS("reactions"),
    GIFT("gift"),
    SHARE("share"),
    LIVE_BLOGS("live-blogs"),
    LIVE_ROOMS("live-rooms"),
    CULTURE("culture"),
    MANAGE_TEAMS("manage-teams"),
    GAME("game"),
    ARTICLE("article"),
    TAG_FEED("tag");

    companion object {
        fun fromType(type: String): UniversalLinkType? = values().firstOrNull {
            it.type == type
        }
    }
}

private fun List<String>.getOrNullIfEmpty(index: Int): String? {
    val value = getOrNull(index)
    return if (value.isNullOrBlank()) null else value
}

/**
 * Some links support an optional middle path section that contains title info
 */
internal fun LinkAttributes.lastPathOrNull() =
    listOf(fourth, third, second).firstOrNull { !it.isNullOrBlank() }