package com.theathletic.preferences.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.R
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.entity.local.EntityQueries
import com.theathletic.entity.local.filter
import com.theathletic.event.SnackbarEventRes
import com.theathletic.feed.search.ui.UserTopicListItem
import com.theathletic.followables.ObserveUserFollowingUseCase
import com.theathletic.followables.data.domain.UserFollowing
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.podcast.data.local.PodcastSeriesEntity
import com.theathletic.settings.data.SettingsRepository
import com.theathletic.ui.DataState
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer
import com.theathletic.ui.list.AthleticListViewModel
import com.theathletic.ui.list.SimpleListViewState
import com.theathletic.user.IUserManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class NotificationPreferenceViewModel @AutoKoin constructor(
    @Assisted val screenNavigator: ScreenNavigator,
    val transformer: NotificationPreferenceTransformer,
    private val entityQueries: EntityQueries,
    private val settingsRepository: SettingsRepository,
    private val observeUserFollowing: ObserveUserFollowingUseCase,
    private val updateTopSportNewsNotificationUseCase: UpdateTopSportNewsNotificationUseCase,
    val userManager: IUserManager,
    val analytics: Analytics
) : AthleticListViewModel<NotificationPreferenceState, SimpleListViewState>(),
    NotificationPreferenceContract.ViewModelInteractor,
    DefaultLifecycleObserver,
    Transformer<NotificationPreferenceState, SimpleListViewState> by transformer {

    override val initialState by lazy {
        NotificationPreferenceState(
            loadingState = LoadingState.INITIAL_LOADING,
            followedItems = emptyList(),
            followedPodcasts = emptyList(),
            commentRepliesEnabled = userManager.isCommentRepliesOptIn(),
            topSportsNewsOptIn = userManager.isTopSportsNewsOptIn()
        )
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        initialize()
    }

    fun initialize() {
        listenForDataChanges()
        analytics.track(
            Event.Preferences.View(
                element = "notifications"
            )
        )
    }

    private fun listenForDataChanges() {
        val podcastsFlow = entityQueries.getFollowedFlow(AthleticEntity.Type.PODCAST_SERIES)
            .filter<PodcastSeriesEntity>()

        combine(observeUserFollowing(), podcastsFlow) { followedItems, podcasts ->
            updateState {
                copy(
                    loadingState = LoadingState.FINISHED,
                    followedItems = followedItems,
                    followedPodcasts = podcasts
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun onTopicItemClicked(item: UserTopicListItem) {
        screenNavigator.startUserTopicNotificationFragment(
            item.topicId.id,
            item.topicType,
            item.name
        )
    }

    override fun onPreferenceToggled(item: PreferenceSwitchItem, isOn: Boolean) {
        when (item) {
            is PushNotificationSwitchItem.CommentReplies -> handleCommentRepliesToggle(isOn)
            is PushNotificationSwitchItem.TopSportsNews -> handleTopSportsNewsToggle(isOn)
            is PushNotificationSwitchItem.Podcast -> handlePodcastToggle(item.id, isOn)
            else -> {}
        }
    }

    private fun handleCommentRepliesToggle(isOn: Boolean) {
        settingsRepository.updateCommentNotification(isOn)
        analytics.track(
            Event.Preferences.Click(
                element = if (isOn) "notifications_on" else "notifications_off",
                object_type = "comment"
            )
        )
    }

    private fun handlePodcastToggle(id: String, isOn: Boolean) {
        settingsRepository.updatePodcastNotification(id, isOn)
        analytics.track(
            Event.Preferences.Click(
                element = if (isOn) "notifications_on" else "notifications_off",
                object_type = "podcast_episode",
                id_type = "podcast_series",
                id = id
            )
        )
    }

    private fun handleTopSportsNewsToggle(optIn: Boolean) {
        viewModelScope.launch {
            val currentOptIn = state.topSportsNewsOptIn
            updateState { copy(topSportsNewsOptIn = optIn) }
            delay(200) // This is necessary meanwhile this screen is not composable
            updateTopSportNewsNotificationUseCase(optIn).onFailure {
                sendEvent(SnackbarEventRes(R.string.global_error))
                updateState { copy(topSportsNewsOptIn = currentOptIn) }
            }
        }
        analytics.track(
            Event.Preferences.Click(
                element = if (optIn) "notifications_on" else "notifications_off",
                object_type = "top_sports_news"
            )
        )
    }
}

data class NotificationPreferenceState(
    val loadingState: LoadingState,
    val followedItems: List<UserFollowing>,
    val followedPodcasts: List<PodcastSeriesEntity>,
    val commentRepliesEnabled: Boolean,
    val topSportsNewsOptIn: Boolean
) : DataState