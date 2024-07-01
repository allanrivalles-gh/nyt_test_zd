package com.theathletic.comments.data.remote.fetcher

import com.theathletic.FlagCommentMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.comments.FlagReason
import com.theathletic.comments.data.remote.CommentsApi
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.utility.coroutines.DispatcherProvider

class ArticleFlagCommentFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val commentsApi: CommentsApi,
    private val userDataRepository: IUserDataRepository,
    private val entityDataSource: EntityDataSource
) : RemoteToLocalFetcher<ArticleFlagCommentFetcher.Params, FlagCommentMutation.Data, Unit>(dispatcherProvider) {

    data class Params(
        val articleId: Long,
        val commentId: Long,
        val flag: FlagReason
    )

    override suspend fun makeRemoteRequest(params: Params) =
        commentsApi.flagCommentAsync(params.commentId.toString(), params.flag).data

    override fun mapToLocalModel(params: Params, remoteModel: FlagCommentMutation.Data) { }

    override suspend fun saveLocally(params: Params, dbModel: Unit) {
        userDataRepository.markCommentFlagged(params.commentId, true)
        entityDataSource.update<ArticleEntity>(params.articleId.toString()) {
            copy(
                comments = comments?.map { comment ->
                    if (comment.commentId == params.commentId) {
                        comment.isFlagged = true
                    }
                    comment
                }
            )
        }
    }
}