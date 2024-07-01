package com.theathletic.entity.room

import com.squareup.moshi.JsonClass
import com.theathletic.datetime.Datetime
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.rooms.create.ui.LiveRoomTagType

@JsonClass(generateAdapter = true)
data class LiveAudioRoomEntity(
    override val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val description: String = "",
    val audienceSize: Int = 0,
    val maxCapacity: Int = Int.MAX_VALUE,
    val permalink: String = "",
    val createdAt: Datetime? = null,
    val startedAt: Datetime? = null,
    val endedAt: Datetime? = null,
    val chatRoomId: String? = null,
    val tags: List<Tag> = emptyList(),
    val categories: List<LiveRoomCategory> = emptyList(),
    val topicImages: List<String> = emptyList(),
    val hosts: List<Host> = emptyList(),
    val moderatorIds: List<String> = emptyList(),
    val lockedUserIds: List<String> = emptyList(),
    val promotionRequests: List<SpeakingRequest> = emptyList(),
    val demotionRequests: List<SpeakingRequest> = emptyList(),
    val muteRequests: List<SpeakingRequest> = emptyList(),
    val usersInRoom: List<String> = emptyList(),
    val isRecording: Boolean = false,
    val chatDisabled: Boolean = true,
    val autoPushEnabled: Boolean = false,
    val autoPushSent: Boolean = false,
) : AthleticEntity {
    override val type = AthleticEntity.Type.LIVE_AUDIO_ROOM

    @JsonClass(generateAdapter = true)
    data class Host(
        val id: String = "",
        val name: String = "",
        val imageUrl: String = "",
        val tagImageUrl: String = "",
    )

    @JsonClass(generateAdapter = true)
    data class Tag(
        val id: String = "",
        val title: String = "",
        val deeplink: String = "",
        val name: String = "",
        val shortname: String = "",
        val color: String? = null,
        val type: LiveRoomTagType = LiveRoomTagType.NONE,
    )

    fun isUserHost(userId: String) = hosts.any { it.id == userId }

    fun isUserModerator(userId: String) = moderatorIds.any { it == userId }

    fun isUserLocked(userId: String) = lockedUserIds.any { it == userId }

    val isLive: Boolean get() = startedAt != null && endedAt == null
}

@JsonClass(generateAdapter = true)
data class SpeakingRequest(
    val userId: String = "",
    val approved: Boolean = false,
    val createdAt: Datetime = Datetime(0),
)