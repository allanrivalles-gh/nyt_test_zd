package com.theathletic.profile.ui

import com.google.common.truth.Truth.assertThat
import com.theathletic.analytics.data.ClickSource
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.article.data.ArticleRepository
import com.theathletic.auth.analytics.AuthenticationNavigationSource
import com.theathletic.followables.ObserveUserFollowingUseCase
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.followables.data.domain.UserFollowing
import com.theathletic.followables.test.fixtures.leagueFollowingFixture
import com.theathletic.followables.test.fixtures.localLeagueFixture
import com.theathletic.followables.test.fixtures.teamFollowingFixture
import com.theathletic.followables.test.fixtures.teamIdFixture
import com.theathletic.links.deep.DeeplinkEventProducer
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.podcast.data.PodcastRepository
import com.theathletic.podcast.data.local.PodcastNewEpisodesDataSource
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.scores.data.SupportedLeagues
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.runTest
import com.theathletic.ui.DisplayPreferences
import com.theathletic.ui.binding.ParameterizedString
import com.theathletic.user.IUserManager
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ProfileViewModelTest {
    @get:Rule var coroutineTestRule = CoroutineTestRule()

    private lateinit var profileViewModel: ProfileViewModel

    @Mock private lateinit var userManager: IUserManager
    @Mock private lateinit var transformer: ProfileTransformer
    @Mock private lateinit var analytics: Analytics
    @Mock private lateinit var navigator: ScreenNavigator
    @Mock private lateinit var podcastNewEpisodesDataSource: PodcastNewEpisodesDataSource
    @Mock private lateinit var podcastRepository: PodcastRepository
    @Mock private lateinit var articleRepository: ArticleRepository
    @Mock private lateinit var userDataRepository: IUserDataRepository
    @Mock private lateinit var profileBadger: ProfileBadger
    @Mock private lateinit var deeplinkEventProducer: DeeplinkEventProducer
    @Mock private lateinit var displayPreferences: DisplayPreferences
    @Mock private lateinit var followableRepository: FollowableRepository
    @Mock private lateinit var supportedLeagues: SupportedLeagues
    @Mock private lateinit var profileEventConsumer: ProfileNavigationEventConsumer
    @Mock private lateinit var observeUserFollowing: ObserveUserFollowingUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        whenever(podcastNewEpisodesDataSource.getItem()).thenReturn(flowOf())
        whenever(userDataRepository.userDataFlow).thenReturn(flowOf())
        whenever(articleRepository.getSavedStoriesFlow()).thenReturn(flowOf())

        profileViewModel = ProfileViewModel(
            ProfileViewModel.Params(),
            navigator,
            userManager,
            transformer,
            analytics,
            podcastNewEpisodesDataSource,
            podcastRepository,
            articleRepository,
            userDataRepository,
            profileBadger,
            deeplinkEventProducer,
            displayPreferences,
            followableRepository,
            supportedLeagues,
            profileEventConsumer,
            observeUserFollowing
        )
    }

    @Test
    fun `initialize updates to single list`() = runTest {
        val initialState = ProfileState(
            user = null,
            isUserSubscribed = false,
            isUserFreeTrialEligible = false,
            isDebugMode = true,
            ncaaLeagues = listOf(localLeagueFixture())
        )
        profileViewModel.updateState { copy(ncaaLeagues = listOf(localLeagueFixture())) }
        assertThat(profileViewModel.state).isEqualTo(initialState)
        whenever(observeUserFollowing()).thenReturn(flowOf(listOf(teamFollowingFixture())))

        profileViewModel.initialize()

        assertThat(
            initialState.copy(
                followingItems = listOf(teamFollowingFixture()), followableLoaded = true
            )
        ).isEqualTo(profileViewModel.state)
    }

    @Test
    fun `multiple updates to flow changes the presenter state`() = runTest {
        val initialState =
            ProfileState(
                user = null,
                isUserSubscribed = false,
                false,
                isDebugMode = true,
                followableLoaded = true,
                ncaaLeagues = listOf(localLeagueFixture())
            )
        profileViewModel.updateState { copy(ncaaLeagues = listOf(localLeagueFixture())) }

        val stateFlow = MutableStateFlow(emptyList<UserFollowing>())
        whenever(observeUserFollowing()).thenReturn(stateFlow)

        profileViewModel.initialize()

        assertEquals(initialState, profileViewModel.state)

        val userFollowing = listOf(
            teamFollowingFixture(),
            leagueFollowingFixture()
        )
        stateFlow.value = userFollowing
        assertThat(initialState.copy(followingItems = userFollowing, followableLoaded = true))
            .isEqualTo(profileViewModel.state)

        stateFlow.value = listOf(teamFollowingFixture())
        assertEquals(
            initialState.copy(
                followingItems = listOf(teamFollowingFixture()),
                followableLoaded = true
            ),
            profileViewModel.state
        )
    }

    @Test
    fun `clicking anonymous header navigates to registration screen`() {
        profileViewModel.onAnonymousHeaderClicked()
        verify(navigator).startAuthenticationActivityOnRegistrationScreen(
            AuthenticationNavigationSource.PROFILE
        )
    }

    @Test
    fun `clicking guest pass navigates to registration screen`() {
        profileViewModel.onProfileListItemClick(ProfileListItem.GuestPasses(ParameterizedString("")))
        verify(analytics).track(Event.Profile.Click(element = "guest_pass"))
        verify(navigator).showReferralsActivity("settings")
    }

    @Test
    fun `clicking privacy policy navigates to privacy policy webview`() {
        profileViewModel.onPrivacyPolicyClick()
        verify(navigator).showPrivacyPolicy()
    }

    @Test
    fun `clicking terms of service navigates to terms of service webview`() {
        profileViewModel.onTermsOfServiceClick()
        verify(navigator).showTermsOfService()
    }

    @Test
    fun `clicking edit navigates to manage user topics screen`() {
        profileViewModel.onEditClicked()
        verify(analytics).track(Event.Profile.Click(element = "following", object_type = "edit"))
        verify(navigator).startManageUserTopicsActivity()
    }

    @Test
    fun `clicking add more navigates to manage user topics screen`() {
        profileViewModel.onEditClicked()
        verify(navigator).startManageUserTopicsActivity()
    }

    @Test
    fun `clicking login item navigates to login screen`() {
        profileViewModel.onLoginClicked()
        verify(navigator).startAuthenticationActivityOnLoginScreen(AuthenticationNavigationSource.PROFILE)
    }

    @Test
    fun `clicking profile list item calls analytics and navigates properly`() {
        val item = ProfileListItem.SavedStory(null)
        profileViewModel.onProfileListItemClick(item)
        verify(analytics).track(Event.Profile.Click(element = item.analyticsElement))
        verify(navigator).startSavedStoriesActivity()
    }

    @Test
    fun `clicking subscribe navigates to plans screen`() {
        profileViewModel.onSubscribeClicked()
        verify(navigator).startPlansActivity(ClickSource.SETTINGS)
    }

    @Test
    fun `clicking on followed team deeplinks user to feed page`() = runTest {
        profileViewModel.onFollowingItemClicked(teamIdFixture(id = "1"))
        verify(deeplinkEventProducer).emit("theathletic://feed?team=1")
    }
}