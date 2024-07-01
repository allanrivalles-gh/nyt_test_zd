package com.theathletic.featureswitch

interface Features {
    val isAdsEnabled: Boolean
    val isArticleAdsEnabled: Boolean
    val isHomeFeedAdsEnabled: Boolean
    val isLeagueFeedAdsEnabled: Boolean
    val isTeamFeedAdsEnabled: Boolean
    val isLiveBlogAdsEnabled: Boolean
    val isLiveBlogWebViewEnabled: Boolean
    val isNewsTopicAdsEnabled: Boolean
    val isAuthorAdsEnabled: Boolean
    val isDiscoverAdsEnabled: Boolean
    val shouldPreventFeedRefreshOnPush: Boolean
    val isLiveBlogRibbonEnabled: Boolean
    val isDeeplinkForegroundCheckDisabled: Boolean
    val isBoxScoresDiscussTabEnabled: Boolean
    val isComposeFeedEnabled: Boolean
    val isFollowingFeedComposeEnabled: Boolean
    val areTeamSpecificCommentsEnabled: Boolean
    val isCommentDrawerEnabled: Boolean
    val isCommentMultiLevelThreadsEnabled: Boolean
    val isComscoreEnabled: Boolean
    val isScoresNewsroomContentEnabled: Boolean
    val isComposeAccountSettingsEnabled: Boolean
    val isGameHubNewViewModelEnabled: Boolean
    val isNbaGameHubFeatureIntroEnabled: Boolean
    val isNhlGameHubFeatureIntroEnabled: Boolean
    val isExtraSubLoggingEnabled: Boolean
    val isSlideStoriesEnabled: Boolean
    val isBoxScoreTopCommentsEnabled: Boolean
    val isTopSportsNewsNotificationEnabled: Boolean
    val isTopSportsNewsFeatureIntroEnabled: Boolean
    val isTcfConsentEnabled: Boolean
}