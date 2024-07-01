package com.theathletic.entity.chat

import com.squareup.moshi.JsonClass
import com.theathletic.datetime.Datetime
import com.theathletic.entity.local.AthleticEntity

@JsonClass(generateAdapter = true)
data class ChatRoomEntity(
    override val id: String = "",
    val messages: List<Message> = emptyList(),
) : AthleticEntity {
    override val type = AthleticEntity.Type.CHAT_ROOM

    @JsonClass(generateAdapter = true)
    data class Message(
        val id: String = "",
        val authorId: String = "",
        val authorFirstname: String = "",
        val authorLastname: String = "",
        val authorAvatarUrl: String? = null,
        val authorIsStaff: Boolean = false,
        val authorIsShadowbanned: Boolean = false,
        val content: String = "",
        val createdAt: Datetime = Datetime(0),
    )
}