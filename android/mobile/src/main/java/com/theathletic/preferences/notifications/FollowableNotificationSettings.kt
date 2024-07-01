package com.theathletic.preferences.notifications

import com.theathletic.followable.FollowableId

data class FollowableNotificationSettings(
    val id: FollowableId,
    val notifyStories: Boolean,
    val notifyGames: Boolean,
    val notifyGamesStart: Boolean,
)