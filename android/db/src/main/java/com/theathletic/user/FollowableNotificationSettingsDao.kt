package com.theathletic.user

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
abstract class FollowableNotificationSettingsDao {

    @Query(value = "SELECT * FROM followable_notification_settings WHERE id = :id")
    abstract suspend fun getSettingsById(id: String): LocalFollowableNotificationSettings

    @Upsert
    abstract suspend fun updateSettings(followingNotification: LocalFollowableNotificationSettings)

    @Upsert
    abstract suspend fun insertSettings(followableNotificationList: List<LocalFollowableNotificationSettings>)
}