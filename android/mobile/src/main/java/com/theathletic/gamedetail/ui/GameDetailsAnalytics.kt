package com.theathletic.gamedetail.ui

import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.gamedetail.data.local.GameStatus
import com.theathletic.scores.GameDetailTab

interface GameDetailsAnalytics {
    fun trackShare(gameId: String)
    fun trackGameTabClick(
        status: GameStatus,
        gameId: String,
        leagueId: String,
        teamId: String?,
        blogId: String = "",
        // nullable to satisfy deprecated GameDetailVM
        previousTab: GameDetailTab? = null
    )

    fun trackStatsTabClick(
        status: GameStatus,
        gameId: String,
        leagueId: String,
        teamId: String?,
        blogId: String = "",
        // nullable to satisfy deprecated GameDetailVM
        previousTab: GameDetailTab? = null
    )

    fun trackDiscussTabClick(
        status: GameStatus,
        gameId: String,
        leagueId: String,
        teamId: String?,
        blogId: String = "",
        previousTab: GameDetailTab
    )

    fun trackPlaysTabClick(
        status: GameStatus,
        gameId: String,
        leagueId: String,
        teamId: String?,
        blogId: String = "",
        previousTab: GameDetailTab
    )

    fun trackLiveBlogTabClick(
        status: GameStatus,
        gameId: String,
        leagueId: String,
        teamId: String?,
        blogId: String = "",
        previousTab: GameDetailTab
    )

    fun trackTeamNavigationToScheduleClick(status: GameStatus, teamId: String, onGameTab: Boolean)

    fun trackGradesTabClick(
        status: GameStatus,
        gameId: String,
        leagueId: String,
        teamId: String?,
        previousTab: GameDetailTab,
    )

    fun trackNavigateToGameDiscussTab(
        view: String,
        gameId: String,
        leagueId: String,
        teamId: String?,
    )
}

internal const val PREGAME_DISCUSS = "pregame_box_score_discuss"
internal const val INGAME_DISCUSS = "ingame_box_score_discuss"
internal const val POSTGAME_DISCUSS = "postgame_box_score_discuss"

class GameDetailsAnalyticsHandler @AutoKoin constructor(
    private val analytics: Analytics
) : GameDetailsAnalytics {

    companion object {
        private const val PREGAME = "pregame_box_score"
        private const val PREGAME_GAME = "pregame_box_score_game"
        private const val INGAME_GAME = "ingame_box_score_game"
        private const val POSTGAME_GAME = "postgame_box_score_game"
        private const val INGAME_STATS = "ingame_box_score_stats"
        private const val POSTGAME_STATS = "postgame_box_score_stats"
        private const val INGAME_PLAYS = "ingame_box_score_plays"
        private const val POSTGAME_PLAYS = "postgame_box_score_plays"
        private const val INGAME_LIVEBLOG = "ingame_box_score_liveblog"
        private const val POSTGAME_LIVEBLOG = "postgame_box_score_liveblog"
        private const val PREGAME_LIVEBLOG = "pregame_box_score_liveblog"
        private const val TEAM_SCORES_AND_SCHEDULES = "team_scores_and_schedules"
    }

    override fun trackShare(gameId: String) {
        analytics.track(
            Event.GameFeed.Click(
                view = "game_feed",
                element = "share",
                object_type = "game_id",
                object_id = gameId
            )
        )
    }

    override fun trackGameTabClick(
        status: GameStatus,
        gameId: String,
        leagueId: String,
        teamId: String?,
        blogId: String,
        previousTab: GameDetailTab?
    ) {
        val gameTab = "game_tab"
        if (previousTab == GameDetailTab.DISCUSS) {
            trackFromDiscussTabClick(status, gameId, leagueId, teamId, gameTab)
        }
        val view = getPreviousView(previousTab, status) ?: return
        analytics.track(
            Event.GameFeed.Click(
                view = view,
                element = "box_score_nav",
                object_type = gameTab,
                object_id = "",
                game_id = gameId,
                league_id = leagueId
            )
        )
    }

    override fun trackStatsTabClick(
        status: GameStatus,
        gameId: String,
        leagueId: String,
        teamId: String?,
        blogId: String,
        previousTab: GameDetailTab?
    ) {
        val statsTab = "stats_tab"
        if (previousTab == GameDetailTab.DISCUSS) {
            trackFromDiscussTabClick(status, gameId, leagueId, teamId, statsTab)
        }
        val view = getPreviousView(previousTab, status) ?: return
        analytics.track(
            Event.GameFeed.Click(
                view = view,
                element = "box_score_nav",
                object_type = statsTab,
                object_id = "",
                game_id = gameId,
                league_id = leagueId
            )
        )
    }

    override fun trackDiscussTabClick(
        status: GameStatus,
        gameId: String,
        leagueId: String,
        teamId: String?,
        blogId: String,
        previousTab: GameDetailTab
    ) {
        val view = getPreviousView(previousTab, status) ?: return
        analytics.track(
            Event.Discuss.Click(
                view = view,
                element = "box_score_nav",
                object_type = "discuss_tab",
                object_id = "",
                game_id = gameId,
                league_id = leagueId,
                team_id = teamId.orEmpty()
            )
        )
    }

    override fun trackPlaysTabClick(
        status: GameStatus,
        gameId: String,
        leagueId: String,
        teamId: String?,
        blogId: String,
        previousTab: GameDetailTab
    ) {
        val playsTab = "plays_tab"
        if (previousTab == GameDetailTab.DISCUSS) {
            trackFromDiscussTabClick(
                status = status,
                gameId = gameId,
                leagueId = leagueId,
                objectType = playsTab,
                teamId = teamId
            )
        }
        val view = getPreviousView(previousTab, status) ?: return
        analytics.track(
            Event.GameFeed.Click(
                view = view,
                element = "box_score_nav",
                object_type = playsTab,
                object_id = "",
                game_id = gameId,
                league_id = leagueId,
                team_id = teamId.orEmpty()
            )
        )
    }

    override fun trackLiveBlogTabClick(
        status: GameStatus,
        gameId: String,
        leagueId: String,
        teamId: String?,
        blogId: String,
        previousTab: GameDetailTab
    ) {
        val blogTab = "liveblog_tab"
        if (previousTab == GameDetailTab.DISCUSS) {
            trackFromDiscussTabClick(
                status = status,
                gameId = gameId,
                leagueId = leagueId,
                objectType = blogTab,
                teamId = teamId
            )
        }
        val view = getPreviousView(previousTab, status) ?: return
        analytics.track(
            Event.GameFeed.Click(
                view = view,
                element = "box_score_nav",
                object_type = blogTab,
                object_id = "",
                game_id = gameId,
                league_id = leagueId,
                team_id = teamId.orEmpty(),
                blog_id = blogId
            )
        )
    }

    override fun trackTeamNavigationToScheduleClick(
        status: GameStatus,
        teamId: String,
        onGameTab: Boolean
    ) {
        when (status) {
            GameStatus.SCHEDULED -> trackTeamNavigationToSchedule(PREGAME, teamId)
            GameStatus.IN_PROGRESS -> trackTeamNavigationToSchedule(
                if (onGameTab) INGAME_GAME else INGAME_STATS,
                teamId
            )
            GameStatus.FINAL -> trackTeamNavigationToSchedule(
                if (onGameTab) POSTGAME_GAME else POSTGAME_STATS,
                teamId
            )
            else -> { /* track nothing */ }
        }
    }

    override fun trackGradesTabClick(
        status: GameStatus,
        gameId: String,
        leagueId: String,
        teamId: String?,
        previousTab: GameDetailTab,
    ) {
        if (previousTab == GameDetailTab.DISCUSS) {
            trackFromDiscussTabClick(
                status = status,
                gameId = gameId,
                leagueId = leagueId,
                objectType = "grades_tab",
                teamId = teamId
            )
        }
        val view = getPreviousView(previousTab, status) ?: return
        analytics.track(
            Event.GameFeed.Click(
                view = view,
                element = "box_score_nav",
                object_type = "grades_tab",
                object_id = "",
                game_id = gameId,
                league_id = leagueId,
                team_id = teamId.orEmpty()
            )
        )
    }

    override fun trackNavigateToGameDiscussTab(
        view: String,
        gameId: String,
        leagueId: String,
        teamId: String?,
    ) {
        analytics.track(
            Event.BoxScore.ClickDiscoveryBoxScore(
                view = view,
                object_id = gameId,
                parent_object_id = leagueId,
                team_id = teamId.orEmpty()
            )
        )
    }

    private fun trackTeamNavigationToSchedule(view: String, teamId: String) {
        analytics.track(
            Event.ScoresTabs.Click(
                view = view,
                element = TEAM_SCORES_AND_SCHEDULES,
                object_type = "team_id",
                object_id = teamId
            )
        )
    }

    private fun trackFromDiscussTabClick(
        status: GameStatus,
        gameId: String,
        leagueId: String,
        teamId: String?,
        objectType: String
    ) {
        val view = status.discussAnalyticsView ?: return
        analytics.track(
            Event.Discuss.Click(
                view = view,
                element = "box_score_nav",
                object_type = objectType,
                object_id = "",
                game_id = gameId,
                league_id = leagueId,
                team_id = teamId.orEmpty()
            )
        )
    }

    private fun getPreviousView(previousTab: GameDetailTab?, status: GameStatus): String? {
        return when (previousTab) {
            GameDetailTab.GAME -> status.gameAnalyticsView
            GameDetailTab.PLAYER_STATS -> status.statsAnalyticsView
            GameDetailTab.PLAYS -> status.playsAnalyticsView
            GameDetailTab.LIVE_BLOG -> status.liveBlogAnalyticsView
            else -> null
        }
    }

    private val GameStatus.gameAnalyticsView: String?
        get() = when (this) {
            GameStatus.SCHEDULED -> PREGAME_GAME
            GameStatus.IN_PROGRESS -> INGAME_GAME
            GameStatus.FINAL -> POSTGAME_GAME
            else -> null
        }

    private val GameStatus.statsAnalyticsView: String?
        get() = when (this) {
            GameStatus.IN_PROGRESS -> INGAME_STATS
            GameStatus.FINAL -> POSTGAME_STATS
            else -> null
        }

    private val GameStatus.playsAnalyticsView: String?
        get() = when (this) {
            GameStatus.IN_PROGRESS -> INGAME_PLAYS
            GameStatus.FINAL -> POSTGAME_PLAYS
            else -> null
        }

    private val GameStatus.liveBlogAnalyticsView: String?
        get() = when (this) {
            GameStatus.SCHEDULED -> PREGAME_LIVEBLOG
            GameStatus.IN_PROGRESS -> INGAME_LIVEBLOG
            GameStatus.FINAL -> POSTGAME_LIVEBLOG
            else -> null
        }
}

val GameStatus.discussAnalyticsView: String?
    get() = when (this) {
        GameStatus.SCHEDULED -> PREGAME_DISCUSS
        GameStatus.IN_PROGRESS -> INGAME_DISCUSS
        GameStatus.FINAL -> POSTGAME_DISCUSS
        else -> null
    }