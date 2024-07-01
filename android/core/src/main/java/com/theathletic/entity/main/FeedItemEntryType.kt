package com.theathletic.entity.main

enum class FeedItemEntryType(val value: String) {
    ARTICLE("article"),
    ARTICLE_HERO("article-hero"),
    ARTICLE_FEATURED("article-featured"),
    ARTICLE_FRANCHISE("franchise_article"),
    COMMENTS("topic"),
    USER_DISCUSSION("userdiscussion"),
    LIVE_DISCUSSION("livediscussion"),
    PODCAST_EPISODE("podcast_episode"),
    UNKNOWN("unknown");

    companion object {
        fun from(value: String?): FeedItemEntryType = values().firstOrNull { it.value == value }
            ?: UNKNOWN
    }
}