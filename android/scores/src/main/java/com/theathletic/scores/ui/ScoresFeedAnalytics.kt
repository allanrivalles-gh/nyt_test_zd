package com.theathletic.scores.ui

import com.theathletic.analytics.IAnalytics
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin

interface ScoresFeedAnalytics {
    fun trackScoresFeedView()
    fun trackSearchScreenView()
    fun trackClickTeamOrLeagueFromNavigation(entityType: String, entityId: String)
    fun trackClickLeagueFromFeed(leagueId: String)
    fun trackClickLeagueAllGamesFromFeed(leagueId: String)
    fun trackChangeDatesOnScores(slate: String, currentSlate: String, hIndex: String)
    fun trackClickOpenSearchScreen()
    fun trackClickCancelSearchScreen(search: String)
    fun trackClickClearSearchInput(search: String)
    fun trackClickTeamAfterSearch(teamId: String, search: String)
    fun trackClickLeagueAfterSearch(leagueId: String, search: String)
    fun trackGameImpression(payload: ImpressionPayload, startTime: Long, endTime: Long)
}

class ScoresFeedAnalyticsHandler @AutoKoin constructor(val analytics: IAnalytics) : ScoresFeedAnalytics {

    companion object {
        private const val SCORES_HOME = "home"
        private const val SEARCH = "search"
        private const val TEAM_ID = "team_id"
        private const val GAME_ID = "game_id"
        private const val LEAGUE_ID = "league_id"
        private const val CLEAR_INPUT = "clear_input"
        private const val CANCEL = "cancel"
        private const val BOX_SCORE_DISCUSS = "box_score_discuss"
        private const val LEAGUES_SCORES_AND_SCHEDULES = "leagues_scores_and_schedules"
        private const val LEAGUES_HEADER = "league_header"
        private const val ALL_GAMES = "all_games"
        private const val FEED_NAVIGATION = "feed_navigation"
        private const val SLATE_NAV = "slate_nav"
    }

    override fun trackScoresFeedView() {
        analytics.track(Event.ScoresFeedTab.View(element = SCORES_HOME))
    }

    override fun trackSearchScreenView() {
        analytics.track(Event.ScoresFeedTab.View(element = SEARCH))
    }

    override fun trackClickTeamOrLeagueFromNavigation(entityType: String, entityId: String) {
        analytics.track(
            Event.ScoresFeedTab.Click(
                element = FEED_NAVIGATION,
                object_type = entityType,
                object_id = entityId
            )
        )
    }

    override fun trackClickLeagueFromFeed(leagueId: String) {
        analytics.track(
            Event.ScoresFeedTab.Click(
                element = LEAGUES_HEADER,
                object_type = LEAGUE_ID,
                object_id = leagueId,
            )
        )
    }

    override fun trackClickLeagueAllGamesFromFeed(leagueId: String) {
        analytics.track(
            Event.ScoresFeedTab.Click(
                element = ALL_GAMES,
                object_type = LEAGUE_ID,
                object_id = leagueId,
            )
        )
    }

    override fun trackChangeDatesOnScores(slate: String, currentSlate: String, hIndex: String) {
        analytics.track(
            Event.ScoresFeedTab.Click(
                element = SLATE_NAV,
                slate = slate,
                current_slate = currentSlate,
                h_index = hIndex
            )
        )
    }

    override fun trackClickOpenSearchScreen() {
        analytics.track(Event.ScoresFeedTab.Click(element = SEARCH))
    }

    override fun trackClickCancelSearchScreen(search: String) {
        analytics.track(
            Event.ScoresFeedTab.Click(
                element = SEARCH,
                object_type = CANCEL,
                search = search
            )
        )
    }

    override fun trackClickClearSearchInput(search: String) {
        analytics.track(
            Event.ScoresFeedTab.Click(
                element = SEARCH,
                object_type = CLEAR_INPUT,
                search = search
            )
        )
    }

    override fun trackClickTeamAfterSearch(teamId: String, search: String) {
        analytics.track(
            Event.ScoresFeedTab.Click(
                element = SEARCH,
                object_type = TEAM_ID,
                object_id = teamId,
                search = search
            )
        )
    }

    override fun trackClickLeagueAfterSearch(leagueId: String, search: String) {
        analytics.track(
            Event.ScoresFeedTab.Click(
                element = SEARCH,
                object_type = LEAGUE_ID,
                object_id = leagueId,
                search = search
            )
        )
    }

    override fun trackGameImpression(payload: ImpressionPayload, startTime: Long, endTime: Long) {
        analytics.track(
            Event.ScoresFeedTab.Impression(
                object_type = payload.objectType,
                object_id = payload.objectId,
                element = payload.element,
                page_order = payload.pageOrder.toLong(),
                v_index = payload.vIndex,
                impress_start_time = startTime,
                impress_end_time = endTime
            )
        )
    }
}