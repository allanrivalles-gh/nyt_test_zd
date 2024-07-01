package com.theathletic.rooms.remote

import com.theathletic.EndLiveRoomMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.rooms.RoomsApi
import com.theathletic.rooms.local.LiveAudioRoomUserDetails
import com.theathletic.rooms.local.LiveAudioRoomUserDetailsDataSource
import com.theathletic.utility.coroutines.DispatcherProvider

class EndLiveRoomFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val roomsApi: RoomsApi,
    private val entityDataSource: EntityDataSource,
    private val liveAudioRoomUserDetails: LiveAudioRoomUserDetailsDataSource
) : RemoteToLocalFetcher<
    EndLiveRoomFetcher.Params,
    EndLiveRoomMutation.Data,
    EndLiveRoomFetcher.LocalModels
    >(dispatcherProvider) {

    data class Params(
        val roomId: String,
    )

    data class LocalModels(
        val entity: LiveAudioRoomEntity,
        val userDetails: List<LiveAudioRoomUserDetails>
    )

    override suspend fun makeRemoteRequest(
        params: Params
    ) = roomsApi.endRoom(roomId = params.roomId).data

    override fun mapToLocalModel(
        params: Params,
        remoteModel: EndLiveRoomMutation.Data
    ) = LocalModels(
        entity = remoteModel.endLiveRoom.fragments.liveRoomFragment.toEntity(),
        userDetails = remoteModel.endLiveRoom.fragments.liveRoomFragment.toParticipantList()
    )

    override suspend fun saveLocally(params: Params, dbModel: LocalModels) {
        entityDataSource.insertOrUpdate(dbModel.entity)
        liveAudioRoomUserDetails.update(dbModel.entity.id, dbModel.userDetails)
    }
}