package com.theathletic.rooms.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.local.InMemoryLocalDataSource
import com.theathletic.data.local.InMemorySingleLocalDataSource
import com.theathletic.data.local.InMemoryStaticLocalDataSource
import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.followable.Followable

data class LiveAudioRoomUserDetails(
    val id: String,
    val firstname: String,
    val lastname: String,
    val name: String,
    val staffInfo: StaffInfo?,
) {
    data class StaffInfo(
        val bio: String,
        val imageUrl: String?,
        val twitterHandle: String,
        val description: String,
        val verified: Boolean,
    )

    data class FollowableItem(
        val followableId: Followable.Id,
        val name: String,
        val imageUrl: String,
    )
}

class LiveAudioRoomUserDetailsDataSource @AutoKoin(Scope.SINGLE) constructor() : InMemoryLocalDataSource<String, List<LiveAudioRoomUserDetails>>() {

    override fun update(key: String, data: List<LiveAudioRoomUserDetails>) {
        val current = getStateFlow(key).value ?: emptyList()
        val newList = (data + current).distinctBy { it.id }
        getStateFlow(key).value = newList
    }
}

class LiveAudioRoomUserFollowingDataSource @AutoKoin(Scope.SINGLE) constructor() : InMemoryStaticLocalDataSource<String, List<LiveAudioRoomUserDetails.FollowableItem>>()

class ScheduledLiveRoomsDataSource @AutoKoin(Scope.SINGLE) constructor() : InMemorySingleLocalDataSource<List<LiveAudioRoomEntity>>()