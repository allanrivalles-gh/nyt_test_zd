package com.theathletic.remoteconfig.local

enum class RemoteConfigEntry(val value: String) {
    FORCE_UPDATE_VERSIONS("android_force_update_versions"),
    CONTENT_DWELL_TIMEOUT_MILLIS("android_content_dwell_timeout_millis"),
    ARTICLE_SCROLL_PERCENT_TO_CONSIDER_READ("article_scroll_percent_to_consider_read"),
    PRIVACY_CCPA_SUPPORTED_STATES("android_privacy_ccpa_supported_states_release"),
    PRIVACY_GDPR_SUPPORTED_COUNTRIES("android_privacy_gdpr_supported_countries_release"),
    FREE_ARTICLES_PER_MONTH_COUNT("android_free_articles_per_month_count_release"),
    ARTICLE_SUBSCRIBER_SCORE_THRESHOLD("android_article_subscriber_score_threshold_release"),
}