package com.theathletic.feed.ui

import android.net.Uri
import androidx.collection.LongSparseArray
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.AthleticConfig
import com.theathletic.adapter.main.PodcastPlayButtonController
import com.theathletic.ads.AdAnalytics
import com.theathletic.ads.AdConfig
import com.theathletic.ads.adPosition
import com.theathletic.ads.data.local.AdLocalModel
import com.theathletic.ads.data.local.ContentType
import com.theathletic.ads.repository.AdsRepository
import com.theathletic.ads.shouldDisplayAds
import com.theathletic.analytics.AnalyticsPayload
import com.theathletic.analytics.data.ClickSource
import com.theathletic.analytics.impressions.ImpressionCalculator
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.announcement.AnnouncementsRepository
import com.theathletic.article.ArticleHasPaywallUseCase
import com.theathletic.article.data.ArticleRepository
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.compass.codegen.CompassExperiment
import com.theathletic.compass.getAdExperiments
import com.theathletic.compass.getHomeAdExperiments
import com.theathletic.compass.shouldImproveAdImpressions
import com.theathletic.data.ContentDescriptor
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.TimeProvider
import com.theathletic.device.IsTabletProvider
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.authentication.UserData
import com.theathletic.entity.main.FeedItem
import com.theathletic.entity.main.FeedItemEntryType
import com.theathletic.entity.main.FeedItemStyle
import com.theathletic.entity.main.PodcastDownloadEntity
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.event.NetworkErrorEvent
import com.theathletic.featureswitch.Features
import com.theathletic.feed.FeedNavItemEvent
import com.theathletic.feed.FeedNavItemEventConsumer
import com.theathletic.feed.FeedType
import com.theathletic.feed.UserFeedState
import com.theathletic.feed.UserFeedStateProducer
import com.theathletic.feed.compose.MarkArticleAsReadUseCase
import com.theathletic.feed.compose.MarkArticleAsSavedUseCase
import com.theathletic.feed.data.FeedRefreshJob
import com.theathletic.feed.data.FeedRepository
import com.theathletic.feed.data.local.AnnouncementEntity
import com.theathletic.feed.data.local.AuthorDetails
import com.theathletic.feed.data.remote.FeedArticlePrefetcher
import com.theathletic.feed.ui.FeedContract.ViewState
import com.theathletic.feed.ui.models.CuratedItemType
import com.theathletic.feed.ui.models.FeedAnnouncementAnalyticsPayload
import com.theathletic.feed.ui.models.FeedArticleAnalyticsPayload
import com.theathletic.feed.ui.models.FeedCuratedItemAnalyticsPayload
import com.theathletic.feed.ui.models.FeedHeadlineAnalyticsPayload
import com.theathletic.feed.ui.models.FeedInsiderAnalyticsPayload
import com.theathletic.feed.ui.models.FeedPodcastEpisodeAnalyticsPayload
import com.theathletic.feed.ui.models.FeedPodcastShowAnalyticsPayload
import com.theathletic.feed.ui.models.FeedScoresAnalyticsPayload
import com.theathletic.feed.ui.models.LiveBlogAnalyticsPayload
import com.theathletic.feed.ui.models.LiveRoomAnalyticsPayload
import com.theathletic.feed.ui.models.SeeAllAnalyticsPayload
import com.theathletic.followable.Followable
import com.theathletic.followables.FollowItemUseCase
import com.theathletic.followables.UnfollowItemUseCase
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.followables.data.UserFollowingRepository
import com.theathletic.frontpage.ui.trendingtopics.TrendingTopicAnalyticsPayload
import com.theathletic.links.deep.DeeplinkEventProducer
import com.theathletic.liveblog.data.local.LiveBlogEntity
import com.theathletic.liveblog.data.remote.LiveBlogRibbonSubscriptionManager
import com.theathletic.location.data.LocationRepository
import com.theathletic.manager.IPodcastManager
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.podcast.analytics.PodcastNavigationSource
import com.theathletic.podcast.data.LegacyPodcastRepository
import com.theathletic.podcast.data.PodcastRepository
import com.theathletic.podcast.download.PodcastDownloadStateStore
import com.theathletic.podcast.state.PodcastPlayerState
import com.theathletic.podcast.state.PodcastPlayerStateBus
import com.theathletic.podcast.state.minuteStateChangeFlow
import com.theathletic.remoteconfig.RemoteConfigRepository
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.rooms.analytics.LiveRoomEntryPoint
import com.theathletic.scores.GameDetailTab
import com.theathletic.scores.GameDetailTabParams
import com.theathletic.scores.data.local.BoxScoreEntity
import com.theathletic.scores.data.local.GameState
import com.theathletic.scores.data.remote.LiveGamesSubscriptionManager
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer
import com.theathletic.user.IUserManager
import com.theathletic.user.ui.PrivacyPolicyViewModelDelegate
import com.theathletic.utility.AppRatingEngine
import com.theathletic.utility.PhoneVibrator
import com.theathletic.utility.coroutines.collectIn
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import timber.log.Timber

@Suppress("LargeClass")
class FeedViewModel @AutoKoin constructor(
    @Assisted private val params: Params,
    @Assisted private val feedType: FeedType,
    @Assisted private val navigator: ScreenNavigator,
    private val analytics: Analytics,
    private val transformer: FeedTransformer,
    private val feedRepository: FeedRepository,
    private val adsRepository: AdsRepository,
    private val articleHasPaywall: ArticleHasPaywallUseCase,
    private val articleRepository: ArticleRepository,
    private val announcementsRepository: AnnouncementsRepository,
    private val podcastPlayerStateBus: PodcastPlayerStateBus,
    private val podcastPlayButtonController: PodcastPlayButtonController,
    private val podcastDownloadStateStore: PodcastDownloadStateStore,
    private val podcastManager: IPodcastManager,
    private val podcastRepository: PodcastRepository,
    private val followableRepository: FollowableRepository,
    private val userFollowingRepository: UserFollowingRepository,
    private val followItemUseCase: FollowItemUseCase,
    private val unfollowItemUseCase: UnfollowItemUseCase,
    private val feedNavEventConsumer: FeedNavItemEventConsumer,
    private val userFeedStateProducer: UserFeedStateProducer,
    private val deeplinkEventProducer: DeeplinkEventProducer,
    private val impressionCalculator: ImpressionCalculator,
    private val userManager: IUserManager,
    private val feedArticlePrefetcher: FeedArticlePrefetcher,
    private val feedRefreshJob: FeedRefreshJob,
    private val phoneVibrator: PhoneVibrator,
    private val appRatingEngine: AppRatingEngine,
    private val userDataRepository: IUserDataRepository,
    private val feedAnalytics: FeedAnalytics,
    private val adAnalytics: AdAnalytics,
    private val privacyPolicyViewModelDelegate: PrivacyPolicyViewModelDelegate,
    private val timeProvider: TimeProvider,
    private val liveGamesSubscriptionManager: LiveGamesSubscriptionManager,
    private val liveBlogRibbonSubscriptionManager: LiveBlogRibbonSubscriptionManager,
    private val isTabletProvider: IsTabletProvider,
    private val locationRepository: LocationRepository,
    private val features: Features,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val adConfigBuilder: AdConfig.Builder,
    private val markArticleAsRead: MarkArticleAsReadUseCase,
    private val markArticleAsSaved: MarkArticleAsSavedUseCase
) : AthleticViewModel<CompleteFeedState, ViewState>(),
    FeedContract.Presenter,
    PodcastPlayButtonController.Callback,
    FeedAnalytics by feedAnalytics,
    DefaultLifecycleObserver,
    Transformer<CompleteFeedState, ViewState> by transformer {

    data class Params(
        val isStandaloneFeed: Boolean = false,
        val feedTitle: String = "",
        val screenWidth: Int,
        val screenHeight: Int
    )

    companion object {
        private const val ITEMS_FROM_BOTTOM_TO_REQUEST_NEXT_PAGE = 10
    }

    override val initialState by lazy {
        CompleteFeedState(
            isStandaloneFeed = params.isStandaloneFeed,
            feedTitle = params.feedTitle,
            userIsStaff = userManager.isStaff,
            feedType = feedType,
            podcastDownloadData = PodcastDownloadWrapper(podcastDownloadStateStore.latestState)
        )
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        initialize()
    }

    fun initialize() {
        feedAnalytics.view = when (feedType) {
            is FeedType.Frontpage -> "front_page"
            else -> "home"
        }
        adsRepository.shouldAllowDiscardingAds = CompassExperiment.HOME_ADS_V2.shouldImproveAdImpressions(feedType)

        listenForConfigUpdates()
        listenForRenderUpdates()
        listenForFeedUpdates()
        listenForAdEvents()
        impressionCalculator.configure(this::fireImpressionEvent)
        viewModelScope.launch {
            loadFollowable()
        }

        if (feedType is FeedType.Author && state.isStandaloneFeed) {
            loadAuthorDetails()
        }
        adAnalytics.trackAdPageView(pageViewId, getFeedView(feedType))
    }

    private fun listenForConfigUpdates() {
        remoteConfigRepository.gdprSupportedCountries.collectIn(viewModelScope) {
            adConfigBuilder.setGDPRCountries(it)
        }
        remoteConfigRepository.ccpaSupportedStates.collectIn(viewModelScope) {
            adConfigBuilder.setCCPAStates(it)
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        if (!displayPrivacyRefreshDialogueIfNeeded()) {
            solicitStoreRatingIfTriggered()
        }
        viewModelScope.launch {
            loadIfNeeded()
            sendEvent(FeedContract.Event.TrackFeedView(::trackFeedView))
        }
    }

    override fun onCleared() {
        adsRepository.clearCache(pageViewId = pageViewId)
        super.onCleared()
    }

    private fun trackFeedView() {
        val objectId = feedType.id.toString()
        when (feedType) {
            FeedType.User -> analytics.track(
                Event.Home.View(object_type = "following", object_id = "following")
            )
            FeedType.Frontpage -> analytics.track(
                Event.Frontpage.View()
            )
            is FeedType.Team -> analytics.track(
                Event.Home.View(object_type = "team_id", object_id = objectId)
            )
            is FeedType.League -> analytics.track(
                Event.Home.View(object_type = "league_id", object_id = objectId)
            )
            is FeedType.Author -> analytics.track(
                Event.Home.View(object_type = "author_id", object_id = objectId)
            )
            else -> {}
        }
    }

    private fun listenForRenderUpdates() {
        podcastPlayerStateBus.minuteStateChangeFlow.collectIn(viewModelScope) {
            updateState { copy(podcastPlayerState = it) }
        }

        podcastDownloadStateStore.downloadStates.subscribe {
            updateState { copy(podcastDownloadData = PodcastDownloadWrapper(it)) }
        }.disposeOnCleared()

        podcastRepository.downloadedEpisodes.collectIn(viewModelScope) {
            updateState { copy(downloadedPodcasts = it) }
        }

        feedNavEventConsumer.observe<FeedNavItemEvent.ScrollToTopOfFeed>(viewModelScope) {
            if (feedType == it.feedType) {
                sendEvent(FeedContract.Event.ScrollToTopOfFeed)
            }
        }

        feedNavEventConsumer.observe<FeedNavItemEvent.ScrollToTopHeadlines>(viewModelScope) {
            analytics.track(Event.Headline.HeadlineWidget())
            sendEvent(FeedContract.Event.ScrollToTopHeadlines)
        }

        userDataRepository.userDataFlow.collectIn(viewModelScope) {
            updateState { copy(userData = it) }
        }
    }

    private fun listenForFeedUpdates() {
        feedRepository.getFeed(feedType).collectIn(viewModelScope) {
            Timber.v("Loaded $it")
            postProcess(it)
            feedArticlePrefetcher.prefetch(
                it.flatMap { module -> module.entities.filterIsInstance<ArticleEntity>() }
            )
        }
    }

    private fun listenForAdEvents() {
        adsRepository.observeAdEvents(pageViewId = pageViewId).collectIn(viewModelScope) { adEvent ->
            adAnalytics.trackAdEvent(pageViewId = pageViewId, view = getFeedView(feedType), event = adEvent)
        }
    }

    private suspend fun loadIfNeeded() {
        val isEmpty = !feedRepository.hasCachedFeed(feedType)
        when {
            isEmpty || feedRefreshJob.shouldRefreshFeed(feedType) -> {
                load()
            }
            else -> updateState { copy(loadingState = LoadingState.FINISHED) }
        }
    }

    private suspend fun load() {
        updateState { copy(loadingState = LoadingState.RELOADING) }
        feedRefreshJob.fetchFeed(feedType)
        if (CompassExperiment.HOME_ADS_V2.shouldImproveAdImpressions(feedType)) {
            loadAds(shouldReplaceDiscarded = true)
        }
        updateState {
            copy(
                loadingState = LoadingState.FINISHED,
                lastPageFetched = 0
            )
        }
        feedAnalytics.trackAdOnLoad(pageViewId, feedType)
    }

    private fun loadAds(
        items: List<FeedItem> = state.feedItems.filter { it.style == FeedItemStyle.DROPZONE },
        shouldReplaceDiscarded: Boolean = false
    ) {
        if (feedType.shouldDisplayAds(features)) {
            viewModelScope.launch {
                initializeAdConfig()

                items.forEach { dropzone ->
                    adConfigBuilder.setAdUnitPath(dropzone.adUnitPath)
                    adsRepository.fetchAd(
                        pageViewId = pageViewId,
                        adId = dropzone.id,
                        adConfigBuilder
                            .setPosition(dropzone.id.adPosition)
                            .build(pageViewId),
                        shouldReplaceDiscarded
                    )
                }
            }
        }
    }

    private suspend fun loadMore() {
        if (state.loadingState == LoadingState.LOADING_MORE) return

        val lastItem = state.feedItems.lastOrNull() ?: return

        if (!lastItem.hasNextPage) return

        val pageToFetch = lastItem.page + 1
        if (state.lastPageFetched >= pageToFetch) return

        updateState { copy(loadingState = LoadingState.LOADING_MORE) }

        feedRepository.fetchFeed(
            feedType = feedType,
            forceRefresh = false,
            page = pageToFetch,
            isAdsEnabled = feedType.shouldDisplayAds(features)
        ).join()

        updateState {
            copy(
                loadingState = LoadingState.FINISHED,
                lastPageFetched = pageToFetch
            )
        }
    }

    private suspend fun loadFollowable() {
        val followableId = feedType.asFollowableId ?: return
        val followable = followableRepository.getFollowable(followableId) ?: return
        updateState { copy(feedTitle = followable.name) }

        userFollowingRepository.userFollowingStream.collectIn(viewModelScope) { following ->
            val isFollowing = following.any { it.id == followableId }
            val isUserFeed = feedType == FeedType.User

            updateState { copy(isFollowingTopic = isFollowing || isUserFeed) }
        }
    }

    private suspend fun initializeAdConfig() {
        adConfigBuilder.subscriber(userManager.isUserSubscribed())
            .viewport(params.screenWidth, params.screenHeight)
            .appVersion(AthleticConfig.VERSION_NAME)
            .setCompassExperiments(
                when (feedType) {
                    is FeedType.User -> CompassExperiment.getHomeAdExperiments()
                    else -> CompassExperiment.getAdExperiments()
                }
            )
            .contentType(
                when (feedType) {
                    is FeedType.User -> ContentType.HOME_PAGE.type
                    else -> ContentType.COLLECTION.type
                }
            )
            .setGeo(locationRepository.getCountryCode(), locationRepository.getState())
    }

    private suspend fun postProcess(items: List<FeedItem>) {
        updateLiveBlogSubscription(items)
        filterCarousel(items)
        displayAds(items)
        val isAnnouncementDismissed = checkHasDismissedAnnouncements(items)

        updateState {
            copy(
                feedItems = items,
                isAnnouncementDismissed = isAnnouncementDismissed,
                isEndOfFeed = !(items.lastOrNull()?.hasNextPage ?: false)
            )
        }
        if (state.feedType == FeedType.User) {
            userFeedStateProducer.value = UserFeedState(
                isEmptyAndLoading = state.feedItems.isEmpty() && state.loadingState == LoadingState.INITIAL_LOADING
            )
        }
    }

    private fun filterCarousel(items: List<FeedItem>) {
        items.filter { it.style == FeedItemStyle.CAROUSEL_SCORES }.forEach { scoresItem ->
            viewModelScope.launch {
                val liveAndUpcomingGames =
                    scoresItem.entities.filterIsInstance(BoxScoreEntity::class.java)
                        .filter { shouldSubscribeForGameUpdates(it) }
                        .map { it.id }
                liveGamesSubscriptionManager.subscribeToGames(liveAndUpcomingGames)
            }
        }
    }

    private fun displayAds(items: List<FeedItem>) {
        if (feedType.shouldDisplayAds(features)) {
            val dropzoneItems = items.filter { it.style == FeedItemStyle.DROPZONE }
            loadAds(dropzoneItems)
            dropzoneItems.forEach { dropzone ->
                adsRepository.getAd(pageViewId = pageViewId, adId = dropzone.id).collectIn(viewModelScope) { adLocalModel ->
                    adLocalModel?.let { adModel ->
                        if (adModel.collapsed) {
                            collapseAdView(adModel.id)
                        } else {
                            showAdView(adModel)
                        }
                    }
                }
            }
        }
    }

    private suspend fun checkHasDismissedAnnouncements(items: List<FeedItem>): Boolean {
        return items.any { module ->
            module.entities.filterIsInstance<AnnouncementEntity>().firstOrNull()?.let {
                announcementsRepository.isAnnouncementDismissed(it.id)
            } ?: false
        }
    }

    private fun updateLiveBlogSubscription(items: List<FeedItem>) {
        items.filter { it.style == FeedItemStyle.CAROUSEL_LIVE_BLOGS }.forEach { liveBlogItems ->
            viewModelScope.launch {
                val liveBlogs = liveBlogItems.entities
                    .filterIsInstance<LiveBlogEntity>()
                    .map { it.id }

                liveBlogRibbonSubscriptionManager.subscribeToLiveBlogs(liveBlogs) {
                    updateState { copy(liveBlogUpdates = liveBlogUpdates + 1) }
                }
            }
        }
    }

    private fun shouldSubscribeForGameUpdates(entity: BoxScoreEntity): Boolean {
        return when (entity.state) {
            GameState.LIVE -> true
            GameState.UPCOMING -> isLeadingUpToGameStart(entity.gameTime)
            else -> false
        }
    }

    private fun isLeadingUpToGameStart(scheduleAt: Datetime): Boolean {
        val now = timeProvider.currentTimeMs
        val prior = scheduleAt.timeMillis - TimeUnit.MINUTES.toMillis(30)
        val overlap = scheduleAt.timeMillis + TimeUnit.MINUTES.toMillis(30)
        return now in prior..overlap
    }

    override fun onPullToRefresh() {
        updateState { copy(loadingState = LoadingState.RELOADING) }
        viewModelScope.launch {
            load()
            if (!CompassExperiment.HOME_ADS_V2.shouldImproveAdImpressions(feedType)) {
                adsRepository.clearCache(pageViewId = pageViewId)
                loadAds()
            }
        }
    }

    override fun onAnnouncementClick(
        id: String,
        analyticsPayload: FeedAnnouncementAnalyticsPayload?
    ) {
        analyticsPayload?.click(id)
        viewModelScope.launch {
            val announcement = announcementsRepository.getAnnouncement(id)

            announcementsRepository.markAnnouncementClicked(id)
            updateState { copy(isAnnouncementDismissed = true) }

            deeplinkEventProducer.emit(announcement?.deeplinkUrl.orEmpty())
        }
    }

    override fun onAnnouncementDismiss(id: String) {
        viewModelScope.launch {
            announcementsRepository.markAnnouncementDismissed(id)
            updateState { copy(isAnnouncementDismissed = true) }
        }
    }

    override fun onArticleClicked(
        id: Long,
        analyticsPayload: FeedArticleAnalyticsPayload,
        title: String
    ) {
        analyticsPayload.click(id)
        navigateToArticle(ContentDescriptor(id, title))
    }

    override fun onArticleLongClicked(id: Long): Boolean {
        val isBookmarked = articleRepository.isArticleBookmarked(id)
        val isRead = userDataRepository.isItemRead(id)
        sendEvent(FeedContract.Event.ShowArticleLongClickSheet(id, isBookmarked, isRead))
        return true
    }

    fun changeArticleBookmarkStatus(articleId: Long, isBookmarked: Boolean) =
        viewModelScope.launch { markArticleAsSaved(articleId, isBookmarked) }

    fun shareArticle(articleId: Long) = viewModelScope.launch {
        val permalink = articleRepository.getArticle(articleId)?.permalink ?: return@launch
        navigator.startShareTextActivity(permalink)
    }

    override fun onPodcastControlClicked(
        episodeId: Long,
        analyticsPayload: FeedPodcastEpisodeAnalyticsPayload
    ) {
        viewModelScope.launch {
            podcastPlayButtonController.onPodcastPlayClick(
                episodeId = episodeId,
                callback = this@FeedViewModel,
                analyticsPayload = analyticsPayload
            )
        }
    }

    override fun onPodcastEpisodeClicked(
        episodeId: Long,
        analyticsPayload: FeedPodcastEpisodeAnalyticsPayload
    ) {
        analyticsPayload.click(episodeId)
        navigator.startPodcastEpisodeDetailActivity(episodeId, PodcastNavigationSource.HOME)
    }

    override fun onPodcastEpisodeOptionsClicked(
        episodeId: Long,
        isPlayed: Boolean,
        isDownloaded: Boolean
    ) {
        sendEvent(
            FeedContract.Event.ShowPodcastEpisodeOptionSheet(
                episodeId,
                isPlayed,
                isDownloaded
            )
        )
    }

    override fun onPodcastSeriesClicked(
        podcastShowId: Long,
        analyticsPayload: FeedPodcastShowAnalyticsPayload
    ) {
        analyticsPayload.click(podcastShowId)
        navigator.startPodcastDetailActivity(podcastShowId, PodcastNavigationSource.HOME)
    }

    fun onPodcastShareClicked(episodeId: Long) {
        viewModelScope.launch {
            podcastRepository.podcastEpisodeEntityById(episodeId.toString())?.let { episode ->
                navigator.startShareTextActivity(episode.permalinkUrl)
            }
        }
    }

    fun onDeletePodcastClick(episodeId: Long) {
        viewModelScope.launch {
            LegacyPodcastRepository.deletePodcastEpisode(episodeId).await()
        }
    }

    fun onMarkPodcastAsPlayedClicked(episodeId: Long) {
        state.podcastPlayerState.activeTrack?.let { activeTrack ->
            if (activeTrack.id == episodeId) {
                podcastManager.trackPodcastListenedState(true)
                return
            }
        }
        viewModelScope.launch {
            podcastRepository.podcastEpisodeEntityById(episodeId.toString())?.let { episode ->
                podcastManager.trackPodcastListenedState(
                    episodeId = episodeId,
                    progress = episode.timeElapsedMs,
                    isFinished = true
                )
            }
        }
    }

    override fun showNetworkOfflineError() {
        sendEvent(NetworkErrorEvent)
    }

    override fun showPayWall() {
        navigator.startPlansActivity(ClickSource.FEED)
    }

    override fun firePlayAnalyticsEvent(podcastEpisodeId: Long, payload: AnalyticsPayload?) {
        when (payload) {
            is FeedCuratedItemAnalyticsPayload -> payload.play(podcastEpisodeId)
            is FeedPodcastEpisodeAnalyticsPayload -> payload.play(podcastEpisodeId)
        }
    }

    override fun firePauseAnalyticsEvent(podcastEpisodeId: Long, payload: AnalyticsPayload?) {
        when (payload) {
            is FeedCuratedItemAnalyticsPayload -> payload.pause(podcastEpisodeId)
            is FeedPodcastEpisodeAnalyticsPayload -> payload.pause(podcastEpisodeId)
        }
    }

    override fun onTopicClicked(
        id: Long,
        title: String,
        analyticsPayload: TrendingTopicAnalyticsPayload
    ) {
        analyticsPayload.click(id)
        navigator.startTopicFeedActivity(id, title)
    }

    override fun onHeadlineClick(
        id: String,
        analyticsPayload: FeedHeadlineAnalyticsPayload
    ) {
        analyticsPayload.click(id)
        navigator.startHeadlineContainerActivity(id, ClickSource.FEED.value)
    }

    override fun onSeeAllClicked(deeplink: String, analyticsPayload: SeeAllAnalyticsPayload?) {
        analyticsPayload?.click()
        viewModelScope.launch {
            deeplinkEventProducer.emit(deeplink)
        }
    }

    override fun onViewVisibilityChanged(
        payload: ImpressionPayload,
        pctVisible: Float
    ) {
        impressionCalculator.onViewVisibilityChanged(payload, pctVisible)
    }

    override fun onCuratedItemClicked(
        id: String,
        type: CuratedItemType,
        payload: FeedCuratedItemAnalyticsPayload,
        title: String
    ) {
        payload.click(id)
        viewModelScope.launch {
            when (type) {
                CuratedItemType.LIVE_BLOG -> navigator.startLiveBlogActivity(id)
                CuratedItemType.HEADLINE ->
                    navigator.startHeadlineContainerActivity(
                        id,
                        ClickSource.FEED.value
                    )
                else -> handleCuratedArticleNavigation(ContentDescriptor(id, title), type)
            }
        }
    }

    private suspend fun handleCuratedArticleNavigation(
        descriptor: ContentDescriptor,
        type: CuratedItemType
    ) {
        val articleId = descriptor.id.toLong()
        when {
            articleHasPaywall(articleId) -> navigator.startArticlePaywallActivity(articleId, ClickSource.FEED)
            type == CuratedItemType.QANDA -> navigateToDiscussion(descriptor, isQandA = true)
            type == CuratedItemType.DISCUSSION -> navigateToDiscussion(descriptor, isQandA = false)
            type == CuratedItemType.PODCAST ->
                navigator.startPodcastEpisodeDetailActivity(articleId, PodcastNavigationSource.HOME)
            else ->
                navigator.startArticleActivity(articleId, ClickSource.FEED)
        }
    }

    override fun onCuratedItemLongClicked(id: String, type: CuratedItemType): Boolean {
        return when (type) {
            CuratedItemType.ARTICLE -> {
                val longId = id.toLong()
                val isBookmarked = articleRepository.isArticleBookmarked(longId)
                val isRead = userDataRepository.isItemRead(longId)
                sendEvent(
                    FeedContract.Event.ShowArticleLongClickSheet(
                        longId,
                        isBookmarked,
                        isRead
                    )
                )
                true
            }
            CuratedItemType.DISCUSSION, CuratedItemType.QANDA -> {
                sendEvent(FeedContract.Event.ShowDiscussionLongClickSheet(id.toLong()))
                true
            }
            else -> false
        }
    }

    override fun onInsiderArticleClicked(
        articleId: Long,
        analyticsInfo: FeedInsiderAnalyticsPayload,
        title: String
    ) {
        analyticsInfo.click()
        navigateToArticle(ContentDescriptor(articleId, title))
    }

    override fun onPodcastControlClicked(
        id: String,
        analyticsPayload: FeedCuratedItemAnalyticsPayload
    ) {
        viewModelScope.launch {
            podcastPlayButtonController.onPodcastPlayClick(
                episodeId = id.toLong(),
                callback = this@FeedViewModel,
                analyticsPayload = analyticsPayload
            )
        }
    }

    override fun onScoresClicked(
        gameId: String,
        leagueId: Long,
        analyticsPayload: FeedScoresAnalyticsPayload
    ) {
        analyticsPayload.click(gameId, leagueId)
        navigator.startGameDetailMvpActivity(gameId)
    }

    override fun onDiscussClicked(gameId: String) {
        navigator.startGameDetailMvpActivity(
            gameId = gameId,
            selectedTabParams = GameDetailTabParams(GameDetailTab.DISCUSS),
            source = "home"
        )
    }

    override fun onPostBindAtPosition(indexBound: Int, listSize: Int) {
        if (
            indexBound == listSize - ITEMS_FROM_BOTTOM_TO_REQUEST_NEXT_PAGE &&
            state.loadingState == LoadingState.FINISHED
        ) {
            viewModelScope.launch { loadMore() }
        }
    }

    override fun onLoadMore() {
        viewModelScope.launch { loadMore() }
    }

    private fun fireImpressionEvent(
        payload: ImpressionPayload,
        startTime: Long,
        endTime: Long
    ) {
        payload.view(startTime, endTime, feedType, userManager.getCurrentUserId())
    }

    private fun solicitStoreRatingIfTriggered() {
        if (appRatingEngine.shouldTryRatingDialogue) {
            appRatingEngine.disable()
            sendEvent(FeedContract.Event.SolicitAppRating)
            analytics.track(Event.AppRating.DialogRequested)
        }
    }

    fun onPrivacyDialogAccepted() = privacyPolicyViewModelDelegate.onPrivacyAccepted()

    private fun displayPrivacyRefreshDialogueIfNeeded(): Boolean {
        if (feedType == FeedType.User && privacyPolicyViewModelDelegate.shouldPresentPrivacyRefresh()) {
            sendEvent(FeedContract.Event.SolicitPrivacyUpdate(privacyPolicyViewModelDelegate.privacyRegion))
            privacyPolicyViewModelDelegate.didDisplayPrivacyDialog()
            return true
        }
        return false
    }

    private fun navigateToArticle(contentDescriptor: ContentDescriptor) {
        viewModelScope.launch {
            val id = contentDescriptor.id.toLong()
            val article = articleRepository.getArticle(id)
            when {
                articleHasPaywall(id, article) ->
                    navigator.startArticlePaywallActivity(id, ClickSource.FEED)
                article?.entryType == FeedItemEntryType.USER_DISCUSSION ||
                    article?.entryType == FeedItemEntryType.COMMENTS -> navigateToDiscussion(
                    contentDescriptor,
                    isQandA = false
                )
                article?.entryType == FeedItemEntryType.LIVE_DISCUSSION -> navigateToDiscussion(
                    contentDescriptor,
                    isQandA = true
                )
                else ->
                    navigator.startArticleActivity(id, ClickSource.FEED)
            }
        }
    }

    override fun onJoinRoomClicked(roomId: String, payload: LiveRoomAnalyticsPayload) {
        payload.click(roomId)
        navigator.startLiveAudioRoomActivity(
            id = roomId,
            entryPoint = LiveRoomEntryPoint.FEED
        )
    }

    override fun collapseAdView(id: String) {
        val updateHideIds = state.hideFeedItemIds.toMutableList()
        if (updateHideIds.contains(id)) {
            return
        }
        updateHideIds.add(id)
        updateState { copy(hideFeedItemIds = updateHideIds) }
    }

    override fun showAdView(ad: AdLocalModel) {
        val updateHideIds = state.hideFeedItemIds.toMutableList()
        updateHideIds.remove(ad.id)
        val adList = state.adList.toMutableMap()
        adList[ad.id] = ad
        updateState { copy(hideFeedItemIds = updateHideIds, adList = adList) }
    }

    override fun onLiveBlogClick(id: String, analyticsPayload: LiveBlogAnalyticsPayload) {
        analyticsPayload.click(id)
        navigator.startLiveBlogActivity(id)
    }

    private fun loadAuthorDetails() {
        feedRepository.getAuthorDetails(feedType.id).collectIn(viewModelScope) {
            updateState { copy(authorDetails = it) }
        }
        feedRepository.fetchAuthorDetails(feedType.id)
    }

    override fun onFeaturedAuthorTwitterClick(twitterHandle: String) {
        navigator.startOpenExternalLink(
            Uri.parse("https://www.twitter.com/${twitterHandle.replace("@", "")}")
        )
    }

    override fun onFollowAuthorClick() {
        viewModelScope.launch {
            feedType.asFollowableId?.let { id ->
                if (state.isFollowingTopic) {
                    updateState { copy(isFollowingTopic = false) }
                    unfollowItemUseCase(id).onFailure { updateState { copy(isFollowingTopic = true) } }
                } else {
                    updateState { copy(isFollowingTopic = true) }
                    followItemUseCase(id).onFailure { updateState { copy(isFollowingTopic = false) } }
                }
            }
        }
    }

    override fun onEndOfFeedClicked() {
        sendEvent(FeedContract.Event.ScrollToTopOfFeed)
    }

    fun onMarkArticleRead(articleId: Long, isRead: Boolean) {
        viewModelScope.launch {
            markArticleAsRead(articleId, isRead)
        }
    }

    private fun navigateToDiscussion(contentDescriptor: ContentDescriptor, isQandA: Boolean) {
        navigator.startCommentsV2Activity(
            contentDescriptor = contentDescriptor,
            type = if (isQandA) CommentsSourceType.QANDA else CommentsSourceType.DISCUSSION,
            clickSource = ClickSource.FEED
        )
    }

    private fun getFeedView(feedType: FeedType): String =
        when (feedType) {
            is FeedType.League -> "leagues"
            is FeedType.Team -> "teams"
            is FeedType.Author -> "author"
            is FeedType.Tag -> "topic"
            else -> view
        }
}

data class CompleteFeedState(
    val loadingState: LoadingState = LoadingState.INITIAL_LOADING,
    val isStandaloneFeed: Boolean = false,
    val feedTitle: String = "",
    val feedItems: List<FeedItem> = emptyList(),
    val hideFeedItemIds: List<String> = emptyList(),
    val podcastPlayerState: PodcastPlayerState = PodcastPlayerState(),
    val filterFollowable: Followable? = null,
    val isFollowingTopic: Boolean = true,
    val isAnnouncementDismissed: Boolean = false,
    val userIsStaff: Boolean = false,
    val lastPageFetched: Int = -1,
    val authorDetails: AuthorDetails? = null,
    val isEndOfFeed: Boolean = false,
    val feedType: FeedType,
    val userData: UserData? = null,
    val downloadedPodcasts: List<PodcastEpisodeItem> = emptyList(),
    val podcastDownloadData: PodcastDownloadWrapper,
    val adList: Map<String, AdLocalModel> = emptyMap(),
    val liveBlogUpdates: Long = 0,
) : DataState

class PodcastDownloadWrapper(val downloads: LongSparseArray<PodcastDownloadEntity>) {

    operator fun get(key: String?): PodcastDownloadEntity? {
        return key?.toLongOrNull()?.let { downloads[it] }
    }
}