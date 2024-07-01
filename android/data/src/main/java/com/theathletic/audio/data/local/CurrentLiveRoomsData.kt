package com.theathletic.audio.data.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.local.InMemorySingleLocalDataSource

data class CurrentLiveRoomsData(
    val followingLiveRoomIds: List<String> = emptyList(),
    val discoverLiveRoomIds: List<String> = emptyList(),
) {
    val hasLiveRooms get() = followingLiveRoomIds.isNotEmpty() || discoverLiveRoomIds.isNotEmpty()
}

class CurrentLiveRoomsLocalDataSource @AutoKoin(Scope.SINGLE) constructor() :
    InMemorySingleLocalDataSource<CurrentLiveRoomsData>()