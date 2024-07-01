package com.theathletic.scores.ui

import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.feed.FeedType

interface ScoresAnalytics {
    fun trackNavigateToTeamScheduleFromStandings(teamId: String, pageId: String, index: Int)
    fun trackNavigateToLeagueScheduleFromTopicSearch(leagueId: String)
    fun trackNavigateToTeamScheduleFromTopicSearch(teamId: String)
    fun trackStandingsPageView(leagueCode: String, group: String)
    fun trackStandingsGroupClick(leagueCode: String, group: String)
    fun ImpressionPayload.impress(startTime: Long, endTime: Long, feedType: FeedType)
}

class ScoresAnalyticsHandler @AutoKoin constructor(
    val analytics: Analytics
) : ScoresAnalytics {

    companion object {
        private const val TODAY_SCORES = "today"
        private const val TEAM_SCORES = "team_scores_and_schedules"
        private const val LEAGUE_SCORES = "league_scores_and_schedules"
        private const val SCORES = "scores"
        private const val STANDINGS = "standings"
        private const val FOLLOWING = "following"
        private const val TEAM_ID = "team_id"
        private const val LEAGUE_ID = "league_id"
    }

    override fun trackNavigateToTeamScheduleFromStandings(
        teamId: String,
        pageId: String,
        index: Int
    ) {
        analytics.track(
            Event.ScoresTabs.Click(
                view = STANDINGS,
                element = TEAM_SCORES,
                object_type = TEAM_ID,
                object_id = teamId,
                v_index = index.toString(),
                parent_object_type = "filter",
                parent_object_id = pageId
            )
        )
    }

    override fun trackNavigateToLeagueScheduleFromTopicSearch(leagueId: String) {
        analytics.track(
            Event.ScoresTabs.Click(
                view = SCORES,
                element = FOLLOWING,
                object_type = LEAGUE_ID,
                object_id = leagueId,
            )
        )
    }

    override fun trackNavigateToTeamScheduleFromTopicSearch(teamId: String) {
        analytics.track(
            Event.ScoresTabs.Click(
                view = SCORES,
                element = FOLLOWING,
                object_type = TEAM_ID,
                object_id = teamId,
            )
        )
    }

    override fun trackStandingsPageView(leagueCode: String, group: String) {
        analytics.track(
            Event.Standings.View(
                element = group,
                object_type = "league_id",
                object_id = leagueCode
            )
        )
    }

    override fun trackStandingsGroupClick(leagueCode: String, group: String) {
        analytics.track(
            Event.Standings.Click(
                element = group,
                object_type = "league_id",
                object_id = leagueCode
            )
        )
    }

    override fun ImpressionPayload.impress(
        startTime: Long,
        endTime: Long,
        feedType: FeedType
    ) {
        analytics.track(
            Event.ScoresTabs.Impression(
                view = "scores",
                impress_start_time = startTime,
                impress_end_time = endTime,
                object_type = objectType,
                object_id = objectId,
                element = feedType.toScoresElement,
                container = container,
                page_order = pageOrder.toLong(),
                h_index = hIndex,
                v_index = vIndex,
                parent_object_id = parentObjectId,
                parent_object_type = parentObjectType
            )
        )
    }

    private val FeedType.toScoresElement
        get() = when (this) {
            is FeedType.ScoresToday -> TODAY_SCORES
            is FeedType.ScoresTeam -> TEAM_SCORES
            is FeedType.ScoresLeague -> LEAGUE_SCORES
            else -> ""
        }
}