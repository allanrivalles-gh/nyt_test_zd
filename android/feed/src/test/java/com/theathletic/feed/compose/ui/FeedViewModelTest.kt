package com.theathletic.feed.compose.ui

import com.google.common.truth.Truth.assertThat
import com.theathletic.datetime.Chronos
import com.theathletic.feed.ObserveFeedDropzonesUseCase
import com.theathletic.feed.compose.ClearAdsCacheUseCase
import com.theathletic.feed.compose.FetchFeedUseCase
import com.theathletic.feed.compose.ListenToAdsUseCase
import com.theathletic.feed.compose.MarkArticleAsReadUseCase
import com.theathletic.feed.compose.MarkArticleAsSavedUseCase
import com.theathletic.feed.compose.ObserveFeedUseCase
import com.theathletic.feed.compose.PrepareAdConfigCreatorUseCase
import com.theathletic.feed.compose.TrackAdsAnalyticsUseCase
import com.theathletic.feed.compose.data.Feed
import com.theathletic.feed.compose.data.FeedType
import com.theathletic.feed.compose.data.LiveGameUpdatesSubscriptionManager
import com.theathletic.feed.compose.feedRequestFixture
import com.theathletic.feed.compose.ui.analytics.FeedAnalytics
import com.theathletic.feed.findDropzones
import com.theathletic.feed.ui.models.SeeAllAnalyticsPayload
import com.theathletic.impressions.ImpressionEvent
import com.theathletic.impressions.ImpressionsDispatcher
import com.theathletic.links.deep.DeeplinkEventProducer
import com.theathletic.main.MainEventConsumer
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.FixedTimeProvider
import com.theathletic.test.assertStream
import com.theathletic.test.runTest
import com.theathletic.test.testFlowOf
import com.theathletic.ui.formatter.CountFormatter
import com.theathletic.ui.formatter.TimeAgoDateFormatter
import com.theathletic.ui.formatter.UpdatedTimeAgoDateFormatter
import com.theathletic.utility.datetime.DateUtilityImpl
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class FeedViewModelTest {

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    private val observeFeedUseCase = mockk<ObserveFeedUseCase>(relaxed = true)
    private val observeFeedDropzonesUseCase = mockk<ObserveFeedDropzonesUseCase>(relaxed = true)
    private val fetchFeedUseCase = mockk<FetchFeedUseCase>()
    private val liveGameUpdatesManager = mockk<LiveGameUpdatesSubscriptionManager>(relaxed = true)
    private val mainEventConsumer = mockk<MainEventConsumer>()
    private val markArticleAsReadUseCase = mockk<MarkArticleAsReadUseCase>()
    private val markArticleAsSavedUseCase = mockk<MarkArticleAsSavedUseCase>()
    private val prepareAdConfigCreatorUseCase = mockk<PrepareAdConfigCreatorUseCase>()
    private val listenToAdsUseCase = mockk<ListenToAdsUseCase>()
    private val trackAdsAnalyticsUseCase = mockk<TrackAdsAnalyticsUseCase>(relaxed = true)
    private val clearAdsCacheUseCase = mockk<ClearAdsCacheUseCase>()
    private val analytics = mockk<FeedAnalytics>(relaxUnitFun = true)
    private val deeplinkEventProducer = mockk<DeeplinkEventProducer>(relaxed = true)
    private val impressionsDispatcher = mockk<ImpressionsDispatcher>(relaxUnitFun = true)

    private lateinit var feedUiMapper: FeedUiMapper
    private lateinit var feedViewModel: FeedViewModel

    private var impressionCallback: (ImpressionEvent) -> Unit = {}

    @Test
    fun `state loading false on a successful feed fetch`() = runTest {
        coEvery { fetchFeedUseCase.invoke(any(), any()) } returns Result.success(Unit)
        setUp()

        val stateTestFlow = testFlowOf(feedViewModel.viewState)

        assertStream(stateTestFlow)
            .lastEvent { feedState -> assertThat(feedState.isLoading).isFalse() }

        stateTestFlow.finish()
    }

    @Test
    fun `state loading is false on a failed feed fetch`() = runTest {
        coEvery { fetchFeedUseCase.invoke(any(), any()) } returns Result.failure(Exception(""))
        setUp()

        val stateTestFlow = testFlowOf(feedViewModel.viewState)

        assertStream(stateTestFlow)
            .lastEvent { feedState -> assertThat(feedState.isLoading).isFalse() }

        stateTestFlow.finish()
    }

    @Test
    fun `state refreshing is false after a successful refresh`() = runTest {
        setUp()
        val stateTestFlow = testFlowOf(feedViewModel.viewState)

        feedViewModel.refresh()

        assertStream(stateTestFlow)
            .lastEvent { feedState -> assertThat(feedState.isRefreshing).isFalse() }

        stateTestFlow.finish()
    }

    @Test
    fun `state refreshing is false after a fail refresh`() = runTest {
        coEvery { fetchFeedUseCase.invoke(any(), any()) } returns Result.failure(Exception(""))
        setUp()

        val stateTestFlow = testFlowOf(feedViewModel.viewState)

        feedViewModel.refresh()

        assertStream(stateTestFlow)
            .lastEvent { feedState -> assertThat(feedState.isRefreshing).isFalse() }

        stateTestFlow.finish()
    }

    @Test
    fun `track analytics view on impression dispatch event`() {
        val event = ImpressionEvent("", "", "", 0)
        setupImpressionCallback()
        setUp()

        impressionCallback.invoke(event)

        verify { analytics.view(event, type = FeedType.DISCOVER) }
    }

    @Test
    fun `emit deeplink event on an see all click`() {
        setUp()

        feedViewModel.onSeeAllClick("com.theathletic.action.deepLink", SeeAllAnalyticsPayload("", 0))

        verify { deeplinkEventProducer.tryEmit("com.theathletic.action.deepLink") }
    }

    @Test
    fun `fetchNextPage sets loading next page and fetches feed with next page when called`() = runTest {
        val result = MutableSharedFlow<Feed>()
        coEvery { observeFeedUseCase.invoke(any()) }.returns(result)
        // we need to also emit dropzones or else feed won't update its state
        coEvery { observeFeedDropzonesUseCase.invoke(any()) }.returns(result.map { it.findDropzones() })
        coEvery { fetchFeedUseCase.invoke(any(), any()) }.coAnswers { result.map { Result.success(Unit) }.first() }

        setUp()

        // we setup initial state with first page already loaded
        result.emit(
            Feed(
                id = "1",
                layouts = listOf(),
                Feed.PageInfo(currentPage = 0, hasNextPage = true),
            )
        )

        clearMocks(
            fetchFeedUseCase,
            answers = false,
            recordedCalls = true,
            childMocks = false,
            verificationMarks = true,
            exclusionRules = false
        )

        feedViewModel.fetchNextPage()

        assertThat(feedViewModel.viewState.value.isLoadingNextPage).isTrue()
        coVerify(exactly = 1) { fetchFeedUseCase.invoke(any(), 1) }

        // and after successfully loading, we test that `isLoadingNextPage` was set to false again
        result.emit(
            Feed(
                id = "1",
                layouts = listOf(),
                Feed.PageInfo(currentPage = 1, hasNextPage = false),
            )
        )

        assertThat(feedViewModel.viewState.value.isLoadingNextPage).isFalse()
    }

    private fun setUp(
        params: FeedViewModel.Params = FeedViewModel.Params(feedRequestFixture(), "route", null)
    ) {
        val timeProvider = FixedTimeProvider()
        val chronos = Chronos(timeProvider)
        feedUiMapper = FeedUiMapper(
            timeProvider,
            UpdatedTimeAgoDateFormatter(chronos),
            TimeAgoDateFormatter(chronos),
            CountFormatter(),
            DateUtilityImpl,
            localeUtility = mockk(),
            features = mockk(),
            scoresCarouselItemFormatter = mockk()
        )
        feedViewModel = FeedViewModel(
            observeFeed = observeFeedUseCase,
            observeFeedDropzones = observeFeedDropzonesUseCase,
            fetchFeed = fetchFeedUseCase,
            liveGameUpdatesManager = liveGameUpdatesManager,
            mainEventConsumer = mainEventConsumer,
            markArticleAsRead = markArticleAsReadUseCase,
            markArticleAsSaved = markArticleAsSavedUseCase,
            feedUiMapper = feedUiMapper,
            analytics = analytics,
            deeplinkEventProducer = deeplinkEventProducer,
            impressionsDispatcher = impressionsDispatcher,
            prepareAdConfigCreator = prepareAdConfigCreatorUseCase,
            listenToAds = listenToAdsUseCase,
            trackAdsAnalytics = trackAdsAnalyticsUseCase,
            clearAdsCache = clearAdsCacheUseCase,
            params = params
        )
    }

    private fun setupImpressionCallback() {
        every { impressionsDispatcher.listenToImpressionEvents(interval = any(), onImpression = any()) }
            .answers { impressionCallback = secondArg<(ImpressionEvent) -> Unit>() }
    }
}