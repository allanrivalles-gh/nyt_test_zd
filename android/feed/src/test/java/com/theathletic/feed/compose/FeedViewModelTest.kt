package com.theathletic.feed.compose

import android.util.Size
import com.google.common.truth.Truth.assertThat
import com.theathletic.ads.data.local.AdLocalModel
import com.theathletic.feed.compose.data.Dropzone
import com.theathletic.feed.compose.data.FeedRequest
import com.theathletic.feed.compose.data.FeedType
import com.theathletic.feed.compose.ui.FeedViewModel
import com.theathletic.feed.compose.ui.ads.FeedAdsState
import com.theathletic.feed.compose.ui.asUpdatedAd
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.runTest
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class FeedViewModelTest {
    @get:Rule val coroutineTestRule = CoroutineTestRule()
    private val params = FeedViewModel.Params(
        request = FeedRequest(FeedType.FOLLOWING),
        "route",
        ads = FeedViewModel.Params.Ads(
            screenSize = Size(720, 1280),
            shouldImproveImpressions = false,
            experiments = listOf(),
            appVersionName = "1.0.0",
        ),
    )
    private lateinit var listenToAdsUseCase: ListenToAdsUseCase
    private lateinit var trackAdsAnalyticsUseCase: TrackAdsAnalyticsUseCase
    private lateinit var viewModel: FeedViewModel

    private fun setUp(
        onFeedDropzonesChanged: Flow<List<Dropzone>> = flowOf(),
        onAdChanged: Flow<AdLocalModel> = flowOf(),
    ) {
        listenToAdsUseCase = mockk() {
            val useCase = this
            every { useCase.invoke(any(), any(), any()) }.returns(onAdChanged)
        }
        trackAdsAnalyticsUseCase = mockk(relaxed = true)
        viewModel = FeedViewModel(
            observeFeed = mockk(relaxed = true),
            observeFeedDropzones = mockk {
                val useCase = this
                every { useCase.invoke(any()) }.returns(onFeedDropzonesChanged)
            },
            fetchFeed = mockk(relaxed = true),
            liveGameUpdatesManager = mockk(relaxed = true),
            feedUiMapper = mockk(relaxed = true),
            analytics = mockk(),
            deeplinkEventProducer = mockk(),
            markArticleAsSaved = mockk(),
            markArticleAsRead = mockk(),
            mainEventConsumer = mockk(),
            impressionsDispatcher = mockk(relaxed = true),
            prepareAdConfigCreator = mockk(relaxed = true),
            listenToAds = listenToAdsUseCase,
            clearAdsCache = mockk(relaxed = true),
            trackAdsAnalytics = trackAdsAnalyticsUseCase,
            params = params,
        )
    }

    @Test
    fun `sets listening ads with INITIAL_PAGE_LOAD reason after initialization`() = runTest(coroutineTestRule.dispatcher) {
        val onFeedDropzonesChanged = MutableSharedFlow<List<Dropzone>>()
        setUp(onFeedDropzonesChanged = onFeedDropzonesChanged)

        onFeedDropzonesChanged.emit(listOf())

        verify(exactly = 1) {
            listenToAdsUseCase(
                viewModel.adsPage,
                listOf(),
                match { it.changeReason == FeedChangeReason.INITIAL_PAGE_LOAD && it.shouldReplaceAdsAfterImpression == params.ads?.shouldImproveImpressions }
            )
        }
    }

    @Test
    fun `sets listening ads with PULL_TO_REFRESH reason after a refresh`() = runTest(coroutineTestRule.dispatcher) {
        val onFeedDropzonesChanged = MutableSharedFlow<List<Dropzone>>()
        setUp(onFeedDropzonesChanged = onFeedDropzonesChanged)

        viewModel.refresh()
        onFeedDropzonesChanged.emit(listOf())

        verify(exactly = 1) {
            listenToAdsUseCase(
                viewModel.adsPage,
                listOf(),
                match { it.changeReason == FeedChangeReason.PULL_TO_REFRESH && it.shouldReplaceAdsAfterImpression == params.ads?.shouldImproveImpressions }
            )
        }
    }

    @Test
    fun `sets listening ads with NEXT_PAGE_LOADED reason after a next page request`() = runTest(coroutineTestRule.dispatcher) {
        val onFeedDropzonesChanged = MutableSharedFlow<List<Dropzone>>()
        setUp(onFeedDropzonesChanged = onFeedDropzonesChanged)

        viewModel.fetchNextPage()
        onFeedDropzonesChanged.emit(listOf())

        verify(exactly = 1) {
            listenToAdsUseCase(
                viewModel.adsPage,
                listOf(),
                match { it.changeReason == FeedChangeReason.NEXT_PAGE_LOADED && it.shouldReplaceAdsAfterImpression == params.ads?.shouldImproveImpressions }
            )
        }
    }

    @Test
    fun `updates the state to reflect changes on the ads`() = runTest(coroutineTestRule.dispatcher) {
        val onFeedDropzonesChanged = MutableSharedFlow<List<Dropzone>>()
        val onAdChanged = MutableSharedFlow<AdLocalModel>()
        setUp(
            onFeedDropzonesChanged = onFeedDropzonesChanged,
            onAdChanged = onAdChanged,
        )

        // we gotta emit a feed dropzones change so that the view model subscribes to changes in the ads
        onFeedDropzonesChanged.emit(listOf())

        var expectedState = FeedAdsState()
        assertThat(viewModel.viewState.value.adsState).isEqualTo(expectedState)

        val anAdWithoutAViewAndNotCollapsed = AdLocalModel(
            "1",
            adConfig = mockk(),
            collapsed = false,
        )
        onAdChanged.emit(anAdWithoutAViewAndNotCollapsed)

        expectedState = expectedState.updatingAd(anAdWithoutAViewAndNotCollapsed.asUpdatedAd())
        assertThat(viewModel.viewState.value.adsState).isEqualTo(expectedState)

        val anAdWithAViewAndNotCollapsed = AdLocalModel(
            "2",
            adConfig = mockk(),
            adView = mockk { every { view }.returns(mockk()) },
            collapsed = false,
        )
        onAdChanged.emit(anAdWithAViewAndNotCollapsed)

        expectedState = expectedState.updatingAd(anAdWithAViewAndNotCollapsed.asUpdatedAd())
        assertThat(viewModel.viewState.value.adsState).isEqualTo(expectedState)

        val anAdWithAViewAndCollapsed = AdLocalModel(
            "3",
            adConfig = mockk(),
            adView = mockk { every { view }.returns(mockk()) },
            collapsed = true,
        )
        onAdChanged.emit(anAdWithAViewAndCollapsed)

        expectedState = expectedState.updatingAd(anAdWithAViewAndCollapsed.asUpdatedAd())
        assertThat(viewModel.viewState.value.adsState).isEqualTo(expectedState)
    }

    @Test
    fun `starts tracking analytics on initialization`() {
        setUp()

        coVerify(exactly = 1) { trackAdsAnalyticsUseCase(viewModel.adsPage) }
    }
}