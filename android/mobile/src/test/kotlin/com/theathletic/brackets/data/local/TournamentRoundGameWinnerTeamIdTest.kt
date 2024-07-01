package com.theathletic.brackets.data.local

import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.Test

class TournamentRoundGameWinnerTeamIdTest {
    private val defaultGame = defaultTournamentRoundGame.copy(
        phase = TournamentRoundGame.Phase.PostGame,
    )
    private val defaultTeam = TournamentRoundGame.TeamData(
        id = "0",
        alias = "A",
        logos = listOf(),
        score = null,
        penaltyScore = null,
        record = null,
        seed = null,
    )

    @Test
    fun `returns null when phase is not post game`() {
        val game = defaultGame.copy(phase = TournamentRoundGame.Phase.InGame)
        assertNull(game.winnerTeamId())
    }

    @Test
    fun `returns home team id when it is not shootout and home team score is higher`() {
        val game = defaultGame.copy(
            homeTeam = TournamentRoundGame.Team.Confirmed(
                defaultTeam.copy(
                    id = "0",
                    score = 1,
                ),
            ),
            awayTeam = TournamentRoundGame.Team.Confirmed(
                defaultTeam.copy(
                    id = "1",
                    score = 0,
                )
            )
        )
        assertEquals("0", game.winnerTeamId())
    }

    @Test
    fun `returns away team id when it is not shootout and away team score is higher`() {
        val game = defaultGame.copy(
            homeTeam = TournamentRoundGame.Team.Confirmed(
                defaultTeam.copy(
                    id = "0",
                    score = 0,
                ),
            ),
            awayTeam = TournamentRoundGame.Team.Confirmed(
                defaultTeam.copy(
                    id = "1",
                    score = 1,
                )
            )
        )
        assertEquals("1", game.winnerTeamId())
    }

    @Test
    fun `returns home team id when it is shootout and home team penalty score is higher`() {
        val game = defaultGame.copy(
            homeTeam = TournamentRoundGame.Team.Confirmed(
                defaultTeam.copy(
                    id = "0",
                    penaltyScore = 1,
                ),
            ),
            awayTeam = TournamentRoundGame.Team.Confirmed(
                defaultTeam.copy(
                    id = "1",
                    penaltyScore = 0,
                )
            )
        )
        assertEquals("0", game.winnerTeamId())
    }

    @Test
    fun `returns away team id when it is shootout and away team penalty score is higher`() {
        val game = defaultGame.copy(
            homeTeam = TournamentRoundGame.Team.Confirmed(
                defaultTeam.copy(
                    id = "0",
                    penaltyScore = 0,
                ),
            ),
            awayTeam = TournamentRoundGame.Team.Confirmed(
                defaultTeam.copy(
                    id = "1",
                    penaltyScore = 1,
                )
            )
        )
        assertEquals("1", game.winnerTeamId())
    }
}