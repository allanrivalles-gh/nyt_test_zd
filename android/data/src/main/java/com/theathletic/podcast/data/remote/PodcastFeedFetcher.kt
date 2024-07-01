package com.theathletic.podcast.data.remote

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.LocalModel
import com.theathletic.data.RemoteToLocalSingleFetcher
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.entity.main.PodcastFeed
import com.theathletic.podcast.data.local.PodcastDao
import com.theathletic.podcast.data.local.PodcastEpisodeEntity
import com.theathletic.podcast.data.local.PodcastNewEpisodesDataSource
import com.theathletic.podcast.data.local.PodcastSeriesEntity
import com.theathletic.utility.coroutines.DispatcherProvider

class PodcastFeedFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val podcastRestApi: PodcastRestApi,
    private val podcastDao: PodcastDao,
    private val entityDataSource: EntityDataSource,
    private val podcastNewEpisodesDataSource: PodcastNewEpisodesDataSource
) : RemoteToLocalSingleFetcher<
    PodcastFeedFetcher.Params,
    PodcastFeedRemote,
    PodcastFeedFetcher.EntityHolder
    >(dispatcherProvider) {

    data class Params(val userId: Long)

    data class EntityHolder(
        val oldEntity: PodcastFeed,
        val newEpisodeEntities: List<PodcastEpisodeEntity>,
        val seriesEntities: List<PodcastSeriesEntity>
    ) : LocalModel

    override suspend fun makeRemoteRequest(params: Params) = podcastRestApi.getFeed(params.userId)

    override fun mapToLocalModel(
        params: Params,
        remoteModel: PodcastFeedRemote
    ) = remoteModel.run {
        EntityHolder(
            oldEntity = this.toDbModel(),
            newEpisodeEntities = userPodcastEpisodes.map { it.toEntity() },
            seriesEntities = (recommendedPodcasts + featuredPodcasts).map { it.toEntity() }
        )
    }

    override suspend fun saveLocally(params: Params, dbModel: EntityHolder) {
        podcastDao.insertPodcastFeed(dbModel.oldEntity)

        entityDataSource.insertOrUpdate(dbModel.newEpisodeEntities + dbModel.seriesEntities)

        podcastNewEpisodesDataSource.update(dbModel.newEpisodeEntities.map { it.entityId })
    }
}