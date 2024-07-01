package com.theathletic.preferences.notifications

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.followable.Followable
import com.theathletic.followable.FollowableId
import com.theathletic.followable.legacyId
import com.theathletic.settings.data.remote.SettingsRestApi
import com.theathletic.user.FollowableNotificationSettingsDao
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async

class FollowableNotificationSettingsRepository @AutoKoin constructor(
    private val settingsRestApi: SettingsRestApi,
    private val followableNotificationSettingsDao: FollowableNotificationSettingsDao,
    dispatcherProvider: DispatcherProvider
) {

    private val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    suspend fun getFollowableNotification(followableId: FollowableId): FollowableNotificationSettings {
        val notification = followableNotificationSettingsDao.getSettingsById(followableId.toString()).toDomain()
        if (notification.id.id.isEmpty()) throw Exception("Followable Notification Not Found")

        return notification
    }

    suspend fun updateStoriesNotification(notificationSettings: FollowableNotificationSettings) = repositoryScope.async {
        notificationSettings.id.legacyId?.also { id ->
            val notificationType = getNotificationType(notificationSettings)

            sendRequest(id, notificationType, NotificationName.STORIES, notificationSettings.notifyStories)
            followableNotificationSettingsDao.updateSettings(notificationSettings.toLocal())
        }
    }.await()

    suspend fun updateGameNotification(notificationSettings: FollowableNotificationSettings) = repositoryScope.async {
        notificationSettings.id.legacyId?.also { id ->
            val notificationType = getNotificationType(notificationSettings)

            sendRequest(id, notificationType, NotificationName.GAMES, notificationSettings.notifyGames)
            followableNotificationSettingsDao.updateSettings(notificationSettings.toLocal())
        }
    }.await()

    suspend fun updateGameStartNotification(notificationSettings: FollowableNotificationSettings) = repositoryScope.async {
        notificationSettings.id.legacyId?.also { id ->
            val notificationType = getNotificationType(notificationSettings)

            sendRequest(id, notificationType, NotificationName.GAME_START, notificationSettings.notifyGamesStart)
            followableNotificationSettingsDao.updateSettings(notificationSettings.toLocal())
        }
    }.await()

    private suspend fun sendRequest(
        id: Long,
        type: NotificationType,
        name: NotificationName,
        isOn: Boolean,
    ) {
        if (isOn) {
            settingsRestApi.addPushSettings(
                notifType = type.paramName,
                notifName = name.paramName,
                notifValue = id
            )
        } else {
            settingsRestApi.removePushSettings(
                notifType = type.paramName,
                notifName = name.paramName,
                notifValue = id
            )
        }
    }

    private fun getNotificationType(followableNotification: FollowableNotificationSettings): NotificationType {
        val notificationType = when (followableNotification.id.type) {
            Followable.Type.TEAM -> NotificationType.TEAM
            Followable.Type.LEAGUE -> NotificationType.LEAGUE
            Followable.Type.AUTHOR -> NotificationType.AUTHOR
        }
        return notificationType
    }

    private enum class NotificationType(val paramName: String) {
        LEAGUE("league"),
        TEAM("team"),
        AUTHOR("author")
    }

    private enum class NotificationName(val paramName: String) {
        STORIES("stories"),
        GAMES("games"),
        GAME_START("game_start"),
    }
}