package com.theathletic.user

import android.content.Context
import android.text.format.DateUtils
import android.util.Log
import androidx.databinding.ObservableBoolean
import com.iterable.iterableapi.IterableApi
import com.theathletic.BuildConfig
import com.theathletic.analytics.AnalyticsManager
import com.theathletic.analytics.KochavaWrapper
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.datetime.Chronos
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.GmtStringToDatetimeTransformer
import com.theathletic.debugtools.DebugPreferences
import com.theathletic.entity.authentication.UserPrivilegeLevel
import com.theathletic.entity.settings.UserContentEdition
import com.theathletic.entity.user.SortType
import com.theathletic.entity.user.UserEntity
import com.theathletic.extension.deviceID
import com.theathletic.extension.extLogError
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.manager.PodcastManager
import com.theathletic.manager.UserDataManager
import com.theathletic.manager.UserTopicsManager
import com.theathletic.repository.AthleticRepository
import com.theathletic.repository.safeApiRequest
import com.theathletic.repository.savedstories.SavedStoriesRepository
import com.theathletic.user.IUserManager.Companion.NO_USER
import com.theathletic.user.data.UserRepository
import com.theathletic.utility.ActivityUtility
import com.theathletic.utility.AttributionPreferences
import com.theathletic.utility.BackedUpPreferences
import com.theathletic.utility.IPreferences
import com.theathletic.utility.Preferences
import com.theathletic.utility.PrivacyPreferences
import com.theathletic.utility.logging.ICrashLogHandler
import java.util.Date
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import timber.log.Timber

object UserManager : IUserManager, KoinComponent {
    private val userLoggedIn = ObservableBoolean(getCurrentUser() != null)
    private var currentUser: UserEntity? = null
    private var validateAuthenticationStatusJob: Job? = null
    private val userRepository by inject<UserRepository>()
    private val crashLogHandler by inject<ICrashLogHandler>()
    private val debugPreferences by inject<DebugPreferences>()
    private val preferences by inject<IPreferences>()
    private val privacyPreferences by inject<PrivacyPreferences>()
    private val attributionPreferences by inject<AttributionPreferences>()
    private val analytics by inject<Analytics>()
    private val followableRepository by inject<FollowableRepository>()
    private val applicationContext: Context by inject(named("application-context"))
    private val kochavaWrapper by inject<KochavaWrapper>()
    private val dateTransformer by inject<GmtStringToDatetimeTransformer>()
    private val chronos by inject<Chronos>()

    override fun logOut() {
        analytics.track(Event.User.LogOut)

        saveCurrentUser(null)
        AthleticRepository.clearAllCachedData()
        Preferences.accessToken = null
        Preferences.clearFeedRefreshData()
        Preferences.communityLastFetchDate = Date().apply { time = 0 }
        Preferences.userDataLastFetchDate = Date().apply { time = 0 }
        Preferences.userFBLinkSkipped = false // Reset User FB Link skipped
        privacyPreferences.privacyPolicyUpdateLastRequestedDate = Datetime(0)

        // Tt Release podcast manager if any
        PodcastManager.destroy()

        // Tt Iterable
        IterableApi.getInstance().apply {
            disablePush()
            inAppManager.setAutoDisplayPaused(true)
        }
    }

    fun logOutWithAuthenticationStart() {
        logOut()
        ActivityUtility.startAuthenticationActivity(applicationContext)
    }

    override fun isUserLoggedIn(): Boolean {
        return userLoggedIn.get() && preferences.accessToken?.isNotBlank() == true
    }

    // Ensures an existing user is wiped out if they receive a 401 from one of our endpoints
    fun shouldLogOutOn401() = userLoggedIn.get()

    override fun isUserSubscribed(): Boolean {
        return isSubscribed()
    }

    @JvmStatic
    fun isUserSubscribedStatic(): Boolean {
        return isSubscribed()
    }

    override val isFbLinked get() = getCurrentUser()?.isFbLinked != 0

    override fun isCodeOfConductAccepted() =
        if (BuildConfig.DEBUG_TOOLS_ENABLED && debugPreferences.forceNotAcceptedCodeOfConduct) {
            false
        } else {
            getCurrentUser()?.isCodeOfConductAccepted ?: false
        }

    override fun getCurrentUser(): UserEntity? {
        if (currentUser == null) {
            currentUser = Preferences.currentUser
        }
        return currentUser
    }

    override fun getCurrentUserId(): Long {
        return getCurrentUser()?.id ?: NO_USER
    }

    override val isStaff: Boolean
        get() {
            return getCurrentUser()?.getUserLevel()
                ?.isAtLeastAtLevel(UserPrivilegeLevel.CONTRIBUTOR) ?: false
        }

    override fun getDeviceId(): String = applicationContext.deviceID()

    override fun setUserContentEdition(region: String) {
        val user = getCurrentUser()
        user?.userContentEdition = region
        saveCurrentUser(user, false)
    }

    override fun getUserContentEdition(): UserContentEdition {
        return Preferences.currentUser?.userContentEdition.toUserContentEdition
    }

    fun updateCurrentUser(userEntity: UserEntity) {
        Preferences.currentUser = userEntity
        currentUser = userEntity
        updateAttributionSurveyEligibility(userEntity)
    }

    override fun saveCurrentUser(userEntity: UserEntity?, withRefresh: Boolean) {
        crashLogHandler.setUserInformation(userEntity)

        Preferences.currentUser = userEntity
        currentUser = userEntity
        updateAttributionSurveyEligibility(userEntity)

        userLoggedIn.set(getCurrentUser() != null)

        userEntity?.let { user ->
            if (withRefresh) {
                // We are going to populate both tables for followable items
                followableRepository.fetchFollowableItems()
                UserTopicsManager.loadUserTopics()
                UserDataManager.loadUserData(true)
                SavedStoriesRepository.cacheData()

                // Tt Iterable
                IterableApi.getInstance().setEmail(user.email)
                IterableApi.getInstance().registerForPush()
            }
            BackedUpPreferences.getInstance().lastKnownUserId = userEntity.id
        }

        AnalyticsManager.updateAnalyticsUser()

        // Tt Kochava
        getCurrentUserId().takeIf { it != NO_USER }?.let { userId ->
            kochavaWrapper.tracker.registerIdentityLink("ta_user_id", userId.toString())
        }
    }

    private fun updateAttributionSurveyEligibility(userEntity: UserEntity?) {
        if (userEntity?.isEligibleForAttributionSurvey == true) {
            attributionPreferences.hasBeenEligibleForSurvey = true
        }
    }

    override fun getArticleRating(articleId: Long): Long {
        return Preferences.articlesRatings[articleId.toString()] ?: -1
    }

    override fun addArticleRating(articleId: Long, rating: Long) {
        val hashMap = Preferences.articlesRatings
        hashMap[articleId.toString()] = rating
        Preferences.articlesRatings = hashMap
    }

    override val isAnonymous get() = getCurrentUser()?.isAnonymous ?: true

    fun validateUserAuthenticationStatus(id: Long, coroutineScope: CoroutineScope) {
        if (validateAuthenticationStatusJob?.isActive == true) {
            return
        }

        validateAuthenticationStatusJob = coroutineScope.launch {
            safeApiRequest {
                userRepository.fetchUser(id)
            }.onSuccess { response ->
                when {
                    response.id != getCurrentUserId() -> {
                        crashLogHandler.trackException(
                            ICrashLogHandler.UserException("Error: User login"),
                            "Local user ID:  ${getCurrentUserId()} doesn't match with server response ID: ${response.id}"
                        )
                        logOutWithAuthenticationStart()
                    }
                    response.shouldLogUserOut -> {
                        crashLogHandler.trackException(
                            ICrashLogHandler.UserException("Error: User login"),
                            "should_log_user_out is set to true!"
                        )
                        logOutWithAuthenticationStart()
                    }
                    else -> {
                        response.endDate?.let { date ->
                            if (Date().after(date)) {
                                crashLogHandler.trackException(
                                    ICrashLogHandler.SubscriptionException("Warning: end_date expired"),
                                    "Server end_date is: $date / Current local time is: ${Date()}"
                                )
                            }
                        }
                        // Tt Save user into Shared Preferences
                        saveCurrentUser(response, false)
                    }
                }
            }.onError {
                it.extLogError()
                crashLogHandler.trackException(
                    ICrashLogHandler.UserException("Warning: User login error"),
                    "Error validating user auth status. Reason: ${it.message}",
                    Log.getStackTraceString(it)
                )
            }
        }
    }

    @JvmStatic
    fun refreshUserIfSubscriptionIsAboutToExpire() {
        if (isUserLoggedIn() && isSubscriptionAboutToExpire()) {
            GlobalScope.launch {
                safeApiRequest { userRepository.fetchUser(getCurrentUserId()) }
                    .onSuccess {
                        updateCurrentUser(userEntity = it)
                        Timber.v("[SUBSCRIPTION] Subscription about to expire, refreshed user details")
                    }
            }
        }
    }

    private fun isSubscriptionAboutToExpire(): Boolean {
        currentUser?.let { user ->

            // Has expired and is in the grace period
            if (user.isInGracePeriod) return true

            user.endDate?.let { endDate ->
                val now = Date().time
                return endDate.time >= now - DateUtils.DAY_IN_MILLIS && endDate.time <= now + DateUtils.DAY_IN_MILLIS
            }
        }

        // User is not a subscriber or currentUser object null
        return false
    }

    private fun isSubscribed(): Boolean {
        if (debugPreferences.forceUnsubscribedStatus) return false

        // TT Check Google sub
        if (Preferences.subscriptionData != null) {
            Timber.v("[SUBSCRIPTION] Subscribed: TRUE - Subscribed on Google")
            return true
        }

        // TT Check API end Date
        if (isUserSubscribedOnBackend()) {
            return true
        }

        Timber.v("[SUBSCRIPTION] Subscribed: FALSE - Not subscribed")
        return false
    }

    override fun isUserSubscribedOnBackend(): Boolean {
        val currentUser = getCurrentUser()
        return currentUser?.endDate?.let { endDate ->
            when {
                Date().before(endDate) -> {
                    Timber.v("[SUBSCRIPTION] Subscribed: TRUE - Subscribed on API")
                    true
                }
                currentUser.isInGracePeriod -> {
                    Timber.v("[SUBSCRIPTION] Subscribed: TRUE - User in grace period")
                    true
                }
                else -> false
            }
        } ?: false
    }

    override fun isUserFreeTrialEligible(): Boolean {
        return !Preferences.hasPurchaseHistory
    }

    override fun updateCommentsSortType(
        commentsSourceType: CommentsSourceType,
        sortType: SortType
    ) {
        currentUser?.apply {
            commentsSortType = when (commentsSourceType) {
                CommentsSourceType.HEADLINE -> commentsSortType?.copy(headline = sortType)
                CommentsSourceType.ARTICLE -> commentsSortType?.copy(article = sortType)
                CommentsSourceType.PODCAST_EPISODE -> commentsSortType?.copy(podcast = sortType)
                CommentsSourceType.DISCUSSION -> commentsSortType?.copy(discussion = sortType)
                CommentsSourceType.QANDA -> commentsSortType?.copy(qanda = sortType)
                CommentsSourceType.GAME -> commentsSortType?.copy(game = sortType)
                CommentsSourceType.TEAM_SPECIFIC_THREAD -> commentsSortType?.copy(game = sortType)
            }
        }
    }

    override fun getCommentsSortType(commentsSourceType: CommentsSourceType): SortType {
        return currentUser?.commentsSortType?.let { commentsSortType ->
            when (commentsSourceType) {
                CommentsSourceType.HEADLINE -> commentsSortType.headline
                CommentsSourceType.ARTICLE -> commentsSortType.article
                CommentsSourceType.PODCAST_EPISODE -> commentsSortType.podcast
                CommentsSourceType.DISCUSSION -> commentsSortType.discussion
                CommentsSourceType.QANDA -> commentsSortType.qanda
                CommentsSourceType.GAME -> commentsSortType.game
                CommentsSourceType.TEAM_SPECIFIC_THREAD -> commentsSortType.game
            }
        } ?: SortType.NEWEST
    }

    override fun isUserTempBanned(): Boolean {
        return if (debugPreferences.tempBanEndTime > Date().time) {
            true
        } else {
            currentUser?.tempBanEndTime.isNullOrEmpty().not()
        }
    }

    override fun getBannedDaysLeft(): Int {
        return if (debugPreferences.tempBanEndTime > Date().time) {
            chronos.timeDiff(to = Datetime(debugPreferences.tempBanEndTime)).inDays.toInt()
        } else {
            currentUser?.tempBanEndTime?.let {
                chronos.timeDiff(to = dateTransformer.transform(it)).inDays.toInt()
            } ?: -1
        }
    }

    override fun isCommentRepliesOptIn(): Boolean {
        return getCurrentUser()?.commentsNotification == 1
    }

    override fun isTopSportsNewsOptIn(): Boolean {
        return getCurrentUser()?.topSportsNewsNotification ?: false
    }
}

private val String?.toUserContentEdition: UserContentEdition
    get() = when (this?.uppercase()) {
        "US" -> UserContentEdition.US
        "UK" -> UserContentEdition.UK
        else -> UserContentEdition.US
    }