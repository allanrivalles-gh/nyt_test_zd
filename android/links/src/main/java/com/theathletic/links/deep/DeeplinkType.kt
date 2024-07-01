package com.theathletic.links.deep

// This represents the first value that comes as part of the deeplink
// e.g. the "article" portion of "theathletic://article/id"
enum class DeeplinkType(val type: String) {
    OPEN_APP(""),
    ARTICLE("article"),
    LEAGUE("league"),
    TEAM("team"),
    AUTHOR("author"),
    CATEGORY("category"),
    LISTEN_DISCOVER("listendiscover"),
    LISTEN_FOLLOWING("listenfollowing"),
    HEADLINE("headline"),
    HEADLINE_WIDGET("headline-widget"),
    HEADLINE_WIDGET_HEADER("headline-widget-header"),
    REACTIONS("reactions"),
    FRONTPAGE("frontpage"),
    SCORES("scores"),
    DISCUSSIONS("discussions"),
    LIVE_DISCUSSIONS("livediscussions"),
    PODCAST_FEED("podcasts"),
    PODCAST("podcast"),
    GIFT("gift"),
    PLANS("plans"),
    SHARE("share"),
    SETTINGS("settings"),
    REGISTER("register"),
    LOGIN("login"),
    BOXSCORE("boxscore"),
    LIVE_BLOGS("live-blogs"),
    LIVE_ROOMS("live-rooms"),
    MANAGE_TEAMS("manage-teams"),
    FEED("feed"),
    TAG_FEED("tag"),
    ACCOUNT_SETTINGS("account_settings"),
    NOTIFICATION_SETTINGS("notification_settings"),
    EMAIL_SETTINGS("email_settings");

    companion object {
        fun fromType(type: String): DeeplinkType? = values().firstOrNull { it.type == type }
    }

    override fun toString(): String {
        return type
    }
}