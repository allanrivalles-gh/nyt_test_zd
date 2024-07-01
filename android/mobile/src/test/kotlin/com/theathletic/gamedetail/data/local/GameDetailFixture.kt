package com.theathletic.gamedetail.data.local

import com.theathletic.datetime.Datetime
import com.theathletic.entity.main.League
import com.theathletic.entity.main.Sport
import java.util.Date

fun gameSummaryLocalModelFixture(
    awayTeam: GameSummaryLocalModel.GameSummaryTeam,
    homeTeam: GameSummaryLocalModel.GameSummaryTeam
) = GameSummaryLocalModel(
    gameTitle = null,
    awayTeam = awayTeam,
    homeTeam = homeTeam,
    id = "1",
    scheduleAt = Datetime(Date().time),
    isScheduledTimeTbd = false,
    sport = Sport.UNKNOWN,
    league = GameDetailLocalModel.League(League.MLB, "leagueId", "MLB", "MLB"),
    status = GameStatus.FINAL,
    period = Period.UNKNOWN,
    clock = null,
    coverage = listOf(),
    permalink = null,
    extras = null,
    liveBlog = null,
    gradeStatus = GradeStatus.ENABLED,
    isAwayTeamTbd = false,
    isHomeTeamTbd = false,
    areCommentsDiscoverable = true,
    gameStatePrimary = null,
    gameStateSecondary = null,
)