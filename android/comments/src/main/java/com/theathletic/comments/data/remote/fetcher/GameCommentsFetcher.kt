package com.theathletic.comments.data.remote.fetcher

import com.theathletic.CommentsForGameQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.comments.data.CommentsFeed
import com.theathletic.comments.data.local.CommentsDataStore
import com.theathletic.comments.data.remote.CommentsApi
import com.theathletic.comments.data.toDomain
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.utility.coroutines.DispatcherProvider

class GameCommentsFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val commentsApi: CommentsApi,
    private val commentsLocalDataStore: CommentsDataStore
) : RemoteToLocalFetcher<
    GameCommentsFetcher.Params,
    CommentsForGameQuery.Data,
    CommentsFeed
    >(dispatcherProvider) {

    data class Params(
        val key: String,
        val gameId: String,
        val teamId: String,
        val sortBy: String
    )

    override suspend fun makeRemoteRequest(params: Params): CommentsForGameQuery.Data? =
        commentsApi.getCommentsForGame(
            id = params.gameId,
            teamId = params.teamId,
            sortBy = params.sortBy
        ).data

    override fun mapToLocalModel(
        params: Params,
        remoteModel: CommentsForGameQuery.Data
    ): CommentsFeed = remoteModel.toDomain()

    override suspend fun saveLocally(params: Params, dbModel: CommentsFeed) {
        commentsLocalDataStore.update(
            params.key,
            dbModel
        )
    }
}