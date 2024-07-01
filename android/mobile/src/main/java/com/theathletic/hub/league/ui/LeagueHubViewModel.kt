package com.theathletic.hub.league.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.R
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.main.Sport
import com.theathletic.event.HapticSuccessFeedback
import com.theathletic.event.SnackbarEventRes
import com.theathletic.feed.FeedType
import com.theathletic.followable.Followable
import com.theathletic.followable.rawId
import com.theathletic.followables.FollowItemUseCase
import com.theathletic.followables.UnfollowItemUseCase
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.followables.data.UserFollowingRepository
import com.theathletic.hub.HubTabType
import com.theathletic.repository.user.LeagueLocal
import com.theathletic.scores.remote.toGraphqlLeagueCode
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer
import com.theathletic.utility.LogoUtility
import com.theathletic.utility.coroutines.collectIn
import com.theathletic.utility.orLongDash
import kotlinx.coroutines.launch

typealias LeagueEntity = com.theathletic.entity.main.League

class LeagueHubViewModel @AutoKoin constructor(
    @Assisted private val params: Params,
    private val followableRepository: FollowableRepository,
    private val userFollowingRepository: UserFollowingRepository,
    private val followItemUseCase: FollowItemUseCase,
    private val unfollowItemUseCase: UnfollowItemUseCase,
    private val analytics: LeagueHubAnalyticsHandler,
    transformer: LeagueHubTransformer
) : AthleticViewModel<LeagueHubState, LeagueHubContract.ViewState>(),
    DefaultLifecycleObserver,
    LeagueHubContract.Interaction,
    Transformer<LeagueHubState, LeagueHubContract.ViewState> by transformer {

    data class Params(
        val feedType: FeedType.League,
        val initialTab: HubTabType,
    )

    override val initialState by lazy {
        LeagueHubState(
            loadingState = LoadingState.INITIAL_LOADING,
            feedType = params.feedType,
            currentTab = params.initialTab
        )
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        loadLeagueDetails()
    }

    private fun loadLeagueDetails() {
        params.feedType.asFollowableId?.let { followableId ->
            if (followableId.type != Followable.Type.LEAGUE) return

            viewModelScope.launch {
                val leagueFollowable = followableRepository.getLeague(followableId) ?: return@launch
                val legacyId = leagueFollowable.rawId()

                userFollowingRepository.userFollowingStream.collectIn(viewModelScope) { followingList ->
                    val isFollowed = followingList.any { it.id == leagueFollowable.id }

                    updateState {
                        copy(
                            league = leagueFollowable.league,
                            leagueId = leagueFollowable.league.toGraphqlLeagueCode.rawValue,
                            legacyLeagueId = legacyId,
                            leagueName = leagueFollowable.name,
                            hasActiveBracket = leagueFollowable.hasActiveBracket,
                            leagueLogoUrl = LogoUtility.getColoredLeagueLogoPath(legacyId),
                            sport = leagueFollowable.sportType?.toSport ?: Sport.UNKNOWN,
                            isFollowed = isFollowed,
                            leagueFollowable = leagueFollowable,
                            showScheduleAndStandingTabs = leagueFollowable.hasScores,
                            loadingState = LoadingState.FINISHED,
                        )
                    }
                }
            }
        }
    }

    override fun onBackButtonClicked() {
        sendEvent(LeagueHubContract.Event.NavigateClose)
    }

    override fun onManageFollowClicked() {
        viewModelScope.launch {
            if (!state.isRequestingFollowChange) {
                state.leagueFollowable?.id?.let { id ->
                    updateState { copy(isRequestingFollowChange = true) }
                    if (state.isFollowed) {
                        unfollowItemUseCase(id).onSuccess {
                            sendEvent(SnackbarEventRes(R.string.league_hub_unfollowing_league_message))
                        }
                    } else {
                        followItemUseCase(id).onSuccess {
                            sendEvent(SnackbarEventRes(R.string.league_hub_following_league_message))
                            sendEvent(HapticSuccessFeedback)
                        }
                    }
                    updateState { copy(isRequestingFollowChange = false) }
                }
            }
        }
    }

    override fun onManageNotificationsClicked() {
        state.legacyLeagueId?.let { legacyId ->
            sendEvent(
                LeagueHubContract.Event.NavigateToNotificationsSettings(
                    legacyId = legacyId,
                    name = state.leagueName.orLongDash()
                )
            )
        }
    }

    override fun onTabClicked(fromTab: HubTabType, toTab: HubTabType) {
        updateState { copy(currentTab = toTab) }
        trackTabClickEvent(fromTab, toTab)
    }

    private fun trackTabClickEvent(fromTab: HubTabType, toTab: HubTabType) {
        val leagueId = state.league.toGraphqlLeagueCode.rawValue
        analytics.trackClickOnLeagueHubTab(
            leagueId,
            fromTab.toAnalyticsView(),
            toTab.toAnalyticsView()
        )
    }

    private val String.toSport: Sport
        get() = Sport.valueOf(this.uppercase())
    private fun HubTabType.toAnalyticsView(): HubAnalyticsViewType {
        return when (this) {
            HubTabType.Home -> HubAnalyticsViewType.Home
            HubTabType.Schedule -> HubAnalyticsViewType.Schedule
            HubTabType.Standings -> HubAnalyticsViewType.Standings
            HubTabType.Brackets -> HubAnalyticsViewType.Brackets
            HubTabType.Stats -> HubAnalyticsViewType.Stats
            HubTabType.Roster -> HubAnalyticsViewType.Roster
        }
    }
}

data class LeagueHubState(
    val loadingState: LoadingState,
    val feedType: FeedType,
    val league: LeagueEntity = LeagueEntity.UNKNOWN,
    val hasActiveBracket: Boolean = false,
    val leagueId: String? = null,
    val legacyLeagueId: Long? = null,
    val leagueName: String? = null,
    val leagueLogoUrl: String? = null,
    val sport: Sport = Sport.UNKNOWN,
    val currentTab: HubTabType,
    val leagueFollowable: LeagueLocal? = null,
    val isFollowed: Boolean = false,
    val isRequestingFollowChange: Boolean = false,
    val showScheduleAndStandingTabs: Boolean = false,
) : DataState