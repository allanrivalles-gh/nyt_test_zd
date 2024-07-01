package com.theathletic.comments.data.remote.fetcher

import com.theathletic.DeleteCommentMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.comments.data.remote.CommentsApi
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.utility.coroutines.DispatcherProvider

class ArticleDeleteCommentFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val commentsApi: CommentsApi,
    private val entityDataSource: EntityDataSource
) : RemoteToLocalFetcher<ArticleDeleteCommentFetcher.Params, DeleteCommentMutation.Data, Unit>(dispatcherProvider) {

    data class Params(
        val articleId: Long,
        val commentId: Long
    )

    override suspend fun makeRemoteRequest(params: Params) =
        commentsApi.deleteCommentAsync(params.commentId.toString()).data

    override fun mapToLocalModel(params: Params, remoteModel: DeleteCommentMutation.Data) { }

    override suspend fun saveLocally(params: Params, dbModel: Unit) {
        entityDataSource.update<ArticleEntity>(params.articleId.toString()) {
            copy(
                comments = comments?.filter { it.commentId != params.commentId }
            )
        }
    }
}