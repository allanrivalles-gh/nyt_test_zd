package com.theathletic.navigation

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.theathletic.AthleticConfig
import com.theathletic.R
import com.theathletic.activity.FullscreenPhotoActivity
import com.theathletic.analytics.data.ClickSource
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Exposes
import com.theathletic.attributionsurvey.ui.SurveyActivity
import com.theathletic.attributionsurvey.ui.SurveyAnalyticsContext
import com.theathletic.auth.analytics.AuthenticationNavigationSource
import com.theathletic.billing.BillingManager
import com.theathletic.billing.BillingSku
import com.theathletic.boxscore.ScrollToModule
import com.theathletic.brackets.navigation.BracketsNavigator
import com.theathletic.comments.analytics.CommentsLaunchAction
import com.theathletic.comments.ui.CommentsActivity
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.data.ContentDescriptor
import com.theathletic.debugtools.billingconfig.BillingConfigActivity
import com.theathletic.entity.main.League
import com.theathletic.entity.main.PodcastTopicEntryType
import com.theathletic.entity.main.Sport
import com.theathletic.entity.settings.UserTopicsItemCategory
import com.theathletic.featureintro.ui.FeatureIntroActivity
import com.theathletic.featureswitch.Features
import com.theathletic.feed.FeedType
import com.theathletic.gamedetail.boxscore.ui.injuryreport.BoxScoreInjuryReportActivity
import com.theathletic.gamedetail.playergrades.ui.PlayerGradesDetailActivity
import com.theathletic.gamedetail.ui.GameDetailActivity
import com.theathletic.gifts.ui.GiftSheetDialogFragment
import com.theathletic.hub.HubTabType
import com.theathletic.hub.game.navigation.GameHubNavigator
import com.theathletic.hub.game.ui.GameHubActivity
import com.theathletic.hub.ui.HubActivity
import com.theathletic.links.LinkHelper
import com.theathletic.links.deep.DeeplinkEventProducer
import com.theathletic.liveblog.ui.LiveBlogActivity
import com.theathletic.main.ui.MainActivity
import com.theathletic.main.ui.SearchActivity
import com.theathletic.onboarding.paywall.ui.OnboardingPaywallActivity
import com.theathletic.podcast.analytics.PodcastNavigationSource
import com.theathletic.podcast.browse.BrowsePodcastActivity
import com.theathletic.podcast.downloaded.ui.PodcastDownloadedActivity
import com.theathletic.preferences.ui.NotificationPreferenceActivity
import com.theathletic.preferences.ui.RegionSelectionActivity
import com.theathletic.preferences.ui.UserTopicNotificationsActivity
import com.theathletic.profile.ManageAccountActivity
import com.theathletic.profile.addfollowing.AddFollowingActivity
import com.theathletic.profile.following.ManageFollowingActivity
import com.theathletic.profile.legacy.account.ui.LegacyManageAccountActivity
import com.theathletic.profile.manage.UserTopicId
import com.theathletic.profile.manage.UserTopicType
import com.theathletic.profile.ui.ProfileActivity
import com.theathletic.profile.ui.consent.ConsentWebViewActivity
import com.theathletic.referrals.ReferralsActivity
import com.theathletic.rooms.analytics.LiveRoomAnalyticsContext
import com.theathletic.rooms.analytics.LiveRoomEntryPoint
import com.theathletic.rooms.create.data.local.LiveRoomCreationSearchMode
import com.theathletic.rooms.create.ui.CreateLiveRoomActivity
import com.theathletic.rooms.create.ui.LiveRoomCategoriesActivity
import com.theathletic.rooms.create.ui.LiveRoomTaggingActivity
import com.theathletic.rooms.schedule.ui.ScheduledLiveRoomsActivity
import com.theathletic.rooms.ui.LiveAudioRoomActivity
import com.theathletic.savedstories.ui.SavedStoriesActivity
import com.theathletic.scores.GameDetailTab
import com.theathletic.scores.GameDetailTabParams
import com.theathletic.scores.navigation.ScoresFeedNavigator
import com.theathletic.scores.standings.ui.ScoresStandingsActivity
import com.theathletic.share.ShareTitle
import com.theathletic.slidestories.ui.SlideStoriesActivity
import com.theathletic.subscriptionplans.SubscriptionPlansActivity
import com.theathletic.utility.ActivityUtility
import com.theathletic.utility.getCodeOfConductLink
import com.theathletic.utility.getPrivacyPolicyLink
import com.theathletic.utility.getTermsOfServiceLink

interface ScreenNavigator : BracketsNavigator, ScoresFeedNavigator, GameHubNavigator {
    fun startAuthenticationActivityOnRegistrationScreen(
        navigationSource: AuthenticationNavigationSource
    )

    fun startAuthenticationActivityOnLoginScreen(
        navigationSource: AuthenticationNavigationSource
    )

    fun startAuthenticationActivityOnRegistrationScreenPostPurchase(
        navigationSource: AuthenticationNavigationSource
    )

    fun startMainActivity()

    fun startSearchActivity()

    fun startPlansActivity(
        source: ClickSource,
        articleId: Long = -1,
        liveRoomId: String? = null,
        liveRoomAction: String? = null,
    )

    fun showReferralsActivity(source: String)
    fun showGiftSheetDialog()

    fun showPrivacyPolicy()
    fun showTermsOfService()
    fun showCodeOfConduct()

    fun startManageUserTopicsActivity(
        newTopicId: UserTopicId? = null,
    )

    fun startAddFollowingActivity()
    fun startManageAccountActivity(requestCode: Int? = null)
    fun startUpdateCreditCardActivity(email: String, idHash: String, requestCode: Int)
    fun startSavedStoriesActivity()
    fun startNewsletterPreferencesActivity()
    fun startNotificationPreferencesActivity()
    fun startRegionSelectionActivity()
    fun startUserTopicNotificationFragment(
        entityId: Long,
        type: UserTopicType,
        title: String
    )

    fun startRateAppActivity()
    fun startFaqActivity()
    fun startContactSupport()
    fun startDebugToolsActivity()

    fun startGameDetailMvpActivity(
        gameId: String,
        commentId: String? = null,
        selectedTabParams: GameDetailTabParams = GameDetailTabParams(GameDetailTab.GAME),
        scrollToModule: ScrollToModule = ScrollToModule.NONE,
        source: String? = null,
    )

    fun startArticleActivity(articleId: Long, source: ClickSource)
    fun startArticleActivity(articleId: Long, source: String)
    fun startArticlePaywallActivity(articleId: Long, source: ClickSource)

    fun startHeadlineContainerActivity(newsId: String, source: String?)
    fun startCodeOfConductSheetActivityForResult()
    fun startCreateAccountWallActivity()

    fun startPodcastDetailActivity(
        podcastId: Long,
        source: PodcastNavigationSource
    )

    fun startPodcastEpisodeDetailActivity(
        podcastEpisodeId: Long,
        source: PodcastNavigationSource
    )

    fun startBrowsePodcastActivity(
        categoryId: Long,
        categoryName: String,
        entryType: PodcastTopicEntryType,
    )

    fun startDownloadedPodcastActivity()
    fun startShareTextActivity(
        textToSend: String,
        title: ShareTitle = ShareTitle.DEFAULT,
        shareKey: String? = null
    )

    fun startImageGalleryActivity(
        images: List<String>,
        index: Int
    )

    fun startTopicFeedActivity(id: Long, name: String)
    fun startStandaloneFeedActivity(
        feedType: FeedType,
        title: String? = null,
        initialTab: HubTabType = HubTabType.Home
    )
    fun startCommentsV2Activity(
        contentDescriptor: ContentDescriptor,
        type: CommentsSourceType,
        clickSource: ClickSource,
        launchAction: CommentsLaunchAction? = null
    )

    fun startLiveBlogActivity(id: String, postId: String? = null)

    fun startLiveAudioRoomActivity(
        id: String,
        entryPoint: LiveRoomEntryPoint? = null
    )

    fun startScheduledLiveRoomActivity()
    fun startCreateLiveRoomActivity(roomToEditId: String? = null)
    fun startLiveRoomTaggingActivity(searchMode: LiveRoomCreationSearchMode)
    fun startLiveRoomCategoriesActivity()

    fun startScoresStandingsMvpActivity(league: League, teamId: String? = null)
    fun startInjuryReportActivity(gameId: String, isFirstTeamSelected: Boolean)
    fun startHubActivity(feedType: FeedType, initialTab: HubTabType = HubTabType.Home)
    fun startPlayerGradesDetailActivity(
        gameId: String,
        playerId: String,
        sport: Sport,
        leagueId: String,
        launchedFromGradesTab: Boolean = false
    )

    fun startSlideStories(storiesId: String)

    fun startBillingConfig()
    fun launchBillingFlow(billingManager: BillingManager, sku: BillingSku)
    fun startSurveyActivity(
        analyticsSource: String,
        analyticsObjectType: String,
        analyticsObjectId: Long
    )

    fun startFullscreenPhotoActivity(url: String)
    fun startSendEmailActivity(uri: Uri)
    fun startOpenExternalLink(uri: Uri)
    fun startProfileActivity()
    fun startFeatureIntroActivity()
    fun startAuthenticationActivity(clearTask: Boolean = true)
    fun startOnboardingPaywall()
    fun finishActivity()
    fun finishAffinity()

    fun openLink(url: String)
    fun startConsentWebView(allowBackPress: Boolean)
}

@Exposes(ScreenNavigator::class)
@Suppress("LongParameterList")
class AthleticNavigator @AutoKoin constructor(
    @Assisted val activity: FragmentActivity,
    private val attributionSurveyAnalyticsContext: SurveyAnalyticsContext,
    private val liveRoomAnalyticsContext: LiveRoomAnalyticsContext,
    private val linkHelper: LinkHelper,
    private val deeplinkEventProducer: DeeplinkEventProducer,
    private val features: Features,
) : ScreenNavigator {

    override fun startAuthenticationActivityOnRegistrationScreen(
        navigationSource: AuthenticationNavigationSource
    ) {
        ActivityUtility.startAuthenticationActivityOnRegistrationScreen(
            activity,
            navigationSource
        )
    }

    override fun startAuthenticationActivityOnLoginScreen(
        navigationSource: AuthenticationNavigationSource
    ) {
        ActivityUtility.startAuthenticationActivityOnLoginScreen(
            activity,
            navigationSource
        )
    }

    override fun startAuthenticationActivityOnRegistrationScreenPostPurchase(
        navigationSource: AuthenticationNavigationSource
    ) {
        ActivityUtility.startAuthenticationActivityOnRegistrationScreenPostPurchase(
            activity,
            navigationSource
        )
    }

    override fun startMainActivity() {
        activity.startActivity(MainActivity.newIntent(activity))
    }

    override fun startSearchActivity() {
        activity.startActivity(SearchActivity.newIntent(activity))
    }

    override fun showReferralsActivity(source: String) {
        ReferralsActivity.launch(activity, source)
    }

    override fun showGiftSheetDialog() {
        GiftSheetDialogFragment
            .newInstance()
            .show(activity.supportFragmentManager, "gift_bottom_bar_sheet")
    }

    override fun startPlansActivity(
        source: ClickSource,
        articleId: Long,
        liveRoomId: String?,
        liveRoomAction: String?,
    ) {
        activity.startActivity(
            SubscriptionPlansActivity.newIntent(
                activity,
                source,
                articleId,
                null,
                liveRoomId,
                liveRoomAction,
            )
        )
    }

    override fun showPrivacyPolicy() {
        ActivityUtility.startCustomTabsActivity(activity, getPrivacyPolicyLink())
    }

    override fun showTermsOfService() {
        ActivityUtility.startCustomTabsActivity(activity, getTermsOfServiceLink())
    }

    override fun showCodeOfConduct() {
        ActivityUtility.startCustomTabsActivity(activity, getCodeOfConductLink())
    }

    override fun startManageUserTopicsActivity(
        newTopicId: UserTopicId?,
    ) {
        activity.startActivity(ManageFollowingActivity.newIntent(activity, newTopicId))
    }

    override fun startAddFollowingActivity() {
        activity.startActivity(AddFollowingActivity.newIntent(activity))
    }

    override fun startSavedStoriesActivity() {
        activity.startActivity(
            SavedStoriesActivity.newIntent(activity)
        )
    }

    override fun startManageAccountActivity(requestCode: Int?) {
        val intent = if (features.isComposeAccountSettingsEnabled) {
            ManageAccountActivity.newIntent(activity)
        } else {
            LegacyManageAccountActivity.newIntent(activity)
        }
        if (requestCode == null) {
            activity.startActivity(intent)
        } else {
            activity.startActivityForResult(intent, requestCode)
        }
    }

    override fun startUpdateCreditCardActivity(
        email: String,
        idHash: String,
        requestCode: Int
    ) {
        ActivityUtility.startWebViewActivity(
            activity,
            "https://theathletic.com/update-cc?email=$email&hash=$idHash&redirect_uri=theathletic://",
            requestCode
        )
    }

    override fun startNewsletterPreferencesActivity() {
        ActivityUtility.startNewsletterPreferencesActivity(activity)
    }

    override fun startNotificationPreferencesActivity() {
        activity.startActivity(NotificationPreferenceActivity.newIntent(activity))
    }

    override fun startRegionSelectionActivity() {
        activity.startActivity(RegionSelectionActivity.newIntent(activity))
    }

    override fun startUserTopicNotificationFragment(
        entityId: Long,
        type: UserTopicType,
        title: String
    ) {
        activity.startActivity(
            UserTopicNotificationsActivity.newIntent(activity, entityId, type, title)
        )
    }

    override fun startRateAppActivity() {
        ActivityUtility.startRateAppActivity(activity)
    }

    override fun startFaqActivity() {
        ActivityUtility.startWebViewActivity(activity, AthleticConfig.FAQ_URL)
    }

    override fun startContactSupport() {
        ActivityUtility.startCustomTabsActivity(activity, AthleticConfig.CONTACT_SUPPORT_URL)
    }

    override fun startDebugToolsActivity() {
        ActivityUtility.startDebugToolsActivity(activity)
    }

    override fun startGameDetailMvpActivity(
        gameId: String,
        commentId: String?,
        selectedTabParams: GameDetailTabParams,
        scrollToModule: ScrollToModule,
        source: String?,
    ) {
        if (features.isGameHubNewViewModelEnabled) {
            activity.startActivity(
                GameHubActivity.newIntent(
                    activity,
                    gameId,
                    commentId,
                    selectedTabParams,
                    scrollToModule,
                    source
                )
            )
        } else {
            activity.startActivity(
                GameDetailActivity.newIntent(
                    activity,
                    gameId,
                    commentId,
                    selectedTabParams,
                    scrollToModule,
                    source
                )
            )
        }
    }

    override fun startArticleActivity(articleId: Long, source: String) {
        ActivityUtility.startArticleActivity(activity, articleId, source)
    }

    override fun startArticleActivity(articleId: Long, source: ClickSource) {
        ActivityUtility.startArticleActivity(activity, articleId, source)
    }

    override fun startArticlePaywallActivity(articleId: Long, source: ClickSource) {
        startArticleActivity(articleId, source)
    }

    override fun startHeadlineContainerActivity(newsId: String, source: String?) {
        newsId.toLongOrNull()?.let {
            startArticleActivity(it, source ?: "Unknown")
        }
    }

    override fun startCodeOfConductSheetActivityForResult() {
        ActivityUtility.startCodeOfConductSheetActivityForResult(activity)
    }

    override fun startCreateAccountWallActivity() {
        ActivityUtility.startCreateAccountWallActivity(activity)
    }

    override fun startPodcastDetailActivity(podcastId: Long, source: PodcastNavigationSource) {
        ActivityUtility.startPodcastDetailActivity(activity, podcastId, source)
    }

    override fun startPodcastEpisodeDetailActivity(
        podcastEpisodeId: Long,
        source: PodcastNavigationSource
    ) {
        ActivityUtility.startPodcastEpisodeDetailActivity(activity, podcastEpisodeId, source)
    }

    override fun startBrowsePodcastActivity(
        categoryId: Long,
        categoryName: String,
        entryType: PodcastTopicEntryType
    ) {
        activity.startActivity(
            BrowsePodcastActivity.newIntent(activity, categoryId, categoryName, entryType)
        )
    }

    override fun startDownloadedPodcastActivity() {
        activity.startActivity(PodcastDownloadedActivity.newIntent(activity))
    }

    override fun startShareTextActivity(
        textToSend: String,
        title: ShareTitle,
        shareKey: String?
    ) {
        ActivityUtility.startShareTextActivity(activity, title, textToSend, shareKey)
    }

    override fun startImageGalleryActivity(
        images: List<String>,
        index: Int
    ) {
        ActivityUtility.startImageGalleryActivity(activity, images, index)
    }

    override fun startTopicFeedActivity(id: Long, name: String) {
        ActivityUtility.startStandaloneFeedActivity(
            activity,
            UserTopicsItemCategory(id, name)
        )
    }

    override fun startStandaloneFeedActivity(
        feedType: FeedType,
        title: String?,
        initialTab: HubTabType
    ) {
        when (feedType) {
            is FeedType.Team -> activity.startActivity(
                HubActivity.newIntent(
                    activity,
                    feedType,
                    initialTab
                )
            )
            is FeedType.League -> activity.startActivity(
                HubActivity.newIntent(
                    activity,
                    feedType,
                    initialTab
                )
            )
            else -> ActivityUtility.startStandaloneFeedActivity(
                activity,
                feedType,
                title
            )
        }
    }

    override fun startCommentsV2Activity(
        contentDescriptor: ContentDescriptor,
        type: CommentsSourceType,
        clickSource: ClickSource,
        launchAction: CommentsLaunchAction?
    ) {
        activity.startActivity(
            CommentsActivity.newIntent(
                context = activity,
                contentDescriptor = contentDescriptor,
                type = type,
                isEntryActive = false,
                launchAction = launchAction,
                analyticsPayload = null,
                clickSource = clickSource
            )
        )
    }

    override fun startLiveBlogActivity(id: String, postId: String?) {
        activity.startActivity(
            LiveBlogActivity.newIntent(activity, id, postId)
        )
    }

    override fun startLiveAudioRoomActivity(
        id: String,
        entryPoint: LiveRoomEntryPoint?
    ) {
        if (entryPoint != null && liveRoomAnalyticsContext.roomIdToEntryPoint[id] == null) {
            liveRoomAnalyticsContext.roomIdToEntryPoint[id] = entryPoint
        }
        activity.startActivity(
            LiveAudioRoomActivity.newIntent(activity, id)
        )
    }

    override fun startScheduledLiveRoomActivity() {
        activity.startActivity(
            ScheduledLiveRoomsActivity.newIntent(activity)
        )
    }

    override fun startCreateLiveRoomActivity(roomToEditId: String?) {
        activity.startActivity(
            CreateLiveRoomActivity.newIntent(activity, roomToEditId)
        )
    }

    override fun startLiveRoomTaggingActivity(searchMode: LiveRoomCreationSearchMode) {
        activity.startActivity(
            LiveRoomTaggingActivity.newIntent(activity, searchMode)
        )
    }

    override fun startLiveRoomCategoriesActivity() {
        activity.startActivity(
            LiveRoomCategoriesActivity.newIntent(activity)
        )
    }

    override fun startScoresStandingsMvpActivity(
        league: League,
        teamId: String?
    ) {
        activity.startActivity(
            ScoresStandingsActivity.newIntent(
                activity,
                league,
                teamId
            )
        )
    }

    override fun startInjuryReportActivity(
        gameId: String,
        isFirstTeamSelected: Boolean
    ) {
        activity.startActivity(
            BoxScoreInjuryReportActivity.newIntent(
                activity,
                gameId,
                isFirstTeamSelected
            )
        )
    }

    override fun startHubActivity(feedType: FeedType, initialTab: HubTabType) {
        activity.startActivity(HubActivity.newIntent(activity, feedType, initialTab))
    }

    override fun startPlayerGradesDetailActivity(
        gameId: String,
        playerId: String,
        sport: Sport,
        leagueId: String,
        launchedFromGradesTab: Boolean
    ) {
        activity.startActivity(
            PlayerGradesDetailActivity.newIntent(
                activity,
                gameId,
                playerId,
                sport,
                leagueId,
                launchedFromGradesTab,
            )
        )
    }

    override fun startSlideStories(storiesId: String) {
        activity.startActivity(SlideStoriesActivity.newIntent(activity, storiesId))
        activity.overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.alpha_out)
    }

    override fun startBillingConfig() {
        activity.startActivity(BillingConfigActivity.newIntent(activity))
    }

    override fun launchBillingFlow(billingManager: BillingManager, sku: BillingSku) {
        billingManager.startPurchaseFlow(sku)
    }

    override fun startSurveyActivity(
        analyticsSource: String,
        analyticsObjectType: String,
        analyticsObjectId: Long
    ) {
        attributionSurveyAnalyticsContext.apply {
            clearValues()
            navigationSource = analyticsSource
            referralObjectType = analyticsObjectType
            referralObjectId = analyticsObjectId
        }
        activity.startActivityForResult(
            SurveyActivity.newIntent(activity),
            SurveyActivity.ATTRIBUTION_SURVEY_REQUEST_CODE
        )
    }

    override fun startFullscreenPhotoActivity(url: String) {
        val intent = FullscreenPhotoActivity.newIntent(activity, url)
        activity.startActivity(intent)
    }

    override fun startSendEmailActivity(uri: Uri) {
        Intent(Intent.ACTION_VIEW).apply {
            data = uri
            putExtra(Intent.EXTRA_TEXT, "")

            val manager: PackageManager = activity.packageManager
            val info = manager.queryIntentActivities(this, 0)
            if (info.isNotEmpty()) {
                activity.startActivity(this)
            }
        }
    }

    override fun startOpenExternalLink(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (activity.packageManager.queryIntentActivities(intent, 0).isNotEmpty()) {
            activity.startActivity(intent)
        }
    }

    override fun startProfileActivity() {
        activity.startActivity(ProfileActivity.newIntent(activity))
    }

    override fun startFeatureIntroActivity() {
        activity.startActivity(FeatureIntroActivity.newIntent(activity))
    }

    override fun startAuthenticationActivity(clearTask: Boolean) {
        ActivityUtility.startAuthenticationActivity(activity.baseContext, clearTask)
    }

    override fun startOnboardingPaywall() {
        activity.startActivity(OnboardingPaywallActivity.newIntent(activity))
    }

    override fun finishActivity() {
        activity.finish()
    }

    override fun finishAffinity() {
        activity.finishAffinity()
    }

    override fun navigateToGameDetails(gameId: String) {
        startGameDetailMvpActivity(gameId)
    }

    override fun navigateToHubActivity(feedType: FeedType, initialTab: HubTabType) {
        startHubActivity(feedType, initialTab)
    }

    override fun navigateToGame(
        gameId: String,
        showDiscussion: Boolean,
        view: String
    ) {
        startGameDetailMvpActivity(
            gameId = gameId,
            commentId = null,
            selectedTabParams = if (showDiscussion) {
                GameDetailTabParams(GameDetailTab.DISCUSS)
            } else {
                GameDetailTabParams(GameDetailTab.GAME)
            },
            source = view
        )
    }

    override fun navigateToExternalLink(url: String) {
        startOpenExternalLink(Uri.parse(url))
    }

    override fun gameHubNavigateBack() {
        finishActivity()
    }

    override fun openLink(url: String) {
        if (linkHelper.isAthleticLink(url)) {
            deeplinkEventProducer.tryEmit(url)
        } else {
            startOpenExternalLink(Uri.parse(url))
        }
    }

    override fun startConsentWebView(allowBackPress: Boolean) {
        activity.startActivity(ConsentWebViewActivity.newIntent(activity, allowBackPress))
    }
}