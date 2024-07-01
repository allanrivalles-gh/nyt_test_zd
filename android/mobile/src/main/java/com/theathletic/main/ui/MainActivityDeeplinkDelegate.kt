package com.theathletic.main.ui

import android.content.Context
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.theathletic.R
import com.theathletic.activity.BaseActivity
import com.theathletic.analytics.data.ClickSource
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.auth.analytics.AuthenticationNavigationSource
import com.theathletic.comments.analytics.CommentsLaunchAction
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.data.ContentDescriptor
import com.theathletic.featureswitches.FeatureSwitch
import com.theathletic.featureswitches.FeatureSwitches
import com.theathletic.feed.FeedType
import com.theathletic.gifts.ui.GiftSheetDialogFragment
import com.theathletic.links.LinkParser
import com.theathletic.links.deep.DeeplinkDestination
import com.theathletic.links.deep.DeeplinkEventProducer
import com.theathletic.main.DeeplinkThrottle
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.notification.NotificationOption
import com.theathletic.podcast.analytics.PodcastNavigationSource
import com.theathletic.preferences.ui.UpdateTopSportNewsNotificationUseCase
import com.theathletic.scores.GameDetailTab
import com.theathletic.scores.GameDetailTabParamKey
import com.theathletic.scores.GameDetailTabParams
import com.theathletic.themes.AthColor
import com.theathletic.utility.ActivityUtility
import com.theathletic.utility.logging.ICrashLogHandler
import timber.log.Timber

class MainActivityDeeplinkDelegate @AutoKoin(Scope.SINGLE) constructor(
    @Assisted private val navigator: ScreenNavigator,
    private val deeplinkThrottle: DeeplinkThrottle,
    private val deeplinkEventProducer: DeeplinkEventProducer,
    private val linkParser: LinkParser,
    private val featureSwitches: FeatureSwitches,
    private val crashLogHandler: ICrashLogHandler,
    private val slugToTagFeedUseCase: SlugToTagFeedUseCase,
    private val getHeadlineArticleIdUseCase: GetHeadlineArticleIdUseCase,
    private val getPodcastEpisodeIdUseCase: GetPodcastEpisodeIdUseCase,
    private val getLiveBlogGameIdUseCase: GetLiveBlogGameIdUseCase,
    private val updateTopSportNewsNotificationUseCase: UpdateTopSportNewsNotificationUseCase,
    private val mainNavigationEventProducer: MainNavigationEventProducer,
) {
    @Suppress("LongMethod", "ComplexMethod")
    suspend fun parseDeeplink(
        activity: BaseActivity,
        uri: String,
        selectPrimaryTab: (tab: BottomTabItem, initialTabIndex: Int?, skipAnalyticsEvent: Boolean) -> Unit,
    ) {
        deeplinkThrottle.startThrottle()

        when (val destination = linkParser.parseDeepLink(Uri.parse(uri))) {
            is DeeplinkDestination.Article -> ActivityUtility.startArticleActivity(
                activity,
                destination.articleId,
                destination.source
            )

            is DeeplinkDestination.StandaloneFeedLeague -> navigator.startStandaloneFeedActivity(
                FeedType.League(destination.id),
                destination.name,
                destination.initialTab
            )
            is DeeplinkDestination.StandaloneFeedTeam -> navigator.startStandaloneFeedActivity(
                FeedType.Team(destination.id),
                destination.name,
                destination.initialTab
            )
            is DeeplinkDestination.StandaloneFeedAuthor -> navigator.startStandaloneFeedActivity(
                FeedType.Author(destination.id),
                destination.name
            )
            is DeeplinkDestination.StandaloneFeedCategory -> navigator.startStandaloneFeedActivity(
                FeedType.Category(destination.id, destination.name)
            )

            DeeplinkDestination.PodcastFeed -> selectPrimaryTab(BottomTabItem.LISTEN, null, true)

            DeeplinkDestination.Scores -> selectPrimaryTab(BottomTabItem.SCORES, null, false)

            is DeeplinkDestination.Podcast -> ActivityUtility.startPodcastDetailActivity(
                activity,
                destination.podcastId,
                PodcastNavigationSource.HOME
            )

            is DeeplinkDestination.PodcastEpisode -> startPodcastEpisode(activity, destination)

            is DeeplinkDestination.Headline -> startHeadlineContainerActivity(destination)

            is DeeplinkDestination.HeadlineAppWidget -> mainNavigationEventProducer.emit(MainNavigationEvent.ScrollToTopHeadlines)

            DeeplinkDestination.GiftPurchase -> {
                if (featureSwitches.isFeatureEnabled(FeatureSwitch.GIFTS_ENABLED)) {
                    GiftSheetDialogFragment.newInstance()
                        .show(activity.supportFragmentManager, "gift_bottom_bar_sheet")
                } else {
                    Timber.v("Gifts not enabled")
                }
            }
            is DeeplinkDestination.Plans -> ActivityUtility.startPlansActivity(
                context = activity,
                source = ClickSource.DEEPLINK,
                specialOffer = destination.offer
            )

            is DeeplinkDestination.Share -> navigator.showReferralsActivity("deeplink")
            is DeeplinkDestination.Settings -> selectPrimaryTab(BottomTabItem.ACCOUNT, null, false)
            is DeeplinkDestination.CreateAccount -> navigator.startAuthenticationActivityOnRegistrationScreen(
                AuthenticationNavigationSource.PROFILE
            )
            is DeeplinkDestination.Login -> navigator.startAuthenticationActivityOnLoginScreen(
                AuthenticationNavigationSource.PROFILE
            )
            is DeeplinkDestination.External ->
                ActivityUtility.startCustomTabsActivity(activity, destination.url)
            is DeeplinkDestination.Universal ->
                ActivityUtility.startBrowserLink(activity, destination.url)

            is DeeplinkDestination.Frontpage -> selectPrimaryTab(BottomTabItem.DISCOVER, null, false)
            is DeeplinkDestination.ListenDiscover -> selectPrimaryTab(BottomTabItem.LISTEN, 1, false)
            is DeeplinkDestination.ListenFollowing -> selectPrimaryTab(BottomTabItem.LISTEN, 0, false)
            is DeeplinkDestination.TagFeed -> startStandaloneTagFeed(destination.slug)
            is DeeplinkDestination.MatchCentre -> navigator.startGameDetailMvpActivity(
                gameId = destination.gameId,
                scrollToModule = destination.scrollToModule
            )
            is DeeplinkDestination.Comments -> {
                // No content title is available on deeplink; we could attempt to fetch one from the article
                // repository in the future if the need arises, but there's no guarantee the repository will
                // have a title
                val contentDescriptor = ContentDescriptor(destination.sourceId)

                navigator.startCommentsV2Activity(
                    contentDescriptor = contentDescriptor,
                    type = destination.sourceType,
                    clickSource = destination.clickSource,
                    launchAction = CommentsLaunchAction.View(destination.commentId)
                )
            }
            is DeeplinkDestination.LiveBlog -> startLiveBlog(destination)
            is DeeplinkDestination.LiveRoom -> {
                navigator.startLiveAudioRoomActivity(destination.id, destination.entryPoint)
            }
            is DeeplinkDestination.ManageTopics -> navigator.startManageUserTopicsActivity(
                destination.userTopicId
            )
            is DeeplinkDestination.FeedSecondaryTab -> {
                navigator.startStandaloneFeedActivity(destination.feedType)
            }
            is DeeplinkDestination.AccountSettings -> {
                navigator.startProfileActivity()
                navigator.startManageAccountActivity()
            }
            is DeeplinkDestination.EmailSettings -> {
                navigator.startProfileActivity()
                navigator.startNewsletterPreferencesActivity()
            }
            is DeeplinkDestination.NotificationSettings -> {
                navigator.startProfileActivity()
                navigator.startNotificationPreferencesActivity()
            }
            is DeeplinkDestination.NotificationOptIn -> {
                changeNotificationOptIn(destination, activity)
            }
            is DeeplinkDestination.RegionSettings -> {
                navigator.startProfileActivity()
                navigator.startRegionSelectionActivity()
            }
            is DeeplinkDestination.GameDetails -> {
                navigator.startGameDetailMvpActivity(
                    gameId = destination.gameId,
                    commentId = destination.commentId,
                    selectedTabParams = GameDetailTabParams(destination.selectedTab)
                )
            }
            is DeeplinkDestination.OpenApp -> { /* No-op - just opening the app */
            }
            is DeeplinkDestination.None -> {
                Timber.v("Deeplink - do nothing")
                crashLogHandler.trackException(
                    ICrashLogHandler.DeeplinkException("Error: Deeplink with no destination"),
                    "Deeplink: $uri resulted in a destination of None"
                )
            }
        }
    }

    private suspend fun startLiveBlog(destination: DeeplinkDestination.LiveBlog) {
        getLiveBlogGameIdUseCase(destination.id).onSuccess { liveBlogGameId ->
            if (liveBlogGameId == null) {
                navigator.startLiveBlogActivity(destination.id, destination.postId)
            } else {
                navigator.startGameDetailMvpActivity(
                    gameId = liveBlogGameId,
                    selectedTabParams = GameDetailTabParams(
                        GameDetailTab.LIVE_BLOG,
                        mapOf(
                            GameDetailTabParamKey.LiveBlogId to destination.id,
                            GameDetailTabParamKey.PostId to destination.postId
                        )
                    )
                )
            }
        }
    }

    private suspend fun startHeadlineContainerActivity(
        destination: DeeplinkDestination.Headline
    ) {
        getHeadlineArticleIdUseCase(destination.id).onSuccess { headlineId ->
            navigator.startHeadlineContainerActivity(
                headlineId,
                destination.source
            )
        }
    }

    private suspend fun startStandaloneTagFeed(slug: String) {
        slugToTagFeedUseCase(slug).onSuccess { feedTag ->
            navigator.startStandaloneFeedActivity(
                feedType = FeedType.Tag(feedTag.id),
                title = feedTag.title
            )
        }
    }

    private suspend fun startPodcastEpisode(
        context: Context,
        destination: DeeplinkDestination.PodcastEpisode
    ) {
        val podcastId = if (destination.episodeId == -1L) {
            getPodcastEpisodeIdUseCase(
                destination.podcastId,
                destination.episodeNumber
            ).getOrNull()
        } else {
            destination.episodeId
        }

        podcastId?.let {
            startPodcastEpisode(context, podcastId)
            if (destination.commentId.isBlank().not()) {
                startPodcastEpisodeComments(destination.copy(episodeId = podcastId))
            }
        }
    }

    private fun startPodcastEpisode(context: Context, podcastEpisodeId: Long) {
        ActivityUtility.startPodcastEpisodeDetailActivity(
            context,
            podcastEpisodeId,
            PodcastNavigationSource.HOME
        )
    }

    private fun startPodcastEpisodeComments(destination: DeeplinkDestination.PodcastEpisode) {
        navigator.startCommentsV2Activity(
            contentDescriptor = ContentDescriptor(destination.episodeId.toString()),
            type = CommentsSourceType.PODCAST_EPISODE,
            clickSource = ClickSource.DEEPLINK,
            launchAction = CommentsLaunchAction.View(destination.commentId)
        )
    }

    private suspend fun changeNotificationOptIn(
        destination: DeeplinkDestination.NotificationOptIn,
        activity: Context
    ) {
        when (destination.notificationOptIn) {
            NotificationOption.TOP_SPORTS_NEWS -> optInTopSports(destination.enable, activity)
            else -> {}
        }
    }

    private suspend fun optInTopSports(enable: Boolean, context: Context) {
        updateTopSportNewsNotificationUseCase(enable).onSuccess {
            mainNavigationEventProducer.emit(
                MainNavigationEvent.ShowActionTextSnackbar(
                    ActionSnackbarData(
                        optInTopSportsText(
                            context,
                            R.string.preferences_top_sports_news_modal_enable_success_1,
                            R.string.preferences_top_sports_news_modal_enable_success_2
                        ),
                        icon = Icons.Default.Notifications,
                        tag = "notification_settings",
                        duration = 8000,
                        isSuccess = true
                    )
                )
            )
        }.onFailure {
            mainNavigationEventProducer.emit(
                MainNavigationEvent.ShowActionTextSnackbar(
                    ActionSnackbarData(
                        optInTopSportsText(
                            context,
                            R.string.preferences_top_sports_news_modal_enable_failure_1,
                            R.string.preferences_top_sports_news_modal_enable_failure_2
                        ),
                        icon = Icons.Default.Info,
                        tag = "notification_settings",
                        duration = 8000,
                        isSuccess = false
                    )
                )
            )
        }
    }

    private fun optInTopSportsText(
        context: Context,
        @StringRes firstPart: Int,
        @StringRes secondPart: Int
    ) = buildAnnotatedString {
        append(context.getString(firstPart))
        append(" ")
        pushStringAnnotation(tag = "notification_settings", annotation = "theathletic://notification_settings")
        withStyle(style = SpanStyle(color = AthColor.Gray800, textDecoration = TextDecoration.Underline)) {
            append(context.getString(R.string.preferences_top_sports_news_modal_notification_settings))
        }
        pop()
        append(" ")
        append(context.getString(secondPart))
        toAnnotatedString()
    }
}