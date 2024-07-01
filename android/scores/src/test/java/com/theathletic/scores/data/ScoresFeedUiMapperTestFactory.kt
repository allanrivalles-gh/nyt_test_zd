package com.theathletic.scores.data

import com.theathletic.datetime.Datetime
import com.theathletic.gamedetail.data.local.GameStatus
import com.theathletic.scores.data.local.ScoresFeedDateTimeFormat
import com.theathletic.scores.data.local.ScoresFeedLocalModel
import com.theathletic.scores.data.local.ScoresFeedTeamIcon.SoccerRedCard
import com.theathletic.scores.data.local.ScoresFeedTextType

internal const val TEST_GAME_TIMESTAMP = 1684888345456L
internal const val TEST_GAME_FORMATTED_DATE = "Mon, 11/7"
internal const val TEST_GAME_FORMATTED_TIME = "7:05 PM"

internal fun createPregameWithHeaderText(isCancelled: Boolean = false): ScoresFeedLocalModel {
    return ScoresFeedLocalModel(
        id = "ScoresFeedLocalModel",
        navigationBar = emptyList(),
        days = listOf(
            scoresFeedDayFixture(
                day = "2023-05-16",
                groups = listOf(
                    scoresFeedLeagueGroupFixture(
                        title = "NHL",
                        league = scoresFeedLeagueFixture(displayName = "NHL"),
                        blocks = listOf(
                            scoresFeedBlockFixture(
                                gameId = "qd5sW95FXMn630iZ",
                                header = "Conference Finals - Game 3, Panthers lead series 3-0",
                                gameBlock = scoresFeedGameBlockFixture(
                                    gameStatus = if (isCancelled) GameStatus.CANCELED else GameStatus.SCHEDULED,
                                    startedAt = Datetime(TEST_GAME_TIMESTAMP),
                                    firstTeam = scoresFeedTeamBlockFixture(
                                        name = "Team 1",
                                        teamInfo = scoresFeedTeamPregameInfoBlockFixture("41-16")
                                    ),
                                    secondTeam = scoresFeedTeamBlockFixture(
                                        name = "Team 2",
                                        teamInfo = scoresFeedTeamPregameInfoBlockFixture("9th in EPL")
                                    ),
                                ),
                                infoBlock = if (isCancelled) {
                                    scoresFeedInfoBlockFixture(
                                        text = listOf(scoresFeedStatusTextBlockFixture("Canceled"))
                                    )
                                } else {
                                    scoresFeedInfoBlockFixture(
                                        text = listOf(
                                            scoresFeedDateTimeTextBlockFixture(
                                                format = ScoresFeedDateTimeFormat.Date,
                                                timestamp = TEST_GAME_TIMESTAMP
                                            ),
                                            scoresFeedDateTimeTextBlockFixture(
                                                format = ScoresFeedDateTimeFormat.Time,
                                                timestamp = TEST_GAME_TIMESTAMP
                                            ),
                                            scoresFeedStandardTextBlockFixture(text = "FOX SPORTS"),
                                            scoresFeedOddsTextBlockFixture()
                                        )
                                    )
                                },
                                widget = scoresFeedDiscussionWidgetBlockFixture()
                            )
                        )
                    )
                )
            )
        )
    )
}

internal fun createPregameGameDayWithRankings(): ScoresFeedLocalModel {
    return ScoresFeedLocalModel(
        id = "ScoresFeedLocalModel",
        navigationBar = emptyList(),
        days = listOf(
            scoresFeedDayFixture(
                day = "2023-05-16",
                groups = listOf(
                    scoresFeedLeagueGroupFixture(
                        title = "NHL",
                        league = scoresFeedLeagueFixture(displayName = "NHL"),
                        blocks = listOf(
                            scoresFeedBlockFixture(
                                gameId = "qd5sW95FXMn630iZ",
                                gameBlock = scoresFeedGameBlockFixture(
                                    gameStatus = GameStatus.SCHEDULED,
                                    startedAt = Datetime(TEST_GAME_TIMESTAMP),
                                    firstTeam = scoresFeedTeamBlockFixture(
                                        name = "Team 1",
                                        teamInfo = scoresFeedTeamPregameInfoBlockFixture("41-16"),
                                        ranking = 5,
                                    ),
                                    secondTeam = scoresFeedTeamBlockFixture(
                                        name = "Team 2",
                                        teamInfo = scoresFeedTeamPregameInfoBlockFixture("9th in EPL"),
                                        ranking = 3,
                                    ),
                                ),
                                infoBlock = scoresFeedInfoBlockFixture(
                                    text = listOf(
                                        scoresFeedDateTimeTextBlockFixture(
                                            format = ScoresFeedDateTimeFormat.Time,
                                            timestamp = TEST_GAME_TIMESTAMP
                                        ),
                                        scoresFeedStandardTextBlockFixture(text = "FOX SPORTS"),
                                        scoresFeedOddsTextBlockFixture()
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    )
}

internal fun createStandardLiveGame(): ScoresFeedLocalModel {
    return ScoresFeedLocalModel(
        id = "ScoresFeedLocalModel",
        navigationBar = emptyList(),
        days = listOf(
            scoresFeedDayFixture(
                day = "2023-05-16",
                groups = listOf(
                    scoresFeedLeagueGroupFixture(
                        title = "NHL",
                        league = scoresFeedLeagueFixture(displayName = "NHL"),
                        blocks = listOf(
                            scoresFeedBlockFixture(
                                gameId = "qd5sW95FXMn630iZ",
                                gameBlock = scoresFeedGameBlockFixture(
                                    gameStatus = GameStatus.IN_PROGRESS,
                                    startedAt = Datetime(TEST_GAME_TIMESTAMP),
                                    firstTeam = scoresFeedTeamBlockFixture(
                                        name = "Team 1",
                                        teamInfo = scoresFeedTeamGameInfoBlockFixture(score = "34")
                                    ),
                                    secondTeam = scoresFeedTeamBlockFixture(
                                        name = "Team 2",
                                        teamInfo = scoresFeedTeamGameInfoBlockFixture(score = "56")
                                    ),
                                ),
                                infoBlock = scoresFeedInfoBlockFixture(
                                    text = listOf(
                                        scoresFeedLiveTextBlockFixture("Q3 3:47"),
                                        scoresFeedStandardTextBlockFixture(text = "FOX SPORTS")
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    )
}

internal fun createLiveBaseballGame(): ScoresFeedLocalModel {
    return ScoresFeedLocalModel(
        id = "ScoresFeedLocalModel",
        navigationBar = emptyList(),
        days = listOf(
            scoresFeedDayFixture(
                day = "2023-05-16",
                groups = listOf(
                    scoresFeedLeagueGroupFixture(
                        title = "MLB",
                        league = scoresFeedLeagueFixture(displayName = "MLB"),
                        blocks = listOf(
                            scoresFeedBlockFixture(
                                gameId = "qd5sW95FXMn630iZ",
                                gameBlock = scoresFeedGameBlockFixture(
                                    gameStatus = GameStatus.IN_PROGRESS,
                                    startedAt = Datetime(TEST_GAME_TIMESTAMP),
                                    firstTeam = scoresFeedTeamBlockFixture(
                                        name = "Team 1",
                                        teamInfo = scoresFeedTeamGameInfoBlockFixture(score = "2")
                                    ),
                                    secondTeam = scoresFeedTeamBlockFixture(
                                        name = "Team 2",
                                        teamInfo = scoresFeedTeamGameInfoBlockFixture(score = "1")
                                    ),
                                ),
                                infoBlock = scoresFeedInfoBlockFixture(
                                    text = listOf(
                                        scoresFeedLiveTextBlockFixture("BOT 2"),
                                        scoresFeedStatusTextBlockFixture("1 OUT"),
                                        scoresFeedStandardTextBlockFixture(text = "FOX SPORTS")
                                    ),
                                    widget = scoresFeedBaseballWidgetBlock()
                                ),
                            )
                        )
                    )
                )
            )
        )
    )
}

internal fun createStandardPostGame(
    hasRedCardIconAndPenaltyGoals: Boolean = false,
): ScoresFeedLocalModel {
    return ScoresFeedLocalModel(
        id = "ScoresFeedLocalModel",
        navigationBar = emptyList(),
        days = listOf(
            scoresFeedDayFixture(
                day = "2023-05-16",
                groups = listOf(
                    scoresFeedLeagueGroupFixture(
                        title = "NHL",
                        league = scoresFeedLeagueFixture(displayName = "NHL"),
                        blocks = listOf(
                            scoresFeedBlockFixture(
                                gameId = "qd5sW95FXMn630iZ",
                                gameBlock = scoresFeedGameBlockFixture(
                                    gameStatus = GameStatus.FINAL,
                                    startedAt = Datetime(TEST_GAME_TIMESTAMP),
                                    firstTeam = scoresFeedTeamBlockFixture(
                                        name = "Team 1",
                                        teamInfo = scoresFeedTeamGameInfoBlockFixture(
                                            score = "56",
                                            penaltyScore = if (hasRedCardIconAndPenaltyGoals) "6" else null,
                                            isWinner = false
                                        ),
                                        icons = if (hasRedCardIconAndPenaltyGoals) listOf(SoccerRedCard) else emptyList()
                                    ),
                                    secondTeam = scoresFeedTeamBlockFixture(
                                        name = "Team 2",
                                        teamInfo = scoresFeedTeamGameInfoBlockFixture(
                                            score = "78",
                                            penaltyScore = if (hasRedCardIconAndPenaltyGoals) "8" else null,
                                            isWinner = true
                                        )
                                    ),
                                ),
                                infoBlock = scoresFeedInfoBlockFixture(
                                    text = listOf(
                                        scoresFeedStatusTextBlockFixture("Final"),
                                        scoresFeedDateTimeTextBlockFixture(
                                            timestamp = TEST_GAME_TIMESTAMP,
                                            format = ScoresFeedDateTimeFormat.Date,
                                            type = ScoresFeedTextType.Default
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    )
}

internal fun createPregameTBDGameDay(): ScoresFeedLocalModel {
    return ScoresFeedLocalModel(
        id = "ScoresFeedLocalModel",
        navigationBar = emptyList(),
        days = listOf(
            scoresFeedDayFixture(
                day = "2023-05-16",
                groups = listOf(
                    scoresFeedLeagueGroupFixture(
                        title = "NHL",
                        league = scoresFeedLeagueFixture(displayName = "NHL"),
                        blocks = listOf(
                            scoresFeedBlockFixture(
                                gameId = "qd5sW95FXMn630iZ",
                                gameBlock = scoresFeedGameBlockFixture(
                                    gameStatus = GameStatus.SCHEDULED,
                                    startedAt = Datetime(TEST_GAME_TIMESTAMP),
                                    firstTeam = scoresFeedTeamBlockFixture(
                                        name = "Team 1",
                                        teamInfo = scoresFeedTeamPregameInfoBlockFixture("41-16"),
                                        isTbd = false
                                    ),
                                    secondTeam = scoresFeedTeamBlockFixture(
                                        name = "TBD",
                                        teamInfo = null,
                                        isTbd = true
                                    ),
                                ),
                                infoBlock = scoresFeedInfoBlockFixture(
                                    text = listOf(
                                        scoresFeedDateTimeTextBlockFixture(
                                            format = ScoresFeedDateTimeFormat.Date,
                                            timestamp = TEST_GAME_TIMESTAMP
                                        ),
                                        scoresFeedDateTimeTextBlockFixture(
                                            format = ScoresFeedDateTimeFormat.Time,
                                            timestamp = TEST_GAME_TIMESTAMP
                                        ),
                                        scoresFeedStandardTextBlockFixture(text = "FOX SPORTS"),
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    )
}

internal fun createGroups(): ScoresFeedLocalModel {
    return ScoresFeedLocalModel(
        id = "ScoresFeedLocalModel",
        navigationBar = emptyList(),
        days = listOf(
            scoresFeedDayFixture(
                day = "2023-05-16",
                groups = listOf(
                    scoresFeedLeagueGroupFixture(
                        title = "NHL",
                        league = scoresFeedLeagueFixture(displayName = "NHL"),
                        blocks = listOf(
                            createBasicGameBlock()
                        )
                    ),
                    scoresFeedLeagueGroupFixture(
                        title = "NBA",
                        league = scoresFeedLeagueFixture(displayName = "NBA"),
                        blocks = listOf(
                            createBasicGameBlock()
                        )
                    ),
                    scoresFeedLeagueGroupFixture(
                        title = "NFL",
                        subTitle = "8 games today",
                        league = scoresFeedLeagueFixture(displayName = "NFL"),
                        blocks = listOf(
                            createBasicGameBlock()
                        )
                    ),
                    scoresFeedLeagueGroupFixture(
                        title = "Premier League",
                        league = scoresFeedLeagueFixture(displayName = "Premier League"),
                        blocks = listOf(
                            createBasicGameBlock()
                        )
                    ),
                    scoresFeedFollowingGroupFixture(
                        title = "Following",
                        subTitle = "4 games today",
                        blocks = listOf(
                            createBasicGameBlock()
                        )
                    )
                )
            )
        )
    )
}

private fun createBasicGameBlock() = scoresFeedBlockFixture(
    gameId = "qd5sW95FXMn630iZ",
    gameBlock = scoresFeedGameBlockFixture(
        gameStatus = GameStatus.FINAL,
        startedAt = Datetime(TEST_GAME_TIMESTAMP),
        firstTeam = scoresFeedTeamBlockFixture(
            name = "Team 1",
            teamInfo = scoresFeedTeamGameInfoBlockFixture(score = "56"),
        ),
        secondTeam = scoresFeedTeamBlockFixture(
            name = "Team 2",
            teamInfo = scoresFeedTeamGameInfoBlockFixture(score = "78")
        ),
    ),
    infoBlock = scoresFeedInfoBlockFixture(
        text = listOf(scoresFeedStatusTextBlockFixture("Final"))
    )
)