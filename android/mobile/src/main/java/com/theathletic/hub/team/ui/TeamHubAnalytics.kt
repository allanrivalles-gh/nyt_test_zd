package com.theathletic.hub.team.ui

import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin

interface TeamHubAnalytics {
    fun trackClickOnHomeTab(teamId: String, leagueId: String)
    fun trackClickOnScheduleTab(teamId: String, leagueId: String)
    fun trackClickOnStandingsTab(teamId: String, leagueId: String)
    fun trackClickOnStatsTab(teamId: String, leagueId: String)
    fun trackClickOnRosterTab(teamId: String, leagueId: String)
    fun trackViewOfHomeFeed(teamId: String, leagueId: String)
    fun trackViewOfTeamSchedule(teamId: String, leagueId: String)
    fun trackViewOfStandings(teamId: String, leagueId: String)
    fun trackViewOfTeamStats(teamId: String, leagueId: String)
    fun trackViewOfPlayerStats(teamId: String, leagueId: String)
    fun trackViewOfTeamRoster(teamId: String, leagueId: String)
}

class TeamHubAnalyticsHandler @AutoKoin constructor(
    private val analytics: Analytics
) : TeamHubAnalytics {
    override fun trackClickOnHomeTab(teamId: String, leagueId: String) {
        analytics.track(
            Event.TeamHub.Click(
                view = "home",
                element = "feed_navigation",
                object_id = teamId,
                league_id = leagueId
            )
        )
    }

    override fun trackClickOnScheduleTab(teamId: String, leagueId: String) {
        analytics.track(
            Event.TeamHub.Click(
                view = "scores",
                element = "team_scores_and_schedules",
                object_id = teamId,
                league_id = leagueId
            )
        )
    }

    override fun trackClickOnStandingsTab(teamId: String, leagueId: String) {
        analytics.track(
            Event.TeamHub.Click(
                view = "standings",
                element = "standings",
                object_id = teamId,
                league_id = leagueId
            )
        )
    }

    override fun trackClickOnStatsTab(teamId: String, leagueId: String) {
        analytics.track(
            Event.TeamHub.Click(
                view = "stats",
                element = "team_stats",
                object_id = teamId,
                league_id = leagueId
            )
        )
    }

    override fun trackClickOnRosterTab(teamId: String, leagueId: String) {
        analytics.track(
            Event.TeamHub.Click(
                view = "roster",
                element = "team_roster",
                object_id = teamId,
                league_id = leagueId
            )
        )
    }

    override fun trackViewOfHomeFeed(teamId: String, leagueId: String) {
        analytics.track(
            Event.TeamHub.View(
                view = "home",
                element = "feed_navigation",
                object_id = teamId,
                league_id = leagueId
            )
        )
    }

    override fun trackViewOfTeamSchedule(teamId: String, leagueId: String) {
        analytics.track(
            Event.TeamHub.View(
                view = "scores",
                element = "team_scores_and_schedules",
                object_id = teamId,
                league_id = leagueId
            )
        )
    }

    override fun trackViewOfStandings(teamId: String, leagueId: String) {
        analytics.track(
            Event.TeamHub.View(
                view = "standings",
                element = "team_scores_and_schedules",
                object_id = teamId,
                league_id = leagueId
            )
        )
    }

    override fun trackViewOfTeamStats(teamId: String, leagueId: String) {
        analytics.track(
            Event.TeamHub.View(
                view = "stats",
                element = "team_stats",
                object_id = teamId,
                league_id = leagueId
            )
        )
    }

    override fun trackViewOfPlayerStats(teamId: String, leagueId: String) {
        analytics.track(
            Event.TeamHub.View(
                view = "stats",
                element = "player_stats",
                object_id = teamId,
                league_id = leagueId
            )
        )
    }

    override fun trackViewOfTeamRoster(teamId: String, leagueId: String) {
        analytics.track(
            Event.TeamHub.View(
                view = "roster",
                element = "team_roster",
                object_id = teamId,
                league_id = leagueId
            )
        )
    }
}