package com.theathletic.feed.compose.ui

import android.util.Size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theathletic.ads.data.local.AdLocalModel
import com.theathletic.analytics.data.ObjectType.ARTICLE_ID
import com.theathletic.analytics.data.ObjectType.BLOG_ID
import com.theathletic.analytics.data.ObjectType.COMMENT_ID
import com.theathletic.analytics.data.ObjectType.GAME_ID
import com.theathletic.analytics.data.ObjectType.HEADLINE_ID
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.feed.ObserveFeedDropzonesUseCase
import com.theathletic.feed.compose.ClearAdsCacheUseCase
import com.theathletic.feed.compose.FeedChangeReason
import com.theathletic.feed.compose.FetchFeedUseCase
import com.theathletic.feed.compose.ListenToAdsUseCase
import com.theathletic.feed.compose.MarkArticleAsReadUseCase
import com.theathletic.feed.compose.MarkArticleAsSavedUseCase
import com.theathletic.feed.compose.ObserveFeedUseCase
import com.theathletic.feed.compose.PrepareAdConfigCreatorUseCase
import com.theathletic.feed.compose.TrackAdsAnalyticsUseCase
import com.theathletic.feed.compose.data.Dropzone
import com.theathletic.feed.compose.data.Feed
import com.theathletic.feed.compose.data.FeedRequest
import com.theathletic.feed.compose.data.LiveGameUpdatesSubscriptionManager
import com.theathletic.feed.compose.ui.ads.FeedAdsPage
import com.theathletic.feed.compose.ui.ads.FeedAdsState
import com.theathletic.feed.compose.ui.analytics.FeedAnalytics
import com.theathletic.feed.compose.ui.components.FeedDetailsMenuOption
import com.theathletic.feed.compose.ui.components.LiveBlogUiModel
import com.theathletic.feed.compose.ui.components.TopCommentUiModel
import com.theathletic.feed.compose.ui.items.A1UiModel
import com.theathletic.feed.compose.ui.items.HeadlineUiModel
import com.theathletic.feed.compose.ui.items.featuredgame.FeaturedGameUiModel
import com.theathletic.feed.compose.ui.items.mostpopular.MostPopularItemUiModel
import com.theathletic.feed.compose.ui.items.scores.ScoresCarouselItemUiModel
import com.theathletic.feed.compose.ui.reusables.ArticleUiModel
import com.theathletic.feed.ui.models.SeeAllAnalyticsPayload
import com.theathletic.impressions.ImpressionsDispatcher
import com.theathletic.impressions.Visibility
import com.theathletic.links.deep.DeeplinkEventProducer
import com.theathletic.main.MainEvent
import com.theathletic.main.MainEventConsumer
import com.theathletic.ui.updateState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import java.util.UUID

internal class FeedViewModel @AutoKoin constructor(
    private val observeFeed: ObserveFeedUseCase,
    private val observeFeedDropzones: ObserveFeedDropzonesUseCase,
    private val fetchFeed: FetchFeedUseCase,
    private val liveGameUpdatesManager: LiveGameUpdatesSubscriptionManager,
    private val feedUiMapper: FeedUiMapper,
    private val analytics: FeedAnalytics,
    private val deeplinkEventProducer: DeeplinkEventProducer,
    private val mainEventConsumer: MainEventConsumer,
    private val markArticleAsSaved: MarkArticleAsSavedUseCase,
    private val markArticleAsRead: MarkArticleAsReadUseCase,
    private val impressionsDispatcher: ImpressionsDispatcher,
    private val prepareAdConfigCreator: PrepareAdConfigCreatorUseCase,
    private val listenToAds: ListenToAdsUseCase,
    private val clearAdsCache: ClearAdsCacheUseCase,
    private val trackAdsAnalytics: TrackAdsAnalyticsUseCase,
    @Assisted val params: Params,
) : ViewModel() {

    val adsPage = FeedAdsPage(
        pageViewId = UUID.randomUUID().toString(),
        feedType = params.request.feedType,
    )

    data class Params(
        val request: FeedRequest,
        val route: String,
        val ads: Ads?
    ) {
        data class Ads(
            val screenSize: Size,
            val shouldImproveImpressions: Boolean,
            val experiments: List<String>,
            val appVersionName: String,
        )
    }

    private val _viewState = MutableStateFlow(FeedState())
    // TODO(leo): needs to be updated for `REFRESH_STALE`
    private var lastChangeReason: FeedChangeReason
    val viewState = _viewState.asStateFlow()

    private val _viewEvent = MutableSharedFlow<FeedEvent>()
    val viewEvent = merge(
        mainEventConsumer.toFeedEvents(params.route),
        _viewEvent.asSharedFlow()
    )

    init {
        lastChangeReason = FeedChangeReason.INITIAL_PAGE_LOAD
        _viewState.updateState { copy(isLoading = true) }
        fetchFeed(page = 0)

        viewModelScope.launch {
            trackAdsAnalytics(adsPage)
        }

        viewModelScope.launch {
            val feedUpdates = observeFeed(params.request)
            val adUpdates = observeFeedDropzones(params.request).handleAds()
            combine(feedUpdates, adUpdates) { feed, _ -> feed }
                .collect(::onFeedUpdate)
        }

        impressionsDispatcher.listenToImpressionEvents { event ->
            analytics.view(event, params.request.feedType)
        }
    }

    override fun onCleared() {
        clearAdsCache(pageViewId = adsPage.pageViewId)
        liveGameUpdatesManager.pause()
        super.onCleared()
    }

    fun refresh() {
        if (_viewState.value.isFetching) return

        lastChangeReason = FeedChangeReason.PULL_TO_REFRESH
        _viewState.updateState { copy(isRefreshing = true) }
        fetchFeed(page = 0)
    }

    fun fetchNextPage() {
        val pageInfo = _viewState.value.uiModel.pageInfo
        if (pageInfo.hasNextPage.not()) return
        if (_viewState.value.isFetching) return

        lastChangeReason = FeedChangeReason.NEXT_PAGE_LOADED
        _viewState.updateState { copy(isLoadingNextPage = true) }
        fetchFeed(page = pageInfo.currentPage + 1)
    }

    private fun fetchFeed(page: Int) {
        viewModelScope.launch {
            fetchFeed(params.request, page = page)
            _viewState.updateState { copy(isLoading = false, isRefreshing = false, isLoadingNextPage = false) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<List<Dropzone>>.handleAds() = flatMapLatest { dropzones ->
        flow {
            // we emit at least once because ads may be disabled for the feed type
            emit(Unit)

            val ads = params.ads ?: return@flow

            val configCreatorEnvironment = PrepareAdConfigCreatorUseCase.Environment(
                screenSize = ads.screenSize,
                appVersionName = ads.appVersionName,
                experiments = ads.experiments,
            )

            val configCreator = prepareAdConfigCreator(adsPage, configCreatorEnvironment)

            val listeningAdsConfiguration = ListenToAdsUseCase.Configuration(
                shouldReplaceAdsAfterImpression = ads.shouldImproveImpressions,
                configCreator = configCreator,
                changeReason = lastChangeReason,
            )

            listenToAds(adsPage, dropzones, listeningAdsConfiguration).collect { ad ->
                _viewState.updateState { copy(adsState = adsState.updatingAd(ad.asUpdatedAd())) }
                emit(Unit)
            }
        }
    }

    private fun onFeedUpdate(feed: Feed) {
        liveGameUpdatesManager.subscribeToLiveGameUpdates(params.request.key, feed)
        _viewState.updateState { copy(uiModel = feedUiMapper.toUiModel(feed, adsState)) }
    }

    fun itemClicked(item: LayoutUiModel.Item) {
        item.deepLink()?.let {
            deeplinkEventProducer.tryEmit(it.value)
        }
        item.analyticsData?.let {
            when (item) {
                is ArticleUiModel -> it.toClickPayload(ARTICLE_ID)
                is LiveBlogUiModel -> item.analyticsData.toClickPayload(BLOG_ID)
                is A1UiModel -> item.analyticsData.toClickPayload(ARTICLE_ID)
                is HeadlineUiModel -> item.analyticsData.toClickPayload(HEADLINE_ID)
                is MostPopularItemUiModel -> item.analyticsData.toClickPayload(ARTICLE_ID)
                is FeaturedGameUiModel -> item.analyticsData.toClickPayload(GAME_ID)
                is TopCommentUiModel -> item.analyticsData.toClickPayload(COMMENT_ID)
                is ScoresCarouselItemUiModel -> item.analyticsData.toClickPayload(GAME_ID)
                else -> null
            }?.let { payload ->
                analytics.click(payload, params.request.feedType, item.id)
            }
        }
    }

    fun onSeeAllClick(deepLink: String, analyticsPayload: SeeAllAnalyticsPayload) {
        analytics.seeAllClick(analyticsPayload)
        deeplinkEventProducer.tryEmit(deepLink)
    }

    fun itemLongClicked(item: LayoutUiModel.Item) {
        _viewState.updateState { copy(modalSheetOptions = item.detailsMenuOptions) }
    }

    fun onNavLinkClick(item: LayoutUiModel.Item, deepLink: String, linkType: String?) {
        deeplinkEventProducer.tryEmit(deepLink)
        item.analyticsData?.let {
            when (item) {
                is FeaturedGameUiModel -> item.analyticsData.toNavLinkClickPayload(GAME_ID, linkType)
                // Scores Carousel Discuss click analytics handled by the Game Hub
                else -> null
            }?.let { payload ->
                analytics.navLinkClick(payload, item.id)
            }
        }
    }

    fun detailsMenuOptionSelected(option: FeedDetailsMenuOption) {
        dismissModalSheet()
        when (option) {
            is FeedDetailsMenuOption.Save -> markArticleAsSaved(option.articleId, option.isSaved.not())
            is FeedDetailsMenuOption.MarkRead -> viewModelScope.launch {
                markArticleAsRead(option.articleId, option.isRead.not())
            }
            is FeedDetailsMenuOption.Share -> viewModelScope.launch {
                _viewEvent.emit(FeedEvent.Share(option.permalink))
            }
        }
    }

    fun dismissModalSheet() = _viewState.updateState { copy(modalSheetOptions = emptyList()) }

    fun onImpressionChange(visibility: Visibility, item: LayoutUiModel.Item) {
        when (item) {
            is ArticleUiModel -> item.analyticsData.toImpressionsPayload(item.id, ARTICLE_ID)
            is LiveBlogUiModel -> item.analyticsData.toImpressionsPayload(item.id, BLOG_ID)
            is A1UiModel -> item.analyticsData.toImpressionsPayload(item.id, ARTICLE_ID)
            is HeadlineUiModel -> item.analyticsData.toImpressionsPayload(item.id, HEADLINE_ID)
            is MostPopularItemUiModel -> item.analyticsData.toImpressionsPayload(item.id, ARTICLE_ID)
            is FeaturedGameUiModel -> item.analyticsData.toImpressionsPayload(item.id, GAME_ID)
            else -> null
        }?.let { impressionsDispatcher.registerImpression(visibility, it) }
    }

    private fun Flow<MainEvent>.toFeedEvents(currentRoute: String): Flow<FeedEvent> {
        return this.map {
            when (it) {
                is MainEvent.ScrollToTop -> {
                    if (it.route == currentRoute)
                        FeedEvent.ScrollToTop
                    else null
                }
                else -> null
            }
        }.filterNotNull()
    }
}

internal fun AdLocalModel.asUpdatedAd() = FeedAdsState.UpdatedAd(
    id = id,
    isCollapsed = collapsed,
    view = adView?.view
)