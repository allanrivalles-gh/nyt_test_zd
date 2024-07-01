package com.theathletic.chat.remote

import com.theathletic.DeleteChatMessageMutation
import com.theathletic.ReportChatMessageMutation
import com.theathletic.SendChatMessageMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.chat.data.local.ChatMessageReportReason
import com.theathletic.chat.data.remote.ChatApi
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.type.ReportedReason
import com.theathletic.utility.coroutines.DispatcherProvider

class ChatMessageSender @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val chatApi: ChatApi,
) : RemoteToLocalFetcher<
    ChatMessageSender.Params,
    SendChatMessageMutation.Data,
    Unit,
    >(dispatcherProvider) {

    data class Params(
        val chatRoomId: String,
        val message: String,
    )

    override suspend fun makeRemoteRequest(
        params: Params
    ) = chatApi.sendMessage(
        chatRoomId = params.chatRoomId,
        message = params.message,
    ).data

    override fun mapToLocalModel(
        params: Params,
        remoteModel: SendChatMessageMutation.Data
    ) = Unit

    override suspend fun saveLocally(params: Params, dbModel: Unit) {
        // Message should be received and saved through the ChatEventsSubscriber new message event
    }
}

class ChatMessageDeleter @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val chatApi: ChatApi,
) : RemoteToLocalFetcher<
    ChatMessageDeleter.Params,
    DeleteChatMessageMutation.Data,
    Unit
    >(dispatcherProvider) {

    data class Params(
        val chatRoomId: String,
        val messageId: String,
    )

    override suspend fun makeRemoteRequest(
        params: Params
    ) = chatApi.deleteMessage(
        chatRoomId = params.chatRoomId,
        messageId = params.messageId,
    ).data

    override fun mapToLocalModel(
        params: Params,
        remoteModel: DeleteChatMessageMutation.Data
    ) = Unit

    override suspend fun saveLocally(params: Params, dbModel: Unit) {
        // Message should be received and saved through the ChatEventsSubscriber new message event
    }
}

class ChatMessageReporter @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val chatApi: ChatApi,
) : RemoteToLocalFetcher<
    ChatMessageReporter.Params,
    ReportChatMessageMutation.Data,
    Unit
    >(dispatcherProvider) {

    data class Params(
        val chatRoomId: String,
        val messageId: String,
        val reason: ChatMessageReportReason,
    )

    override suspend fun makeRemoteRequest(
        params: Params
    ) = chatApi.reportMessage(
        chatRoomId = params.chatRoomId,
        messageId = params.messageId,
        reason = when (params.reason) {
            ChatMessageReportReason.ABUSE -> ReportedReason.abusive
            ChatMessageReportReason.TROLLING -> ReportedReason.trolling
            ChatMessageReportReason.SPAM -> ReportedReason.spam
        }
    ).data

    override fun mapToLocalModel(
        params: Params,
        remoteModel: ReportChatMessageMutation.Data
    ) = Unit

    override suspend fun saveLocally(params: Params, dbModel: Unit) {
        // Message should be received and saved through the ChatEventsSubscriber new message event
    }
}