package com.theathletic.podcast.data.remote

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.LocalModel
import com.theathletic.data.RemoteToLocalListFetcher
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.entity.local.EntityQueries
import com.theathletic.entity.main.PodcastItem
import com.theathletic.podcast.data.local.PodcastDao
import com.theathletic.podcast.data.local.PodcastSeriesEntity
import com.theathletic.utility.coroutines.DispatcherProvider

class UserPodcastsFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val podcastRestApi: PodcastRestApi,
    private val podcastDao: PodcastDao,
    private val entityDataSource: EntityDataSource,
    private val entityQueries: EntityQueries
) : RemoteToLocalListFetcher<UserPodcastsFetcher.Params, PodcastRemote, UserPodcastsFetcher.EntityHolder>(dispatcherProvider) {

    data class Params(val userId: Long)

    data class EntityHolder(
        val oldEntity: PodcastItem,
        val newEntity: PodcastSeriesEntity
    ) : LocalModel

    override suspend fun makeRemoteRequest(
        params: Params
    ) = podcastRestApi.getUserFollowedPodcasts(params.userId)

    override fun mapToLocalModel(
        params: Params,
        networkModel: List<PodcastRemote>
    ) = networkModel.map {
        EntityHolder(it.toDbModel(), it.toEntity())
    }

    override suspend fun saveLocally(params: Params, dbModel: List<EntityHolder>) {
        podcastDao.insertPodcasts(dbModel.map { it.oldEntity })

        val entities = dbModel.map { it.newEntity }
        entityQueries.replaceFollowedByType(AthleticEntity.Type.PODCAST_SERIES, entities)

        entityDataSource.insert(entities)
    }
}