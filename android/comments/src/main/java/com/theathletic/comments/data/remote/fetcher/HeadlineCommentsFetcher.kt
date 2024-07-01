package com.theathletic.comments.data.remote.fetcher

import com.theathletic.CommentsForHeadlineQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.comments.data.CommentsFeed
import com.theathletic.comments.data.local.CommentsDataStore
import com.theathletic.comments.data.remote.CommentsApi
import com.theathletic.comments.data.toDomain
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.headline.data.local.HeadlineEntity
import com.theathletic.utility.coroutines.DispatcherProvider

class HeadlineCommentsFetcher @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider,
    private val commentsApi: CommentsApi,
    private val commentsLocalDataStore: CommentsDataStore,
    private val entityDataSource: EntityDataSource
) : RemoteToLocalFetcher<
    HeadlineCommentsFetcher.Params,
    CommentsForHeadlineQuery.Data,
    CommentsFeed
    >(dispatcherProvider) {

    data class Params(
        val headlineId: String,
        val key: String,
        val sortBy: String
    )

    override suspend fun makeRemoteRequest(params: Params) = commentsApi.getCommentsForHeadline(
        params.headlineId,
        params.sortBy
    ).data

    override fun mapToLocalModel(
        params: Params,
        remoteModel: CommentsForHeadlineQuery.Data
    ) = remoteModel.toDomain()

    override suspend fun saveLocally(params: Params, dbModel: CommentsFeed) {
        commentsLocalDataStore.update(
            params.key,
            dbModel
        )

        entityDataSource.update<HeadlineEntity>(params.headlineId) {
            copy(commentsCount = dbModel.commentsCount)
        }
    }
}