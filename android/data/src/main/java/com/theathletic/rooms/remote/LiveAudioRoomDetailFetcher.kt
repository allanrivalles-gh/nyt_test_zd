package com.theathletic.rooms.remote

import com.theathletic.GetLiveRoomQuery
import com.theathletic.LiveRoomDetailsSubscription
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.data.RemoteToLocalSubscriber
import com.theathletic.entity.chat.ChatRoomEntity
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.rooms.RoomsApi
import com.theathletic.rooms.local.LiveAudioRoomUserDetails
import com.theathletic.rooms.local.LiveAudioRoomUserDetailsDataSource
import com.theathletic.utility.coroutines.DispatcherProvider

class LiveAudioRoomDetailFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val roomsApi: RoomsApi,
    private val entityDataSource: EntityDataSource,
    private val liveAudioRoomUserDetails: LiveAudioRoomUserDetailsDataSource
) : RemoteToLocalFetcher<
    LiveAudioRoomDetailFetcher.Params,
    GetLiveRoomQuery.Data,
    LiveAudioRoomDetailFetcher.LocalModels
    >(dispatcherProvider) {

    data class Params(
        val roomId: String,
    )

    data class LocalModels(
        val entity: LiveAudioRoomEntity,
        val userDetails: List<LiveAudioRoomUserDetails>,
        val chatRoom: ChatRoomEntity,
    )

    override suspend fun makeRemoteRequest(
        params: Params
    ) = roomsApi.getRoom(roomId = params.roomId).data

    override fun mapToLocalModel(
        params: Params,
        remoteModel: GetLiveRoomQuery.Data
    ) = remoteModel.liveRoom.fragments.liveRoomFragment.let { fragment ->
        LocalModels(
            entity = fragment.toEntity(),
            userDetails = fragment.toParticipantList(),
            chatRoom = fragment.toChatRoomEntity(),
        )
    }

    override suspend fun saveLocally(params: Params, dbModel: LocalModels) {
        entityDataSource.insertOrUpdate(dbModel.entity)
        liveAudioRoomUserDetails.update(dbModel.entity.id, dbModel.userDetails)
        entityDataSource.insertOrUpdate(dbModel.chatRoom)
    }
}

class LiveRoomDetailSubscriber @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val roomsApi: RoomsApi,
    private val entityDataSource: EntityDataSource,
    private val liveAudioRoomUserDetails: LiveAudioRoomUserDetailsDataSource
) : RemoteToLocalSubscriber<
    LiveRoomDetailSubscriber.Params,
    LiveRoomDetailsSubscription.Data,
    LiveRoomDetailSubscriber.LocalModels
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
    ) = roomsApi.getRoomSubscription(roomId = params.roomId)

    override fun mapToLocalModel(
        params: Params,
        remoteModel: LiveRoomDetailsSubscription.Data
    ) = LocalModels(
        entity = remoteModel.updatedLiveRoom.fragments.liveRoomFragment.toEntity(),
        userDetails = remoteModel.updatedLiveRoom.fragments.liveRoomFragment.toParticipantList()
    )

    override suspend fun saveLocally(params: Params, dbModel: LocalModels) {
        entityDataSource.insertOrUpdate(dbModel.entity)
        liveAudioRoomUserDetails.update(dbModel.entity.id, dbModel.userDetails)
    }
}