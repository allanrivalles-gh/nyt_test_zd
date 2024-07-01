package com.theathletic.rooms.remote

import com.theathletic.GenerateLiveRoomTokenMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.rooms.RoomsApi
import com.theathletic.rooms.local.RoomsTokenLocalStorage
import com.theathletic.utility.coroutines.DispatcherProvider

class RoomsTokenFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val roomsApi: RoomsApi,
    private val tokenLocalStorage: RoomsTokenLocalStorage
) : RemoteToLocalFetcher<
    RoomsTokenFetcher.Params,
    GenerateLiveRoomTokenMutation.Data,
    String
    >(dispatcherProvider) {

    data class Params(
        val roomId: String,
        val userId: Long
    )

    override suspend fun makeRemoteRequest(params: Params) = roomsApi.getToken(
        roomId = params.roomId,
        userId = params.userId
    ).data

    override fun mapToLocalModel(
        params: Params,
        remoteModel: GenerateLiveRoomTokenMutation.Data
    ) = remoteModel.generateLiveRoomToken.token

    override suspend fun saveLocally(params: Params, token: String) {
        tokenLocalStorage.put(params.roomId, token)
    }
}