package com.theathletic.comments.data.remote

import com.theathletic.CreatedQaCommentSubscription
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.comments.data.QandaComment
import com.theathletic.comments.data.local.QandaCommentsLocalDataStore
import com.theathletic.data.RemoteToLocalSubscriber
import com.theathletic.utility.coroutines.DispatcherProvider

class QandaCommentSubscriber @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val commentsApi: CommentsApi,
    private val qandaInMemoryDataStore: QandaCommentsLocalDataStore
) : RemoteToLocalSubscriber<
    QandaCommentSubscriber.Params,
    CreatedQaCommentSubscription.Data,
    QandaComment
    >(dispatcherProvider) {

    data class Params(
        val key: String,
        val qandaId: String
    )

    override suspend fun makeRemoteRequest(
        params: Params
    ) = commentsApi.subscribeCreatedQAComment(params.qandaId)

    override fun mapToLocalModel(
        params: Params,
        remoteModel: CreatedQaCommentSubscription.Data
    ) = QandaComment(
        commentId = remoteModel.createdQaComment.id,
        authorId = remoteModel.createdQaComment.author_id,
        authorName = remoteModel.createdQaComment.author_name,
        authorUserLevel = remoteModel.createdQaComment.author_user_level,
        authorAvatarUrl = remoteModel.createdQaComment.avatar_url.orEmpty(),
        parentCommentId = if (remoteModel.createdQaComment.parent_id == "0") {
            null
        } else {
            remoteModel.createdQaComment.parent_id
        },
        parentUserId = remoteModel.createdQaComment.parent_user_id
    )

    override suspend fun saveLocally(params: Params, dbModel: QandaComment) {
        qandaInMemoryDataStore.update(params.key, dbModel)
    }
}