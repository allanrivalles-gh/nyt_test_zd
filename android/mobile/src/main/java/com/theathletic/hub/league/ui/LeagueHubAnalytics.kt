package com.theathletic.hub.league.ui

import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin

interface LeagueHubAnalytics {
    fun trackClickOnLeagueHubTab(
        leagueId: String,
        fromTab: HubAnalyticsViewType,
        toTab: HubAnalyticsViewType
    )
    fun trackViewDifferentTabInStandings(leagueId: String, title: String)
    fun trackClickTeamFromStandingsTab(teamId: String, leagueId: String)
    fun trackViewTabOnLeagueHub(leagueId: String, currentTab: HubAnalyticsViewType)
}
enum class HubAnalyticsViewType(val view: String) {
    Home("home"),
    Schedule("schedule"),
    Standings("standings"),
    Brackets("brackets"),
    Stats("stats"),
    Roster("roster"),
}

class LeagueHubAnalyticsHandler @AutoKoin constructor(
    private val analytics: Analytics
) : LeagueHubAnalytics {

    override fun trackClickOnLeagueHubTab(
        leagueId: String,
        fromTab: HubAnalyticsViewType,
        toTab: HubAnalyticsViewType
    ) {
        val currentView = fromTab.view
        val destinationView = toTab.view
        analytics.track(
            Event.LeagueHub.Click(
                view = currentView,
                element = "feed_navigation",
                object_type = destinationView,
                league_id = leagueId
            )
        )
    }

    override fun trackViewDifferentTabInStandings(leagueId: String, title: String) {
        analytics.track(
            Event.LeagueHub.View(
                view = "standings",
                element = "standings",
                object_id = leagueId,
                league_id = leagueId,
                object_type = "league_id",
                meta_blob = title
            )
        )
    }

    override fun trackClickTeamFromStandingsTab(teamId: String, leagueId: String) {
        analytics.track(
            Event.LeagueHub.Click(
                view = "standings",
                element = "team_scores_and_schedules",
                object_id = teamId,
                object_type = "team_id",
                league_id = leagueId
            )
        )
    }

    override fun trackViewTabOnLeagueHub(leagueId: String, currentTab: HubAnalyticsViewType) {
        val currentView = currentTab.view
        analytics.track(
            Event.LeagueHub.View(
                view = currentView,
                element = "feed_navigation",
                object_id = leagueId,
                league_id = leagueId,
                object_type = "league_id"
            )
        )
    }
}