package com.theathletic.comments

import com.google.common.truth.Truth.assertThat
import com.theathletic.comments.data.CommentsRepository
import com.theathletic.comments.data.commentFixture
import com.theathletic.comments.data.commentInputFixture
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.data.ContentDescriptor
import com.theathletic.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class AddCommentUseCaseTest {

    @Mock lateinit var commentsRepository: CommentsRepository

    private lateinit var addComment: AddCommentUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        addComment = AddCommentUseCase(commentsRepository)
    }

    @Test
    fun `returns Result-success with the new comment in a success comment add`() = runTest {
        val commentInput = commentInputFixture(sourceType = CommentsSourceType.TEAM_SPECIFIC_THREAD)
        whenever(commentsRepository.addComment(commentInput)).thenReturn(commentFixture(comment = "Test comment"))

        val result = addComment(commentInput)

        assert(result.isSuccess)
        assertThat(result.getOrNull()?.comment).isEqualTo("Test comment")
    }

    @Test
    fun `returns Result-failure when the add comment operation fails`() = runTest {
        val teamComment = commentInputFixture(sourceDescriptor = ContentDescriptor("gameId"), sourceType = CommentsSourceType.TEAM_SPECIFIC_THREAD)
        whenever(commentsRepository.addComment(teamComment)).then { throw Exception("Can't add the comment") }

        val result = addComment(teamComment)

        assert(result.isFailure)
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Can't add the comment")
    }
}