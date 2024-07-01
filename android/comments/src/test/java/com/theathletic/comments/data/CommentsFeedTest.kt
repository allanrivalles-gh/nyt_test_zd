package com.theathletic.comments.data

import com.google.common.truth.Truth.assertThat
import com.theathletic.entity.user.SortType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CommentsFeedTest {
    private lateinit var commentsFeed: CommentsFeed
    private val initialCommentsList = listOf(
        commentFixture(),
        commentFixture(id = "commentId-2", parentId = "commentId-2")
    )

    @Before
    fun setUp() {
        commentsFeed = CommentsFeed(comments = initialCommentsList, commentsCount = initialCommentsList.size)
    }

    @Test
    fun `adding a new comment with NEWEST sort type results in addition to index 0`() {
        val newComment = commentFixture(comment = "new comment")
        val updatedFeed = commentsFeed.addComment(newComment = newComment, sortedBy = SortType.NEWEST)
        val updatedList = (setOf(newComment) + initialCommentsList).toList()

        assertThat(updatedFeed.updatedIndex).isEqualTo(0)
        assertThat(updatedFeed.updatedCommentsFeed).isEqualTo(
            commentsFeed.copy(
                comments = updatedList,
                commentsCount = 3
            )
        )
    }

    @Test
    fun `adding a new comment with MOST LIKED sort type results in addition to last index`() {
        val newComment = commentFixture(comment = "new comment")
        val updateFeed = commentsFeed.addComment(newComment = newComment, sortedBy = SortType.MOST_LIKED)
        val lastIndex = updateFeed.updatedCommentsFeed.comments.lastIndex
        val updatedList = initialCommentsList + setOf(newComment)

        assertThat(updateFeed.updatedIndex).isEqualTo(lastIndex)
        assertThat(updateFeed.updatedCommentsFeed).isEqualTo(
            commentsFeed.copy(
                comments = updatedList,
                commentsCount = 3
            )
        )
    }

    @Test
    fun `adding a new comment with OLDEST sort type results in addition to last index`() {
        val newComment = commentFixture(comment = "new comment")
        val updateFeed = commentsFeed.addComment(newComment = newComment, sortedBy = SortType.OLDEST)
        val lastIndex = updateFeed.updatedCommentsFeed.comments.lastIndex
        val updatedList = initialCommentsList + setOf(newComment)

        assertThat(updateFeed.updatedIndex).isEqualTo(lastIndex)
        assertThat(updateFeed.updatedCommentsFeed).isEqualTo(
            commentsFeed.copy(
                comments = updatedList,
                commentsCount = 3
            )
        )
    }

    @Test
    fun `adding a new comment with TRENDING sort type results in addition to last index`() {
        val newComment = commentFixture(comment = "new comment")
        val updateFeed = commentsFeed.addComment(newComment = newComment, sortedBy = SortType.TRENDING)
        val updatedList = initialCommentsList + setOf(newComment)

        assertThat(updateFeed.updatedIndex).isEqualTo(updateFeed.updatedCommentsFeed.comments.lastIndex)
        assertThat(updateFeed.updatedCommentsFeed).isEqualTo(
            commentsFeed.copy(
                comments = updatedList,
                commentsCount = 3
            )
        )
    }

    @Test
    fun `deleting a comment results in list with commentId removed`() {
        val deleteComment = commentsFeed.comments.first()
        val updateFeed = commentsFeed.deleteComment(deleteComment.id)
        val updatedList = initialCommentsList - setOf(deleteComment)

        assertThat(updateFeed.updatedIndex).isEqualTo(-1)
        assertThat(updateFeed.updatedCommentsFeed).isEqualTo(
            commentsFeed.copy(
                comments = updatedList,
                commentsCount = 1
            )
        )
    }

    @Test
    fun `editing a comment results in list with edited comment`() {
        val firstComment = commentsFeed.comments.first()
        val updateFeed = commentsFeed.editComment(firstComment.id, "new comment")
        val updatedList = (setOf(firstComment.copy(comment = "new comment")) + setOf(initialCommentsList.last())).toList()

        assertThat(updateFeed.updatedIndex).isEqualTo(0)
        assertThat(updateFeed.updatedCommentsFeed).isEqualTo(
            commentsFeed.copy(comments = updatedList)
        )
    }

    @Test
    fun `liking a comment results in list with liked comment`() {
        val firstComment = commentsFeed.comments.first()
        val updateFeed = commentsFeed.addLikeAction(firstComment.id, LikeAction.LIKE)
        val updatedList = (
            setOf(
                firstComment.copy(
                    likesCount = firstComment.likesCount.inc(),
                    hasUserLiked = true
                )
            ) + setOf(initialCommentsList.last())
            ).toList()

        assertThat(updateFeed.updatedIndex).isEqualTo(0)
        assertThat(updateFeed.updatedCommentsFeed).isEqualTo(
            commentsFeed.copy(comments = updatedList)
        )
    }

    @Test
    fun `unliking a comment results in list with unliked comment`() {
        val firstComment = commentsFeed.comments.first()
        val updateFeed = commentsFeed.addLikeAction(firstComment.id, LikeAction.UNLIKE)
        val updatedList = (
            setOf(
                firstComment.copy(
                    likesCount = firstComment.likesCount.dec(),
                    hasUserLiked = false
                )
            ) + setOf(initialCommentsList.last())
            ).toList()

        assertThat(updateFeed.updatedIndex).isEqualTo(0)
        assertThat(updateFeed.updatedCommentsFeed).isEqualTo(
            commentsFeed.copy(comments = updatedList)
        )
    }
}