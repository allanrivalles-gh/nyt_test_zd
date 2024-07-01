package com.theathletic.gamedetail.boxscore.ui

import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.RelatedStoriesUi
import com.theathletic.gamedetail.data.local.GameStatus

interface BoxScoreAnalytics {
    fun trackScreenView(gameId: String)
    fun trackLineUpExpandEvent(gameId: String)
    fun ImpressionPayload.impress(startTime: Long, endTime: Long)
    fun trackBoxScoreGameView(status: GameStatus, gameId: String, leagueId: String, teamId: String?)
    fun trackBoxScoreStatsView(
        status: GameStatus,
        gameId: String,
        leagueId: String,
        teamId: String?
    )
    fun trackBoxScorePlaysView(
        status: GameStatus,
        gameId: String,
        leagueId: String,
        teamId: String?
    )
    fun trackBoxScoreLiveBlogView(
        status: GameStatus,
        gameId: String,
        leagueId: String,
        teamId: String?,
        blogId: String
    )

    fun trackRecentGamesClick(gameId: String, leagueId: String, teamId: String)
    fun trackAllPlaysClicked(gameId: String, leagueId: String, status: GameStatus)
    fun trackScoringPlaysClicked(gameId: String, leagueId: String, status: GameStatus)
    fun trackFullPlayByPlaysClicked(gameId: String, leagueId: String)
    fun trackTicketsBuyClicked(gameId: String, ticketPartner: String)

    fun RelatedStoriesUi.RelatedStoriesAnalyticsPayload.click(
        status: GameStatus
    )

    fun trackTeamStatsToggleClick(
        status: GameStatus,
        gameId: String,
        leagueId: String,
        teamId: String
    )
}

class BoxScoreAnalyticsHandler @AutoKoin constructor(
    val analytics: Analytics
) : BoxScoreAnalytics {

    companion object {
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
    }

    override fun trackScreenView(gameId: String) {
        analytics.track(
            Event.GameFeed.View(
                view = "box_score",
                object_type = "game_id",
                object_id = gameId
            )
        )
    }

    override fun trackLineUpExpandEvent(gameId: String) {
        analytics.track(
            Event.GameFeed.Click(
                view = "box_score",
                element = "line_up",
                object_type = "game_id",
                object_id = gameId
            )
        )
    }

    override fun ImpressionPayload.impress(startTime: Long, endTime: Long) {
        analytics.track(
            Event.LiveBlog.Impression(
                view = "box_score",
                impress_start_time = startTime,
                impress_end_time = endTime,
                object_type = objectType,
                object_id = objectId,
                element = element,
                container = container,
                page_order = pageOrder.toLong(),
                h_index = hIndex,
                v_index = vIndex,
                parent_object_id = parentObjectId,
                parent_object_type = parentObjectType
            )
        )
    }

    override fun trackBoxScoreGameView(
        status: GameStatus,
        gameId: String,
        leagueId: String,
        teamId: String?
    ) {
        val view = when (status) {
            GameStatus.SCHEDULED -> PREGAME_GAME
            GameStatus.IN_PROGRESS -> INGAME_GAME
            GameStatus.FINAL -> POSTGAME_GAME
            else -> return
        }
        analytics.track(
            Event.BoxScore.View(
                view = view,
                object_id = gameId,
                league_id = leagueId,
                team_id = teamId.orEmpty()
            )
        )
    }

    override fun trackBoxScoreStatsView(
        status: GameStatus,
        gameId: String,
        leagueId: String,
        teamId: String?
    ) {
        val view = when (status) {
            GameStatus.IN_PROGRESS -> INGAME_STATS
            GameStatus.FINAL -> POSTGAME_STATS
            else -> return
        }
        analytics.track(
            Event.BoxScore.View(
                view = view,
                object_id = gameId,
                league_id = leagueId,
                team_id = teamId.orEmpty()
            )
        )
    }

    override fun trackBoxScorePlaysView(
        status: GameStatus,
        gameId: String,
        leagueId: String,
        teamId: String?
    ) {
        val view = when (status) {
            GameStatus.IN_PROGRESS -> INGAME_PLAYS
            GameStatus.FINAL -> POSTGAME_PLAYS
            else -> return
        }
        analytics.track(
            Event.BoxScore.View(
                view = view,
                object_id = gameId,
                league_id = leagueId,
                team_id = teamId.orEmpty()
            )
        )
    }

    override fun trackBoxScoreLiveBlogView(
        status: GameStatus,
        gameId: String,
        leagueId: String,
        teamId: String?,
        blogId: String
    ) {
        val view = when (status) {
            GameStatus.SCHEDULED -> PREGAME_LIVEBLOG
            GameStatus.IN_PROGRESS -> INGAME_LIVEBLOG
            GameStatus.FINAL -> POSTGAME_LIVEBLOG
            else -> return
        }
        analytics.track(
            Event.BoxScore.View(
                view = view,
                object_id = gameId,
                league_id = leagueId,
                team_id = teamId.orEmpty(),
                blog_id = blogId
            )
        )
    }

    override fun trackRecentGamesClick(
        gameId: String,
        leagueId: String,
        teamId: String
    ) {
        analytics.track(
            Event.BoxScore.Click(
                view = "pregame_box_score_game",
                element = "recent_games",
                object_type = "team_id",
                object_id = teamId,
                game_id = gameId,
                league_id = leagueId
            )
        )
    }

    override fun trackAllPlaysClicked(
        gameId: String,
        leagueId: String,
        status: GameStatus,
    ) {
        val view = status.playsAnalyticsView ?: return
        analytics.track(
            Event.BoxScore.Click(
                view = view,
                element = "plays_tab_nav",
                object_type = "all_plays",
                game_id = gameId,
                league_id = leagueId,
                object_id = ""
            )
        )
    }

    override fun trackScoringPlaysClicked(
        gameId: String,
        leagueId: String,
        status: GameStatus,
    ) {
        val view = status.playsAnalyticsView ?: return
        analytics.track(
            Event.BoxScore.Click(
                view = view,
                element = "plays_tab_nav",
                object_type = "scoring_plays",
                game_id = gameId,
                league_id = leagueId,
                object_id = ""
            )
        )
    }

    override fun trackFullPlayByPlaysClicked(gameId: String, leagueId: String) {
        analytics.track(
            Event.BoxScore.Click(
                view = "ingame_box_score_game",
                element = "all_plays",
                object_type = "scoring_plays",
                game_id = gameId,
                league_id = leagueId,
                object_id = ""
            )
        )
    }

    override fun trackTicketsBuyClicked(gameId: String, ticketPartner: String) {
        analytics.track(
            Event.BoxScore.Click(
                view = "pregame_box_score_game",
                element = "tickets",
                object_type = "game_id",
                game_id = gameId,
                ticket_partner = ticketPartner,
                object_id = gameId,
                league_id = ""
            )
        )
    }

    override fun RelatedStoriesUi.RelatedStoriesAnalyticsPayload.click(
        status: GameStatus
    ) {
        val view = when (status) {
            GameStatus.SCHEDULED -> PREGAME_GAME
            GameStatus.IN_PROGRESS -> INGAME_GAME
            GameStatus.FINAL -> POSTGAME_GAME
            else -> return
        }
        analytics.track(
            Event.BoxScore.Click(
                view = view,
                element = "related_stories",
                object_type = "article_id",
                object_id = articleId,
                game_id = gameId,
                league_id = leagueId,
                page_order = pageOrder.toString(),
                v_index = articlePosition.toString(),
                h_index = "0"
            )
        )
    }

    override fun trackTeamStatsToggleClick(
        status: GameStatus,
        gameId: String,
        leagueId: String,
        teamId: String
    ) {
        val view = when (status) {
            GameStatus.IN_PROGRESS -> INGAME_STATS
            GameStatus.FINAL -> POSTGAME_STATS
            else -> return
        }
        analytics.track(
            Event.BoxScore.Click(
                view = view,
                element = "stats_tab_nav",
                object_type = "team_id",
                object_id = teamId,
                game_id = gameId,
                league_id = leagueId
            )
        )
    }

    private val GameStatus.playsAnalyticsView: String?
        get() = when (this) {
            GameStatus.IN_PROGRESS -> INGAME_PLAYS
            GameStatus.FINAL -> POSTGAME_PLAYS
            else -> null
        }
}