package com.theathletic.feed.compose

import com.theathletic.ads.data.local.AdLocalModel
import com.theathletic.ads.data.local.AdsLocalDataStore
import com.theathletic.ads.data.local.AdsLocalLastEventDataStore
import com.theathletic.ads.data.remote.AdFetcher
import com.theathletic.ads.repository.AdsRepository
import com.theathletic.feed.compose.data.Dropzone
import com.theathletic.feed.compose.data.FeedType
import com.theathletic.feed.compose.ui.ads.FeedAdsPage
import com.theathletic.test.runTest
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Test

class ListenToAdsUseCaseImproveImpressionsExperimentTest {
    @Test
    fun `discards ads on impression and only fetch these ads when pulling to refresh if improving impressions`() = runTest {
        val tester = SetListeningAdsUseCaseTester(shouldImproveImpressions = true)
        tester.setAds(count = 2)
        tester.triggerChange(FeedChangeReason.INITIAL_PAGE_LOAD)
        tester.clearAdFetchedVerifications()

        tester.triggerImpression(1)
        tester.triggerChange(FeedChangeReason.PULL_TO_REFRESH)
        tester.verifyAdFetched(index = 0, exactly = 0)
        tester.verifyAdFetched(index = 1)
    }

    @Test
    fun `re-fetches all ads when pulling to refresh if not improving impressions`() = runTest {
        val tester = SetListeningAdsUseCaseTester(shouldImproveImpressions = false)
        tester.setAds(count = 2)
        tester.triggerChange(FeedChangeReason.INITIAL_PAGE_LOAD)
        tester.clearAdFetchedVerifications()

        tester.triggerImpression(1)
        tester.triggerChange(FeedChangeReason.PULL_TO_REFRESH)
        tester.verifyAdFetched(index = 0)
        tester.verifyAdFetched(index = 1)
    }

    @Test
    fun `discards ads on impression and fetch these ads when content gets stale if improving impressions`() = runTest {
        val tester = SetListeningAdsUseCaseTester(shouldImproveImpressions = true)
        tester.setAds(count = 2)
        tester.triggerChange(FeedChangeReason.INITIAL_PAGE_LOAD)
        tester.clearAdFetchedVerifications()

        tester.triggerImpression(1)
        tester.triggerChange(FeedChangeReason.REFRESH_STALE)
        tester.verifyAdFetched(index = 0, exactly = 0)
        tester.verifyAdFetched(index = 1)
    }

    @Test
    fun `does not fetch any ads when content gets stale if not improving impressions`() = runTest {
        val tester = SetListeningAdsUseCaseTester(shouldImproveImpressions = false)
        tester.setAds(count = 2)
        tester.triggerChange(FeedChangeReason.INITIAL_PAGE_LOAD)
        tester.clearAdFetchedVerifications()

        tester.triggerImpression(1)
        tester.triggerChange(FeedChangeReason.REFRESH_STALE)
        tester.verifyAdFetched(index = 0, exactly = 0)
        tester.verifyAdFetched(index = 1, exactly = 0)
    }
}

private class SetListeningAdsUseCaseTester(private val shouldImproveImpressions: Boolean) {
    private val coroutineScope = CoroutineScope(UnconfinedTestDispatcher())
    private val page = FeedAdsPage(
        pageViewId = "1fdac676-aa9b-4f5c-9490-3e4ed82d8532",
        feedType = FeedType.FOLLOWING,
    )
    private val adFetcher: AdFetcher = mockk {
        // any request to fetch ad will respond right away
        every { fetchAd(any(), any(), any(), any()) }.answers {
            val listener: AdFetcher.AdFetchListener = arg(2)
            listener.onAdLoaded(
                key = firstArg(),
                adConfig = arg(3),
                ad = mockk(),
            )
        }
    }
    private val adConfigCreator: AdConfigCreator = mockk(relaxed = true)
    private var ads = listOf<AdLocalModel>()
    private val adsRepository: AdsRepository
    private val setListeningAds: ListenToAdsUseCase
    private var job: Job? = null

    init {
        adsRepository = AdsRepository(
            AdsLocalDataStore(),
            AdsLocalLastEventDataStore(),
            adFetcher = adFetcher,
            adViewFactory = mockk(relaxed = true),
        )
        setListeningAds = ListenToAdsUseCase(
            features = mockk(relaxed = true) {
                every { shouldDisplayAds(page.feedType) }.returns(true)
            },
            adsRepository = adsRepository,
        )
    }

    fun triggerChange(reason: FeedChangeReason) {
        val configuration = ListenToAdsUseCase.Configuration(
            configCreator = adConfigCreator,
            shouldReplaceAdsAfterImpression = shouldImproveImpressions,
            changeReason = reason,
        )
        // cancelling and resubscribing mimics the behavior of `flatMapLatest` in the `FeedViewModel`
        job?.cancel()
        job = coroutineScope.launch {
            setListeningAds(page, ads.map { Dropzone(id = it.id, unitPath = null) }, configuration)
        }
    }

    fun triggerImpression(index: Int) {
        val ad = ads[index]
        val key = AdsLocalDataStore.AdKey(pageViewId = page.pageViewId, adId = ad.id)
        adsRepository.onAdImpression(key, ad.adConfig, ad = mockk())
    }

    fun setAds(count: Int) {
        ads = List(count) { (it + 1).toString() }
            .map {
                AdLocalModel(
                    id = it,
                    adConfig = adConfigCreator.createConfig(null, it)
                )
            }
    }

    fun verifyAdFetched(index: Int, exactly: Int = 1) {
        val ad = ads[index]
        val key = AdsLocalDataStore.AdKey(pageViewId = page.pageViewId, adId = ad.id)
        verify(exactly = exactly) {
            adFetcher.fetchAd(key, adView = any(), listener = any(), adConfig = ad.adConfig)
        }
    }

    fun clearAdFetchedVerifications() {
        clearMocks(
            adFetcher,
            answers = false,
            recordedCalls = true,
            childMocks = false,
            verificationMarks = true,
            exclusionRules = false
        )
    }
}