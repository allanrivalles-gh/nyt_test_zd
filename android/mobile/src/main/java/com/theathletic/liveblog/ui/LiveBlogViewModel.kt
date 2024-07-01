package com.theathletic.liveblog.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.AthleticConfig
import com.theathletic.ads.AdAnalytics
import com.theathletic.ads.AdConfig
import com.theathletic.ads.adPosition
import com.theathletic.ads.data.local.AdLocalModel
import com.theathletic.ads.getLiveBlogTypeFromGameId
import com.theathletic.ads.repository.AdsRepository
import com.theathletic.analytics.data.ClickSource
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.compass.codegen.CompassExperiment
import com.theathletic.compass.getAdExperiments
import com.theathletic.featureswitch.Features
import com.theathletic.feed.FeedType
import com.theathletic.gamedetail.boxscore.ui.BoxScoreAnalytics
import com.theathletic.gamedetail.boxscore.ui.BoxScoreAnalyticsHandler
import com.theathletic.gamedetail.data.local.GameStatus
import com.theathletic.links.deep.DeeplinkEventProducer
import com.theathletic.liveblog.data.LiveBlogRepository
import com.theathletic.liveblog.data.local.NativeLiveBlog
import com.theathletic.liveblog.data.local.NativeLiveBlogDropzone
import com.theathletic.liveblog.data.local.NativeLiveBlogPostBasic
import com.theathletic.location.data.LocationRepository
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.remoteconfig.RemoteConfigRepository
import com.theathletic.twitter.data.TwitterRepository
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.ComposeViewModel
import com.theathletic.ui.ContentTextSize
import com.theathletic.ui.DataState
import com.theathletic.ui.DisplayPreferences
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer
import com.theathletic.user.IUserManager
import com.theathletic.utility.coroutines.collectIn
import com.theathletic.utility.safeLet
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

class LiveBlogViewModel @AutoKoin constructor(
    @Assisted val params: Params,
    @Assisted private val screenNavigator: ScreenNavigator,
    private val userManager: IUserManager,
    private val deeplinkEventProducer: DeeplinkEventProducer,
    private val liveBlogRepository: LiveBlogRepository,
    private val displayPreferences: DisplayPreferences,
    private val liveBlogAnalytics: LiveBlogAnalytics,
    private val adAnalytics: AdAnalytics,
    private val twitterRepository: TwitterRepository,
    private val adsRepository: AdsRepository,
    private val locationRepository: LocationRepository,
    private val features: Features,
    private val analyticsHandler: BoxScoreAnalyticsHandler,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val adConfigBuilder: AdConfig.Builder,
    transformer: LiveBlogTransformer
) : AthleticViewModel<LiveBlogState, LiveBlogContract.ViewState>(),
    LiveBlogContract.Presenter,
    ComposeViewModel,
    DefaultLifecycleObserver,
    BoxScoreAnalytics by analyticsHandler,
    Transformer<LiveBlogState, LiveBlogContract.ViewState> by transformer {

    data class Params(
        val liveBlogId: String,
        val initialPostId: String?,
        val isDayMode: Boolean,
        val screenWidth: Int,
        val screenHeight: Int,
        val status: GameStatus?,
        val leagueId: String,
        val gameId: String,
        val isViewEventFromBoxScore: Boolean = false,
    )

    override val initialState: LiveBlogState = LiveBlogState(initialPostId = params.initialPostId)

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        initialize()
    }

    override fun initialize() {
        updateState { copy(loadingState = LoadingState.INITIAL_LOADING) }
        displayPreferences.contentTextSizeState.collectIn(viewModelScope) { contentTextSize ->
            updateState { copy(contentTextSize = contentTextSize) }
        }
        listenForConfigUpdates()
        initializeAdConfig()
        listenForAdEvents()
        listenForRenderUpdates()
        fetchLiveBlog()
        subscribeToLiveBlogPosts()
        liveBlogAnalytics.trackView(
            blogId = params.liveBlogId,
            boxScoreState = params.status.toStatus
        )
    }

    private fun listenForConfigUpdates() {
        remoteConfigRepository.gdprSupportedCountries.collectIn(viewModelScope) {
            adConfigBuilder.setGDPRCountries(it)
        }
        remoteConfigRepository.ccpaSupportedStates.collectIn(viewModelScope) {
            adConfigBuilder.setCCPAStates(it)
        }
    }

    private fun initializeAdConfig() {
        viewModelScope.launch {
            if (features.isLiveBlogAdsEnabled) {
                adConfigBuilder.subscriber(userManager.isUserSubscribed())
                    .viewport(params.screenWidth, params.screenHeight)
                    .setCompassExperiments(CompassExperiment.getAdExperiments())
                    .appVersion(AthleticConfig.VERSION_NAME)
                    .setGeo(locationRepository.getCountryCode(), locationRepository.getState())
                loadAdContent(state.liveBlog)
            }
        }
    }

    fun refreshLiveBlog() {
        updateState { copy(loadingState = LoadingState.RELOADING) }
        fetchLiveBlog()
    }

    private fun fetchLiveBlog() {
        viewModelScope.launch {
            liveBlogRepository.fetchLiveBlog(params.liveBlogId).join()
            trackLiveBlogPageView()
            updateState { copy(loadingState = LoadingState.FINISHED) }
        }
    }

    private fun subscribeToLiveBlogPosts() {
        viewModelScope.launch {
            liveBlogRepository.subscribeToLiveBlogPosts(params.liveBlogId)
        }
    }

    private fun listenForRenderUpdates() {
        liveBlogRepository.getLiveBlogFlow(params.liveBlogId).collectIn(viewModelScope) { liveBlog ->
            liveBlog?.let {
                if (state.loadingState.isFreshLoadingState || state.loadingState == LoadingState.LOADING_MORE) {
                    updateState {
                        copy(
                            liveBlog = liveBlog,
                            currentPage = liveBlog.currentPage,
                        )
                    }
                } else {
                    trackFabView()
                    updateState {
                        copy(stagedLiveBlog = liveBlog)
                    }
                }
                loadTweetContent(it)
                loadAdContent(it)
                if (params.isViewEventFromBoxScore) {
                    params.status?.let { status ->
                        if (!state.hasViewEventBeenSent) {
                            logViewEvent(
                                status,
                                params.gameId,
                                params.leagueId,
                                params.liveBlogId
                            )
                        }
                    }
                }
            }
        }
    }

    private fun listenForAdEvents() {
        adsRepository.observeAdEvents(pageViewId = pageViewId).collectIn(viewModelScope) { adEvent ->
            val view = if (state.liveBlog?.gameId.isNullOrEmpty().not()) { "game" } else { "blog" }
            adAnalytics.trackAdEvent(pageViewId = pageViewId, view = view, event = adEvent)
        }
    }

    private fun logViewEvent(status: GameStatus, gameId: String, leagueId: String, blogId: String) {
        trackBoxScoreLiveBlogView(status = status, gameId = gameId, leagueId = leagueId, teamId = "", blogId = blogId)
        updateState { copy(hasViewEventBeenSent = true) }
    }

    private fun loadTweetContent(liveBlog: NativeLiveBlog) {
        val allTweetUrls = liveBlog.tweetUrls + liveBlog.posts.fold(mutableListOf()) { acc, post ->
            if (post is NativeLiveBlogPostBasic) {
                acc.addAll(post.tweetUrls)
            }
            acc
        }

        viewModelScope.launch {
            val newTweets = mutableMapOf<String, String>()
            allTweetUrls.filterNot { state.tweetUrlToHtml.containsKey(it) }.forEach {
                val tweet = async { twitterRepository.getTwitterUrl(it, params.isDayMode) }
                safeLet(it, tweet.await()?.html) { url, html ->
                    newTweets.put(url, html)
                }
            }
            if (newTweets.isNotEmpty()) {
                updateState { copy(tweetUrlToHtml = state.tweetUrlToHtml + newTweets) }
            }
        }
    }

    private fun loadAdContent(liveBlog: NativeLiveBlog?) {
        if (features.isLiveBlogAdsEnabled) {
            val adContent = liveBlog?.posts?.filterIsInstance<NativeLiveBlogDropzone>() ?: return
            adConfigBuilder
                .setAdUnitPath(liveBlog.adUnitPath)
                .setAdTargeting(liveBlog.adTargeting)
                .contentType(liveBlog.gameId.getLiveBlogTypeFromGameId().type)
            adContent.forEach { dropzone ->
                prefetchAd(dropzone)
                listenForAdView(dropzone)
            }
        }
    }

    private fun prefetchAd(dropzone: NativeLiveBlogDropzone) {
        adsRepository.fetchAd(
            pageViewId = pageViewId,
            adId = dropzone.dropzoneId,
            adConfigBuilder
                .setPosition(dropzone.dropzoneId.adPosition)
                .build(pageViewId),
            false
        )
    }

    private fun listenForAdView(dropzone: NativeLiveBlogDropzone) {
        adsRepository.getAd(pageViewId = pageViewId, adId = dropzone.dropzoneId).collectIn(viewModelScope) { adLocalModel ->
            adLocalModel?.let { adModel ->
                if (adModel.collapsed) {
                    collapseAdView(adModel.id)
                } else {
                    showAdView(adModel)
                }
            }
        }
    }

    private fun trackLiveBlogPageView() {
        val view = if (state.liveBlog?.gameId.isNullOrEmpty().not()) { "game" } else { "blog" }
        if (state.loadingState != LoadingState.RELOADING) {
            adAnalytics.trackAdPageView(pageViewId, view)
        }
        liveBlogAnalytics.trackOnAdLoad(
            view = view,
            pageViewId = pageViewId
        )
    }

    override fun onSponsoredArticleClick(id: String) {
        try {
            screenNavigator.startArticleActivity(
                id.toLong(),
                ClickSource.LIVEBLOG_SPONSORED
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse article id: $id")
        }
    }

    override fun onUrlClick(url: String) {
        updateState { copy(loadingState = LoadingState.RELOADING) }
        viewModelScope.launch {
            deeplinkEventProducer.emit(url)
        }
        updateState { copy(loadingState = LoadingState.FINISHED) }
    }

    override fun onBackClick() {
        screenNavigator.finishActivity()
    }

    override fun onShareClick() {
        state.liveBlog?.let { screenNavigator.startShareTextActivity(it.permalink) }
    }

    override fun onTextStyleClick() {
        showBottomSheet(
            LiveBlogContract.ModalSheetType.TextStyleBottomSheet(liveBlogId = state.liveBlog?.id.orEmpty())
        )
        liveBlogAnalytics.trackClick(
            blogId = params.liveBlogId,
            element = "settings_drawer"
        )
    }

    override fun onBlogAuthorClick(authorId: Long, liveBlogId: String) {
        screenNavigator.startStandaloneFeedActivity(FeedType.Author(authorId))
        liveBlogAnalytics.trackClick(
            element = "author",
            view = "blog",
            objectType = "blog_id",
            objectId = liveBlogId,
            blogId = state.liveBlog?.id.orEmpty(),
            authorId = authorId.toString()
        )
    }

    override fun onPostAuthorClick(authorId: Long, liveBlogPostId: String) {
        screenNavigator.startStandaloneFeedActivity(FeedType.Author(authorId))
        liveBlogAnalytics.trackClick(
            element = "author",
            objectType = "blog_post_id",
            objectId = liveBlogPostId,
            blogId = state.liveBlog?.id.orEmpty(),
            authorId = authorId.toString(),
            pageOrder = state.liveBlog?.posts?.indexOfFirst { it.id == liveBlogPostId }.toString()
        )
    }

    override fun onRelatedArticleClick(id: Long, liveBlogPostId: String) {
        screenNavigator.startArticleActivity(id, ClickSource.LIVE_BLOG)
        liveBlogAnalytics.trackClick(
            element = "read_more",
            objectType = "blog_post_id",
            objectId = liveBlogPostId,
            blogId = state.liveBlog?.id.orEmpty(),
            articleId = id.toString(),
            pageOrder = state.liveBlog?.posts?.indexOfFirst { it.id == liveBlogPostId }.toString()
        )
    }

    override fun onFabClick() {
        state.stagedLiveBlog?.let { stagedLiveBlog ->
            updateState {
                copy(
                    liveBlog = stagedLiveBlog,
                    stagedLiveBlog = null
                )
            }
        }
        sendEvent(LiveBlogContract.Event.ScrollToFirstPost)
        liveBlogAnalytics.trackClick(
            blogId = params.liveBlogId,
            element = "new_update_cta"
        )
    }

    override fun loadMorePosts() {
        state.liveBlog?.let {
            if (it.hasNextPage && state.loadingState == LoadingState.FINISHED) {
                updateState { copy(loadingState = LoadingState.LOADING_MORE) }
                viewModelScope.launch {
                    liveBlogRepository.fetchLiveBlogPosts(
                        it.id,
                        state.currentPage + 1,
                        features.isLiveBlogAdsEnabled
                    ).join()
                    loadAdContent(state.liveBlog)
                    updateState { copy(loadingState = LoadingState.FINISHED) }
                }
            }
        }
    }

    override fun collapseAdView(id: String) {
        val adList = state.adMap.toMutableMap()
        adList.remove(id)
        updateState { copy(adMap = adList) }
    }

    override fun showAdView(ad: AdLocalModel) {
        val adList = state.adMap.toMutableMap()
        adList[ad.id] = ad
        updateState { copy(adMap = adList) }
    }

    override fun dismissBottomSheet() {
        showBottomSheet(null)
    }

    override fun trackLiveBlogContent(liveBlogId: String) {
        liveBlogAnalytics.trackClick(
            element = "in_text_link",
            objectType = "blog_id",
            objectId = liveBlogId
        )
    }

    override fun trackLiveBlogPostContent(liveBlogPostId: String) {
        liveBlogAnalytics.trackClick(
            element = "in_text_link",
            objectType = "blog_post_id",
            objectId = liveBlogPostId,
            blogId = state.liveBlog?.id.orEmpty(),
            pageOrder = state.liveBlog?.posts?.indexOfFirst { it.id == liveBlogPostId }.toString()
        )
    }

    override fun onCleared() {
        adsRepository.clearCache(pageViewId = pageViewId)
        super.onCleared()
    }

    private fun showBottomSheet(modal: LiveBlogContract.ModalSheetType.TextStyleBottomSheet?) {
        updateState { copy(currentBottomSheetModal = modal) }
    }

    private fun trackFabView() {
        if (state.stagedLiveBlog == null) {
            liveBlogAnalytics.trackView(
                blogId = params.liveBlogId,
                element = "new_update_cta",
                boxScoreState = ""
            )
        }
    }

    override fun onInitialPostScroll() {
        updateState { copy(initialPostId = null) }
    }

    private val GameStatus?.toStatus: String
        get() = when (this) {
            GameStatus.FINAL -> "postgame"
            GameStatus.IN_PROGRESS -> "ingame"
            GameStatus.SCHEDULED -> "pregame"
            else -> ""
        }
}

data class LiveBlogState(
    val liveBlog: NativeLiveBlog? = null,
    // After the initial loading is finished,if we receive any updates to the live blog flow
    // we know it was from an update in the subscription so we add to the stagedLiveBlog,
    // this is how we determine if we show pill for new posts.
    val stagedLiveBlog: NativeLiveBlog? = null,
    val initialPostId: String? = null,
    val currentBottomSheetModal: LiveBlogContract.ModalSheetType.TextStyleBottomSheet? = null,
    val contentTextSize: ContentTextSize = ContentTextSize.DEFAULT,
    val loadingState: LoadingState = LoadingState.INITIAL_LOADING,
    val tweetUrlToHtml: Map<String, String> = emptyMap(),
    val adMap: Map<String, AdLocalModel> = emptyMap(),
    val currentPage: Int = 0,
    val hasViewEventBeenSent: Boolean = false,
) : DataState