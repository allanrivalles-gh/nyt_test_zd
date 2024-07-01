package com.theathletic.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "followable_notification_settings")
data class LocalFollowableNotificationSettings(
    @PrimaryKey val id: String,
    val notifyStories: Boolean,
    val notifyGames: Boolean,
    val notifyGamesStart: Boolean,
)