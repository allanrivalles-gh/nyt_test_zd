package com.theathletic.ads.data.local

enum class ContentType(val type: String) {
    ARTICLES("art"),
    HOME_PAGE("hp"),
    COLLECTION("coll"),
    LIVE_BLOG("livebl"),
    MATCH_LIVE_BLOG("games");

    companion object {
        fun findByType(value: String?): ContentType {
            return values().firstOrNull { it.type == value } ?: ARTICLES
        }
    }
}