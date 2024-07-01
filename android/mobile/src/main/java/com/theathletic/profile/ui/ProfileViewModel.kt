package com.theathletic.profile.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.AthleticConfig
import com.theathletic.analytics.data.ClickSource
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.article.data.ArticleRepository
import com.theathletic.auth.analytics.AuthenticationNavigationSource
import com.theathletic.entity.user.UserEntity
import com.theathletic.followable.Followable
import com.theathletic.followable.analyticsType
import com.theathletic.followables.ObserveUserFollowingUseCase
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.followables.data.domain.UserFollowing
import com.theathletic.links.deep.DeeplinkEventProducer
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.podcast.data.PodcastRepository
import com.theathletic.podcast.data.local.PodcastEpisodeEntity
import com.theathletic.podcast.data.local.PodcastNewEpisodesDataSource
import com.theathletic.profile.ui.ProfileContract.ProfileViewState
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.repository.user.LeagueLocal
import com.theathletic.scores.data.SupportedLeagues
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.DayNightMode
import com.theathletic.ui.DisplayPreferences
import com.theathletic.ui.Transformer
import com.theathletic.user.IUserManager
import com.theathletic.user.UserManager
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class ProfileViewModel @AutoKoin constructor(
    @Assisted params: Params,
    @Assisted private val navigator: ScreenNavigator,
    userManager: IUserManager,
    private val transformer: ProfileTransformer,
    private val analytics: Analytics,
    private val podcastNewEpisodesDataSource: PodcastNewEpisodesDataSource,
    private val podcastRepository: PodcastRepository,
    private val articleRepository: ArticleRepository,
    private val userDataRepository: IUserDataRepository,
    private val profileBadger: ProfileBadger,
    private val deeplinkEventProducer: DeeplinkEventProducer,
    private val displayPreferences: DisplayPreferences,
    private val followableRepository: FollowableRepository,
    private val supportedLeagues: SupportedLeagues,
    private val profileNavigationEventConsumer: ProfileNavigationEventConsumer,
    private val observeUserFollowing: ObserveUserFollowingUseCase
) : AthleticViewModel<ProfileState, ProfileViewState>(),
    Transformer<ProfileState, ProfileViewState> by transformer,
    DefaultLifecycleObserver,
    ProfileContract.ProfileInteractor {

    data class Params(val displayTheme: DayNightMode = DayNightMode.NIGHT_MODE)

    override val initialState by lazy {
        ProfileState(
            user = userManager.getCurrentUser(),
            isUserSubscribed = userManager.isUserSubscribed(),
            isUserFreeTrialEligible = userManager.isUserFreeTrialEligible(),
            isStaff = userManager.isStaff,
            isDebugMode = AthleticConfig.DEBUG_TOOLS_ENABLED,
            displayTheme = params.displayTheme
        )
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        initialize()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        analytics.track(Event.Profile.View("profile"))
    }

    fun initialize() {
        listenForUserFollowingUpdates()

        podcastNewEpisodesDataSource.getItem().collectIn(viewModelScope) { episodes ->
            if (episodes == null) return@collectIn

            val episodeEntities = podcastRepository.podcastEpisodeEntitiesById(
                episodes.map { it.id }
            )
            updateState {
                copy(newPodcastEpisodes = episodeEntities.filterNot { it.timeElapsedMs > 0 })
            }
        }

        profileNavigationEventConsumer.collectIn(viewModelScope) {
            sendEvent(ProfileContract.Event.ScrollToTopOfFeed)
        }

        combine(
            userDataRepository.userDataFlow,
            articleRepository.getSavedStoriesFlow()
        ) { userData, savedStories -> userData to savedStories }
            .collectIn(viewModelScope) {
                val (userData, savedStories) = it
                if (userData == null) return@collectIn

                val unreadSavedStories = savedStories
                    .filterNot { story -> userData.articlesRead.contains(story.id.toLong()) }

                updateState { copy(unreadSavedStoryCount = unreadSavedStories.size) }
            }

        loadBadgeData()
    }

    private fun loadBadgeData() = viewModelScope.launch {
        joinAll(
            async { podcastRepository.refreshFollowed() },
            async { articleRepository.fetchSavedStories() }
        )

        val followed = podcastRepository.getFollowedPodcastSeries()
        val canShowPodcastDiscover = profileBadger.shouldShowPodcastDiscoverBadge()

        updateState {
            copy(showDiscoverPodcastBadge = canShowPodcastDiscover && followed.isEmpty())
        }
    }

    override fun onProfileSettingsClicked() {
        analytics.track(Event.Profile.Click(element = "manage_account"))
        navigator.startManageAccountActivity()
    }

    override fun onAnonymousHeaderClicked() {
        navigator.startAuthenticationActivityOnRegistrationScreen(AuthenticationNavigationSource.PROFILE)
    }

    override fun onProfileListItemClick(item: ProfileListItem) {
        analytics.track(Event.Profile.Click(element = item.analyticsElement))

        when (item) {
            ProfileListItem.CreateLiveRoom -> navigator.startCreateLiveRoomActivity()
            ProfileListItem.ScheduledLiveRooms -> navigator.startScheduledLiveRoomActivity()
            is ProfileListItem.SavedStory -> navigator.startSavedStoriesActivity()
            is ProfileListItem.Podcasts -> onPodcastItemClicked()
            ProfileListItem.NewsletterPreferences -> navigator.startNewsletterPreferencesActivity()
            ProfileListItem.NotificationPreferences -> navigator.startNotificationPreferencesActivity()
            ProfileListItem.RegionSelection -> navigator.startRegionSelectionActivity()
            ProfileListItem.GiveGift -> navigator.showGiftSheetDialog()
            ProfileListItem.RateApp -> navigator.startRateAppActivity()
            ProfileListItem.FAQ -> navigator.startFaqActivity()
            ProfileListItem.EmailSupport -> navigator.startContactSupport()
            ProfileListItem.LogOut -> {
                UserManager.logOut()
                navigator.startAuthenticationActivity(false)
                navigator.finishAffinity()
            }
            ProfileListItem.DebugTools -> navigator.startDebugToolsActivity()
            is ProfileListItem.GuestPasses -> navigator.showReferralsActivity("settings")
        }
    }

    private fun onPodcastItemClicked() {
        profileBadger.resetPodcastDiscoverBadge()
        viewModelScope.launch {
            deeplinkEventProducer.emit("theathletic://podcasts")
        }
    }

    override fun onPrivacyPolicyClick() {
        navigator.showPrivacyPolicy()
    }

    override fun onTermsOfServiceClick() {
        navigator.showTermsOfService()
    }

    override fun onEditClicked() {
        analytics.track(
            Event.Profile.Click(
                element = "following",
                object_type = "edit"
            )
        )
        navigator.startManageUserTopicsActivity()
    }

    override fun onAddMoreClicked() {
        analytics.track(
            Event.Profile.Click(
                element = "following",
                object_type = "add"
            )
        )
        navigator.startAddFollowingActivity()
    }

    override fun onLoginClicked() {
        navigator.startAuthenticationActivityOnLoginScreen(AuthenticationNavigationSource.PROFILE)
    }

    override fun onSubscribeClicked() {
        navigator.startPlansActivity(ClickSource.SETTINGS)
    }

    override fun onDayNightToggle(displayTheme: DayNightMode) {
        analytics.track(
            Event.Profile.Click(
                element = "display_theme",
                object_type = when (displayTheme) {
                    DayNightMode.NIGHT_MODE -> "dark"
                    DayNightMode.DAY_MODE -> "light"
                    DayNightMode.SYSTEM -> "system"
                }
            )
        )
        displayPreferences.dayNightMode = displayTheme
        updateState { copy(displayTheme = displayTheme) }
    }

    override fun onFollowingItemClicked(id: Followable.Id) {
        analytics.track(
            Event.Profile.Click(
                element = "following",
                object_type = id.analyticsType,
                object_id = id.id
            )
        )
        viewModelScope.launch {
            deeplinkEventProducer.emit("theathletic://feed?${id.type.name.lowercase()}=${id.id}")
        }
    }

    private fun listenForUserFollowingUpdates() {
        observeUserFollowing().collectIn(viewModelScope) { followingItems ->
            if (state.ncaaLeagues.isEmpty()) {
                val ncaaLeagues = followableRepository.getFilteredLeagues(supportedLeagues.collegeLeagues)
                updateState { copy(ncaaLeagues = ncaaLeagues) }
            }
            updateState {
                copy(
                    followingItems = followingItems,
                    followableLoaded = true
                )
            }
        }
    }
}

data class ProfileState(
    val user: UserEntity?,
    val isUserSubscribed: Boolean,
    val isUserFreeTrialEligible: Boolean,
    val isStaff: Boolean = false,
    val followingItems: List<UserFollowing> = emptyList(),
    val followableLoaded: Boolean = false,
    val ncaaLeagues: List<LeagueLocal> = emptyList(),

    // Badge info
    val newPodcastEpisodes: List<PodcastEpisodeEntity> = emptyList(),
    val showDiscoverPodcastBadge: Boolean = false,
    val unreadSavedStoryCount: Int = 0,

    val isDebugMode: Boolean = false,
    val displayTheme: DayNightMode = DayNightMode.NIGHT_MODE
) : DataState