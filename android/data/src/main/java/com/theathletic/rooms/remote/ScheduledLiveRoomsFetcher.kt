package com.theathletic.rooms.remote

import com.theathletic.GetLiveRoomsQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.EmptyParams
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.rooms.RoomsApi
import com.theathletic.rooms.local.ScheduledLiveRoomsDataSource
import com.theathletic.utility.coroutines.DispatcherProvider

class ScheduledLiveRoomsFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val roomsApi: RoomsApi,
    private val scheduledLiveRoomsDataSource: ScheduledLiveRoomsDataSource,
) : RemoteToLocalFetcher<
    EmptyParams,
    GetLiveRoomsQuery.Data,
    ScheduledLiveRoomsFetcher.LocalModels
    >(dispatcherProvider) {

    data class LocalModels(val entities: List<LiveAudioRoomEntity>)

    override suspend fun makeRemoteRequest(params: EmptyParams) = roomsApi.getScheduledRooms().data

    override fun mapToLocalModel(
        params: EmptyParams,
        remoteModel: GetLiveRoomsQuery.Data
    ) = LocalModels(
        entities = remoteModel.liveRooms.items.mapNotNull {
            it?.fragments?.liveRoomFragment?.toEntity()
        }
    )

    override suspend fun saveLocally(params: EmptyParams, dbModel: LocalModels) {
        scheduledLiveRoomsDataSource.update(dbModel.entities)
    }
}