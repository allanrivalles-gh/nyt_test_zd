package com.theathletic.comments

import com.theathletic.comments.data.CommentsRepository
import com.theathletic.comments.game.TeamThreadsRepository
import com.theathletic.comments.game.data.teamThreadsFixture
import com.theathletic.comments.game.data.threadFixture
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.entity.user.SortType
import com.theathletic.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class FetchCommentsUseCaseTest {

    @Mock private lateinit var commentsRepository: CommentsRepository
    @Mock private lateinit var teamThreadsRepository: TeamThreadsRepository

    private lateinit var fetchComments: FetchCommentsUseCase

    @Before
    fun setUp() {
        fetchComments = FetchCommentsUseCase(commentsRepository, teamThreadsRepository)
    }

    @Test
    fun `fetch team threads when the source type is TEAM_SPECIFIC_THREAD`() = runTest {
        fetchComments("gameId", SortType.NEWEST, CommentsSourceType.TEAM_SPECIFIC_THREAD)

        verify(teamThreadsRepository).fetchTeamThreads("gameId")
    }

    @Test
    fun `do not fetch team threads when the source type is not TEAM_SPECIFIC_THREAD`() = runTest {
        fetchComments("gameId", SortType.NEWEST, CommentsSourceType.GAME)
        fetchComments("gameId", SortType.NEWEST, CommentsSourceType.ARTICLE)

        verify(teamThreadsRepository, times(0)).fetchTeamThreads("gameId")
    }

    @Test
    fun `fetch GAME comments for the current team thread team on a successful team threads load`() = runTest {
        val teamThreads = teamThreadsFixture(currentThread = threadFixture(teamId = "cam13"))
        whenever(teamThreadsRepository.fetchTeamThreads("gameId")).thenReturn(teamThreads)

        fetchComments("gameId", SortType.NEWEST, CommentsSourceType.TEAM_SPECIFIC_THREAD)

        verify(commentsRepository).fetchGameComments("gameId", "cam13", "GAME-gameId", "recent")
    }

    @Test
    fun `fetch GAME comments for both teams when the source type is GAME`() = runTest {
        fetchComments("gameId", SortType.NEWEST, CommentsSourceType.GAME)

        verify(commentsRepository).fetchGameComments(gameId = "gameId", key = "GAME-gameId", sortBy = "recent")
    }
}