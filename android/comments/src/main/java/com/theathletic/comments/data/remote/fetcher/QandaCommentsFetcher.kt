package com.theathletic.comments.data.remote.fetcher

import com.theathletic.CommentsForQandaQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.comments.data.CommentsFeed
import com.theathletic.comments.data.local.CommentsDataStore
import com.theathletic.comments.data.remote.CommentsApi
import com.theathletic.comments.data.toDomain
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.utility.coroutines.DispatcherProvider

class QandaCommentsFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val commentsApi: CommentsApi,
    private val commentsLocalDataStore: CommentsDataStore
) : RemoteToLocalFetcher<
    QandaCommentsFetcher.Params,
    CommentsForQandaQuery.Data,
    CommentsFeed?
    >(dispatcherProvider) {

    data class Params(
        val key: String,
        val qandaId: String,
        val sortBy: String
    )

    override suspend fun makeRemoteRequest(params: Params) =
        commentsApi.getCommentsForQanda(params.qandaId, params.sortBy).data

    override fun mapToLocalModel(
        params: Params,
        remoteModel: CommentsForQandaQuery.Data
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