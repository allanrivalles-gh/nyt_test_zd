package com.theathletic.featureintro.ui

import com.theathletic.analytics.IAnalytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.featureintro.data.local.FeatureIntro
import com.theathletic.links.deep.DeeplinkEventProducer
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.assertStream
import com.theathletic.test.runTest
import com.theathletic.test.testFlowOf
import com.theathletic.utility.FeatureIntroductionPreferences
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FeatureIntroViewModelTest {
    @Mock private lateinit var createFeatureIntroUseCase: CreateFeatureIntroUseCase
    @Mock private lateinit var featureIntroPreferences: FeatureIntroductionPreferences
    @Mock private lateinit var analytics: IAnalytics
    @Mock private lateinit var deeplinkProducer: DeeplinkEventProducer

    private lateinit var viewModel: FeatureIntroViewModel

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        val featureIntro = createFeatureIntro(createFeatureNews())
        whenever(createFeatureIntroUseCase.invoke(any())).thenReturn(featureIntro)

        viewModel = FeatureIntroViewModel(
            createFeatureIntroUseCase,
            featureIntroPreferences,
            deeplinkProducer,
            analytics,
        )
    }

    @Test
    fun `set feature intro as viewed on init the view model`() {
        verify(featureIntroPreferences).hasSeenFeatureIntro = true
    }

    @Test
    fun `finish the activity on close intro call`() = runTest {
        val testFlow = testFlowOf(viewModel.viewEvents)

        viewModel.onClose()

        assertStream(testFlow).lastEvent().isEqualTo(FeatureIntroViewEvent.CloseScreen)
        testFlow.finish()
    }

    @Test
    fun `finish the intro on a nextAction in the last page`() = runTest {
        val testFlow = testFlowOf(viewModel.viewEvents)

        viewModel.onNextAction()

        assertStream(testFlow).lastEvent().isEqualTo(FeatureIntroViewEvent.CloseScreen)
        testFlow.finish()
    }

    @Test
    fun `track dismiss click with current page trackID on close feature intro`() {
        viewModel.onClose()

        verify(analytics).track(
            Event.FeatureIntro.Click(view = "track_1", element = "dismiss")
        )
    }

    @Test
    fun `track ok click on finish the intro on a nextAction in the last page`() {
        viewModel.onNextAction()

        verify(analytics).track(
            Event.FeatureIntro.Click(view = "track_1", element = "ok")
        )
    }

    @Test
    fun `track viewed page on page change`() {
        viewModel.onPageChanged(0)

        verify(analytics).track(
            Event.FeatureIntro.View(view = "track_1")
        )
    }

    private fun createFeatureNews(trackId: String = "track_1") =
        FeatureIntro.IntroPage(trackId, 0, 0, 0, 0)

    private fun createFeatureIntro(vararg pages: FeatureIntro.IntroPage): FeatureIntro {
        return FeatureIntro(pages.toList())
    }
}