package com.theathletic.comments

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.comments.data.Comment
import com.theathletic.comments.data.CommentInput
import com.theathletic.comments.data.CommentsRepository

class AddCommentUseCase @AutoKoin constructor(
    private val commentsRepository: CommentsRepository
) {
    suspend operator fun invoke(commentInput: CommentInput): Result<Comment> = try {
        val comment = commentsRepository.addComment(commentInput)
        Result.success(comment)
    } catch (error: Throwable) {
        Result.failure(error)
    }
}