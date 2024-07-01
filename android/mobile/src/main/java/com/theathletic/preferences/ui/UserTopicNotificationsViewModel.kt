package com.theathletic.preferences.ui

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.settings.UserTopicsBaseItem
import com.theathletic.entity.settings.UserTopicsItemAuthor
import com.theathletic.entity.settings.UserTopicsItemLeague
import com.theathletic.entity.settings.UserTopicsItemTeam
import com.theathletic.followable.Followable
import com.theathletic.followable.FollowableId
import com.theathletic.preferences.notifications.FollowableNotificationSettings
import com.theathletic.preferences.notifications.FollowableNotificationSettingsRepository
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.LegacyAthleticListViewModel
import com.theathletic.ui.list.list
import kotlinx.coroutines.launch
import timber.log.Timber

class UserTopicNotificationsViewModel @AutoKoin constructor(
    @Assisted extras: Bundle,
    private val repository: FollowableNotificationSettingsRepository,
    private val analytics: Analytics
) : LegacyAthleticListViewModel() {

    private val _uiModels = MutableLiveData<List<UiModel>>()
    override val uiModels: LiveData<List<UiModel>> = _uiModels

    private val topicId = extras.getLong(UserTopicNotificationsActivity.EXTRA_ID)
    private val topicType = extras.getString(UserTopicNotificationsActivity.EXTRA_TYPE)!!

    private var followableNotificationSettings: FollowableNotificationSettings? = null

    init {
        viewModelScope.launch {
            val id = FollowableId(
                topicId.toString(),
                Followable.Type.valueOf(topicType)
            )
            try {
                followableNotificationSettings = repository.getFollowableNotification(id)
                rerender()
            } catch (error: Throwable) {
                Timber.e(error)
            }
        }
    }

    private fun rerender() {
        _uiModels.postValue(generateList())
    }

    fun onItemToggled(item: PreferenceSwitchItem, isOn: Boolean) {
        viewModelScope.launch {
            when (item) {
                is PushNotificationSwitchItem.Stories -> updateStories(isOn)
                is PushNotificationSwitchItem.GameResults -> updateGames(isOn)
                is PushNotificationSwitchItem.GameStart -> updateGameStart(isOn)
                else -> {}
            }
        }
    }

    private suspend fun updateStories(isOn: Boolean) {
        val notificationSettings = followableNotificationSettings?.copy(notifyStories = isOn) ?: return
        followableNotificationSettings = notificationSettings

        try {
            repository.updateStoriesNotification(notificationSettings)
            trackNotificationToggle(
                isOn = isOn,
                objectType = "stories",
                idType = notificationSettings.id.type.name,
                id = notificationSettings.id.toString()
            )
        } catch (error: Throwable) {
            Timber.e(error)
        }
    }

    private suspend fun updateGames(isOn: Boolean) {
        val notificationSettings = followableNotificationSettings?.copy(notifyGames = isOn) ?: return
        followableNotificationSettings = notificationSettings

        try {
            repository.updateGameNotification(notificationSettings)
            trackNotificationToggle(
                isOn = isOn,
                objectType = "game_results",
                idType = "team",
                id = notificationSettings.id.toString()
            )
        } catch (error: Throwable) {
            Timber.e(error)
        }
    }

    private suspend fun updateGameStart(isOn: Boolean) {
        val notificationSettings = followableNotificationSettings?.copy(notifyGamesStart = isOn) ?: return
        followableNotificationSettings = notificationSettings

        try {
            repository.updateGameStartNotification(notificationSettings)
            trackNotificationToggle(
                isOn = isOn,
                objectType = "game_start",
                idType = "team",
                id = notificationSettings.id.toString()
            )
        } catch (error: Throwable) {
            Timber.e(error)
        }
    }

    private fun generateList(): List<UiModel> {
        val notification = followableNotificationSettings ?: return emptyList()
        val isTeam = notification.id.type == Followable.Type.TEAM
        return list {
            section(PreferencesSection.TopicPushNotifs) {
                if (isTeam) {
                    listOf(
                        PushNotificationSwitchItem.Stories(notification.notifyStories, showDivider = true),
                        PushNotificationSwitchItem.GameResults(notification.notifyGames),
                        PushNotificationSwitchItem.GameStart(notification.notifyGamesStart),
                    )
                } else {
                    listOf(PushNotificationSwitchItem.Stories(notification.notifyStories))
                }
            }
        }
    }

    private fun trackNotificationToggle(
        isOn: Boolean,
        objectType: String,
        idType: String,
        id: String
    ) {
        analytics.track(
            Event.Preferences.Click(
                element = if (isOn) "notifications_on" else "notifications_off",
                object_type = objectType,
                id_type = idType,
                id = id
            )
        )
    }

    private val UserTopicsBaseItem.analyticsIdType: String
        get() = when (this) {
            is UserTopicsItemLeague -> "league"
            is UserTopicsItemTeam -> "team"
            is UserTopicsItemAuthor -> "author"
            else -> ""
        }
}