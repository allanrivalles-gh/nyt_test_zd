package com.theathletic.chat.remote

import com.theathletic.ChatEventsSubscription
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.chat.data.remote.ChatApi
import com.theathletic.data.RemoteToLocalSubscriber
import com.theathletic.entity.chat.ChatRoomEntity
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.utility.coroutines.DispatcherProvider

class ChatEventsSubscriber @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val entityDataSource: EntityDataSource,
    private val chatApi: ChatApi,
) : RemoteToLocalSubscriber<
    ChatEventsSubscriber.Params,
    ChatEventsSubscription.Data,
    ChatEvent?,
    >(dispatcherProvider) {

    data class Params(
        val chatRoomId: String,
    )

    override suspend fun makeRemoteRequest(
        params: Params
    ) = chatApi.subscribeToChatEvents(chatRoomId = params.chatRoomId)

    override fun mapToLocalModel(
        params: Params,
        remoteModel: ChatEventsSubscription.Data
    ) = remoteModel.chatEvents.toLocalEvent()

    override suspend fun saveLocally(params: Params, event: ChatEvent?) {
        event ?: return

        val chatRoom = entityDataSource.get(params.chatRoomId)
            ?: ChatRoomEntity(params.chatRoomId)

        val updatedChatRoom = when (event) {
            is ChatEvent.NewMessage -> chatRoom.copy(
                messages = (chatRoom.messages + event.message).distinctBy { it.id }
            )
            is ChatEvent.DeleteMessage -> chatRoom.copy(
                messages = chatRoom.messages.filterNot { it.id == event.messageId }
            )
        }

        entityDataSource.insertOrUpdate(updatedChatRoom)
    }
}