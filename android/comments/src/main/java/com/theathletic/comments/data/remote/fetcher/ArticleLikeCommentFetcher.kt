package com.theathletic.comments.data.remote.fetcher

import com.theathletic.LikeCommentMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.comments.data.remote.CommentsApi
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.utility.coroutines.DispatcherProvider

class ArticleLikeCommentFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val commentsApi: CommentsApi,
    private val userDataRepository: IUserDataRepository,
    private val entityDataSource: EntityDataSource
) : RemoteToLocalFetcher<ArticleLikeCommentFetcher.Params, LikeCommentMutation.Data, Unit>(dispatcherProvider) {

    data class Params(
        val articleId: Long,
        val commentId: Long
    )

    override suspend fun makeRemoteRequest(params: Params) =
        commentsApi.likeCommentAsync(params.commentId.toString()).data

    override fun mapToLocalModel(params: Params, remoteModel: LikeCommentMutation.Data) { }

    override suspend fun saveLocally(params: Params, dbModel: Unit) {
        userDataRepository.markCommentLiked(params.commentId, true)
        entityDataSource.update<ArticleEntity>(params.articleId.toString()) {
            copy(
                comments = comments?.map { comment ->
                    if (comment.commentId == params.commentId) {
                        comment.likes++
                    }
                    comment
                }
            )
        }
    }
}