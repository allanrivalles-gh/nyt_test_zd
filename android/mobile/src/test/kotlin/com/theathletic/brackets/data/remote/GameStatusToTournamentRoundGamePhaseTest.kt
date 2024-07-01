package com.theathletic.brackets.data.remote

import com.theathletic.brackets.data.local.TournamentRoundGame
import com.theathletic.type.GameStatusCode
import kotlin.test.assertEquals
import org.junit.Test

class GameStatusToTournamentRoundGamePhaseTest {
    @Test
    fun `returns pre game phase if status is delayed and scheduled date is not set`() {
        val phase = GameStatusCode.delayed.toTournamentRoundGamePhase(null)
        assertEquals(TournamentRoundGame.Phase.PreGame, phase)
    }

    @Test
    fun `returns in game phase if status is delayed and scheduled date is set`() {
        val phase = GameStatusCode.delayed.toTournamentRoundGamePhase(0)
        assertEquals(TournamentRoundGame.Phase.InGame, phase)
    }

    @Test
    fun `returns post game phase if status is final`() {
        val phase = GameStatusCode.final.toTournamentRoundGamePhase()
        assertEquals(TournamentRoundGame.Phase.PostGame, phase)
    }

    @Test
    fun `returns in game phase if status is in progress`() {
        val phase = GameStatusCode.in_progress.toTournamentRoundGamePhase()
        assertEquals(TournamentRoundGame.Phase.InGame, phase)
    }

    @Test
    fun `returns in game phase if status is suspended`() {
        val phase = GameStatusCode.suspended.toTournamentRoundGamePhase()
        assertEquals(TournamentRoundGame.Phase.InGame, phase)
    }

    @Test
    fun `returns pre game phase if status is scheduled`() {
        val phase = GameStatusCode.scheduled.toTournamentRoundGamePhase()
        assertEquals(TournamentRoundGame.Phase.PreGame, phase)
    }

    @Test
    fun `returns pre game phase if status is if necessary`() {
        val phase = GameStatusCode.if_necessary.toTournamentRoundGamePhase()
        assertEquals(TournamentRoundGame.Phase.PreGame, phase)
    }

    @Test
    fun `returns pre game phase if status is unnecessary`() {
        val phase = GameStatusCode.unnecessary.toTournamentRoundGamePhase()
        assertEquals(TournamentRoundGame.Phase.PreGame, phase)
    }

    @Test
    fun `returns pre game phase if status is postponed`() {
        val phase = GameStatusCode.postponed.toTournamentRoundGamePhase()
        assertEquals(TournamentRoundGame.Phase.PreGame, phase)
    }

    @Test
    fun `returns pre game phase if status is cancelled`() {
        val phase = GameStatusCode.cancelled.toTournamentRoundGamePhase()
        assertEquals(TournamentRoundGame.Phase.PreGame, phase)
    }
}

// extracted default parameter value, so tests don't have to explicitly consider it
// unless it has something to do with the test setup
private fun GameStatusCode.toTournamentRoundGamePhase(): TournamentRoundGame.Phase? {
    return toTournamentRoundGamePhase(null)
}