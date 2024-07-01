package com.theathletic.scores.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theathletic.analytics.impressions.ImpressionCalculator
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.DateUtility
import com.theathletic.entity.main.League
import com.theathletic.followable.Followable
import com.theathletic.followable.legacyId
import com.theathletic.scores.analytics.ScheduleAnalyticsHandler
import com.theathletic.scores.data.local.Schedule
import com.theathletic.scores.di.FetchScheduleFeedGroupUseCase
import com.theathletic.scores.di.FetchScheduleUseCase
import com.theathletic.scores.di.ObserveScheduleUpdatesUseCase
import com.theathletic.scores.di.RefreshScheduleFeedGroupUseCase
import com.theathletic.scores.di.ScheduleUpdatesSubscriptionManager
import com.theathletic.ui.LoadingState
import com.theathletic.ui.updateState
import com.theathletic.utility.LocaleUtility
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ScheduleViewModel @AutoKoin constructor(
    @Assisted private val params: Params,
    observeScheduleUpdates: ObserveScheduleUpdatesUseCase,
    private val fetchSchedule: FetchScheduleUseCase,
    private val fetchScheduleFeedGroup: FetchScheduleFeedGroupUseCase,
    private val refreshScheduleFeedGroup: RefreshScheduleFeedGroupUseCase,
    private val subscriptionManager: ScheduleUpdatesSubscriptionManager,
    private val analytics: ScheduleAnalyticsHandler,
    private val impressionCalculator: ImpressionCalculator,
    private val dateUtility: DateUtility,
    private val localeUtility: LocaleUtility,
) : ViewModel() {
    private val _viewState = MutableStateFlow(ScheduleViewState())
    val viewState = _viewState.asStateFlow()

    private val _viewEvents = MutableSharedFlow<ScheduleViewEvent>()
    val viewEvents = _viewEvents.asSharedFlow()

    private var entityKey: String
    private var isLeague: Boolean = false
    private var forceSubscriptionCheck = false

    data class Params(
        val followable: Followable.Id
    )

    init {
        entityKey = params.followable.toEntityKey()
        isLeague = params.followable.type == Followable.Type.LEAGUE
        observeScheduleUpdates(entityKey)
            .onEach { it?.let { update -> onScheduleUpdate(update) } }
            .launchIn(viewModelScope)
        loadInitialFeed()
        impressionCalculator.configure(::fireImpressionEvent)
    }

    override fun onCleared() {
        subscriptionManager.pause()
        super.onCleared()
    }

    private fun loadInitialFeed() {
        viewModelScope.launch {
            _viewState.updateState {
                copy(
                    fullLoadingState = LoadingState.INITIAL_LOADING
                )
            }
            fetchSchedule(entityKey, isLeague)
                .onFailure {
                    _viewState.updateState {
                        copy(
                            fullLoadingState = LoadingState.FINISHED,
                            showErrorLoadingDataMessage = true
                        )
                    }
                }.onSuccess {
                    _viewState.updateState {
                        copy(
                            fullLoadingState = LoadingState.FINISHED,
                            showErrorLoadingDataMessage = false
                        )
                    }
                    trackScheduleView()
                }
        }
    }

    fun refreshCurrentSchedule(loadingState: LoadingState = LoadingState.RELOADING) {
        viewModelScope.launch {
            forceSubscriptionCheck = true
            _viewState.updateState {
                copy(
                    fullLoadingState = loadingState
                )
            }
            refreshScheduleFeedGroup(
                entityId = entityKey,
                isLeague = isLeague,
                index = _viewState.value.selectedTab,
                filterId = _viewState.value.selectedFilter?.id
            ).onFailure {
                _viewState.updateState {
                    copy(
                        fullLoadingState = LoadingState.FINISHED,
                        partialLoadingState = LoadingState.FINISHED,
                        showErrorLoadingDataMessage = true
                    )
                }
            }.onSuccess {
                _viewState.updateState {
                    copy(
                        fullLoadingState = LoadingState.FINISHED,
                        partialLoadingState = LoadingState.FINISHED
                    )
                }
            }
        }
    }

    private fun onScheduleUpdate(schedule: Schedule) {
        val selectedTab = schedule.defaultSelectedTab(_viewState.value.selectedTab)
        val scheduleFilters = schedule.filters?.firstOrNull()?.values?.mapToUiModel()
        val selectedFilter = setSelectedFilter(schedule)
        val scheduleFeed = schedule.mapToFeedUiModel(
            selectedTab = selectedTab,
            isUnitedStatesOrCanada = localeUtility.isUnitedStatesOrCanada(),
            dateUtility = dateUtility
        )

        _viewState.updateState {
            copy(
                scheduleTabs = schedule.mapToTabUiModel(),
                scheduleFilters = scheduleFilters,
                selectedFilter = selectedFilter,
                showFilters = scheduleFilters != null && selectedFilter != null,
                showNoGamesMessage = checkForNoGames(scheduleFeed),
                scheduleFeed = scheduleFeed,
                showScheduleNoDataMessage = schedule.isAllDataMissing(),
                selectedTab = selectedTab,
                partialLoadingState = LoadingState.FINISHED
            )
        }

        if (schedule.groups.size > selectedTab) {
            subscriptionManager.subscribeForUpdates(
                entityKey = entityKey,
                groupId = schedule.groups[selectedTab].navItem.id,
                filterId = _viewState.value.selectedFilter?.id,
                forceSubscriptionCheck = forceSubscriptionCheck
            )
            forceSubscriptionCheck = false
        }
    }

    private fun setSelectedFilter(schedule: Schedule) = if (_viewState.value.selectedFilter == null) {
        schedule.filters?.firstOrNull()?.values?.firstOrNull { it.isDefault }?.scheduleFilterUi()
    } else {
        _viewState.value.selectedFilter
    }

    private fun Schedule.isAllDataMissing(): Boolean {
        return groups.isEmpty()
    }

    fun navigateToSchedule(index: Int, id: String) {
        if (_viewState.value.selectedTab == index) return
        impressionCalculator.clearImpressionCache()
        trackNavigatingToSchedule(
            fromIndex = _viewState.value.selectedTab,
            toIndex = index
        )
        viewModelScope.launch {
            _viewState.updateState {
                copy(
                    partialLoadingState = LoadingState.INITIAL_LOADING,
                    showNoGamesMessage = false,
                    selectedTab = index
                )
            }
            fetchScheduleFeedGroup.invoke(
                entityId = entityKey,
                isLeague = isLeague,
                groupId = id,
                filterId = _viewState.value.selectedFilter?.id,
            )
                .onFailure {
                    _viewState.updateState {
                        copy(
                            partialLoadingState = LoadingState.FINISHED,
                            showErrorLoadingDataMessage = true
                        )
                    }
                }.onSuccess { feedGroup ->
                    if (feedGroup != null) {
                        val scheduleFeed = feedGroup.toUiModel(
                            isUnitedStatesOrCanada = localeUtility.isUnitedStatesOrCanada(),
                            dateUtility = dateUtility
                        )
                        _viewState.updateState {
                            copy(
                                partialLoadingState = LoadingState.FINISHED,
                                showErrorLoadingDataMessage = false,
                                showNoGamesMessage = checkForNoGames(scheduleFeed),
                                scheduleFeed = scheduleFeed,
                            )
                        }
                        subscriptionManager.subscribeForUpdates(
                            entityKey = entityKey,
                            groupId = id,
                            filterId = _viewState.value.selectedFilter?.id,
                            forceSubscriptionCheck = false
                        )
                    } else {
                        _viewState.updateState { copy(partialLoadingState = LoadingState.FINISHED) }
                    }
                    trackScheduleView()
                }
        }
    }

    private fun checkForNoGames(scheduleFeed: List<ScoresFeedUI.FeedGroup>) =
        scheduleFeed.isEmpty() || scheduleFeed.all { it.games.isEmpty() }

    fun navigateToGame(index: Int, gameId: String, showDiscussion: Boolean = false) {
        viewModelScope.launch {
            analytics.trackNavigateToBoxScoresClick(gameId, index)
            _viewEvents.emit(ScheduleViewEvent.NavigateToGame(gameId, showDiscussion))
        }
    }

    fun navigateToTicketsSite(url: String, provider: String) {
        trackTicketsClick(provider)
        viewModelScope.launch {
            _viewEvents.emit(ScheduleViewEvent.NavigateToTicketingSite(url))
        }
    }

    fun onFilterOptionSelected(option: String, index: Int) {
        val newSelectedFilter = _viewState.value.scheduleFilters?.get(index)
        if (newSelectedFilter?.id != _viewState.value.selectedFilter?.id) {
            val selectedFilter = _viewState.value.scheduleFilters?.get(index)
            forceSubscriptionCheck = true
            _viewState.updateState {
                copy(
                    selectedFilter = selectedFilter,
                    showFilters = _viewState.value.scheduleFilters != null && selectedFilter != null,
                    partialLoadingState = LoadingState.INITIAL_LOADING,
                    showNoGamesMessage = false
                )
            }
            refreshCurrentSchedule(LoadingState.NONE)
        }
    }

    private fun Followable.Id.toEntityKey(): String {
        return if (type == Followable.Type.LEAGUE) {
            League.parseFromId(legacyId).name.uppercase()
        } else {
            id
        }
    }

    private fun trackNavigatingToSchedule(fromIndex: Int, toIndex: Int) {
        val newSlate = _viewState.value.scheduleTabs.toSlate(toIndex)
        val oldSlate = _viewState.value.scheduleTabs.toSlate(fromIndex)
        if (isLeague) {
            analytics.trackLeagueScheduleNavigationClick(entityKey, newSlate.orEmpty(), oldSlate.orEmpty())
        } else {
            analytics.trackTeamScheduleNavigationClick(entityKey, newSlate.orEmpty(), oldSlate.orEmpty())
        }
    }

    private fun trackScheduleView() {
        _viewState.value.scheduleTabs.toSlate(_viewState.value.selectedTab)?.let { slate ->
            if (isLeague) {
                analytics.trackLeagueScheduleView(entityKey, slate)
            } else {
                analytics.trackTeamScheduleView(entityKey, slate)
            }
        }
    }

    private fun trackTicketsClick(provider: String) {
        if (isLeague) {
            _viewState.value.scheduleTabs.toSlate(_viewState.value.selectedTab)?.let { slate ->
                analytics.trackNavigationToLeagueTicketsWebSite(entityKey, slate, provider)
            }
        } else {
            // No slate for team schedules at the moment
            analytics.trackNavigationToTeamTicketsWebSite(entityKey, provider)
        }
    }

    private fun List<ScoresFeedUI.DayTabItem>.toSlate(selectedTab: Int): String? {
        return if (size > selectedTab) getOrNull(selectedTab)?.payload?.slate else null
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
        analytics.trackImpression(payload, startTime, endTime, isLeague)
    }
}