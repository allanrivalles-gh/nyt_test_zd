package com.theathletic.comments.data.remote.fetcher

import com.theathletic.CommentsForDiscussionQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.comments.data.CommentsFeed
import com.theathletic.comments.data.local.CommentsDataStore
import com.theathletic.comments.data.remote.CommentsApi
import com.theathletic.comments.data.toDomain
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.utility.coroutines.DispatcherProvider

class DiscussionCommentsFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val commentsApi: CommentsApi,
    private val commentsLocalDataStore: CommentsDataStore
) : RemoteToLocalFetcher<
    DiscussionCommentsFetcher.Params,
    CommentsForDiscussionQuery.Data,
    CommentsFeed?
    >(dispatcherProvider) {

    data class Params(
        val key: String,
        val discussionId: String,
        val sortBy: String
    )

    override suspend fun makeRemoteRequest(params: Params) =
        commentsApi.getCommentsForDiscussion(params.discussionId, params.sortBy).data

    override fun mapToLocalModel(
        params: Params,
        remoteModel: CommentsForDiscussionQuery.Data
    ) = remoteModel.toDomain()

    override suspend fun saveLocally(params: Params, dbModel: CommentsFeed?) {
        dbModel?.let {
            commentsLocalDataStore.update(
                params.key,
                dbModel
            )
        }
    }
}