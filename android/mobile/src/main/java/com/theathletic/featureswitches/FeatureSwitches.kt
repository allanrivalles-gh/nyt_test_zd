package com.theathletic.featureswitches

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.theathletic.AthleticConfig
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Exposes
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.debugtools.DebugToolsDao
import com.theathletic.featureswitch.Features

/**
 * Simple enum that represent a switchable feature. Use this to hide/show code for given features.
 *
 * NOTE: do not ever add a "_prerelease" feature to the Firebase RemoteConfig Production backend
 */
enum class FeatureSwitch(val key: String) {
    /**
     * Use _feature for feature switches, _ab for AB tests, _config for general configuration.
     */
    APP_RATING_ENABLED("android_feature_app_rating_prompt_release"), // Defaulting to false
    GIFTS_ENABLED("android_feature_gifts_enabled_release"), // Defaulting to false
    ATTRIBUTION_SURVEY("android_attribution_survey_release"), // Defaulting to true
    EXTRA_SUB_LOGGING_ENABLED("android_enable_extra_sub_logging_release"),
    PREFETCH_FEED_DURING_SPLASH("android_feature_prefetch_feed_splash_release"),
    PREVENT_FEED_REFRESH_ON_PUSH("android_feature_prevent_feed_refresh_on_push_release"),
    PRIVACY_REFRESH_DIALOG_ENABLED("android_feature_privacy_refresh_release"),
    WEBVIEW_VERSION_VALIDATOR_ENABLED("android_webview_version_validator_enabled_release"),
    EMBRACE_LOGGING("android_embrace_logging_release"),
    BOX_SCORE_DISCUSS_TAB_ENABLED("android_box_scores_discuss_tab_enabled_release"),
    LIVE_BLOG_RIBBON_ENABLED("android_live_blog_ribbon_enabled_release"),
    LIVE_BLOG_WEBVIEW_ENABLED("android_live_blog_webview_enabled_release"),
    IS_DEEPLINK_FOREGROUND_CHECK_DISABLED("android_deeplink_foreground_check_disabled_release"),
    HARDCODED_NCAAMB_BRACKETS_SEASON("android_hardcoded_ncaamb_brackets_season_prerelease"),
    COMSCORE_ENABLED("android_comscore_enabled_release"),
    PLAYER_GRADES_BASEBALL("android_player_grades_baseball_release"),
    TEAM_SPECIFIC_COMMENTS("android_team_specific_comments_prerelease"),
    TEAM_SPECIFIC_COMMENTS_RELEASE("android_team_specific_comments_release"),
    SCORES_NEWSROOM_CONTENT("android_scores_newsroom_content_prerelease"),
    FEED_COMPOSE("android_feed_compose_prerelease"),
    FOLLOWING_FEED_COMPOSE("android_following_feed_compose_prerelease"),
    ACCOUNT_SETTINGS_COMPOSE("android_account_settings_compose_prerelease"),
    COMMENT_DRAWER("android_comment_drawer_prerelease"),
    GAME_HUB_NEW_VIEW_MODEL("android_game_hub_new_view_model_prerelease"),
    BOX_SCORE_LATEST_NEWS("android_box_score_latest_news_release"),
    NBA_GAME_HUB_FEATURE_INTRO("android_nba_game_hub_feature_intro_release"),
    NHL_GAME_HUB_FEATURE_INTRO("android_nhl_game_hub_feature_intro_release"),
    COMMENT_MULTI_LEVEL_THREADS("android_comment_multi_level_threads_prerelease"),
    SLIDE_STORIES_ENABLED("android_slide_stories_enabled_prerelease"),
    BOX_SCORE_TOP_COMMENTS("android_box_score_top_comments_prerelease"),
    TCF_CONSENT("android_tcf_consent_release"),
    TOP_SPORTS_NEWS_NOTIFICATION_ENABLED("android_top_sports_news_notification_enabled_release"),
    TOP_SPORTS_NEWS_FEATURE_INTRO("android_top_sports_news_notification_intro_release"),

    // Android Ads based feature flags
    ADS_ENABLED("android_ads_enabled_release"),
    ADS_ON_HOME_FEED("android_ads_on_home_feed_release"),
    ADS_ON_ARTICLE("android_ads_on_article_release"),
    ADS_ON_LEAGUE_FEED("android_ads_on_league_feed_release"),
    ADS_ON_TEAM_FEED("android_ads_on_team_feed_release"),
    ADS_ON_LIVE_BLOG("android_ads_on_live_blog_release"),
    ADS_ON_NEWS_TOPIC_FEED("android_ads_on_news_topic_feed_release"),
    ADS_ON_DISCOVER_FEED("android_ads_on_discover_feed_release"),
    ADS_ON_AUTHOR_FEED("android_ads_on_author_feed_release")
}

/**
 * Class that updates and provides values for a set of FeatureSwitches.
 */
class FeatureSwitches @AutoKoin(Scope.SINGLE) constructor(
    val debugToolsDao: DebugToolsDao
) {

    /**
     * Returns the current enabled status of a given feature. Values may change when app is backgrounded/foregrounded.
     *
     * Features are enabled differently across build types:
     *  * DEBUG: all features are enabled
     *  * Staging (non-DEBUG): features are enabled according to values set in Staging Firebase RemoteConfig
     *  * Production: features are enabled according to values set in Production Firebase RemoteConfig
     */
    fun isFeatureEnabled(feature: FeatureSwitch): Boolean {
        return if (AthleticConfig.DEBUG_TOOLS_ENABLED) {
            val remoteConfigDatabaseEntry =
                debugToolsDao.getModifiedRemoteConfigSync()
                    .firstOrNull { it.entryKey == feature.key }
            remoteConfigDatabaseEntry?.entryValue ?: FirebaseRemoteConfig.getInstance()
                .getBoolean(feature.key)
        } else {
            FirebaseRemoteConfig.getInstance().getBoolean(feature.key)
        }
    }
}

/**
 * Class that allows these feature switch states to be access by the API module
 */
@Exposes(Features::class)
class FeaturesImpl @AutoKoin(Scope.SINGLE) constructor(
    private val featuresSwitches: FeatureSwitches
) : Features {
    override val isAdsEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.ADS_ENABLED)
    override val isArticleAdsEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.ADS_ON_ARTICLE)
    override val isHomeFeedAdsEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.ADS_ON_HOME_FEED)
    override val isLeagueFeedAdsEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.ADS_ON_LEAGUE_FEED)
    override val isTeamFeedAdsEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.ADS_ON_TEAM_FEED)
    override val isLiveBlogAdsEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.ADS_ON_LIVE_BLOG)
    override val isNewsTopicAdsEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.ADS_ON_NEWS_TOPIC_FEED)
    override val isAuthorAdsEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.ADS_ON_AUTHOR_FEED)
    override val isDiscoverAdsEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.ADS_ON_DISCOVER_FEED)
    override val shouldPreventFeedRefreshOnPush: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.PREVENT_FEED_REFRESH_ON_PUSH)
    override val isLiveBlogRibbonEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.LIVE_BLOG_RIBBON_ENABLED)
    override val isLiveBlogWebViewEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.LIVE_BLOG_WEBVIEW_ENABLED)
    override val isDeeplinkForegroundCheckDisabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.IS_DEEPLINK_FOREGROUND_CHECK_DISABLED)
    override val isBoxScoresDiscussTabEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.BOX_SCORE_DISCUSS_TAB_ENABLED)
    override val isComposeFeedEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.FEED_COMPOSE)
    override val isFollowingFeedComposeEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.FOLLOWING_FEED_COMPOSE)
    override val areTeamSpecificCommentsEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.TEAM_SPECIFIC_COMMENTS) ||
            featuresSwitches.isFeatureEnabled(FeatureSwitch.TEAM_SPECIFIC_COMMENTS_RELEASE)
    override val isCommentDrawerEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.COMMENT_DRAWER)
    override val isCommentMultiLevelThreadsEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.COMMENT_MULTI_LEVEL_THREADS)
    override val isComscoreEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.COMSCORE_ENABLED)
    override val isScoresNewsroomContentEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.SCORES_NEWSROOM_CONTENT)
    override val isComposeAccountSettingsEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.ACCOUNT_SETTINGS_COMPOSE)
    override val isGameHubNewViewModelEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.GAME_HUB_NEW_VIEW_MODEL)
    override val isNbaGameHubFeatureIntroEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.NBA_GAME_HUB_FEATURE_INTRO)
    override val isNhlGameHubFeatureIntroEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.NHL_GAME_HUB_FEATURE_INTRO)
    override val isExtraSubLoggingEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.EXTRA_SUB_LOGGING_ENABLED)
    override val isSlideStoriesEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.SLIDE_STORIES_ENABLED)
    override val isBoxScoreTopCommentsEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.BOX_SCORE_TOP_COMMENTS)
    override val isTcfConsentEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.TCF_CONSENT)
    override val isTopSportsNewsNotificationEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.TOP_SPORTS_NEWS_NOTIFICATION_ENABLED)
    override val isTopSportsNewsFeatureIntroEnabled: Boolean
        get() = featuresSwitches.isFeatureEnabled(FeatureSwitch.TOP_SPORTS_NEWS_FEATURE_INTRO)
}