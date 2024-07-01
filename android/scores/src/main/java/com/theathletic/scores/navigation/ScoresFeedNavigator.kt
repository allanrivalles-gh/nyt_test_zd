package com.theathletic.scores.navigation

import com.theathletic.feed.FeedType
import com.theathletic.hub.HubTabType

interface ScoresFeedNavigator {
    fun navigateToHubActivity(feedType: FeedType, initialTab: HubTabType)
    fun navigateToGame(gameId: String, showDiscussion: Boolean, view: String)
    fun navigateToExternalLink(url: String)
}