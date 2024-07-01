package com.theathletic.chat.remote

import com.theathletic.ChatEventsSubscription
import com.theathletic.datetime.Datetime
import com.theathletic.entity.chat.ChatRoomEntity
import com.theathletic.fragment.ChatMessageFragment
import com.theathletic.rooms.remote.showAsLiveRoomVerified

sealed class ChatEvent {
    data class NewMessage(val message: ChatRoomEntity.Message) : ChatEvent()
    data class DeleteMessage(val messageId: String) : ChatEvent()
}

fun ChatEventsSubscription.ChatEvents.toLocalEvent(): ChatEvent? {
    asChatMessage?.let {
        return ChatEvent.NewMessage(it.fragments.chatMessageFragment.toEntity())
    }

    asDeletedMessageEvent?.let {
        return ChatEvent.DeleteMessage(it.message_id)
    }

    return null
}

internal fun ChatMessageFragment.toEntity(): ChatRoomEntity.Message {
    return ChatRoomEntity.Message(
        id = message_id,
        authorId = created_by.id,
        authorFirstname = created_by.first_name,
        authorLastname = created_by.last_name,
        authorAvatarUrl = created_by.asStaff?.avatar_uri,
        authorIsStaff = created_by.asStaff?.role?.showAsLiveRoomVerified ?: false,
        authorIsShadowbanned = created_by.asCustomer?.is_shadow_ban == true,
        content = message,
        createdAt = Datetime(created_at),
    )
}