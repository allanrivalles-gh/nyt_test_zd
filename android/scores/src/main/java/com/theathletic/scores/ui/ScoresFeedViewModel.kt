package com.theathletic.scores.ui

import androidx.lifecycle.viewModelScope
import com.theathletic.analytics.impressions.ImpressionCalculator
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.DateUtility
import com.theathletic.extension.toStringOrEmpty
import com.theathletic.feed.FeedType
import com.theathletic.followable.Followable
import com.theathletic.followable.analyticsType
import com.theathletic.followable.legacyId
import com.theathletic.main.ui.NavigationItem
import com.theathletic.scores.data.ScoresFeedRepository
import com.theathletic.scores.data.local.ScoresFeedLocalModel
import com.theathletic.scores.ui.usecases.FetchScoresFeedForADayUseCase
import com.theathletic.scores.ui.usecases.FetchScoresFeedUseCase
import com.theathletic.scores.ui.usecases.LeagueIdToLeagueCodeUseCase
import com.theathletic.scores.ui.usecases.ObserveFeedNavigationItemsUseCase
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.ComposeViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.Transformer
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ScoresFeedViewModel @AutoKoin constructor(
    private val fetchScoresFeedUseCase: FetchScoresFeedUseCase,
    private val fetchScoresFeedForADayUseCase: FetchScoresFeedForADayUseCase,
    private val scoresFeedRepository: ScoresFeedRepository,
    private val observeNavigationItemsUseCase: ObserveFeedNavigationItemsUseCase,
    private val scoresFeedAnalytics: ScoresFeedAnalyticsHandler,
    private val subscriptionManager: ScoresFeedUpdatesSubscriptionManager,
    private val leagueIdToLeagueCodeUseCase: LeagueIdToLeagueCodeUseCase,
    private val impressionCalculator: ImpressionCalculator,
    private val dateUtility: DateUtility,
    private val dateChangeEventConsumer: DateChangeEventConsumer,
    transformer: ScoresFeedTransformer,
) : AthleticViewModel<ScoresFeedState, ScoresFeedContract.ViewState>(),
    ComposeViewModel,
    ScoresFeedAnalytics by scoresFeedAnalytics,
    ScoresFeedContract.Interaction,
    Transformer<ScoresFeedState, ScoresFeedContract.ViewState> by transformer {

    override val initialState by lazy {
        ScoresFeedState(
            isLoadingFullFeed = true,
            currentFeedId = dateUtility.getCurrentLocalDate()
        )
    }

    init {
        listenForScoresUpdates()
        fetchBaseScoresFeed()
        scoresFeedAnalytics.trackScoresFeedView()
        impressionCalculator.configure(::fireImpressionEvent)
        observeBroadcastReceiverEvents()
    }

    private fun observeBroadcastReceiverEvents() {
        dateChangeEventConsumer.collectIn(viewModelScope) {
            if (it == DateChangeEvents.OnDateChanged) {
                refreshBaseScoresFeed()
            }
        }
    }

    override fun onCleared() {
        subscriptionManager.pause()
        super.onCleared()
    }

    private fun listenForScoresUpdates() {
        scoresFeedRepository.getScoresFeed(state.currentFeedId).collectIn(viewModelScope) { scoresFeed ->
            scoresFeed?.let { feed ->
                if (state.selectedDayIndex == -1) {
                    val topGamesIndex = feed.days.indexOfFirst { it.isTopGames }
                    updateState {
                        copy(
                            selectedDayIndex = topGamesIndex,
                            currentDayId = if (topGamesIndex > -1) feed.days[topGamesIndex].day else ""
                        )
                    }
                }
                updateState {
                    copy(
                        isLoadingDayFeed = false,
                        isLoadingFullFeed = false,
                        scoresFeedLocalModel = feed
                    )
                }

                subscriptionManager.subscribeForUpdates(
                    currentFeedIdentifier = state.currentFeedId,
                    scoresFeed = scoresFeed
                )
            }
        }
        if (state.navigationItems.isEmpty()) {
            viewModelScope.launch {
                observeNavigationItemsUseCase(state.currentFeedId).collectIn(viewModelScope) { navigationItems ->
                    updateState { copy(navigationItems = navigationItems) }
                }
            }
        }
    }

    /*
     * Use this function to fetch the initial base scores feed. This will return the empty day groups
     * except for the current day. Use fetchScoresFeedForDay to receive those additional days or to refresh
     * the current day.
     */
    private fun fetchBaseScoresFeed() {
        viewModelScope.launch {
            fetchScoresFeedUseCase(state.currentFeedId)
                .onSuccess {
                    updateState { copy(isLoadingFullFeed = false) }
                }
                .onFailure {
                    /* Todo: maybe error message? */
                    updateState { copy(isLoadingFullFeed = false) }
                }
        }
    }

    /*
     * Use this to fetch the Scores feed for a particular day using the date that was received
     * in the initial fetchBaseScoresFeed call. The received data will be inserted into the current
     * local data source for that day.
     */
    private fun fetchScoresFeedForDay(dayGroupIdentifier: String) {
        viewModelScope.launch {
            fetchScoresFeedForADayUseCase(
                currentFeedIdentifier = state.currentFeedId,
                dayGroupIdentifier = dayGroupIdentifier
            )
                .onSuccess {
                    updateState { copy(isLoadingDayFeed = false) }
                }
                .onFailure {
                    /* Todo: maybe error message? */
                    updateState { copy(isLoadingDayFeed = false) }
                }
        }
    }

    fun onEvent(event: ScoresFeedContract.Interaction) {
        when (event) {
            is ScoresFeedContract.Interaction.OnNavItemClicked ->
                navigateToHub(event.followableId, NavigationSource.NAVIGATION_BAR)
            is ScoresFeedContract.Interaction.OnTabClicked -> navigateToDayTab(event.index, event.dayId)
            is ScoresFeedContract.Interaction.OnLeagueSectionClicked ->
                navigateToHub(
                    followableId = Followable.Id(event.leagueId.toString(), Followable.Type.LEAGUE),
                    source = NavigationSource.LEAGUE_HEADER
                )
            is ScoresFeedContract.Interaction.OnAllGamesClicked ->
                navigateToHub(
                    followableId = Followable.Id(event.leagueId.toString(), Followable.Type.LEAGUE),
                    source = NavigationSource.LEAGUE_ALL_GAMES
                )
            is ScoresFeedContract.Interaction.OnGameClicked -> navigateToGame(event.gameId)
            is ScoresFeedContract.Interaction.OnDiscussionLinkClicked ->
                navigateToGame(event.gameId, true)
            is ScoresFeedContract.Interaction.OnPullToRefresh -> refreshDayFeed()
        }
    }

    private fun refreshBaseScoresFeed() {
        ScoresFeedState(
            isLoadingFullFeed = true,
            currentFeedId = dateUtility.getCurrentLocalDate()
        )
        fetchBaseScoresFeed()
    }
    enum class NavigationSource { NAVIGATION_BAR, LEAGUE_HEADER, LEAGUE_ALL_GAMES }

    private fun navigateToHub(followableId: Followable.Id, source: NavigationSource) {
        viewModelScope.launch {
            sendEvent(ScoresFeedContract.Event.NavigateToHub(FeedType.fromFollowable(followableId)))
            sendNavigationAnalyticsEvent(followableId, source)
        }
    }

    private suspend fun sendNavigationAnalyticsEvent(
        followableId: Followable.Id,
        source: NavigationSource
    ) {
        val leagueCode = followableId.legacyId?.let { leagueIdToLeagueCodeUseCase(it) }.toStringOrEmpty()
        when (source) {
            NavigationSource.NAVIGATION_BAR ->
                trackClickTeamOrLeagueFromNavigation(
                    entityType = followableId.analyticsType,
                    entityId = leagueCode
                )
            NavigationSource.LEAGUE_HEADER -> trackClickLeagueFromFeed(leagueCode)
            NavigationSource.LEAGUE_ALL_GAMES -> trackClickLeagueAllGamesFromFeed(leagueCode)
        }
    }

    private fun navigateToGame(gameId: String, showDiscussion: Boolean = false) {
        viewModelScope.launch {
            sendEvent(ScoresFeedContract.Event.NavigateToGame(gameId, showDiscussion))
        }
    }

    private fun navigateToDayTab(index: Int, dayId: String) {
        if (state.selectedDayIndex == index) return
        impressionCalculator.clearImpressionCache()
        trackDayChangeClicks(currentIndex = state.selectedDayIndex, newIndex = index)
        viewModelScope.launch {
            // Only request day feed if it has not ben lazy loaded before
            if (scoresFeedRepository.isEmptyDayFeed(
                    currentFeedIdentifier = state.currentFeedId,
                    dayGroupIdentifier = dayId
                )
            ) {
                updateState { copy(isLoadingDayFeed = true) }
                fetchScoresFeedForDay(dayId)
            }
            updateState {
                copy(
                    selectedDayIndex = index,
                    currentDayId = dayId
                )
            }
        }
    }

    private fun refreshDayFeed() {
        if (state.currentDayId.isNotEmpty()) {
            updateState { copy(isLoadingDayFeed = true) }
            fetchScoresFeedForDay(state.currentDayId)
        }
    }

    fun trackSearchBarClicked() {
        scoresFeedAnalytics.trackClickOpenSearchScreen()
    }

    private fun trackDayChangeClicks(currentIndex: Int, newIndex: Int) {
        val currentSlate = formatAnalyticsSlate(currentIndex)
        val newSlate = formatAnalyticsSlate(newIndex)
        scoresFeedAnalytics.trackChangeDatesOnScores(
            slate = newSlate,
            currentSlate = currentSlate,
            hIndex = getAnalyticsHorizontalIndex(newIndex).toString()
        )
    }

    private fun formatAnalyticsSlate(dayIndex: Int): String {
        return state.scoresFeedLocalModel?.days?.get(dayIndex)?.day?.let { day ->
            return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(day)?.let { convertedDate ->
                val slate = SimpleDateFormat("EEE_LLL_d", Locale.getDefault()).format(convertedDate)
                slate.lowercase()
            } ?: ""
        } ?: ""
    }

    private fun getAnalyticsHorizontalIndex(dayIndex: Int): Int {
        return state.scoresFeedLocalModel?.days?.let { days ->
            val topGamesIndex = days.indexOfFirst { it.isTopGames }
            dayIndex - topGamesIndex
        } ?: 0
    }

    fun onViewVisibilityChanged(
        payload: ImpressionPayload,
        pctVisible: Float
    ) {
        impressionCalculator.onViewVisibilityChanged(payload, pctVisible)
    }

    private fun fireImpressionEvent(
        payload: ImpressionPayload,
        startTime: Long,
        endTime: Long
    ) {
        scoresFeedAnalytics.trackGameImpression(payload, startTime, endTime)
    }
}

data class ScoresFeedState(
    val isLoadingFullFeed: Boolean,
    val isLoadingDayFeed: Boolean = false,
    val currentFeedId: String,
    val currentDayId: String = "",
    val scoresFeedLocalModel: ScoresFeedLocalModel? = null,
    val navigationItems: List<NavigationItem> = emptyList(),
    val selectedDayIndex: Int = -1,
) : DataState