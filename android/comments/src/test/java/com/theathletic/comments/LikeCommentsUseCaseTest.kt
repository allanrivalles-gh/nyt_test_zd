package com.theathletic.comments

import com.google.common.truth.Truth.assertThat
import com.theathletic.comments.data.CommentsRepository
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

private const val TEST_ID = "123"
private const val USER_LIKED = true
private const val USER_UNLIKED = false

@RunWith(MockitoJUnitRunner::class)
class LikeCommentsUseCaseTest {
    @Mock private lateinit var userDataRepository: IUserDataRepository
    @Mock private lateinit var commentsRepository: CommentsRepository

    private lateinit var likeCommentsUseCase: LikeCommentsUseCase

    @Before
    fun setUp() {
        likeCommentsUseCase = LikeCommentsUseCase(userDataRepository, commentsRepository)
    }

    @Test
    fun `like a comment which is unliked results in success`() = runTest {
        whenever(commentsRepository.likeComment(TEST_ID)).thenAnswer { true }
        val result = likeCommentsUseCase(hasUserLiked = USER_UNLIKED, commentId = TEST_ID)

        assertThat(result.isSuccess)
    }

    @Test
    fun `like a comment which is unliked should mark comment as liked`() = runTest {
        whenever(commentsRepository.likeComment(TEST_ID)).thenAnswer { true }
        likeCommentsUseCase(hasUserLiked = USER_UNLIKED, commentId = TEST_ID)

        verify(userDataRepository).markCommentLiked(id = TEST_ID.toLong(), isLiked = true)
    }

    @Test
    fun `result is a failure when like call fails on the server`() = runTest {
        whenever(commentsRepository.likeComment(TEST_ID)).thenAnswer { throw Exception() }
        val result = likeCommentsUseCase(hasUserLiked = USER_UNLIKED, commentId = TEST_ID)

        assertThat(result.isFailure)
    }

    @Test
    fun `comment should not be marked as liked if server call fails`() = runTest {
        whenever(commentsRepository.likeComment(TEST_ID)).thenAnswer { throw Exception() }
        likeCommentsUseCase(hasUserLiked = USER_UNLIKED, commentId = TEST_ID)

        verify(userDataRepository, never()).markCommentLiked(id = TEST_ID.toLong(), isLiked = true)
    }

    @Test
    fun `unlike a comment which is liked results in success`() = runTest {
        whenever(commentsRepository.unlikeComment(TEST_ID)).thenAnswer { true }
        val result = likeCommentsUseCase(hasUserLiked = USER_LIKED, commentId = TEST_ID)

        assertThat(result.isSuccess)
    }

    @Test
    fun `unlike a comment which is liked should mark comment as unliked`() = runTest {
        whenever(commentsRepository.unlikeComment(TEST_ID)).thenAnswer { true }
        likeCommentsUseCase(hasUserLiked = USER_LIKED, commentId = TEST_ID)

        verify(userDataRepository).markCommentLiked(id = TEST_ID.toLong(), isLiked = false)
    }

    @Test
    fun `result is a failure when unlike fails on server`() = runTest {
        whenever(commentsRepository.unlikeComment(TEST_ID)).thenAnswer { throw Exception() }
        val result = likeCommentsUseCase(hasUserLiked = USER_LIKED, commentId = TEST_ID)

        assertThat(result.isFailure)
    }

    @Test
    fun `comment should not be marked as unliked if server call fails`() = runTest {
        whenever(commentsRepository.unlikeComment(TEST_ID)).thenAnswer { throw Exception() }
        likeCommentsUseCase(hasUserLiked = USER_LIKED, commentId = TEST_ID)

        verify(userDataRepository, never()).markCommentLiked(id = TEST_ID.toLong(), isLiked = false)
    }
}