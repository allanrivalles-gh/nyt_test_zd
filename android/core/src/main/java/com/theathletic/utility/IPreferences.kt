package com.theathletic.utility

import com.theathletic.datetime.Datetime
import com.theathletic.entity.authentication.SubscriptionDataEntity
import com.theathletic.feed.FeedType
import com.theathletic.followable.Followable
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

/**
 * Interface that wraps Preferences static methods.
 */
interface IPreferences {
    companion object {
        const val SPLIT_DELIMITER = ";"
        const val PREF_HAS_SEEN_WEBVIEW_VERSION_ALERT = "pref_has_seen_webview_alert"
    }

    var kochavaArticleId: String?
    var kochavaArticleDate: Date?
    var giftsPendingPaymentDataJson: String?
    var articlesRatings: HashMap<String, Long>
    var accessToken: String?
    var logGoogleSubLastToken: String?
    var lastGoogleSubArticleId: Long?
    var lastGoogleSubPodcastId: Long?
    var lastDeclinedUpdateVersionCode: Int
    var hasSeenWebViewVersionNotice: Boolean?
    var followablesOrder: Map<String, Int>
    val followablesOrderStateFlow: StateFlow<Map<String, Int>>
    var hasCustomFollowableOrder: Boolean
    var pushTokenKey: String?
}

interface BillingPreferences {
    var hasPurchaseHistory: Boolean
    var lastPurchaseDate: Date?
    var subscriptionData: SubscriptionDataEntity?
    fun setSubscriptionData(productId: String?, purchaseToken: String?)
}

interface AdPreferences {
    companion object {
        const val KEY_AD_KEYWORD = "key_ad_keyword"
        const val KEY_AD_PRIVACY_ENABLED = "key_ad_privacy_enabled"
        const val KEY_AD_PRIVACY_COUNTRY_CODE = "key_ad_privacy_country_code"
        const val KEY_AD_PRIVACY_STATE = "key_ad_privacy_state"
    }

    var adKeyword: String?
    var privacyCountryCode: String?
    var privacyStateAbbr: String?
    var privacyEnabled: Boolean
}

interface AttributionPreferences {
    var hasSeenAttributionSurvey: Boolean
    var hasBeenEligibleForSurvey: Boolean
    var hasMadePurchaseForSurvey: Boolean
}

interface FeedPreferences {
    fun setFeedLastFetchDate(feedType: FeedType, timeMs: Long)
    fun getFeedLastFetchDate(feedType: FeedType): Long
}

interface OnboardingPreferences {
    var isOnboarding: Boolean
    var chosenFollowables: List<Followable.Id>
    var chosenPodcasts: List<String>
}

interface LiveBlogPreferences {
    companion object {
        const val PREF_LAST_REFRESH_TIME_MS = "pref_live_blog_last_refresh_time"
    }

    var lastLiveBlogRefreshTimeMs: Long
}

interface ProfileBadgingPreferences {
    companion object {
        const val PREF_PODCAST_DISCOVER_BADGE_LAST_CLICK = "pref_podcast_discover_badge_last_click"
    }

    var podcastDiscoverBadgeLastClick: Datetime
}

interface ArticlePreferences {
    companion object {
        const val PREF_REFERRER_URI = "pref_referrer_uri"
    }

    var referrerURI: String?
}

interface PrivacyPreferences {
    companion object {
        const val PREF_PRIVACY_POLICY_UPDATE_LAST_REQUESTED = "pref_privacy_policy_update_last_requested"
    }

    var privacyPolicyUpdateLastRequestedDate: Datetime
}

interface FeatureIntroductionPreferences {
    companion object {
        const val PREF_FEATURE_INTRO_INTRO_VISUALIZED = "pref_nfl_season_game_hub"
        // todo: Adil remove this once both features are done
        const val PREF_FEATURE_INTRO_NBA_VISUALIZED = "pref_nba_season_game_hub"
        const val PREF_FEATURE_INTRO_NHL_VISUALIZED = "pref_nhl_season_game_hub"
        const val PREF_FEATURE_INTRO_TOP_SPORTS_NEWS_VISUALIZED = "pref_top_sports_news_intro"
    }

    var hasSeenFeatureIntro: Boolean
    var hasSeenNbaFeatureIntro: Boolean
    var hasSeenNhlFeatureIntro: Boolean
    var hasSeenTopSportsNewsIntro: Boolean
}