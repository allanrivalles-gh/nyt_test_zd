package com.theathletic.comments.game.data

import com.google.common.truth.Truth.assertThat
import com.theathletic.comments.game.TeamThreadContextSwitch
import org.junit.Test

class TeamThreadsTest {
    private val threads = listOf(threadFixture(teamId = "team1"), threadFixture(teamId = "team2"))

    @Test
    fun `returns a thread context switch a single team when the second team is not present`() {
        val teamThreads = teamThreadsFixture(
            currentThread = threads[0],
            threads = threads
        )
        val expected = TeamThreadContextSwitch(
            currentTeamThread = threads[0],
            secondTeamThread = threads[1]
        )
        assertThat(teamThreads.getTeamThreadContextSwitch()).isEqualTo(expected)
    }

    @Test
    fun `returns a thread context switch with two teams when both teams are present`() {
        val teamThreads = teamThreadsFixture(
            currentThread = threads[0],
            threads = listOf()
        )
        val expected = TeamThreadContextSwitch(
            currentTeamThread = threads[0],
            secondTeamThread = null
        )
        assertThat(teamThreads.getTeamThreadContextSwitch()).isEqualTo(expected)
    }
}