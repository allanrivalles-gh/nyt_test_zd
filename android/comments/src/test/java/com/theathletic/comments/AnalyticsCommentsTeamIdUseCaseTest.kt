package com.theathletic.comments

import com.google.common.truth.Truth
import com.theathletic.comments.game.data.teamFixture
import com.theathletic.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AnalyticsCommentsTeamIdUseCaseTest {
    private val analyticsBoxScoreTeamIdUseCase = AnalyticsCommentTeamIdUseCase()

    @Test
    fun `analytics team id returns team id if it is single team space and no legacy`() = runTest {
        val response = analyticsBoxScoreTeamIdUseCase.invoke(
            isTeamSpecificComment = true,
            team = teamFixture(id = "testId"),
            useLegacy = false
        )
        Truth.assertThat(response).isEqualTo("testId")
    }

    @Test
    fun `analytics team id returns legacy id if it is single team space and legacy`() = runTest {
        val response = analyticsBoxScoreTeamIdUseCase.invoke(
            isTeamSpecificComment = true,
            team = teamFixture(id = "testId", legacyId = "6"),
            useLegacy = true
        )
        Truth.assertThat(response).isEqualTo("6")
    }

    @Test
    fun `analytics team id returns null if is not team specific`() = runTest {
        val response = analyticsBoxScoreTeamIdUseCase.invoke(false, null)
        Truth.assertThat(response).isEqualTo(null)
    }

    @Test
    fun `analytics team id returns null if it is a multi team space`() = runTest {
        val response = analyticsBoxScoreTeamIdUseCase.invoke(
            false,
            team = teamFixture(id = "testId", legacyId = "6")
        )
        Truth.assertThat(response).isEqualTo(null)
    }
}