package com.theathletic.comments

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.comments.data.CommentsRepository
import com.theathletic.repository.user.IUserDataRepository
import timber.log.Timber

class LikeCommentsUseCase @AutoKoin constructor(
    private val userDataRepository: IUserDataRepository,
    private val commentsRepository: CommentsRepository,
) {
    suspend operator fun invoke(
        hasUserLiked: Boolean,
        commentId: String,
    ) = if (hasUserLiked) {
        unlikeComment(commentId)
    } else {
        likeComment(commentId)
    }

    private suspend fun likeComment(
        commentId: String,
    ) = try {
        commentsRepository.likeComment(commentId)
        userDataRepository.markCommentLiked(id = commentId.toLong(), isLiked = true)
        Result.success(Unit)
    } catch (e: Throwable) {
        Timber.e(e, "Error on like comment $commentId")
        Result.failure(e)
    }

    private suspend fun unlikeComment(
        commentId: String,
    ) = try {
        commentsRepository.unlikeComment(commentId)
        userDataRepository.markCommentLiked(id = commentId.toLong(), isLiked = false)
        Result.success(Unit)
    } catch (e: Throwable) {
        Timber.e(e, "Error on unlike comment $commentId")
        Result.failure(e)
    }
}