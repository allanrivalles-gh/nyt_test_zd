package com.theathletic.utility

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.theathletic.AthleticApplication
import com.theathletic.AthleticConfig
import com.theathletic.datetime.Datetime
import com.theathletic.entity.authentication.SubscriptionDataEntity
import com.theathletic.entity.user.UserEntity
import com.theathletic.extension.extLogError
import com.theathletic.extension.get
import com.theathletic.extension.set
import com.theathletic.feed.FeedType
import com.theathletic.followable.Followable
import com.theathletic.utility.AdPreferences.Companion.KEY_AD_KEYWORD
import com.theathletic.utility.AdPreferences.Companion.KEY_AD_PRIVACY_COUNTRY_CODE
import com.theathletic.utility.AdPreferences.Companion.KEY_AD_PRIVACY_ENABLED
import com.theathletic.utility.AdPreferences.Companion.KEY_AD_PRIVACY_STATE
import com.theathletic.utility.ArticlePreferences.Companion.PREF_REFERRER_URI
import com.theathletic.utility.IPreferences.Companion.PREF_HAS_SEEN_WEBVIEW_VERSION_ALERT
import com.theathletic.utility.IPreferences.Companion.SPLIT_DELIMITER
import com.theathletic.utility.PrivacyPreferences.Companion.PREF_PRIVACY_POLICY_UPDATE_LAST_REQUESTED
import com.theathletic.utility.ProfileBadgingPreferences.Companion.PREF_PODCAST_DISCOVER_BADGE_LAST_CLICK
import java.util.Date
import kotlin.collections.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject
import timber.log.Timber

object Preferences :
    IPreferences,
    BillingPreferences,
    AdPreferences,
    AttributionPreferences,
    OnboardingPreferences,
    FeedPreferences,
    LiveBlogPreferences,
    ProfileBadgingPreferences,
    ArticlePreferences,
    PrivacyPreferences,
    FeatureIntroductionPreferences
{
    private val sharedPreferences: SharedPreferences = AthleticApplication.getContext().getSharedPreferences(AthleticConfig.PREFS_NAME, Context.MODE_PRIVATE)
    private val feedRefreshPreferences = AthleticApplication.getContext().getSharedPreferences(
        AthleticConfig.FEED_REFRESH_PREFS_NAME,
        Context.MODE_PRIVATE
    )
    private const val PREF_ACCESS_TOKEN = "pref_access_token"
    private const val PREF_ARTICLES_READ_BY_ANONYMOUS_REACHED_DATE = "PREF_ARTICLES_READ_BY_ANONYMOUS_REACHED_DATE"
    private const val PREF_ARTICLES_RATINGS = "PREF_ARTICLES_RATINGS"
    private const val PREF_GIFTS_PENDING_PAYMENT_DATA_JSON = "pref_gifts_pending_payment_data_json"
    private const val PREF_GOOGLE_SUB_LAST_ARTICLE_ID = "PREF_GOOGLE_SUB_LAST_ARTICLE_ID"
    private const val PREF_GOOGLE_SUB_LAST_PODCAST_ID = "PREF_GOOGLE_SUB_LAST_PODCAST_ID"
    private const val PREF_ATTR_SURVEY_HAS_SEEN_SURVEY = "PREF_ATTR_SURVEY_HAS_SEEN_SURVEY"
    private const val PREF_ATTR_SURVEY_HAS_BEEN_ELIGIBLE = "PREF_ATTR_SURVEY_HAS_BEEN_ELIGIBLE"
    private const val PREF_ATTR_SURVEY_HAS_MADE_PURCHASE = "PREF_ATTR_SURVEY_HAS_MADE_PURCHASE"
    private const val PREF_ATTR_IS_ONBOARDING = "PREF_ATTR_IS_ONBOARDING"
    private const val PREF_ONBOARDING_FOLLOWED_ITEMS = "PREF_ONBOARDING_FOLLOWED_ITEMS"
    private const val PREF_ONBOARDING_FOLLOWED_PODCASTS = "PREF_ONBOARDING_FOLLOWED_PODCASTS"
    private const val PREF_FEED_FINAL_SCORES = "pref_feed_final_scores"
    private const val PREF_USER_FB_LINK_SKIPPED = "pref_user_fb_link_skipped"
    private const val PREF_PUSH_TOKEN_KEY = "pref_push_token_key"
    private const val PREF_KOCHAVA_ARTICLE_ID = "pref_kochava_article_id"
    private const val PREF_KOCHAVA_ARTICLE_DATE = "pref_kochava_article_date"
    private const val PREF_SUBSCRIPTION_DATA = "pref_subscription_data"
    private const val PREF_SUBSCRIPTION_DATA_PRODUCT_ID = "pref_subscription_data_product_id"
    private const val PREF_SUBSCRIPTION_DATA_PURCHASE_TOKEN = "pref_subscription_data_purchase_token"
    private const val PREF_HAS_PURCHASE_HISTORY = "pref_has_purchase_history"
    private const val PREF_PODCAST_LAST_CHECK_DATE = "pref_podcast_last_check_date"
    private const val PREF_PODCAST_LAST_PLAYBACK_SPEED = "pref_podcast_last_playback_speed"
    private const val PREF_FEED_LAST_FETCH_DATE_PREFIX = "pref_feed_last_fetch_ms"
    private const val PREF_LOG_GOOGLE_SUB_LAST_TOKEN = "pref_log_google_sub_last_token"
    private const val PREF_COMMUNITY_LAST_FETCH_DATE = "pref_community_last_fetch_date"
    private const val PREF_USER_DATA_LAST_FETCH_DATE = "pref_user_data_last_fetch_date"
    private const val PREF_LAST_APP_VERSION = "pref_last_app_version"
    private const val PREF_PODCAST_TIMER_SLEEP_TIMESTAMP = "pref_podcast_timer_sleep_timestamp"
    private const val PREF_PINNED_ARTICLES_READ = "pref_pinned_articles_read"
    private const val PREF_IN_APP_UPDATE_LAST_VERSION_DECLINED = "pref_in_app_update_last_version_declined"
    private const val PREF_FOLLOWABLES_ORDER = "pref_user_followables_order"
    private const val PREF_HAS_CUSTOM_FOLLOWABLE_ORDER = "has_custom_followable_order"
    private const val PREF_LAST_SUCCESSFUL_SUBSCRIPTION_PURCHASE_DATE =
        "pref_last_successful_subscription_purchase_date"
    // User Object
    private const val PREF_USER_ENTITY = "pref_user_entity"
    private val moshi =
        Moshi.Builder().add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe()).build()
    private val jsonAdapter: JsonAdapter<UserEntity> = moshi.adapter(UserEntity::class.java)

    // PREF_ACCESS_TOKEN
    override var accessToken: String?
        get() = sharedPreferences[PREF_ACCESS_TOKEN, null as String?]
        set(value) {
            Timber.d("setting access token: $value")
            sharedPreferences[PREF_ACCESS_TOKEN] = value
        }

    override var referrerURI: String?
        get() = sharedPreferences[PREF_REFERRER_URI]
        set(value) {
            sharedPreferences[PREF_REFERRER_URI] = value
        }

    override var adKeyword: String?
        get() = sharedPreferences.getString(KEY_AD_KEYWORD, null)
        set(value) {
            sharedPreferences.edit { putString(KEY_AD_KEYWORD, value) }
        }

    override var privacyEnabled: Boolean
        get() = sharedPreferences.getBoolean(KEY_AD_PRIVACY_ENABLED, true)
        set(value) {
            sharedPreferences.edit { putBoolean(KEY_AD_PRIVACY_ENABLED, value) }
        }

    override var privacyCountryCode: String?
        get() = sharedPreferences.getString(KEY_AD_PRIVACY_COUNTRY_CODE, null)
        set(value) {
            sharedPreferences.edit { putString(KEY_AD_PRIVACY_COUNTRY_CODE, value) }
        }

    override var privacyStateAbbr: String?
        get() = sharedPreferences.getString(KEY_AD_PRIVACY_STATE, null)
        set(value) {
            sharedPreferences.edit { putString(KEY_AD_PRIVACY_STATE, value) }
        }

    override var hasSeenWebViewVersionNotice: Boolean?
        get() = sharedPreferences[PREF_HAS_SEEN_WEBVIEW_VERSION_ALERT]
        set(value) {
            sharedPreferences[PREF_HAS_SEEN_WEBVIEW_VERSION_ALERT] = value
        }

    private val _followablesOrderStateFlow: MutableStateFlow<Map<String, Int>> = MutableStateFlow(followablesOrder)
    override val followablesOrderStateFlow: StateFlow<Map<String, Int>>
        get() = _followablesOrderStateFlow

    override var followablesOrder: Map<String, Int>
        get() = sharedPreferences.getString(PREF_FOLLOWABLES_ORDER, null)
            ?.split(SPLIT_DELIMITER)
            ?.withIndex()
            ?.associate { it.value to it.index } ?: emptyMap()
        set(value) {
            sharedPreferences.edit {
                putString(
                    PREF_FOLLOWABLES_ORDER,
                    value.toList()
                        .sortedBy { it.second }
                        .toMap()
                        .keys
                        .joinToString(SPLIT_DELIMITER)
                )
            }
            _followablesOrderStateFlow.value = value
        }

    override var hasCustomFollowableOrder: Boolean
        get() = sharedPreferences.getBoolean(PREF_HAS_CUSTOM_FOLLOWABLE_ORDER, false)
        set(value) {
            sharedPreferences.edit { putBoolean(PREF_HAS_CUSTOM_FOLLOWABLE_ORDER, value) }
        }

    // PREF_ARTICLES_RATINGS
    override var articlesRatings: HashMap<String, Long>
        get() {
            val outputMap = HashMap<String, Long>()
            try {
                val jsonObject = JSONObject(sharedPreferences[PREF_ARTICLES_RATINGS, (JSONObject()).toString()] ?: "")
                jsonObject.keys().forEach {
                    outputMap[it] = (jsonObject.get(it) as Int).toLong()
                }
            } catch (e: Exception) {
                e.extLogError()
            }
            return outputMap
        }
        set(value) {
            sharedPreferences[PREF_ARTICLES_RATINGS] = JSONObject(value.toMap()).toString()
        }

    // PREF_PUSH_TOKEN_KEY
    override var giftsPendingPaymentDataJson: String?
        get() = sharedPreferences[PREF_GIFTS_PENDING_PAYMENT_DATA_JSON, null as String?]
        set(value) {
            sharedPreferences[PREF_GIFTS_PENDING_PAYMENT_DATA_JSON] = value
        }

    // PREF_LAST_ARTICLE_ID
    override var lastGoogleSubArticleId: Long?
        get() = sharedPreferences[PREF_GOOGLE_SUB_LAST_ARTICLE_ID, null as Long?]
        set(value) {
            sharedPreferences[PREF_GOOGLE_SUB_LAST_ARTICLE_ID] = value
        }

    // PREF_LAST_PODCAST_ID
    override var lastGoogleSubPodcastId: Long?
        get() = sharedPreferences[PREF_GOOGLE_SUB_LAST_PODCAST_ID, null as Long?]
        set(value) {
            sharedPreferences[PREF_GOOGLE_SUB_LAST_PODCAST_ID] = value
        }

    override var hasSeenAttributionSurvey: Boolean
        get() = sharedPreferences[PREF_ATTR_SURVEY_HAS_SEEN_SURVEY, false] ?: false
        set(value) {
            sharedPreferences[PREF_ATTR_SURVEY_HAS_SEEN_SURVEY] = value
        }

    override var hasBeenEligibleForSurvey: Boolean
        get() = sharedPreferences[PREF_ATTR_SURVEY_HAS_BEEN_ELIGIBLE, false] ?: false
        set(value) {
            sharedPreferences[PREF_ATTR_SURVEY_HAS_BEEN_ELIGIBLE] = value
        }

    override var isOnboarding: Boolean
        get() = sharedPreferences[PREF_ATTR_IS_ONBOARDING, false] ?: false
        set(value) {
            sharedPreferences[PREF_ATTR_IS_ONBOARDING] = value
        }
    override var chosenFollowables: List<Followable.Id>
        get() = getListOfStrings(PREF_ONBOARDING_FOLLOWED_ITEMS).mapNotNull { Followable.Id.parse(it) }
        set(value) { setListOfStrings(value.map { it.toString() }, PREF_ONBOARDING_FOLLOWED_ITEMS) }

    override var chosenPodcasts: List<String>
        get() = getListOfStrings(PREF_ONBOARDING_FOLLOWED_PODCASTS)
        set(value) { setListOfStrings(value, PREF_ONBOARDING_FOLLOWED_PODCASTS) }

    private fun getListOfStrings(prefsKey: String) = sharedPreferences.getString(prefsKey, null)
        ?.split(SPLIT_DELIMITER) ?: emptyList()

    private fun setListOfStrings(value: List<String>, prefsKey: String) {
        sharedPreferences.edit {
            putString(
                prefsKey,
                value.joinToString(SPLIT_DELIMITER)
            )
        }
    }

    override var hasMadePurchaseForSurvey: Boolean
        get() = sharedPreferences[PREF_ATTR_SURVEY_HAS_MADE_PURCHASE, false] ?: false
        set(value) {
            sharedPreferences[PREF_ATTR_SURVEY_HAS_MADE_PURCHASE] = value
        }

    override var lastLiveBlogRefreshTimeMs: Long
        get() = sharedPreferences[LiveBlogPreferences.PREF_LAST_REFRESH_TIME_MS, 0L] ?: 0L
        set(value) {
            sharedPreferences[LiveBlogPreferences.PREF_LAST_REFRESH_TIME_MS] = value
        }

    // PREF_USER_FB_LINK_SKIPPED
    var userFBLinkSkipped: Boolean
        get() = sharedPreferences[PREF_USER_FB_LINK_SKIPPED, false] ?: false
        set(value) {
            sharedPreferences[PREF_USER_FB_LINK_SKIPPED] = value
        }

    // PREF_PUSH_TOKEN_KEY
    override var pushTokenKey: String?
        get() = sharedPreferences[PREF_PUSH_TOKEN_KEY, null as String?]
        set(value) {
            sharedPreferences[PREF_PUSH_TOKEN_KEY] = value
        }

    override var kochavaArticleId: String?
        get() = sharedPreferences[PREF_KOCHAVA_ARTICLE_ID, null as String?]
        set(value) {
            sharedPreferences[PREF_KOCHAVA_ARTICLE_ID] = value
        }

    override var kochavaArticleDate: Date?
        get() = sharedPreferences[PREF_KOCHAVA_ARTICLE_DATE, null as Date?]
        set(value) {
            sharedPreferences[PREF_KOCHAVA_ARTICLE_DATE] = value
        }

    override var lastPurchaseDate: Date?
        get() = sharedPreferences[PREF_LAST_SUCCESSFUL_SUBSCRIPTION_PURCHASE_DATE, null as Date?]
        set(value) {
            sharedPreferences[PREF_LAST_SUCCESSFUL_SUBSCRIPTION_PURCHASE_DATE] = value
        }

    override var podcastDiscoverBadgeLastClick: Datetime
        get() {
            val time = sharedPreferences[PREF_PODCAST_DISCOVER_BADGE_LAST_CLICK, 0L]
            return Datetime(time ?: 0L)
        }
        set(value) {
            sharedPreferences[PREF_PODCAST_DISCOVER_BADGE_LAST_CLICK] = value.timeMillis
        }

    // PREF_HAS_PURCHASE_HISTORY
    override var hasPurchaseHistory: Boolean
        get() = sharedPreferences[PREF_HAS_PURCHASE_HISTORY, false] ?: false
        set(value) {
            sharedPreferences[PREF_HAS_PURCHASE_HISTORY] = value
        }

    // PREF_LAST_APP_VERSION
    var lastAppVersionInstalled: String?
        get() = sharedPreferences[PREF_LAST_APP_VERSION, null as String?]
        set(value) {
            sharedPreferences[PREF_LAST_APP_VERSION] = value
        }

    // PREF_PODCAST_LAST_CHECK_DATE
    var podcastLastCheckDate: Date
        get() = sharedPreferences[PREF_PODCAST_LAST_CHECK_DATE, Date().apply { time = 0 }]
            ?: Date().apply { time = 0 }
        set(value) {
            sharedPreferences[PREF_PODCAST_LAST_CHECK_DATE] = value
        }

    // PREF_PODCAST_LAST_PLAYBACK_SPEED
    var lastPodcastPlaybackSpeed: Float
        get() = sharedPreferences[PREF_PODCAST_LAST_PLAYBACK_SPEED, 1.0f] ?: 1.0f
        set(value) {
            sharedPreferences[PREF_PODCAST_LAST_PLAYBACK_SPEED] = value
        }

    // Feed Refresh Data
    override fun setFeedLastFetchDate(feedType: FeedType, timeMs: Long) {
        val key = "$PREF_FEED_LAST_FETCH_DATE_PREFIX:${feedType.compositeId}"
        feedRefreshPreferences[key] = timeMs
    }

    override fun getFeedLastFetchDate(feedType: FeedType): Long {
        val key = "$PREF_FEED_LAST_FETCH_DATE_PREFIX:${feedType.compositeId}"
        return feedRefreshPreferences[key, 0L] ?: 0L
    }

    fun clearFeedRefreshData() {
        feedRefreshPreferences.edit().clear().apply()
    }

    // PREF_LOG_GOOGLE_SUB_LAST_TOKEN
    override var logGoogleSubLastToken: String?
        get() = sharedPreferences[PREF_LOG_GOOGLE_SUB_LAST_TOKEN, null as String?]
        set(value) {
            sharedPreferences[PREF_LOG_GOOGLE_SUB_LAST_TOKEN] = value
        }

    // PREF_COMMUNITY_LAST_FETCH_DATE
    var communityLastFetchDate: Date
        get() = sharedPreferences[PREF_COMMUNITY_LAST_FETCH_DATE, Date().apply { time = 0 }]
            ?: Date().apply { time = 0 }
        set(value) {
            sharedPreferences[PREF_COMMUNITY_LAST_FETCH_DATE] = value
        }

    // PREF_USER_DATA_LAST_FETCH_DATE
    var userDataLastFetchDate: Date
        get() = sharedPreferences[PREF_USER_DATA_LAST_FETCH_DATE, Date().apply { time = 0 }]
            ?: Date().apply { time = 0 }
        set(value) {
            sharedPreferences[PREF_USER_DATA_LAST_FETCH_DATE] = value
        }

    // PREF_PODCAST_TIMER_SLEEP_TIMESTAMP
    var podcastSleepTimestampMillis: Long
        get() = sharedPreferences[PREF_PODCAST_TIMER_SLEEP_TIMESTAMP, -1L] ?: -1L
        set(value) {
            sharedPreferences[PREF_PODCAST_TIMER_SLEEP_TIMESTAMP] = value
        }

    fun clearPodcastSleepTimestamp() {
        podcastSleepTimestampMillis = -1
    }

    // PREF_SUBSCRIPTION_DATA
    override var subscriptionData: SubscriptionDataEntity?
        get() {
            val productId = sharedPreferences[PREF_SUBSCRIPTION_DATA_PRODUCT_ID, null as String?]
            val token = sharedPreferences[PREF_SUBSCRIPTION_DATA_PURCHASE_TOKEN, null as String?]
            return if (productId == null || token == null) null else SubscriptionDataEntity(productId, token)
        }
        set(value) {
            if (value == null) {
                sharedPreferences.edit {
                    remove(PREF_SUBSCRIPTION_DATA)
                    remove(PREF_SUBSCRIPTION_DATA_PRODUCT_ID)
                    remove(PREF_SUBSCRIPTION_DATA_PURCHASE_TOKEN)
                }
            } else {
                sharedPreferences[PREF_SUBSCRIPTION_DATA] = Gson().toJson(value)
                sharedPreferences[PREF_SUBSCRIPTION_DATA_PRODUCT_ID] = value.productId
                sharedPreferences[PREF_SUBSCRIPTION_DATA_PURCHASE_TOKEN] = value.token
            }
        }

    override fun setSubscriptionData(productId: String?, purchaseToken: String?) {
        subscriptionData = if (productId != null && purchaseToken != null) {
            Timber.d("[IAB] SET Product ID: $productId With token: $purchaseToken")
            SubscriptionDataEntity(productId, purchaseToken)
        } else {
            Timber.i("[IAB] No google product found. Clear preferences product!")
            null
        }
    }

    fun getBoolean(key: String): Boolean {
        return sharedPreferences[key, false] ?: false
    }

    override var lastDeclinedUpdateVersionCode: Int
        get() = sharedPreferences[PREF_IN_APP_UPDATE_LAST_VERSION_DECLINED] ?: 0
        set(value) {
            sharedPreferences[PREF_IN_APP_UPDATE_LAST_VERSION_DECLINED] = value
        }

    override var privacyPolicyUpdateLastRequestedDate: Datetime
        get() {
            val time = sharedPreferences[PREF_PRIVACY_POLICY_UPDATE_LAST_REQUESTED, 0L]
            return Datetime(time ?: 0L)
        }
        set(value) {
            sharedPreferences[PREF_PRIVACY_POLICY_UPDATE_LAST_REQUESTED] = value.timeMillis
        }

    override var hasSeenFeatureIntro: Boolean
        get() = sharedPreferences[FeatureIntroductionPreferences.PREF_FEATURE_INTRO_INTRO_VISUALIZED] ?: false
        set(value) {
            sharedPreferences[FeatureIntroductionPreferences.PREF_FEATURE_INTRO_INTRO_VISUALIZED] = value
        }

    override var hasSeenNbaFeatureIntro: Boolean
        get() = sharedPreferences[FeatureIntroductionPreferences.PREF_FEATURE_INTRO_NBA_VISUALIZED] ?: false
        set(value) {
            sharedPreferences[FeatureIntroductionPreferences.PREF_FEATURE_INTRO_NBA_VISUALIZED] = value
        }

    override var hasSeenNhlFeatureIntro: Boolean
        get() = sharedPreferences[FeatureIntroductionPreferences.PREF_FEATURE_INTRO_NHL_VISUALIZED] ?: false
        set(value) {
            sharedPreferences[FeatureIntroductionPreferences.PREF_FEATURE_INTRO_NHL_VISUALIZED] = value
        }

    override var hasSeenTopSportsNewsIntro: Boolean
        get() = sharedPreferences[FeatureIntroductionPreferences.PREF_FEATURE_INTRO_TOP_SPORTS_NEWS_VISUALIZED] ?: false
        set(value) {
            sharedPreferences[FeatureIntroductionPreferences.PREF_FEATURE_INTRO_TOP_SPORTS_NEWS_VISUALIZED] = value
        }

    // Current User
    var currentUser: UserEntity?
        get() {
            UserEntityMigration.migrate(sharedPreferences) { userEntity ->
                currentUser = userEntity
            }
            val userJson: String? = sharedPreferences[PREF_USER_ENTITY]
            return userJson?.let { userEntity ->
                jsonAdapter.fromJson(userEntity)
            }
        }
        set(value) {
            if (value == null) {
                sharedPreferences.edit { remove(PREF_USER_ENTITY) }
            } else {
                val userJson = jsonAdapter.toJson(value)
                sharedPreferences[PREF_USER_ENTITY] = userJson
            }
        }

    fun clear() = sharedPreferences.edit().clear().apply()
}