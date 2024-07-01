package com.theathletic.feed.search.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.feed.search.ListSearchFollowablesUseCase
import com.theathletic.feed.search.SearchFollowableItem
import com.theathletic.feed.search.getLeagueCode
import com.theathletic.followable.FollowableId
import com.theathletic.followable.FollowableType
import com.theathletic.followables.data.domain.Filter
import com.theathletic.followables.data.domain.Followable
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.onboarding.data.OnboardingRepository
import com.theathletic.profile.manage.UserTopicId
import com.theathletic.repository.user.toUserTopicId
import com.theathletic.scores.data.remote.toGraphqlLeagueCode
import com.theathletic.scores.ui.ScoresAnalyticsHandler
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.Transformer
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class UserTopicSearchViewModel @AutoKoin constructor(
    @Assisted parameters: UserTopicSearchFragment.UserTopicSearchParameters,
    @Assisted private val navigator: ScreenNavigator,
    private val onboardingRepository: OnboardingRepository,
    private val listSearchFollowablesUseCase: ListSearchFollowablesUseCase,
    private val analytics: Analytics,
    private val scoresAnalytics: ScoresAnalyticsHandler,
    transformer: UserTopicSearchStateTransformer
) : AthleticViewModel<UserTopicSearchState, UserTopicSearch.ViewState>(),
    UserTopicSearch.Interactor,
    DefaultLifecycleObserver,
    Transformer<UserTopicSearchState, UserTopicSearch.ViewState> by transformer {

    private val nonFollowingFilter = Filter.NonFollowing()

    override val initialState by lazy {
        UserTopicSearchState(
            selectedFollowable = parameters.selectedTopic,
            applyScoresFiltering = parameters.applyScoresFiltering
        )
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        initialize()
    }

    fun initialize() {
        analytics.track(Event.FilterFollow.View())

        onboardingRepository.recommendedTeamsStream
            .onEach { updateState { copy(recommendedTeams = it) } }
            .launchIn(viewModelScope)

        listSearchFollowablesUseCase(nonFollowingFilter, showShortName = false)
            .onEach { filteredFollowablesList ->
                updateState {
                    copy(
                        filteredFollowables = filteredFollowablesList,
                        isLoading = false
                    )
                }
            }.launchIn(viewModelScope)

        listSearchFollowablesUseCase(Filter.Simple(), showShortName = true).onEach { followingList ->
            updateState {
                copy(
                    following = followingList,
                    isLoading = false
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun onQueryChanged(query: String) {
        updateState {
            copy(queryText = query)
        }
        nonFollowingFilter.update { Filter.NonFollowing(query = query) }
    }

    override fun onCloseClick() {
        sendEvent(UserTopicSearch.Event.CloseDialog)
    }

    override fun onSearchClearClick() {
        sendEvent(UserTopicSearch.Event.ClearSearch)
    }

    override fun onEditClick() {
        analytics.track(
            Event.FilterFollow.Click(
                element = "following",
                object_type = "edit",
                object_id = ""
            )
        )
        navigator.startManageUserTopicsActivity()
    }

    override fun onTopicClicked(followableId: FollowableId) {
        trackTopicClick(followableId.toUserTopicId(), "following")
        navigateToTopic(followableId)
    }

    // TODO convert the list items to return UserTopicId instead of the full item
    override fun onTopicItemClicked(followableId: FollowableId) {
        val analyticsElement = if (state.queryText.isNotEmpty()) "search" else "suggested"
        trackTopicClick(followableId.toUserTopicId(), analyticsElement)
        navigateToTopic(followableId)
    }

    private fun trackTopicClick(topicId: UserTopicId, element: String) {
        analytics.track(
            Event.FilterFollow.Click(
                element = element,
                object_type = when (topicId) {
                    is UserTopicId.Team -> "team_id"
                    is UserTopicId.League -> "league_id"
                    is UserTopicId.Author -> "author_id"
                },
                object_id = topicId.id.toString()
            )
        )
    }

    private fun navigateToTopic(followableId: FollowableId) {
        state.filteredFollowables.firstOrNull { it.followableId == followableId }?.let { searchFollowableItem ->
            trackClickToScoresSchedule(searchFollowableItem)
            sendEvent(UserTopicSearch.Event.ItemSelected(searchFollowableItem))
            updateState { state.copy(selectedFollowable = followableId) }
        } ?: Timber.e("Topic $followableId not found")
    }

    private fun trackClickToScoresSchedule(topic: SearchFollowableItem) {
        if (state.applyScoresFiltering) {
            when (topic.followableId.type) {
                FollowableType.LEAGUE -> scoresAnalytics.trackNavigateToLeagueScheduleFromTopicSearch(
                    topic.followableId.getLeagueCode().toGraphqlLeagueCode.rawValue
                )
                FollowableType.TEAM -> {
                    scoresAnalytics.trackNavigateToTeamScheduleFromTopicSearch(topic.graphqlId)
                }
                else -> { /* Do Nothing */ }
            }
        }
    }
}

data class UserTopicSearchState(
    val isLoading: Boolean = true,
    val recommendedTeams: List<Followable.Team> = listOf(),
    val filteredFollowables: List<SearchFollowableItem> = listOf(),
    val following: List<SearchFollowableItem> = listOf(),
    val queryText: String = "",
    val selectedFollowable: FollowableId?,
    val applyScoresFiltering: Boolean = false
) : DataState