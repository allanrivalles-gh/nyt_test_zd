package com.theathletic.gamedetail.playergrades.ui

import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin

interface PlayerGradesAnalytics {

    fun trackPlayerGradeDetailsView(
        playerId: String,
        teamId: String,
        gameId: String,
        leagueId: String,
        fromGradeTab: Boolean
    )
    fun trackGradeTabScreenView(
        gameId: String,
        leagueId: String,
        isGameInProgress: Boolean
    )
    fun trackPlayerDetailScreenView(
        playerId: String,
        teamId: String,
        gameId: String,
        leagueId: String,
        fromGradeTab: Boolean
    )
    fun trackGradePlayersClick(gameId: String, leagueId: String)
    fun trackViewAllPlayerGradesClick(gameId: String, leagueId: String)
    fun trackPlayerGradeTeamSwitchClick(gameId: String, leagueId: String, teamId: String)
    fun trackToGradePlayerDetailsClick(
        playerId: String,
        teamId: String,
        gameId: String,
        leagueId: String,
        fromGradeTab: Boolean
    )
    @Suppress("LongParameterList")
    fun trackGradingPlayerClick(
        grade: String,
        gameId: String,
        leagueId: String,
        teamId: String,
        playerId: String,
        fromView: PlayerGradeSourceView
    )
    fun trackUngradingPlayerClick(
        gameId: String,
        leagueId: String,
        teamId: String,
        playerId: String,
    )
    fun trackSeeAllPlayerGrades(gameId: String, leagueId: String)
    fun trackPlayerGradeFlowNavigation(
        action: PlayerGradeNavigationAction,
        gameId: String,
        leagueId: String,
        fromGradeTab: Boolean
    )
    fun trackToPlayerDetailsClick(
        playerId: String,
        teamId: String,
        gameId: String,
        leagueId: String,
        fromGradeTab: Boolean
    )
    fun trackGradeTabTeamSwitchClick(teamId: String, gameId: String, leagueId: String)
    fun trackPlayerOnGradeTabClick(teamMemberId: String, gameId: String, leagueId: String)
}

enum class PlayerGradeNavigationAction(val value: String) {
    CLICK_PREV("click_prev"),
    CLICK_NEXT("click_next"),
    SWIPE_PREV("swipe_prev"),
    SWIPE_NEXT("swipe_next")
}

enum class PlayerGradeSourceView(val value: String) {
    GAME_TAB("game_tab"),
    GRADE_TAB_LIST("grades_tab_list"),
    GRADE_TAB_MODAL("grades_tab_modal")
}

private const val GRADE_PLAYERS_GAME_TAB = "grade_players_game_tab"
private const val GRADE_PLAYERS_GRADE_TAB = "grade_players_grades_tab"
private const val GRADE = "grade"
private const val UNGRADE = "ungrade"
private const val INGAME_BOX_SCORE_GRADES = "ingame_box_score_grades"
private const val POSTGAME_BOX_SCORE_GRADES = "postgame_box_score_grades"

class PlayerGradesAnalyticsHandler @AutoKoin constructor(val analytics: Analytics) : PlayerGradesAnalytics {

    override fun trackPlayerGradeDetailsView(
        playerId: String,
        teamId: String,
        gameId: String,
        leagueId: String,
        fromGradeTab: Boolean
    ) {
        val view = if (fromGradeTab) GRADE_PLAYERS_GRADE_TAB else GRADE_PLAYERS_GAME_TAB
        analytics.track(
            Event.PlayerGrades.View(
                view = view,
                element = "team_player",
                object_type = "team_member_id",
                object_id = playerId,
                league_id = leagueId,
                game_id = gameId,
                team_id = teamId
            )
        )
    }

    override fun trackGradeTabScreenView(
        gameId: String,
        leagueId: String,
        isGameInProgress: Boolean
    ) {
        analytics.track(
            Event.PlayerGrades.View(
                view = if (isGameInProgress) INGAME_BOX_SCORE_GRADES else POSTGAME_BOX_SCORE_GRADES,
                element = "",
                object_type = "game_id",
                object_id = gameId,
                league_id = leagueId
            )
        )
    }

    override fun trackPlayerDetailScreenView(
        playerId: String,
        teamId: String,
        gameId: String,
        leagueId: String,
        fromGradeTab: Boolean
    ) {
        analytics.track(
            Event.PlayerGrades.View(
                view = if (fromGradeTab) GRADE_PLAYERS_GRADE_TAB else GRADE_PLAYERS_GAME_TAB,
                element = "team_player",
                object_type = "team_member_id",
                object_id = playerId,
                league_id = leagueId,
                team_id = teamId,
                game_id = gameId,
                team_member_id = playerId
            )
        )
    }

    override fun trackGradePlayersClick(gameId: String, leagueId: String) {
        analytics.track(
            Event.PlayerGrades.Click(
                view = "game_tab",
                element = "grade_players",
                object_type = "game_id",
                object_id = gameId,
                league_id = leagueId
            )
        )
    }

    override fun trackViewAllPlayerGradesClick(gameId: String, leagueId: String) {
        analytics.track(
            Event.PlayerGrades.Click(
                view = "game_tab",
                element = "grade_players_view",
                object_type = "game_id",
                object_id = gameId,
                league_id = leagueId
            )
        )
    }

    override fun trackPlayerGradeTeamSwitchClick(gameId: String, leagueId: String, teamId: String) {
        analytics.track(
            Event.PlayerGrades.Click(
                view = "grade_players_game_tab",
                element = "team",
                object_type = "team_id",
                object_id = teamId,
                league_id = leagueId,
                game_id = gameId
            )
        )
    }

    override fun trackToGradePlayerDetailsClick(
        playerId: String,
        teamId: String,
        gameId: String,
        leagueId: String,
        fromGradeTab: Boolean
    ) {
        analytics.track(
            Event.PlayerGrades.Click(
                view = if (fromGradeTab) GRADE_PLAYERS_GRADE_TAB else GRADE_PLAYERS_GAME_TAB,
                element = "team_player",
                object_type = "team_member_id",
                object_id = playerId,
                team_id = teamId,
                league_id = leagueId,
                game_id = gameId
            )
        )
    }

    override fun trackGradingPlayerClick(
        grade: String,
        gameId: String,
        leagueId: String,
        teamId: String,
        playerId: String,
        fromView: PlayerGradeSourceView
    ) {
        analytics.track(
            Event.PlayerGrades.Click(
                view = fromView.value,
                element = GRADE,
                object_type = GRADE,
                object_id = grade,
                league_id = leagueId,
                game_id = gameId,
                team_member_id = playerId,
                team_id = teamId
            )
        )
    }

    override fun trackUngradingPlayerClick(
        gameId: String,
        leagueId: String,
        teamId: String,
        playerId: String,
    ) {
        analytics.track(
            Event.PlayerGrades.Click(
                view = PlayerGradeSourceView.GRADE_TAB_MODAL.value,
                element = GRADE,
                object_type = UNGRADE,
                object_id = "0",
                league_id = leagueId,
                game_id = gameId,
                team_member_id = playerId,
                team_id = teamId
            )
        )
    }

    override fun trackSeeAllPlayerGrades(gameId: String, leagueId: String) {
        analytics.track(
            Event.PlayerGrades.Click(
                view = PlayerGradeSourceView.GAME_TAB.value,
                element = "all_grades",
                object_type = "game_id",
                object_id = gameId,
                league_id = leagueId,
            )
        )
    }

    override fun trackPlayerGradeFlowNavigation(
        action: PlayerGradeNavigationAction,
        gameId: String,
        leagueId: String,
        fromGradeTab: Boolean,
    ) {
        val view = if (fromGradeTab) GRADE_PLAYERS_GRADE_TAB else GRADE_PLAYERS_GAME_TAB
        analytics.track(
            Event.PlayerGrades.Click(
                view = view,
                element = action.value,
                object_type = "game_id",
                object_id = gameId,
                league_id = leagueId,
            )
        )
    }

    override fun trackToPlayerDetailsClick(
        playerId: String,
        teamId: String,
        gameId: String,
        leagueId: String,
        fromGradeTab: Boolean
    ) {
        val view = if (fromGradeTab) GRADE_PLAYERS_GRADE_TAB else GRADE_PLAYERS_GAME_TAB
        analytics.track(
            Event.PlayerGrades.Click(
                view = view,
                element = "team_player",
                object_type = "team_member_id",
                object_id = playerId,
                league_id = leagueId,
                team_id = teamId,
                game_id = gameId,
                team_member_id = playerId
            )
        )
    }

    override fun trackGradeTabTeamSwitchClick(teamId: String, gameId: String, leagueId: String) {
        analytics.track(
            Event.PlayerGrades.Click(
                view = GRADE_PLAYERS_GRADE_TAB,
                element = "team",
                object_type = "team_id",
                object_id = teamId,
                league_id = leagueId,
                game_id = gameId
            )
        )
    }

    override fun trackPlayerOnGradeTabClick(playerId: String, gameId: String, leagueId: String) {
        analytics.track(
            Event.PlayerGrades.Click(
                view = GRADE_PLAYERS_GRADE_TAB,
                element = "team_player",
                object_type = "team_member_id",
                object_id = playerId,
                league_id = leagueId,
                game_id = gameId
            )
        )
    }
}