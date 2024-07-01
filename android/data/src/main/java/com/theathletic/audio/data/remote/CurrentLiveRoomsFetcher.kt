package com.theathletic.audio.data.remote

import com.theathletic.LiveRoomAvailableQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.audio.data.local.CurrentLiveRoomsData
import com.theathletic.audio.data.local.CurrentLiveRoomsLocalDataSource
import com.theathletic.data.EmptyParams
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.utility.coroutines.DispatcherProvider

class CurrentLiveRoomsFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val audioApi: AudioApi,
    private val currentLiveRoomsDataSource: CurrentLiveRoomsLocalDataSource,
) : RemoteToLocalFetcher<
    EmptyParams,
    LiveRoomAvailableQuery.Data,
    CurrentLiveRoomsFetcher.LocalModels
    >(dispatcherProvider) {

    data class LocalModels(
        val liveRoomData: CurrentLiveRoomsData,
    )

    override suspend fun makeRemoteRequest(params: EmptyParams) = audioApi.currentLiveRooms().data

    override fun mapToLocalModel(
        params: EmptyParams,
        remoteModel: LiveRoomAvailableQuery.Data
    ) = LocalModels(
        liveRoomData = CurrentLiveRoomsData(
            followingLiveRoomIds = remoteModel.podcastFeed?.user_live_rooms?.mapNotNull { it?.id }.orEmpty(),
            discoverLiveRoomIds = remoteModel.podcastFeed?.discover_live_rooms?.mapNotNull { it?.id }.orEmpty(),
        ),
    )

    override suspend fun saveLocally(params: EmptyParams, models: LocalModels) {
        currentLiveRoomsDataSource.update(models.liveRoomData)
    }
}