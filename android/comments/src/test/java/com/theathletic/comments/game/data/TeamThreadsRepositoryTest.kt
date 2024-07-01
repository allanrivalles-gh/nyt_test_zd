package com.theathletic.comments.game.data

import com.google.common.truth.Truth.assertThat
import com.theathletic.TeamSpecificThreadsQuery
import com.theathletic.comments.game.TeamThreadsRepository
import com.theathletic.comments.game.local.TeamThreadsLocalDataSource
import com.theathletic.comments.game.remote.TeamThreadsApi
import com.theathletic.test.assertStream
import com.theathletic.test.runTest
import com.theathletic.test.testFlowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class TeamThreadsRepositoryTest {

    @Mock private lateinit var teamThreadsApi: TeamThreadsApi
    @Spy private val teamThreadDataSource = TeamThreadsLocalDataSource()

    private lateinit var repository: TeamThreadsRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = TeamThreadsRepository(teamThreadsApi, teamThreadDataSource)
    }

    @Test
    fun `fetch team threads for the given game id`() = runTest {
        teamThreadDataSource.clearCache()
        whenever(teamThreadsApi.getTeamThreads("gameId1")).thenReturn(createRemoteTeamThreads())

        repository.fetchTeamThreads("gameId1")

        verify(teamThreadsApi).getTeamThreads("gameId1")
    }

    @Test
    fun `puts the team thread in memory on a success team threads fetch operation`() = runTest {
        whenever(teamThreadsApi.getTeamThreads("gameId")).thenReturn(createRemoteTeamThreads())

        repository.fetchTeamThreads("gameId")

        verify(teamThreadDataSource).update("gameId", teamThreadsFixture())
    }

    @Test
    fun `exposes thrown fetch operation exceptions on a team threads fetch failure`() = runTest {
        whenever(teamThreadsApi.getTeamThreads("gameId")).then { throw Exception("Can't fetch team threads") }

        val exception = assertThrows(Exception::class.java) {
            runBlocking { repository.fetchTeamThreads("gameId") }
        }
        assertThat(exception.message).isEqualTo("Can't fetch team threads")
    }

    @Test
    fun `emits team threads on a memory data source update`() = runTest {
        val testFlow = testFlowOf(repository.observeTeamThreads("gameId1"))

        teamThreadDataSource.update("gameId1", teamThreadsFixture("contentId"))
        teamThreadDataSource.update("gameId1", teamThreadsFixture("contentId2"))

        assertStream(testFlow)
            .hasReceivedExactly(null, teamThreadsFixture("contentId"), teamThreadsFixture("contentId2"))
        testFlow.finish()
    }

    @Test
    fun `call api to update the current thread on a switch thread`() = runTest {
        val remoteThreads = createRemoteTeamThreads()
        whenever(teamThreadsApi.updateTeamThread("gameId", "teamId")).thenReturn(remoteThreads)
        val success = teamThreadsApi.updateTeamThread("gameId", "teamId")

        verify(teamThreadsApi).updateTeamThread("gameId", "teamId")
        assertThat(success).isEqualTo(remoteThreads)
    }

    @Test
    fun `exposes thrown update operation exception on a team threads update failure`() = runTest {
        whenever(teamThreadsApi.updateTeamThread("gameId", "teamId")).then {
            throw Exception("Can't select the team thread for game gameId team: teamId")
        }
        val exception = assertThrows(Exception::class.java) {
            runBlocking { teamThreadsApi.updateTeamThread("gameId", "teamId") }
        }

        assertThat(exception.message).isEqualTo("Can't select the team thread for game gameId team: teamId")
    }

    private fun createRemoteTeamThreads() = TeamSpecificThreadsQuery.TeamSpecificThreads(
        content_id = "contentId",
        content_type = "contentType",
        current_thread = TeamSpecificThreadsQuery.Current_thread(__typename = "", fragments = currentTeamThreadFragment()),
        threads = emptyList()
    )

    private fun currentTeamThreadFragment() = TeamSpecificThreadsQuery.Current_thread.Fragments(remoteTeamThreadFixture())
}