package com.theathletic.utility

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.theathletic.AthleticApplication
import com.theathletic.R
import com.theathletic.activity.ForceUpdateActivity
import com.theathletic.activity.GoogleServicesUnavailableActivity
import com.theathletic.activity.main.StandaloneFeedActivity
import com.theathletic.analytics.data.ClickSource
import com.theathletic.article.ui.ArticleActivity
import com.theathletic.attributionsurvey.ui.SurveyActivity
import com.theathletic.attributionsurvey.ui.SurveyAnalyticsContext
import com.theathletic.auth.AuthenticationActivity
import com.theathletic.auth.AuthenticationActivity.FragmentType.REGISTRATION_OPTIONS
import com.theathletic.auth.CreateAccountWallActivity
import com.theathletic.auth.analytics.AuthenticationAnalyticsContext
import com.theathletic.auth.analytics.AuthenticationNavigationSource
import com.theathletic.billing.SpecialOffer
import com.theathletic.conduct.CODE_OF_CONDUCT_ACTIVITY
import com.theathletic.conduct.CodeOfConductSheetActivity
import com.theathletic.debugtools.DebugToolsActivity
import com.theathletic.entity.main.PodcastTopicEntryType
import com.theathletic.entity.settings.UserTopicsBaseItem
import com.theathletic.extension.extGetColor
import com.theathletic.featureswitches.FeatureSwitches
import com.theathletic.feed.FeedType
import com.theathletic.fragment.main.PodcastDetailActivity
import com.theathletic.fragment.main.PodcastEpisodeDetailActivity
import com.theathletic.main.ui.MainActivity
import com.theathletic.main.ui.SearchActivity
import com.theathletic.onboarding.ui.OnboardingMvpActivity
import com.theathletic.podcast.analytics.PodcastAnalyticsContext
import com.theathletic.podcast.analytics.PodcastNavigationSource
import com.theathletic.podcast.browse.BrowsePodcastActivity
import com.theathletic.podcast.downloaded.ui.PodcastDownloadedActivity
import com.theathletic.preferences.ui.NewsletterPreferencesActivity
import com.theathletic.preferences.ui.UserTopicNotificationsActivity
import com.theathletic.profile.manage.UserTopicType
import com.theathletic.profile.ui.ProfileActivity
import com.theathletic.share.ShareTitle
import com.theathletic.share.asString
import com.theathletic.share.startShareTextActivity
import com.theathletic.subscriptionplans.SubscriptionPlansActivity
import com.theathletic.ui.gallery.ImageGalleryActivity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("LargeClass")
object ActivityUtility : IActivityUtility, KoinComponent {
    private val podcastAnalyticsContext by inject<PodcastAnalyticsContext>()
    private val authenticationAnalyticsContext by inject<AuthenticationAnalyticsContext>()
    private val attributionSurveyAnalyticsContext by inject<SurveyAnalyticsContext>()
    private val featureSwitches by inject<FeatureSwitches>()

    @JvmStatic
    fun startStandaloneFeedActivity(context: Context, topic: UserTopicsBaseItem) {
        startStandaloneFeedActivity(context, FeedType.fromUserTopic(topic), topic.name)
    }

    fun startStandaloneFeedActivity(
        context: Context,
        feedType: FeedType,
        displayTitle: String? = null
    ) {
        val parsedTitle = when {
            displayTitle != null -> displayTitle
            feedType is FeedType.Category -> feedType.name
            else -> null
        }
        val intent = StandaloneFeedActivity.newIntent(context, feedType, parsedTitle)
        if (context is AthleticApplication) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    override fun startSearchActivity(context: Context) {
        context.startActivity(SearchActivity.newIntent(context))
    }

    @JvmStatic
    fun startOnBoardingActivity(context: Context) {
        context.startActivity(OnboardingMvpActivity.newIntent(context))
    }

    override fun startArticleActivity(
        context: Context,
        articleId: Long,
        source: ClickSource
    ) = startArticleActivity(context, articleId, source.value)

    override fun startArticleActivity(
        context: Context,
        articleId: Long,
        source: String
    ) {
        val intent = ArticleActivity.newIntent(context, articleId, source)
        if (context is AthleticApplication) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    @JvmStatic
    fun startCodeOfConductSheetActivityForResult(context: Activity) {
        val intent = CodeOfConductSheetActivity.newIntent(context)
        context.startActivityForResult(intent, CODE_OF_CONDUCT_ACTIVITY)
    }

    @JvmStatic
    fun startMainActivityNewTask(context: Context) {
        val intent = MainActivity.newIntent(context = context)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
    }

    @JvmStatic
    fun startMainActivity(context: Context, intent: Intent) {
        val newIntent = MainActivity.newIntent(context)
        newIntent.putExtras(intent)
        context.startActivity(newIntent)
    }

    @JvmStatic
    fun startAuthenticationActivity(context: Context, clearTask: Boolean = true) {
        val intent = AuthenticationActivity.newIntent(context, AuthenticationActivity.FragmentType.AUTHENTICATION)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        if (clearTask) {
            intent.flags = intent.flags or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        authenticationAnalyticsContext.navigationSource =
            AuthenticationNavigationSource.START_SCREEN
        context.startActivity(intent)
    }

    @JvmStatic
    fun startAuthenticationActivityOnRegistrationScreen(
        context: Context,
        navigationSource: AuthenticationNavigationSource =
            AuthenticationNavigationSource.START_SCREEN
    ) {
        val intent = AuthenticationActivity.newIntent(context, REGISTRATION_OPTIONS)
        authenticationAnalyticsContext.navigationSource = navigationSource
        context.startActivity(intent)
    }

    @JvmStatic
    fun startAuthenticationActivityOnLoginScreen(
        context: Context,
        navigationSource: AuthenticationNavigationSource =
            AuthenticationNavigationSource.START_SCREEN
    ) {
        val intent = AuthenticationActivity.newIntent(context, AuthenticationActivity.FragmentType.LOGIN_OPTIONS)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        authenticationAnalyticsContext.navigationSource = navigationSource
        context.startActivity(intent)
    }

    @JvmStatic
    fun startAuthenticationActivityOnRegistrationScreenPostPurchase(
        context: Activity,
        navigationSource: AuthenticationNavigationSource = AuthenticationNavigationSource.START_SCREEN
    ) {
        val intent = AuthenticationActivity.newIntent(
            context,
            REGISTRATION_OPTIONS,
            finishOnContinue = true,
            isPostPurchase = true
        )
        authenticationAnalyticsContext.navigationSource = navigationSource
        context.startActivityForResult(
            intent,
            AuthenticationActivity.AUTH_RESULT_CODE
        )
    }

    @JvmStatic
    fun startCreateAccountWallActivity(context: Context) {
        val intent = CreateAccountWallActivity.newIntent(context)
        context.startActivity(intent)
    }

    @JvmStatic
    fun startPlansActivity(
        context: Context,
        source: ClickSource,
        articleId: Long = -1,
        specialOffer: SpecialOffer? = null,
    ) {
        context.startActivity(
            SubscriptionPlansActivity.newIntent(
                context,
                source,
                articleId,
                specialOffer,
            )
        )
    }

    override fun startProfileV2Activity(context: Context) {
        val intent = ProfileActivity.newIntent(context)
        context.startActivity(intent)
    }

    override fun startPodcastDownloadedActivity(context: Context) {
        val intent = PodcastDownloadedActivity.newIntent(context)
        context.startActivity(intent)
    }

    fun startPodcastDetailActivity(
        context: Context,
        podcastId: Long,
        source: PodcastNavigationSource
    ) {
        podcastAnalyticsContext.source = source
        val intent = PodcastDetailActivity.newIntent(context, podcastId)
        context.startActivity(intent)
    }

    fun startPodcastEpisodeDetailActivity(
        context: Context,
        podcastEpisodeId: Long,
        source: PodcastNavigationSource
    ) {
        podcastAnalyticsContext.source = source
        val intent = PodcastEpisodeDetailActivity.newIntent(context, podcastEpisodeId)
        context.startActivity(intent)
    }

    fun startBrowsePodcastActivity(
        context: Context,
        topicId: Long,
        topicName: String,
        entryType: PodcastTopicEntryType
    ) {
        val intent = BrowsePodcastActivity.newIntent(context, topicId, topicName, entryType)
        context.startActivity(intent)
    }

    fun startNewsletterPreferencesActivity(context: Context) {
        val intent = NewsletterPreferencesActivity.newIntent(context)
        context.startActivity(intent)
    }

    fun startUserTopicNotificationsFragment(
        context: Context,
        teamId: Long,
        type: UserTopicType,
        title: String
    ) {
        val intent = UserTopicNotificationsActivity.newIntent(context, teamId, type, title)
        context.startActivity(intent)
    }

    @JvmStatic
    fun startGoogleServicesUnavailableActivity(context: Context) {
        val intent = GoogleServicesUnavailableActivity.newIntent(context)
        context.startActivity(intent)
    }

    @JvmStatic
    fun startForceUpdateActivity(context: Context) {
        val intent = ForceUpdateActivity.newIntent(context)
        context.startActivity(intent)
    }

    @JvmStatic
    fun startWebViewActivity(context: Context, url: String) {
        Intent(Intent.ACTION_VIEW).apply {
            val intentData = Uri.parse(url)
            data = intentData

            val manager: PackageManager = context.packageManager
            if (manager.queryIntentActivities(this, 0).isNotEmpty()) {
                context.startActivity(this)
            }
        }
    }

    @JvmStatic
    fun startWebViewActivity(
        activity: Activity,
        url: String,
        requestCode: Int
    ) {
        Intent(Intent.ACTION_VIEW).apply {
            val intentData = Uri.parse(url)
            data = intentData

            val manager: PackageManager = activity.packageManager
            if (manager.queryIntentActivities(this, 0).isNotEmpty()) {
                activity.startActivityForResult(this, requestCode)
            }
        }
    }

    @JvmStatic
    fun startManageSubscriptionActivity(context: Context) {
        // this would bring user right to the managing of subscriptions
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/account/subscriptions?package=${context.packageName}")
        )
        context.startActivity(browserIntent)
    }

    fun startShareTextActivity(
        context: Context,
        title: ShareTitle,
        textToSend: String,
        shareKey: String? = null
    ) {
        startShareTextActivity(
            context,
            title.asString(context),
            textToSend,
            shareKey
        )
    }

    fun startShareTextActivity(
        context: Context,
        title: String,
        textToSend: String,
        shareKey: String? = null
    ) = context.startShareTextActivity(title, textToSend, shareKey)

    fun startBrowserLink(context: Context, url: String) {
        val emptyBrowserIntent = Intent().apply {
            action = Intent.ACTION_VIEW
            addCategory(Intent.CATEGORY_BROWSABLE)
            data = Uri.fromParts("http", "", null)
        }
        val targetIntent = Intent().apply {
            action = Intent.ACTION_VIEW
            addCategory(Intent.CATEGORY_BROWSABLE)
            data = Uri.parse(url)
            selector = emptyBrowserIntent
        }
        context.startActivity(targetIntent)
    }

    @JvmStatic
    fun startCustomTabsActivity(context: Context, url: String) {
        if (ChromeTabsHelper().isChromeTabsAvailable(context)) {
            @Suppress("DEPRECATION")
            val builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(R.color.global_color_primary.extGetColor())

            val tabIntent = builder.build()
            tabIntent.intent.putExtra("EXTRA_DEFAULT_SHARE_MENU_ITEM", false)
            tabIntent.launchUrl(context, Uri.parse(url))
        } else {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

    @JvmStatic
    fun startRateAppActivity(context: Context) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.packageName)))
        } catch (e: android.content.ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)
                )
            )
        }
    }

    @JvmStatic
    fun startDebugToolsActivity(context: Context) {
        val intent = DebugToolsActivity.newIntent(context)
        context.startActivity(intent)
    }

    @JvmStatic
    fun startSendEmailActivity(context: Context, uri: Uri) {
        Intent(Intent.ACTION_VIEW).apply {
            data = uri
            putExtra(Intent.EXTRA_TEXT, "")

            val manager: PackageManager = context.packageManager
            val info = manager.queryIntentActivities(this, 0)
            if (info.isNotEmpty()) {
                context.startActivity(this)
            }
        }
    }

    @JvmStatic
    fun startImageGalleryActivity(
        context: Context,
        images: List<String>,
        index: Int
    ) {
        val intent = ImageGalleryActivity.newIntent(context, images, index)
        context.startActivity(intent)
    }

    override fun startAttributionSurveyActivityForResult(
        context: Activity?,
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
        context?.startActivityForResult(
            SurveyActivity.newIntent(context),
            SurveyActivity.ATTRIBUTION_SURVEY_REQUEST_CODE
        )
    }

    private fun appVersion(context: Context) = context.packageManager.getPackageInfo(context.packageName, 0).versionName
}