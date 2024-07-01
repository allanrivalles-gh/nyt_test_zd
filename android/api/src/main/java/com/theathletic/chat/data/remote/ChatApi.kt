package com.theathletic.chat.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.theathletic.ChatEventsSubscription
import com.theathletic.DeleteChatMessageMutation
import com.theathletic.ReportChatMessageMutation
import com.theathletic.SendChatMessageMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.network.apollo.notPersistedSubscription
import com.theathletic.type.ReportedReason
import com.theathletic.utility.coroutines.retryWithBackoff
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class ChatApi @AutoKoin(Scope.SINGLE) constructor(
    val apolloClient: ApolloClient,
) {
    suspend fun sendMessage(
        chatRoomId: String,
        message: String,
    ): ApolloResponse<SendChatMessageMutation.Data> {
        return apolloClient.mutation(SendChatMessageMutation(chatRoomId, message))
            .execute()
    }

    suspend fun deleteMessage(
        chatRoomId: String,
        messageId: String,
    ): ApolloResponse<DeleteChatMessageMutation.Data> {
        return apolloClient.mutation(DeleteChatMessageMutation(chatRoomId, messageId))
            .execute()
    }

    suspend fun reportMessage(
        chatRoomId: String,
        messageId: String,
        reason: ReportedReason,
    ): ApolloResponse<ReportChatMessageMutation.Data> {
        return apolloClient.mutation(ReportChatMessageMutation(chatRoomId, messageId, reason))
            .execute()
    }

    fun subscribeToChatEvents(
        chatRoomId: String
    ): Flow<ChatEventsSubscription.Data> {
        return apolloClient.notPersistedSubscription(ChatEventsSubscription(chatRoomId))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }
}