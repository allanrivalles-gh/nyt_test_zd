package com.theathletic.gamedetail.data.local

import com.google.common.truth.Truth.assertThat
import com.theathletic.datetime.Datetime
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class PlayerGradesLatestUpdatesMapperTest {

    private lateinit var updateMapper: PlayerGradesLatestUpdatesMapper

    @Before
    fun setUp() {
        updateMapper = PlayerGradesLatestUpdatesMapper()
    }

    @Test
    fun `Created model will always have the players with the latest updatedAt timestamps`() {
        val newModel = updateMapper.map(
            originalModel = playerGradesLocalModelFixture(
                players = listOf(
                    createPlayerGradeFixture("Player_1", 1000L),
                    createPlayerGradeFixture("Player_2", 800),
                    createPlayerGradeFixture("Player_3", 1000L),
                )
            ),
            updatedModel = playerGradesLocalModelFixture(
                players = listOf(
                    createPlayerGradeFixture("Player_1", 1500L),
                    createPlayerGradeFixture("Player_2", 500L),
                    createPlayerGradeFixture("Player_3", 1000L),
                )
            )
        )

        assertThat(newModel.homeTeam?.players?.get(0)?.grading?.updatedAt == Datetime(1500)).isTrue()
        assertThat(newModel.homeTeam?.players?.get(1)?.grading?.updatedAt == Datetime(800)).isTrue()
        assertThat(newModel.homeTeam?.players?.get(2)?.grading?.updatedAt == Datetime(1000)).isTrue()
    }

    private fun createPlayerGradeFixture(
        id: String,
        updatedAt: Long
    ) = PlayerGradesLocalModel.Player(
        playerId = id,
        displayName = id,
        headshots = emptyList(),
        position = PlayerPosition.QUARTERBACK,
        jerseyNumber = "1",
        grading = PlayerGradesLocalModel.Grading(
            playerId = id,
            averageGradeDisplay = "1.0",
            grade = 1,
            totalGrades = 1,
            order = 1,
            updatedAt = Datetime(updatedAt)
        ),
        summaryStatistics = emptyList(),
        defaultStatistics = emptyList(),
        extraStatistics = emptyList()
    )

    private fun playerGradesLocalModelFixture(
        players: List<PlayerGradesLocalModel.Player>,
    ) = PlayerGradesLocalModel(
        gameId = "GameId",
        gameStatus = GameStatus.IN_PROGRESS,
        gradeStatus = GradeStatus.ENABLED,
        period = Period.SECOND_QUARTER,
        gameStateSecondary = "",
        gameStatePrimary = "",
        clock = null,
        scheduledAt = Datetime(0),
        homeTeam = PlayerGradesLocalModel.PlayerGradesTeam(
            id = "home_team",
            name = "Home Team",
            alias = null,
            logos = emptyList(),
            backgroundColor = null,
            score = 0,
            players = players
        ),
        awayTeam = PlayerGradesLocalModel.PlayerGradesTeam(
            id = "away_team",
            name = "Away Team",
            alias = null,
            logos = emptyList(),
            backgroundColor = null,
            score = 0,
            players = emptyList()
        ),
    )
}