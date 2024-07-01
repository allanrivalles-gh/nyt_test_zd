package com.theathletic.feed.compose

import com.theathletic.ads.AdAnalytics
import com.theathletic.ads.bridge.data.local.AdEvent
import com.theathletic.ads.repository.AdsRepository
import com.theathletic.feed.compose.data.FeedType
import com.theathletic.feed.compose.ui.ads.FeedAdsPage
import com.theathletic.test.runTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Test

class TrackAdsAnalyticsUseCaseTest {
    private val coroutineContext = UnconfinedTestDispatcher()
    private val page = FeedAdsPage(
        pageViewId = "1fdac676-aa9b-4f5c-9490-3e4ed82d8532",
        feedType = FeedType.FOLLOWING,
    )
    private lateinit var adAnalytics: AdAnalytics
    private lateinit var adsRepository: AdsRepository
    private lateinit var trackAdsAnalytics: TrackAdsAnalyticsUseCase

    private fun setUp(adEvents: StateFlow<AdEvent> = MutableStateFlow(AdEvent.NotInitialized)) {
        adAnalytics = mockk(relaxed = true)
        adsRepository = mockk {
            every { observeAdEvents(pageViewId = page.pageViewId) }.returns(adEvents)
        }
        trackAdsAnalytics = TrackAdsAnalyticsUseCase(
            adAnalytics,
            adsRepository,
        )
    }

    @Test
    fun `tracks page view when called`() = runTest(coroutineContext) {
        setUp()

        val job = launch { trackAdsAnalytics(page) }

        verify { adAnalytics.trackAdPageView(page.pageViewId, page.feedType.adsAnalyticsView!!) }

        job.cancel()
    }

    @Test
    fun `tracks any AdEvent emitted by the repository after being called`() = runTest(coroutineContext) {
        val events = listOf(
            AdEvent.NotInitialized,
            AdEvent.Attach,
            AdEvent.AdImpression(1, null),
        )
        val adEvents = MutableStateFlow(events.first())
        setUp(adEvents = adEvents)

        val job = launch { trackAdsAnalytics(page) }

        verify { adAnalytics.trackAdEvent(page.pageViewId, page.feedType.adsAnalyticsView!!, AdEvent.NotInitialized) }

        for (event in events.drop(1)) {
            adEvents.emit(event)

            verify { adAnalytics.trackAdEvent(page.pageViewId, page.feedType.adsAnalyticsView!!, event) }
        }

        job.cancel()
    }
}