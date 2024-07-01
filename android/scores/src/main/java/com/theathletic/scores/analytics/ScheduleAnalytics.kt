package com.theathletic.scores.analytics

import com.theathletic.analytics.IAnalytics
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin

interface ScheduleAnalytics {
    fun trackTeamScheduleView(teamId: String, slate: String)
    fun trackLeagueScheduleView(leagueId: String, slate: String)
    fun trackTeamScheduleNavigationClick(teamId: String, newSlate: String, oldSlate: String)
    fun trackLeagueScheduleNavigationClick(leagueId: String, newSlate: String, oldSlate: String)
    fun trackNavigateToBoxScoresClick(gameId: String, index: Int)
    fun trackNavigationToTeamTicketsWebSite(teamId: String, provider: String)
    fun trackNavigationToLeagueTicketsWebSite(leagueId: String, slate: String, provider: String)
    fun trackImpression(payload: ImpressionPayload, startTime: Long, endTime: Long, isLeague: Boolean)
}

private const val SCORES = "scores"
private const val TEAM_SCORES = "team_scores_and_schedules"
private const val LEAGUE_SCORES = "league_scores_and_schedules"
private const val BOX_SCORE = "box_score"
private const val BOX_SCORE_DISCUSS = "box_score_discuss"
private const val SCHEDULE = "schedule"
private const val TICKETS = "tickets"
private const val TEAM_ID = "team_id"
private const val LEAGUE_ID = "league_id"
private const val GAME_ID = "game_id"

class ScheduleAnalyticsHandler @AutoKoin constructor(val analytics: IAnalytics) : ScheduleAnalytics {

    override fun trackTeamScheduleView(teamId: String, slate: String) {
        analytics.track(
            Event.ScoresTabs.View(
                element = TEAM_SCORES,
                object_type = TEAM_ID,
                object_id = teamId,
                slate = slate.formatSlateForLogging()
            )
        )
    }

    override fun trackLeagueScheduleView(leagueId: String, slate: String) {
        analytics.track(
            Event.ScoresTabs.View(
                element = LEAGUE_SCORES,
                object_type = LEAGUE_ID,
                object_id = leagueId,
                slate = slate.formatSlateForLogging()
            )
        )
    }

    override fun trackTeamScheduleNavigationClick(teamId: String, newSlate: String, oldSlate: String) {
        analytics.track(
            Event.ScoresTabs.Click(
                view = SCHEDULE,
                element = TEAM_SCORES,
                object_type = TEAM_ID,
                object_id = teamId,
                slate = newSlate.formatSlateForLogging(),
                current_slate = oldSlate.formatSlateForLogging()
            )
        )
    }

    override fun trackLeagueScheduleNavigationClick(leagueId: String, newSlate: String, oldSlate: String) {
        analytics.track(
            Event.ScoresTabs.Click(
                view = SCHEDULE,
                element = LEAGUE_SCORES,
                object_type = LEAGUE_ID,
                object_id = leagueId,
                slate = newSlate.formatSlateForLogging(),
                current_slate = oldSlate.formatSlateForLogging()
            )
        )
    }

    override fun trackNavigateToBoxScoresClick(gameId: String, index: Int) {
        analytics.track(
            Event.Scores.Click(
                element = BOX_SCORE,
                object_type = GAME_ID,
                object_id = gameId,
                v_index = index.toString()
            )
        )
    }

    override fun trackNavigationToTeamTicketsWebSite(teamId: String, provider: String) {
        analytics.track(
            Event.ScoresTabs.Click(
                view = SCORES,
                element = TICKETS,
                object_type = TEAM_ID,
                object_id = teamId,
                ticket_partner = provider
            )
        )
    }

    override fun trackNavigationToLeagueTicketsWebSite(leagueId: String, slate: String, provider: String) {
        analytics.track(
            Event.ScoresTabs.Click(
                view = SCORES,
                element = TICKETS,
                object_type = LEAGUE_ID,
                object_id = leagueId,
                slate = slate,
                ticket_partner = provider
            )
        )
    }

    override fun trackImpression(payload: ImpressionPayload, startTime: Long, endTime: Long, isLeague: Boolean) {
        analytics.track(
            Event.ScoresTabs.Impression(
                view = SCORES,
                impress_start_time = startTime,
                impress_end_time = endTime,
                object_type = payload.objectType,
                object_id = payload.objectId,
                element = if (isLeague) LEAGUE_SCORES else TEAM_SCORES,
                container = payload.container,
                page_order = payload.pageOrder.toLong(),
                h_index = payload.hIndex,
                v_index = payload.vIndex,
                parent_object_id = payload.parentObjectId,
                parent_object_type = payload.parentObjectType
            )
        )
    }

    private fun String.formatSlateForLogging() = replace(" ", "_").lowercase()
}