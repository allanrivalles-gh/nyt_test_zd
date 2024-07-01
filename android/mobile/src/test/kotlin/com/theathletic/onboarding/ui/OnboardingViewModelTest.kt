package com.theathletic.onboarding.ui

import com.theathletic.analytics.AnalyticsTracker
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.followable.Followable
import com.theathletic.followables.ListFollowableUseCase
import com.theathletic.followables.data.domain.FollowableItem
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.onboarding.GetChosenFollowablesUseCase
import com.theathletic.onboarding.data.OnboardingPodcastItem
import com.theathletic.onboarding.data.OnboardingRepository
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.assertStream
import com.theathletic.test.runTest
import com.theathletic.test.testFlowOf
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    private val analytics = mockk<Analytics>(relaxed = true)
    private val analyticsTracker = mockk<AnalyticsTracker>(relaxed = true)
    private val getChosenFollowablesUseCase = mockk<GetChosenFollowablesUseCase>(relaxed = true)
    private val listFollowablesUseCase = mockk<ListFollowableUseCase>(relaxed = true)
    private val onboardingRepository = mockk<OnboardingRepository>(relaxed = true)
    private val screenNavigator = mockk<ScreenNavigator>(relaxed = true)
    private val transformer = mockk<OnboardingTransformer>(relaxed = true)

    private lateinit var viewModel: OnboardingViewModel

    private val league1FollowableId = Followable.Id.parse("LEAGUE:1")!!
    private val league1Followable = FollowableItem(league1FollowableId, "League 1", "", false)

    private val followableItems = listOf(
        league1Followable
    )

    private val chosenFollowables: List<FollowableItem> = emptyList()

    private val podcast1 = OnboardingPodcastItem("1", "Test Podcast 1", notifEpisodesOn = false)

    private val recommendedPodcasts = listOf(
        podcast1
    )

    @Before
    fun setUp() {
        setUpViewModel()

        coEvery { listFollowablesUseCase.invoke(any()) } returns flowOf(followableItems)
        every { getChosenFollowablesUseCase.invoke() } returns flowOf(chosenFollowables)
        coEvery { onboardingRepository.getRecommendedPodcasts()} returns flowOf(recommendedPodcasts)
    }

    @Test
    fun `state loading false on successful recommendations fetch`() = runTest {
        assertTrue(viewModel.state.isLoading)

        val stateTestFlow = testFlowOf(viewModel.viewState)

        viewModel.initialize()

        assertStream(stateTestFlow)
            .lastEvent { viewState -> assertFalse(viewState.isLoading) }

        stateTestFlow.finish()
    }

    @Test
    fun `clicked followable should be added to chosen list`() {
        viewModel.initialize()

        viewModel.onFollowableClick(league1FollowableId.toString())

        verify { onboardingRepository.setChosenFollowables(listOf(league1Followable)) }
    }

    @Test
    fun `clicked podcast should be added to chosen list`() {
        viewModel.initialize()

        viewModel.onPodcastClick(podcast1.id)

        verify { onboardingRepository.setChosenPodcasts(listOf(podcast1)) }
    }

    private fun setUpViewModel() {
        viewModel = OnboardingViewModel(
            navigator = screenNavigator,
            onboardingRepository = onboardingRepository,
            getChosenFollowablesUseCase = getChosenFollowablesUseCase,
            analytics = analytics,
            analyticsTracker = analyticsTracker,
            listFollowableUseCase = listFollowablesUseCase,
            transformer = transformer
        )
    }
}