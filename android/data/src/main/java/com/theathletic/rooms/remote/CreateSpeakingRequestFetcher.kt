package com.theathletic.rooms.remote

import com.theathletic.CreateSpeakingRequestMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.rooms.RoomsApi
import com.theathletic.rooms.local.LiveAudioRoomUserDetails
import com.theathletic.rooms.local.LiveAudioRoomUserDetailsDataSource
import com.theathletic.user.IUserManager
import com.theathletic.utility.coroutines.DispatcherProvider

class CreateSpeakingRequestFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val userManager: IUserManager,
    private val roomsApi: RoomsApi,
    private val entityDataSource: EntityDataSource,
    private val liveAudioRoomUserDetails: LiveAudioRoomUserDetailsDataSource
) : RemoteToLocalFetcher<
    CreateSpeakingRequestFetcher.Params,
    CreateSpeakingRequestMutation.Data,
    CreateSpeakingRequestFetcher.LocalModels
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
    ) = roomsApi.requestToSpeak(
        roomId = params.roomId,
        userId = userManager.getCurrentUserId().toString()
    ).data

    override fun mapToLocalModel(
        params: Params,
        remoteModel: CreateSpeakingRequestMutation.Data
    ) = LocalModels(
        entity = remoteModel.createSpeakingRequest.fragments.liveRoomFragment.toEntity(),
        userDetails = remoteModel.createSpeakingRequest.fragments.liveRoomFragment.toParticipantList()
    )

    override suspend fun saveLocally(params: Params, dbModel: LocalModels) {
        entityDataSource.insertOrUpdate(dbModel.entity)
        liveAudioRoomUserDetails.update(dbModel.entity.id, dbModel.userDetails)
    }
}