package com.theathletic.hub.team.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.R
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.data.SizedImage
import com.theathletic.data.SizedImages
import com.theathletic.entity.main.League
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
import com.theathletic.repository.user.TeamLocal
import com.theathletic.scores.data.ScoresRepository
import com.theathletic.scores.data.local.TeamDetailsLocalModel
import com.theathletic.scores.remote.toGraphqlLeagueCode
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer
import com.theathletic.utility.LogoUtility
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.launch

class TeamHubViewModel @AutoKoin constructor(
    @Assisted private val params: Params,
    private val followableRepository: FollowableRepository,
    private val userFollowingRepository: UserFollowingRepository,
    private val scoresRepository: ScoresRepository,
    private val followItemUseCase: FollowItemUseCase,
    private val unfollowItemUseCase: UnfollowItemUseCase,
    private val analytics: TeamHubAnalyticsHandler,
    private val teamHubEventConsumer: TeamHubEventConsumer,
    transformer: TeamHubTransformer
) : AthleticViewModel<TeamHubState, TeamHubContract.ViewState>(),
    DefaultLifecycleObserver,
    TeamHubContract.Interaction,
    Transformer<TeamHubState, TeamHubContract.ViewState> by transformer {

    data class Params(
        val feedType: FeedType.Team,
        val initialTab: HubTabType,
    )

    private var isRequestingFollowChange = false

    override val initialState by lazy {
        TeamHubState(
            loadingState = LoadingState.INITIAL_LOADING,
            feedType = params.feedType,
            currentTab = params.initialTab,
            hasAutoScrolled = false
        )
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        loadTeamDetails()

        teamHubEventConsumer.collectIn(viewModelScope) {
            if (it is Event.AutoScrollCompleted) autoScrollCompleted()
        }
    }

    private fun loadTeamDetails() {
        params.feedType.asFollowableId?.let { followableId ->
            if (followableId.type != Followable.Type.TEAM) return
            viewModelScope.launch {
                val teamFollowable = (followableRepository.getFollowable(followableId) as? TeamLocal)
                when {
                    teamFollowable == null -> sendEvent(TeamHubContract.Event.NavigateClose)
                    teamFollowable.graphqlId == null -> updateStateForLegacyTeam(teamFollowable)
                    else -> teamFollowable.graphqlId?.let { teamId ->
                        updateStateForTeam(teamFollowable, teamId)
                    }
                }

                userFollowingRepository.userFollowingStream.collectIn(viewModelScope) {
                    updateState {
                        copy(
                            isFollowed = it.any { it.id == teamFollowable?.id }
                        )
                    }
                }
            }
        }
    }

    private suspend fun updateStateForTeam(teamFollowable: TeamLocal, teamId: String) {
        val teamDetails = scoresRepository.getTeamDetails(teamId)
        if (teamDetails != null) {
            updateState {
                copy(
                    teamId = teamId,
                    teamFollowable = teamFollowable,
                    teamName = teamFollowable.name,
                    teamContrastColor = teamFollowable.colorScheme.iconContrastColor,
                    teamLogos = teamDetails.logoUrls,
                    league = teamDetails.league,
                    sport = teamDetails.sport,
                    teamStanding = teamDetails.currentStanding,
                    showStatsAndRosterTabs = teamDetails.isInternationalTeam.not(),
                    loadingState = LoadingState.FINISHED,
                )
            }
            analytics.trackViewOfHomeFeed(teamId, teamDetails.league.toGraphqlLeagueCode.rawValue)
        } else {
            val leagueFollowable = followableRepository.getLeague(teamFollowable.leagueId)
            updateState {
                copy(
                    teamId = teamId,
                    teamFollowable = teamFollowable,
                    teamName = teamFollowable.name,
                    teamContrastColor = teamFollowable.colorScheme.iconContrastColor,
                    teamLogos = getTeamLogos(teamFollowable),
                    league = leagueFollowable?.league ?: this.league,
                    sport = getSportForLeague(leagueFollowable),
                    teamStanding = null,
                    showStatsAndRosterTabs = false,
                    loadingState = LoadingState.FINISHED,
                )
            }
            leagueFollowable?.league?.toGraphqlLeagueCode?.rawValue?.also { leagueId ->
                analytics.trackViewOfHomeFeed(teamId, leagueId)
            }
        }
    }

    private suspend fun updateStateForLegacyTeam(teamFollowable: TeamLocal) {
        followableRepository.getLeague(teamFollowable.leagueId)?.let { leagueFollowable ->
            updateState {
                copy(
                    teamId = null,
                    teamFollowable = teamFollowable,
                    teamName = teamFollowable.name,
                    teamContrastColor = teamFollowable.colorScheme.iconContrastColor,
                    teamLogos = getTeamLogos(teamFollowable),
                    league = leagueFollowable.league,
                    sport = getSportForLeague(leagueFollowable),
                    teamStanding = null,
                    isLegacyTeam = true,
                    showStatsAndRosterTabs = false,
                    loadingState = LoadingState.FINISHED,
                )
            }
        }
    }

    override fun onBackButtonClicked() {
        sendEvent(TeamHubContract.Event.NavigateClose)
    }

    override fun onManageFollowClicked() {
        viewModelScope.launch {
            if (!isRequestingFollowChange) {
                state.teamFollowable?.id?.let { id ->
                    isRequestingFollowChange = true
                    if (state.isFollowed) {
                        unfollowItemUseCase(id).onSuccess {
                            sendEvent(SnackbarEventRes(R.string.team_hub_unfollowing_team_message))
                        }
                    } else {
                        followItemUseCase(id).onSuccess {
                            sendEvent(SnackbarEventRes(R.string.team_hub_following_team_message))
                            sendEvent(HapticSuccessFeedback)
                        }
                    }
                    isRequestingFollowChange = false
                }
            }
        }
    }

    override fun onManageNotificationsClicked() {
        viewModelScope.launch {
            state.teamFollowable?.id?.let {
                followableRepository.getTeam(it)?.let { teamFollowable ->
                    sendEvent(
                        TeamHubContract.Event.NavigateToNotificationsSettings(
                            legacyId = teamFollowable.rawId(),
                            name = teamFollowable.name,
                        )
                    )
                }
            }
        }
    }

    override fun onTabClicked(fromTab: HubTabType, toTab: HubTabType) {
        updateState { copy(currentTab = toTab) }
        trackTabClickEvent(toTab)
    }

    private fun trackTabClickEvent(tabType: HubTabType) {
        state.teamId?.let { teamId ->
            val leagueId = state.league.toGraphqlLeagueCode.rawValue
            when (tabType) {
                HubTabType.Home -> analytics.trackClickOnHomeTab(teamId, leagueId)
                HubTabType.Schedule -> analytics.trackClickOnScheduleTab(teamId, leagueId)
                HubTabType.Standings -> analytics.trackClickOnStandingsTab(teamId, leagueId)
                HubTabType.Stats -> analytics.trackClickOnStatsTab(teamId, leagueId)
                HubTabType.Roster -> analytics.trackClickOnRosterTab(teamId, leagueId)
                else -> { /* currently not tracked */
                }
            }
        }
    }

    private fun autoScrollCompleted() {
        updateState { copy(hasAutoScrolled = true) }
    }

    private fun getTeamLogos(teamFollowable: TeamLocal) = listOf(
        SizedImage(
            width = 0,
            height = 0,
            uri = LogoUtility.getTeamLogoPath(teamFollowable.rawId())
        )
    )

    private fun getSportForLeague(leagueFollowable: LeagueLocal?) =
        leagueFollowable?.sportType?.let { sport ->
            Sport.valueOf(sport.uppercase())
        } ?: Sport.UNKNOWN

    private val TeamDetailsLocalModel.isInternationalTeam: Boolean
        get() = type == TeamDetailsLocalModel.TeamType.CLUB && isPrimaryLeague.not()
}

data class TeamHubState(
    val loadingState: LoadingState,
    val teamId: String? = null,
    val league: League = League.UNKNOWN,
    val feedType: FeedType,
    val teamName: String? = null,
    val teamContrastColor: String? = null,
    val teamLogos: SizedImages = emptyList(),
    val sport: Sport = Sport.UNKNOWN,
    val teamStanding: String? = null,
    val currentTab: HubTabType,
    val teamFollowable: TeamLocal? = null,
    val isFollowed: Boolean = false,
    val isLegacyTeam: Boolean = false,
    val hasAutoScrolled: Boolean,
    val showStatsAndRosterTabs: Boolean = true
) : DataState