package com.theathletic.comments.data.remote.handler

import com.theathletic.comments.data.CommentsFeed
import com.theathletic.comments.data.CommentsRepository
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.CoroutineScope

abstract class BaseCommentsDataHandler(
    protected val commentsRepository: CommentsRepository
) {

    abstract fun getCommentsSourceType(): CommentsSourceType

    open fun setupListenerForDataUpdates(
        entityId: String,
        scope: CoroutineScope,
        updateBlock: suspend (CommentsFeed?) -> Unit
    ) {
        commentsRepository.getCommentsFeed(
            buildKey(entityId)
        ).collectIn(scope) {
            updateBlock.invoke(it)
        }
    }

    open fun startSubscriptions(entityId: String) { }

    fun buildKey(entityId: String) = "${getCommentsSourceType()}-$entityId"
}