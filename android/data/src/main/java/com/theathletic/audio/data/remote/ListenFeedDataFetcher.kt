package com.theathletic.audio.data.remote

import com.theathletic.ListenFeedDataQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.audio.data.local.CurrentLiveRoomsData
import com.theathletic.audio.data.local.CurrentLiveRoomsLocalDataSource
import com.theathletic.audio.data.local.ListenFeedData
import com.theathletic.audio.data.local.ListenFeedDataLocalDataSource
import com.theathletic.data.EmptyParams
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.entity.main.fromEntity
import com.theathletic.podcast.data.local.PodcastDao
import com.theathletic.podcast.data.local.PodcastEpisodeEntity
import com.theathletic.utility.coroutines.DispatcherProvider

class ListenFeedDataFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val audioApi: AudioApi,
    private val entityDataSource: EntityDataSource,
    private val listenFeedDataSource: ListenFeedDataLocalDataSource,
    private val podcastDao: PodcastDao,
    private val currentLiveRoomsDataSource: CurrentLiveRoomsLocalDataSource,
) : RemoteToLocalFetcher<
    EmptyParams,
    ListenFeedDataQuery.Data,
    ListenFeedDataFetcher.LocalModels?
    >(dispatcherProvider) {

    data class LocalModels(
        val entities: List<AthleticEntity>,
        val feedData: ListenFeedData.WithIds,
        val liveRoomData: CurrentLiveRoomsData,
    )

    override suspend fun makeRemoteRequest(
        params: EmptyParams
    ) = audioApi.getListenFeedData().data

    override fun mapToLocalModel(
        params: EmptyParams,
        remoteModel: ListenFeedDataQuery.Data
    ) = remoteModel.podcastFeed?.let { feed ->
        LocalModels(
            entities = feed.getAllEntities(),
            feedData = feed.toLocalModel(),
            liveRoomData = CurrentLiveRoomsData(
                followingLiveRoomIds = feed.user_live_rooms.mapNotNull { it?.fragments?.liveRoomFragment?.id },
                discoverLiveRoomIds = feed.discover_live_rooms.mapNotNull { it?.fragments?.liveRoomFragment?.id },
            ),
        )
    }

    override suspend fun saveLocally(params: EmptyParams, localModels: LocalModels?) {
        val models = localModels ?: return

        // Needed to mark in the non-entity DB as downloaded or not. Once we revamp the podcast
        // download pipeline, we can delete the legacy things like this
        podcastDao.insertPodcastEpisodesTransaction(
            localModels.entities.filterIsInstance<PodcastEpisodeEntity>().map {
                PodcastEpisodeItem.fromEntity(it)
            }
        )

        currentLiveRoomsDataSource.update(models.liveRoomData)

        entityDataSource.insertOrUpdate(models.entities)
        listenFeedDataSource.update(localModels.feedData)
    }
}