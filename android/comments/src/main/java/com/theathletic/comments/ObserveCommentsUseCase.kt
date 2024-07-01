package com.theathletic.comments

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.comments.data.Comment
import com.theathletic.comments.data.CommentsDataHandlerFactory
import com.theathletic.comments.data.CommentsFeed
import com.theathletic.comments.data.Flair
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.featureswitch.Features
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.user.IUserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class ObserveCommentsUseCase @AutoKoin constructor(
    private val commentDataHandlerFactory: CommentsDataHandlerFactory,
    private val userDataRepository: IUserDataRepository,
    private val userManager: IUserManager,
    private val features: Features,
) {
    operator fun invoke(
        sourceId: String,
        sourceType: CommentsSourceType,
        scope: CoroutineScope
    ) = callbackFlow {
        commentDataHandlerFactory
            .instantiate(sourceType)
            .setupListenerForDataUpdates(sourceId, scope) { commentsFeed ->
                val updatedFeed = commentsFeed?.copy(
                    comments = commentsFeed.flattenComments.updateUserData(sourceType),
                ) ?: CommentsFeed()
                send(updatedFeed)
            }

        awaitClose()
    }

    private fun List<Comment>.updateUserData(sourceType: CommentsSourceType) = map { comment ->
        comment.copy(
            hasUserLiked = userDataRepository.isCommentLiked(comment.id.toLong()),
            isAuthor = userManager.getCurrentUserId().toString() == comment.authorId,
            authorFlairs = comment.authorFlairs.filterGameFlairs(sourceType),
        )
    }

    private fun List<Flair>.filterGameFlairs(sourceType: CommentsSourceType): List<Flair> {
        val isGameComment = (
            sourceType == CommentsSourceType.TEAM_SPECIFIC_THREAD &&
                features.areTeamSpecificCommentsEnabled
            ) || sourceType == CommentsSourceType.GAME

        return if (isGameComment) { this } else { emptyList() }
    }

    private val CommentsFeed.flattenComments get() = comments.flatMap { listOf(it) + it.replies }
}