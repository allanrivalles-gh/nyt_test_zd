package com.theathletic.chat.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.chat.data.local.ChatMessageReportReason
import com.theathletic.chat.remote.ChatEventsSubscriber
import com.theathletic.chat.remote.ChatMessageDeleter
import com.theathletic.chat.remote.ChatMessageReporter
import com.theathletic.chat.remote.ChatMessageSender
import com.theathletic.entity.chat.ChatRoomEntity
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.repository.CoroutineRepository
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ChatRepository @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val entityDataSource: EntityDataSource,
    private val chatEventsSubscriber: ChatEventsSubscriber,
    private val chatMessageSender: ChatMessageSender,
    private val chatMessageDeleter: ChatMessageDeleter,
    private val chatMessageReporter: ChatMessageReporter,
) : CoroutineRepository {

    override val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    fun sendMessage(
        chatRoomId: String,
        message: String,
    ) = repositoryScope.launch {
        chatMessageSender.fetchRemote(
            ChatMessageSender.Params(
                chatRoomId = chatRoomId,
                message = message,
            )
        )
    }

    fun deleteMessage(
        chatRoomId: String,
        messageId: String,
    ) = repositoryScope.launch {
        chatMessageDeleter.fetchRemote(
            ChatMessageDeleter.Params(
                chatRoomId = chatRoomId,
                messageId = messageId,
            )
        )
    }

    fun reportMessage(
        chatRoomId: String,
        messageId: String,
        reason: ChatMessageReportReason,
    ) = repositoryScope.launch {
        chatMessageReporter.fetchRemote(
            ChatMessageReporter.Params(
                chatRoomId = chatRoomId,
                messageId = messageId,
                reason = reason,
            )
        )
    }

    fun getChatRoomFlow(
        chatRoomId: String
    ) = entityDataSource.getFlow<ChatRoomEntity>(chatRoomId)

    suspend fun subscribeToChatEvents(chatRoomId: String) {
        chatEventsSubscriber.subscribe(
            ChatEventsSubscriber.Params(chatRoomId = chatRoomId)
        )
    }
}