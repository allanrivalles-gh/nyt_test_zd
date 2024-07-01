package com.theathletic.comments.data.remote.fetcher

import com.theathletic.CommentsForPodcastEpisodeQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.comments.data.CommentsFeed
import com.theathletic.comments.data.local.CommentsDataStore
import com.theathletic.comments.data.remote.CommentsApi
import com.theathletic.comments.data.toDomain
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.podcast.data.local.PodcastDao
import com.theathletic.utility.coroutines.DispatcherProvider

class PodcastEpisodeCommentsFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val commentsApi: CommentsApi,
    private val podcastDao: PodcastDao,
    private val commentsLocalDataStore: CommentsDataStore
) : RemoteToLocalFetcher<
    PodcastEpisodeCommentsFetcher.Params,
    CommentsForPodcastEpisodeQuery.Data,
    CommentsFeed
    >(dispatcherProvider) {

    data class Params(
        val key: String,
        val episodeId: String,
        val sortBy: String
    )

    override suspend fun makeRemoteRequest(
        params: Params
    ) = commentsApi.getCommentsForPodcastEpisode(params.episodeId, params.sortBy).data

    override fun mapToLocalModel(
        params: Params,
        remoteModel: CommentsForPodcastEpisodeQuery.Data
    ): CommentsFeed {
        return remoteModel.toDomain()
    }

    override suspend fun saveLocally(params: Params, dbModel: CommentsFeed) {
        val commentsLocked = podcastDao.getPodcastEpisodeSuspend(params.episodeId.toLong())?.commentsLocked ?: true
        commentsLocalDataStore.update(params.key, dbModel.copy(commentsLocked = commentsLocked))
    }
}