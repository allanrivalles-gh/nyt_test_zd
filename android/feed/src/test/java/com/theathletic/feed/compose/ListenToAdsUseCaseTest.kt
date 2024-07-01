package com.theathletic.feed.compose

import com.google.common.truth.Truth.assertThat
import com.theathletic.ads.data.local.AdLocalModel
import com.theathletic.ads.repository.AdsRepository
import com.theathletic.feed.compose.data.Dropzone
import com.theathletic.feed.compose.data.FeedType
import com.theathletic.feed.compose.ui.ads.FeedAdsPage
import com.theathletic.test.runTest
import io.mockk.Called
import io.mockk.Ordering
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Test
import kotlin.test.fail

class ListenToAdsUseCaseTest {
    private val coroutineContext = UnconfinedTestDispatcher()
    private val page = FeedAdsPage(
        pageViewId = "1fdac676-aa9b-4f5c-9490-3e4ed82d8532",
        feedType = FeedType.FOLLOWING,
    )
    private val configuration = ListenToAdsUseCase.Configuration(
        configCreator = mockk(relaxed = true),
        shouldReplaceAdsAfterImpression = false,
        changeReason = FeedChangeReason.INITIAL_PAGE_LOAD,
    )
    private lateinit var configCreator: AdConfigCreator
    private lateinit var repository: TestRepositoryManager
    private lateinit var setListeningAds: ListenToAdsUseCase

    private fun setUp(shouldDisplayAds: Boolean = true) {
        repository = TestRepositoryManager()
        configCreator = mockk(relaxed = true)
        setListeningAds = ListenToAdsUseCase(
            features = mockk(relaxed = true) {
                every { shouldDisplayAds(page.feedType) }.returns(shouldDisplayAds)
            },
            adsRepository = repository.mock,
        )
    }

    @Test
    fun `should not do any work if ads disabled`() = runTest(coroutineContext) {
        setUp(shouldDisplayAds = false)

        setListeningAds(
            page,
            listOf(Dropzone(id = "1", unitPath = null)),
            configuration,
        )

        verify { repository.mock wasNot Called }
    }

    @Test
    fun `fetch only new ads when next page loaded`() = runTest(coroutineContext) {
        setUp()

        val subscription = AdsChangedSubscription(this)

        subscription.update(
            setListeningAds(
                page,
                listOf(Dropzone(id = "1", unitPath = null)),
                configuration,
            )
        )

        repository.clearVerification()

        subscription.update(
            setListeningAds(
                page,
                listOf(
                    Dropzone(id = "1", unitPath = null),
                    Dropzone(id = "2", unitPath = null),
                ),
                configuration.copy(changeReason = FeedChangeReason.NEXT_PAGE_LOADED),
            )
        )

        // shouldn't fetch again ad that was previously listened to
        repository.verifyFetched(pageViewId = page.pageViewId, adId = "1", exactly = 0)
        repository.verifyFetched(pageViewId = page.pageViewId, adId = "2")

        // but should still receive updates for both
        repository.emitUpdate("1")
        repository.emitUpdate("2")
        assertThat(subscription.updatesCount("1")).isEqualTo(1)
        assertThat(subscription.updatesCount("2")).isEqualTo(1)

        subscription.cancel()
    }

    @Test
    fun `fetch new and existing ads when refreshing from stale`() = runTest(coroutineContext) {
        setUp()

        val subscription = AdsChangedSubscription(this)

        subscription.update(
            setListeningAds(
                page,
                listOf(Dropzone(id = "1", unitPath = null)),
                configuration,
            )
        )

        repository.clearVerification()

        subscription.update(
            setListeningAds(
                page,
                listOf(
                    Dropzone(id = "1", unitPath = null),
                    Dropzone(id = "2", unitPath = null),
                ),
                configuration.copy(changeReason = FeedChangeReason.REFRESH_STALE),
            )
        )

        // should have fetched all ads
        repository.verifyFetched(pageViewId = page.pageViewId, adId = "1")
        repository.verifyFetched(pageViewId = page.pageViewId, adId = "2")

        // should still receive updates for both normally
        repository.emitUpdate("1")
        repository.emitUpdate("2")
        assertThat(subscription.updatesCount("1")).isEqualTo(1)
        assertThat(subscription.updatesCount("2")).isEqualTo(1)

        subscription.cancel()
    }

    @Test
    fun `starts listening for update if set contains ad id`() = runTest(coroutineContext) {
        setUp()

        val subscription = AdsChangedSubscription(this)

        subscription.update(
            setListeningAds(
                page,
                listOf(Dropzone(id = "1", unitPath = null)),
                configuration,
            )
        )

        repository.emitUpdate("1")
        assertThat(subscription.updatesCount("1")).isEqualTo(1)

        subscription.cancel()
    }

    @Test
    fun `stops listening for update if new set does not contain ad id`() = runTest(coroutineContext) {
        setUp()

        val subscription = AdsChangedSubscription(this)

        subscription.update(
            setListeningAds(
                page,
                listOf(Dropzone(id = "1", unitPath = null)),
                configuration,
            )
        )

        subscription.update(
            setListeningAds(
                page,
                listOf(),
                configuration.copy(changeReason = FeedChangeReason.NEXT_PAGE_LOADED),
            )
        )

        repository.emitUpdate("1")
        assertThat(subscription.updatesCount("1")).isEqualTo(0)

        subscription.cancel()
    }

    @Test
    fun `correctly configures repository and pass in correct discard parameter when fetching`() = runTest(coroutineContext) {
        for (shouldReplaceAdsAfterImpression in listOf(false, true)) {
            setUp()

            setListeningAds(
                page,
                listOf(Dropzone(id = "1", unitPath = null)),
                configuration.copy(shouldReplaceAdsAfterImpression = shouldReplaceAdsAfterImpression),
            )

            repository.verifyShouldAllowDiscardingAdsSet(shouldReplaceAdsAfterImpression)

            repository.verifyFetched(
                pageViewId = page.pageViewId,
                adId = "1",
                shouldReplaceDiscarded = shouldReplaceAdsAfterImpression
            )
        }
    }

    @Test
    fun `clears cache before fetching when pulling to refresh if it should not replace ads after impression`() = runTest(coroutineContext) {
        setUp()

        setListeningAds(
            page,
            listOf(Dropzone(id = "1", unitPath = null)),
            configuration.copy(
                shouldReplaceAdsAfterImpression = false,
                changeReason = FeedChangeReason.PULL_TO_REFRESH
            ),
        )

        verify(ordering = Ordering.ORDERED) {
            repository.mock.clearCache(pageViewId = page.pageViewId)
            repository.mock.fetchAd(pageViewId = page.pageViewId, any(), any(), any())
        }
    }

    @Test
    fun `does not clear cache when pulling to refresh if it should replace ads after impression`() = runTest(coroutineContext) {
        setUp()

        setListeningAds(
            page,
            listOf(Dropzone(id = "1", unitPath = null)),
            configuration.copy(
                shouldReplaceAdsAfterImpression = true,
                changeReason = FeedChangeReason.PULL_TO_REFRESH,
            ),
        )

        verify(exactly = 0) { repository.mock.clearCache(pageViewId = any()) }
    }

    @Test
    fun `does not clear cache when refreshing from stale if it should not replace ads after impression`() = runTest(coroutineContext) {
        setUp()

        setListeningAds(
            page,
            listOf(Dropzone(id = "1", unitPath = null)),
            configuration.copy(
                shouldReplaceAdsAfterImpression = false,
                changeReason = FeedChangeReason.REFRESH_STALE,
            ),
        )

        verify(exactly = 0) { repository.mock.clearCache(pageViewId = any()) }
    }
}

private class TestRepositoryManager {
    private val adsFlows = mutableMapOf<String, MutableStateFlow<AdLocalModel?>>()
    val mock = mockk<AdsRepository>(relaxed = true)

    init {
        every { mock.getAd(any(), any()) }.answers {
            val adId = secondArg<String>()
            val flow = adsFlows[adId] ?: MutableStateFlow<AdLocalModel?>(null)
            adsFlows[adId] = flow
            flow
        }
    }

    fun verifyShouldAllowDiscardingAdsSet(value: Boolean) {
        verify { mock.shouldAllowDiscardingAds = value }
    }

    fun verifyFetched(pageViewId: String, adId: String, exactly: Int = 1, shouldReplaceDiscarded: Boolean? = null) {
        verify(exactly = exactly) {
            mock.fetchAd(
                pageViewId = pageViewId,
                adId = adId,
                adConfig = any(),
                shouldReplaceDiscarded = shouldReplaceDiscarded ?: any(),
            )
        }
    }

    fun clearVerification() = clearMocks(
        mock,
        answers = false,
        recordedCalls = true,
        childMocks = false,
        verificationMarks = true,
        exclusionRules = false
    )

    suspend fun emitUpdate(adId: String) {
        val flow = adsFlows[adId] ?: fail()
        val ad = mockk<AdLocalModel>()
        // the only thing that matters in `AdLocalModel` for our tests is the id
        every { ad.id }.answers { adId }
        flow.emit(ad)
    }
}

private class AdsChangedSubscription(private val coroutineScope: CoroutineScope) {
    private val receivedUpdatesCount = hashMapOf<String, Int>()
    private var job: Job? = null

    // this mimic the behavior of `flatMapLatest` in the `FeedViewModel`
    fun update(adChanged: Flow<AdLocalModel>) {
        job?.cancel()
        job = coroutineScope.launch {
            adChanged.collect { ad ->
                var count = receivedUpdatesCount[ad.id] ?: 0
                count += 1
                receivedUpdatesCount[ad.id] = count
            }
        }
    }

    fun updatesCount(adId: String) = receivedUpdatesCount[adId] ?: 0

    fun cancel() = job?.cancel()
}