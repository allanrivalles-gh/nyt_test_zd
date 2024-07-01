package com.theathletic.onboarding.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.analytics.AnalyticsTracker
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.followable.FollowableId
import com.theathletic.followables.ListFollowableUseCase
import com.theathletic.followables.data.domain.Filter
import com.theathletic.followables.data.domain.Followable
import com.theathletic.followables.data.domain.FollowableItem
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.onboarding.GetChosenFollowablesUseCase
import com.theathletic.onboarding.data.OnboardingPodcastItem
import com.theathletic.onboarding.data.OnboardingRepository
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.Transformer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class OnboardingViewModel @AutoKoin constructor(
    @Assisted private val navigator: ScreenNavigator,
    private val onboardingRepository: OnboardingRepository,
    private val getChosenFollowablesUseCase: GetChosenFollowablesUseCase,
    private val analytics: Analytics,
    private val analyticsTracker: AnalyticsTracker,
    private val listFollowableUseCase: ListFollowableUseCase,
    transformer: OnboardingTransformer
) : AthleticViewModel<OnboardingDataState, OnboardingContract.OnboardingViewState>(), Transformer<OnboardingDataState, OnboardingContract.OnboardingViewState> by transformer, OnboardingContract.Interactor, DefaultLifecycleObserver {

    companion object {
        private const val MIN_PODCASTS_TO_DISPLAY = 2
    }

    override val initialState = OnboardingDataState()
    private val followableFilter = Filter.Simple(type = Filter.Type.TEAM)

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        initialize()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        analyticsTracker.startOneOffUploadWork()
    }

    fun initialize() {
        analytics.track(Event.Onboarding.NewOnboardingStart)
        analytics.track(Event.Onboarding.FollowTeamView())

        loadRecommendedPodcasts()

        getChosenFollowablesUseCase()
            .onEach { updateState { copy(chosenFollowables = it) } }
            .catch { e ->
                Timber.e(e, "Exception loading followed items")
                updateState {
                    copy(
                        errorState = OnboardingUi.ErrorState.NetworkErrorLoadingData
                    )
                }
            }
            .launchIn(viewModelScope)

        onboardingRepository.recommendedTeamsStream
            .onEach {
                updateState { copy(recommendedTeams = it, loadingRecommendedTeams = false) }
            }
            .catch { e ->
                Timber.w(e, "Exception loading recommended items")
                updateState {
                    copy(
                        loadingRecommendedTeams = false, errorState = OnboardingUi.ErrorState.NetworkErrorLoadingData
                    )
                }
            }
            .launchIn(viewModelScope)

        listFollowableUseCase(followableFilter)
            .onEach {
                updateState { copy(followableItems = it, loadingFollowableItems = false) }
            }
            .launchIn(viewModelScope)
    }

    override fun onFollowableClick(id: String) {
        val followableId = FollowableId.parse(id) ?: return

        val updatedSelection = state.chosenFollowables.toMutableList().apply {
            val alreadyChosenItem = firstOrNull { it.followableId == followableId }
            if (alreadyChosenItem != null) {
                remove(alreadyChosenItem)
            } else {
                val followableItem = state.followableItems.firstOrNull { it.followableId == followableId }
                followableItem?.also { add(it) }
            }
        }

        updateState { copy(chosenFollowables = updatedSelection) }
        onboardingRepository.setChosenFollowables(updatedSelection)
    }

    override fun onPodcastClick(id: String) {
        updateState { copy(loadingPodcastIds = loadingPodcastIds + id) }
        val chosenPodcasts = state.chosenPodcasts.toMutableList()
        val followedPodcast = chosenPodcasts.firstOrNull { it.id == id }
        if (followedPodcast != null) {
            chosenPodcasts.remove(followedPodcast)
        } else {
            state.podcasts.firstOrNull { it.id == id }?.also { podcast ->
                analytics.track(Event.Podcast.FollowClick(podcast_id = id, source = "onboarding"))
                chosenPodcasts.add(podcast)
            }
        }
        onboardingRepository.setChosenPodcasts(chosenPodcasts)
        updateState {
            copy(
                chosenPodcasts = chosenPodcasts, loadingPodcastIds = loadingPodcastIds - id
            )
        }
    }

    override fun onSearchUpdated(searchText: String) {
        updateState { copy(searchText = searchText) }
        followableFilter.update { Filter.Simple(query = searchText, type = it.type) }
    }

    override fun onTeamGroupSelected(index: Int) {
        updateState { copy(teamGroupIndex = index) }
    }

    override fun onNextClick() {
        if (state.chosenFollowables.isEmpty()) return

        when (state.onboardingStep) {
            is OnboardingUi.OnboardingStep.Teams -> {
                advanceToLeaguesView()
            }
            is OnboardingUi.OnboardingStep.Leagues -> {
                advanceToPodcastsView()
            }
            is OnboardingUi.OnboardingStep.Podcasts -> {
                navigator.startOnboardingPaywall()
                navigator.finishAffinity()
            }
        }
    }

    private fun advanceToLeaguesView() {
        followableFilter.update { Filter.Simple(type = Filter.Type.LEAGUE) }
        analytics.track(Event.Onboarding.FollowLeagueView())
        updateState {
            copy(
                onboardingStep = OnboardingUi.OnboardingStep.Leagues,
                searchText = "",
                loadingFollowableItems = true,
                errorState = null
            )
        }
    }

    private fun advanceToPodcastsView() {
        analytics.track(Event.Onboarding.FollowPodcastView())
        loadRecommendedPodcasts()
        updateState {
            copy(
                onboardingStep = OnboardingUi.OnboardingStep.Podcasts, searchText = "", errorState = null
            )
        }
    }

    override fun onBackClick() {
        when (state.onboardingStep) {
            is OnboardingUi.OnboardingStep.Teams -> navigator.finishActivity()
            is OnboardingUi.OnboardingStep.Leagues -> {
                analytics.track(Event.Onboarding.FollowTeamView())
                followableFilter.update { Filter.Simple(type = Filter.Type.TEAM) }
                updateState {
                    copy(
                        onboardingStep = OnboardingUi.OnboardingStep.Teams, searchText = ""
                    )
                }
            }
            is OnboardingUi.OnboardingStep.Podcasts -> {
                analytics.track(Event.Onboarding.FollowLeagueView())
                followableFilter.update { Filter.Simple(type = Filter.Type.LEAGUE) }
                updateState {
                    copy(
                        onboardingStep = OnboardingUi.OnboardingStep.Leagues, searchText = ""
                    )
                }
            }
        }
    }

    private fun loadRecommendedPodcasts() {
        updateState { copy(loadingPodcasts = true) }

        onboardingRepository.getRecommendedPodcasts()
            .onEach { podcasts ->
                if (state.onboardingStep == OnboardingUi.OnboardingStep.Podcasts && state.errorState == null && podcasts.size < MIN_PODCASTS_TO_DISPLAY) {
                    onNextClick()
                }
                updateState {
                    copy(
                        loadingPodcasts = false,
                        podcasts = podcasts,
                        chosenPodcasts = podcasts.filter { onboardingRepository.isChosenPodcast(it) }
                    )
                }
            }
            .catch { e ->
                Timber.w(e, "Exception loading recommended podcasts")
                updateState {
                    copy(
                        loadingPodcasts = false,
                        podcasts = emptyList(),
                        chosenPodcasts = emptyList(),
                        errorState = OnboardingUi.ErrorState.NetworkErrorLoadingData
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}

data class OnboardingDataState(
    val loadingRecommendedTeams: Boolean = true,
    val loadingFollowableItems: Boolean = true,
    val loadingPodcasts: Boolean = false,
    val onboardingStep: OnboardingUi.OnboardingStep = OnboardingUi.OnboardingStep.Teams,
    val teamGroupIndex: Int = 0,
    val recommendedTeams: List<Followable.Team> = emptyList(),
    val followableItems: List<FollowableItem> = emptyList(),
    val podcasts: List<OnboardingPodcastItem> = emptyList(),
    val chosenFollowables: List<FollowableItem> = emptyList(),
    val chosenPodcasts: List<OnboardingPodcastItem> = emptyList(),
    val searchText: String = "",
    val errorState: OnboardingUi.ErrorState? = null,
    val loadingPodcastIds: Set<String> = emptySet()
) : DataState {
    val isLoading: Boolean = loadingRecommendedTeams || loadingFollowableItems || loadingPodcasts
}