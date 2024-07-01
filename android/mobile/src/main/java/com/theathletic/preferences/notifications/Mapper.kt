package com.theathletic.preferences.notifications

import com.theathletic.followable.Followable
import com.theathletic.followable.FollowableId
import com.theathletic.user.LocalFollowableNotificationSettings

fun FollowableNotificationSettings.toLocal() = LocalFollowableNotificationSettings(
    id = id.toString(),
    notifyStories = notifyStories,
    notifyGames = notifyGames,
    notifyGamesStart = notifyGamesStart,
)

fun LocalFollowableNotificationSettings.toDomain() = FollowableNotificationSettings(
    id = Followable.Id.parse(id) ?: FollowableId("", Followable.Type.TEAM),
    notifyStories = notifyStories,
    notifyGames = notifyGames,
    notifyGamesStart = notifyGamesStart,
)