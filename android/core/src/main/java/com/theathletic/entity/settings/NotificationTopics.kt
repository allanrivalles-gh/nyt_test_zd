package com.theathletic.entity.settings

/**
 * Marker for User Topics who support push notifications for stories about them.
 */
interface StoriesNotificationsTopic {
    var notifyStories: Boolean
}

/**
 * Marker for User Topics who support push notifications for games they are involved in.
 */
interface GameNotificationsTopic {
    var notifyGames: Boolean
}