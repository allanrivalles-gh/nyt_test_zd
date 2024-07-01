package com.theathletic.analytics.data

enum class ClickSource(val value: String) {
    FEED("Feed"),
    SEARCH("Search"),
    SETTINGS("Settings"),
    SAVED_STORIES("Saved Stories"),
    ARTICLE("Article"),
    RELATED("Related"),
    GAME_DETAIL("Game detail"),
    EVERGREEN("Evergreen"),
    FRONTPAGE("Frontpage"),
    LIVE_ROOM("Live Room"),
    LIVEBLOG_SPONSORED("liveblog_sponsored"),
    PAYWALL("Paywall"),
    HEADLINE("Headline"),
    PODCAST_PAYWALL("Podcast Paywall"),
    PODCAST_STORY("Podcast Story"),
    DEEPLINK("Deeplink"),
    DEEPLINK_USER_SHARED("user_shared_article"),
    DEEPLINK_EMPLOYEE_SHARED("emp_shared_article"),
    PUSH_NOTIFICATION("Push Notification"),
    REFERRED("Referred"),
    NEWS("News"),
    SPOTLIGHT("Spotlight"),
    LIVE_BLOG("Live Blog"),
    UNKNOWN("Unknown"),
}

enum class ContentType(val value: String) {
    ARTICLE("Article"),
    PODCAST("Podcast"),
    PODCAST_EPISODE("Podcast Episode"),
    ARTICLE_COMMENTS("Article Comments"),
    PLANS("Plans")
}

object ObjectType {
    const val ARTICLE_ID = "article_id"
    const val BLOG_ID = "blog_id"
    const val HEADLINE_ID = "headline_id"
    const val GAME_ID = "game_id"
    const val COMMENT_ID = "comment_id"
}