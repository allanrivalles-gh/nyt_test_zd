package com.theathletic.attributionsurvey.ui

import com.google.common.truth.Truth.assertThat
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.attributionsurvey.data.local.AttributionSurvey
import com.theathletic.attributionsurvey.data.local.AttributionSurveyOption
import com.theathletic.onboarding.data.OnboardingRepository
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.assertStream
import com.theathletic.test.runTest
import com.theathletic.test.testFlowOf
import com.theathletic.utility.AttributionPreferences
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SurveyViewModelTest {
    private lateinit var surveyViewModel: SurveyViewModel

    private var transformer = mockk<SurveyStateTransformer>(relaxed = true)
    private var onboardingRepository = mockk<OnboardingRepository>(relaxed = true)
    private var preferences = mockk<AttributionPreferences>(relaxed = true)
    private var analytics = mockk<Analytics>(relaxed = true)
    private var analyticsContext = mockk<SurveyAnalyticsContext>(relaxed = true)

    private lateinit var survey: AttributionSurvey

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    @Before
    fun setup() {
        survey = createTestSurvey()
        every { onboardingRepository.getLocalSurvey() } returns survey
        every { analyticsContext.navigationSource } returns "NAV SOURCE"
        every { analyticsContext.referralObjectId } returns 13
        every { analyticsContext.referralObjectType } returns "article"
        surveyViewModel = SurveyViewModel(
            transformer,
            onboardingRepository,
            preferences,
            analytics,
            analyticsContext
        )
    }

    private fun createTestSurvey(): AttributionSurvey {
        val options = mutableListOf<AttributionSurveyOption>()
        options.add(AttributionSurveyOption("tv", "tv", 0))
        options.add(AttributionSurveyOption("internet", "internet", 1))
        options.add(AttributionSurveyOption("disco", "disco", 2))
        return AttributionSurvey(
            HEADER_TEXT_STRING,
            "...",
            "...",
            options
        )
    }

    @Test
    fun `initialize sends FinishEvent if Repository has no Survey`() = runTest {
        val testFlow = testFlowOf(surveyViewModel.eventConsumer)
        every { onboardingRepository.getLocalSurvey() } returns null

        surveyViewModel.initialize()

        verify { onboardingRepository.getLocalSurvey() }
        confirmVerified(onboardingRepository) // Make sure no other interactions occurred
        assertStream(testFlow).lastEvent().isInstanceOf(SurveyContract.Event.FinishEvent::class.java)

        testFlow.finish()
    }

    @Test
    fun `initialize updates State with Survey`() = runTest {
        surveyViewModel.initialize()
        assertThat(surveyViewModel.state.survey.headerText).isEqualTo(HEADER_TEXT_STRING)
    }

    @Test
    fun `initialize stores preference that survey was seen`() = runTest {
        assertThat(preferences.hasSeenAttributionSurvey).isFalse()
        surveyViewModel.initialize()
        coVerify { preferences.hasSeenAttributionSurvey = true }
    }

    @Test
    fun `initialize posts to repository that survey was seen`() = runTest {
        surveyViewModel.initialize()
        coVerify { onboardingRepository.setHasSeenAttributionSurvey() }
    }

    @Test
    fun `onEntryClick updates state with selected entry`() = runTest {
        surveyViewModel.initialize()
        assertThat(surveyViewModel.state.selectedEntryIndex).isLessThan(0)

        surveyViewModel.onEntryClick(1)

        assertThat(surveyViewModel.state.selectedEntryIndex).isEqualTo(1)
    }

    @Test
    fun `initialize tracks view event if displayed`() = runTest {
        surveyViewModel.initialize()

        coVerify { analytics.trackEvent(any<Event.AttributionSurvey.View>(), any(), any()) }
    }

    @Test
    fun `initialize tracks nothing if early exit`() {
        every { onboardingRepository.getLocalSurvey() } returns null

        surveyViewModel.initialize()

        confirmVerified(analytics)
    }

    @Test
    fun `onDismissClick tracks exit event`() {
        surveyViewModel.initialize()
        surveyViewModel.onDismissClick()

        verify { analytics.trackEvent(any<Event.AttributionSurvey.Exit>(), any(), any()) }
    }

    @Test
    fun `onEntryClick tracks option select event`() {
        surveyViewModel.initialize()
        surveyViewModel.onEntryClick(2)

        verify { analytics.trackEvent(any<Event.AttributionSurvey.SelectOption>(), any(), any()) }
    }

    @Test
    fun `onSubmitClick tracks submit event`() = runTest {
        surveyViewModel.initialize()
        surveyViewModel.onEntryClick(2)
        surveyViewModel.onSubmitClick()

        coVerify { analytics.trackEvent(any<Event.AttributionSurvey.Submit>(), any(), any()) }
    }

    @Test
    fun `onDismissClick sends FinishEvent`() = runTest {
        val testFlow = testFlowOf(surveyViewModel.eventConsumer)

        surveyViewModel.initialize()
        surveyViewModel.onDismissClick()

        assertStream(testFlow).lastEvent().isInstanceOf(SurveyContract.Event.FinishEvent::class.java)
        testFlow.finish()
    }

    @Test
    fun `onSubmitClick submits survey selection and sends FinishEvent`() = runTest {
        val testFlow = testFlowOf(surveyViewModel.eventConsumer)

        val selectedOption = survey.surveyOptions[1]
        surveyViewModel.initialize()
        surveyViewModel.onEntryClick(1)
        surveyViewModel.onSubmitClick()

        coVerify {
            onboardingRepository.submitSurveySelection(
                selectedOption.remoteKey,
                selectedOption.displayOrder
            )
        }
        assertStream(testFlow).lastEvent().isInstanceOf(SurveyContract.Event.FinishEvent::class.java)

        testFlow.finish()
    }

    companion object {
        const val HEADER_TEXT_STRING = "TEST SURVEY"
    }
}