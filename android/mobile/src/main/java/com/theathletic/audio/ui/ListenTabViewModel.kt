package com.theathletic.audio.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.theathletic.adapter.main.PodcastPlayButtonController
import com.theathletic.analytics.AnalyticsPayload
import com.theathletic.analytics.data.ClickSource
import com.theathletic.analytics.impressions.ImpressionCalculator
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.audio.ListenFeedDataCacheUseCase
import com.theathletic.audio.data.ListenFeedRepository
import com.theathletic.audio.data.local.ListenFeedData
import com.theathletic.audio.ui.ListenTabContract.ViewState
import com.theathletic.event.NetworkErrorEvent
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedInteractor
import com.theathletic.feed.ui.PodcastDownloadWrapper
import com.theathletic.feed.ui.modules.audio.EmptyPodcastsModule
import com.theathletic.feed.ui.modules.audio.LatestPodcastEpisodesModule
import com.theathletic.feed.ui.modules.audio.LiveRoomModule
import com.theathletic.feed.ui.modules.audio.PodcastCarouselModule
import com.theathletic.feed.ui.modules.audio.PodcastCategoriesModule
import com.theathletic.main.ui.listen.ListenTabEvent
import com.theathletic.main.ui.listen.ListenTabEventProducer
import com.theathletic.manager.IPodcastManager
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.podcast.analytics.PodcastAnalyticsContext
import com.theathletic.podcast.analytics.PodcastNavigationSource
import com.theathletic.podcast.data.LegacyPodcastRepository
import com.theathletic.podcast.data.PodcastRepository
import com.theathletic.podcast.download.PodcastDownloadStateStore
import com.theathletic.podcast.state.PodcastPlayerState
import com.theathletic.podcast.state.PodcastPlayerStateBus
import com.theathletic.podcast.state.minuteStateChangeFlow
import com.theathletic.rooms.analytics.LiveRoomEntryPoint
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await

class ListenTabViewModel @AutoKoin constructor(
    @Assisted private val params: Params,
    @Assisted private val navigator: ScreenNavigator,
    transformer: ListenTabTransformer,
    listenTabAnalytics: ListenTabAnalytics,
    listenFeedDataCacheUseCase: ListenFeedDataCacheUseCase,
    private val analytics: Analytics,
    private val podcastAnalyticsContext: PodcastAnalyticsContext,
    private val impressionCalculator: ImpressionCalculator,
    private val listenFeedRepository: ListenFeedRepository,
    private val podcastRepository: PodcastRepository,
    private val podcastPlayerStateBus: PodcastPlayerStateBus,
    private val podcastPlayButtonController: PodcastPlayButtonController,
    private val podcastManager: IPodcastManager,
    private val podcastDownloadStateStore: PodcastDownloadStateStore,
    private val eventProducer: ListenTabEventProducer,
) : AthleticViewModel<ListenFollowingState, ViewState>(),
    ListenTabContract.Presenter,
    FeedInteractor,
    PodcastPlayButtonController.Callback,
    DefaultLifecycleObserver,
    ListenTabAnalytics by listenTabAnalytics,
    Transformer<ListenFollowingState, ViewState> by transformer {

    data class Params(
        val tabType: ListenTabContract.TabType,
    )

    override val initialState by lazy {
        ListenFollowingState(
            tabType = params.tabType,
            podcastDownloadData = PodcastDownloadWrapper(podcastDownloadStateStore.latestState)
        )
    }

    init {
        listenFeedDataCacheUseCase().collectIn(viewModelScope) { feed ->
            updateState { copy(feedData = feed) }
        }

        impressionCalculator.configure(this::fireImpressionEvent)
        fetchData()
        listenForPodcastStateUpdates()
    }

    private fun listenForPodcastStateUpdates() {
        podcastPlayerStateBus.minuteStateChangeFlow.collectIn(viewModelScope) {
            updateState { copy(podcastPlayerState = it) }
        }

        podcastDownloadStateStore.downloadStates.subscribe {
            updateState { copy(podcastDownloadData = PodcastDownloadWrapper(it)) }
        }.disposeOnCleared()

        podcastRepository.downloadedEpisodes.collectIn(viewModelScope) {
            updateState { copy(downloadedPodcastIds = it.map { podcast -> podcast.id.toString() }) }
        }
    }

    fun fetchData(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                updateState { copy(loadingState = LoadingState.RELOADING) }
            }
            listenFeedRepository.fetchListenFeed().join()
            updateState { copy(loadingState = LoadingState.FINISHED) }
        }
    }

    override fun send(interaction: FeedInteraction) {
        when (interaction) {
            is LiveRoomModule.Interaction.LiveRoomClick -> {
                interaction.analyticsPayload.click(state.tabType, interaction.id)
                navigator.startLiveAudioRoomActivity(
                    id = interaction.id,
                    entryPoint = LiveRoomEntryPoint.LISTEN_TAB,
                )
            }
            is PodcastCarouselModule.Interaction.PodcastClick -> {
                interaction.payload.click(state.tabType, interaction.id)
                navigator.startPodcastDetailActivity(
                    podcastId = interaction.id.toLong(),
                    source = analyticsNavigationSource,
                )
            }
            is PodcastCategoriesModule.Interaction.CategoryClick -> {
                podcastAnalyticsContext.source = PodcastNavigationSource.DISCOVER
                interaction.payload.click(interaction.id)
                navigator.startBrowsePodcastActivity(
                    categoryId = interaction.id.toLong(),
                    categoryName = interaction.name,
                    entryType = interaction.type,
                )
            }

            // Latest Podcast Episodes module
            is LatestPodcastEpisodesModule.Interaction.EpisodeClick -> {
                interaction.payload.click(state.tabType, interaction.id)
                navigator.startPodcastEpisodeDetailActivity(
                    podcastEpisodeId = interaction.id.toLong(),
                    source = analyticsNavigationSource,
                )
            }
            is LatestPodcastEpisodesModule.Interaction.EpisodeLongClick -> showPodcastEpisodeMenu(interaction.id)
            is LatestPodcastEpisodesModule.Interaction.EpisodeMenuClick -> showPodcastEpisodeMenu(interaction.id)
            is LatestPodcastEpisodesModule.Interaction.ControlClick -> {
                viewModelScope.launch {
                    podcastPlayButtonController.onPodcastPlayClick(
                        episodeId = interaction.id.toLong(),
                        callback = this@ListenTabViewModel,
                    )
                }
            }
            is LatestPodcastEpisodesModule.Interaction.MyDownloadsClick ->
                navigator.startDownloadedPodcastActivity()

            is EmptyPodcastsModule.Interaction.DiscoverShowsClick -> {
                viewModelScope.launch {
                    eventProducer.emit(ListenTabEvent.SwitchToDiscoverTab)
                }
            }
        }
    }

    private fun showPodcastEpisodeMenu(episodeId: String) {
        viewModelScope.launch {
            val episode = podcastRepository.podcastEpisodeEntityById(episodeId) ?: return@launch
            sendEvent(
                ListenTabContract.Event.ShowPodcastEpisodeMenu(
                    episodeId = episodeId,
                    isFinished = episode.isFinished,
                    isDownloaded = state.downloadedPodcastIds.contains(episodeId),
                )
            )
        }
    }

    override fun showNetworkOfflineError() {
        sendEvent(NetworkErrorEvent)
    }

    override fun showPayWall() {
        navigator.startPlansActivity(ClickSource.FEED)
    }

    override fun firePlayAnalyticsEvent(podcastEpisodeId: Long, payload: AnalyticsPayload?) {
        analytics.track(
            Event.Podcast.Play(
                view = "listen",
                element = "following",
                object_id = podcastEpisodeId.toString()
            )
        )
    }

    override fun firePauseAnalyticsEvent(podcastEpisodeId: Long, payload: AnalyticsPayload?) {
        analytics.track(
            Event.Podcast.Pause(
                view = "listen",
                element = "following",
                object_id = podcastEpisodeId.toString()
            )
        )
    }

    fun onShareEpisodeClicked(episodeId: String) {
        viewModelScope.launch {
            val episode = podcastRepository.podcastEpisodeEntityById(episodeId) ?: return@launch
            navigator.startShareTextActivity(episode.permalinkUrl)
        }
    }

    fun onMarkPodcastAsPlayedClicked(episodeId: String) {
        if (state.podcastPlayerState.activeTrack?.id?.toString() == episodeId) {
            podcastManager.trackPodcastListenedState(onComplete = true)
            return
        }
        viewModelScope.launch {
            podcastRepository.podcastEpisodeEntityById(episodeId)?.let { episode ->
                podcastManager.trackPodcastListenedState(
                    episodeId = episodeId.toLong(),
                    progress = episode.timeElapsedMs,
                    isFinished = true
                )
            }
        }
    }

    fun onDeletePodcastClick(episodeId: String) {
        viewModelScope.launch {
            LegacyPodcastRepository.deletePodcastEpisode(episodeId.toLong()).await()
        }
    }

    fun onViewVisibilityChanged(
        payload: ImpressionPayload,
        pctVisible: Float
    ) {
        impressionCalculator.onViewVisibilityChanged(payload, pctVisible)
    }

    private fun fireImpressionEvent(
        payload: ImpressionPayload,
        startTime: Long,
        endTime: Long
    ) {
        payload.view(state.tabType, startTime, endTime)
    }

    private val analyticsNavigationSource
        get() = when (params.tabType) {
            ListenTabContract.TabType.FOLLOWING -> PodcastNavigationSource.FOLLOWING
            ListenTabContract.TabType.DISCOVER -> PodcastNavigationSource.DISCOVER
        }
}

data class ListenFollowingState(
    val loadingState: LoadingState = LoadingState.INITIAL_LOADING,
    val tabType: ListenTabContract.TabType,
    val feedData: ListenFeedData.WithEntities? = null,
    val podcastPlayerState: PodcastPlayerState = PodcastPlayerState(),
    val downloadedPodcastIds: List<String> = emptyList(),
    val podcastDownloadData: PodcastDownloadWrapper
) : DataState