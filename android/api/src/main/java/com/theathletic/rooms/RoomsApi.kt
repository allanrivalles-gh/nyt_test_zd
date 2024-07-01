package com.theathletic.rooms

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.theathletic.CreateLiveRoomMutation
import com.theathletic.CreateSpeakingRequestMutation
import com.theathletic.DeleteSpeakingRequestMutation
import com.theathletic.EndLiveRoomMutation
import com.theathletic.GenerateLiveRoomTokenMutation
import com.theathletic.GetLiveRoomQuery
import com.theathletic.GetLiveRoomsQuery
import com.theathletic.LiveRoomDetailsSubscription
import com.theathletic.LiveRoomHostsQuery
import com.theathletic.LiveRoomTagsQuery
import com.theathletic.LockLiveRoomUserMutation
import com.theathletic.StartLiveRoomMutation
import com.theathletic.UnlockLiveRoomUserMutation
import com.theathletic.UpdateLiveRoomMutation
import com.theathletic.UpdateSpeakingRequestMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.network.apollo.notPersistedSubscription
import com.theathletic.type.CreateLiveRoomInput
import com.theathletic.type.LiveRoomTokenInput
import com.theathletic.type.LiveRoomUserRole
import com.theathletic.type.NodeFilterInput
import com.theathletic.type.SpeakingRequestType
import com.theathletic.type.StringFilterInput
import com.theathletic.type.UpdateLiveRoomInput
import com.theathletic.utility.coroutines.retryWithBackoff
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class RoomsApi @AutoKoin(Scope.SINGLE) constructor(
    val apolloClient: ApolloClient
) {

    suspend fun getToken(
        roomId: String,
        userId: Long,
    ): ApolloResponse<GenerateLiveRoomTokenMutation.Data> {
        return apolloClient.mutation(
            GenerateLiveRoomTokenMutation(
                LiveRoomTokenInput(
                    user_id = userId.toInt(),
                    live_room_id = roomId,
                    user_role = LiveRoomUserRole.listener
                )
            )
        ).execute()
    }

    suspend fun getRoom(roomId: String): ApolloResponse<GetLiveRoomQuery.Data> {
        return apolloClient.query(
            GetLiveRoomQuery(id = roomId)
        ).execute()
    }

    suspend fun getScheduledRooms(): ApolloResponse<GetLiveRoomsQuery.Data> {
        return apolloClient.query(
            GetLiveRoomsQuery(
                filter = SCHEDULED_ROOMS_FILTER,
                page = Optional.present(0),
                perPage = Optional.present(25),
            )
        ).execute()
    }

    fun getRoomSubscription(roomId: String): Flow<LiveRoomDetailsSubscription.Data> {
        return apolloClient.notPersistedSubscription(LiveRoomDetailsSubscription(id = roomId))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }

    suspend fun requestToSpeak(
        roomId: String,
        userId: String,
    ): ApolloResponse<CreateSpeakingRequestMutation.Data> {
        return apolloClient.mutation(
            CreateSpeakingRequestMutation(
                liveRoomId = roomId,
                userId = userId,
                requestType = SpeakingRequestType.promotion
            )
        ).execute()
    }

    suspend fun cancelRequest(
        roomId: String,
        userId: String,
    ): ApolloResponse<DeleteSpeakingRequestMutation.Data> {
        return apolloClient.mutation(
            DeleteSpeakingRequestMutation(
                liveRoomId = roomId,
                userId = userId,
                requestType = SpeakingRequestType.promotion
            )
        ).execute()
    }

    suspend fun updateRequest(
        approved: Boolean,
        roomId: String,
        userId: String,
    ): ApolloResponse<UpdateSpeakingRequestMutation.Data> {
        return apolloClient.mutation(
            UpdateSpeakingRequestMutation(
                approved = approved,
                liveRoomId = roomId,
                userId = userId,
                requestType = SpeakingRequestType.promotion,
            )
        ).execute()
    }

    suspend fun createDemotionRequest(
        roomId: String,
        userId: String,
    ): ApolloResponse<CreateSpeakingRequestMutation.Data> {
        return apolloClient.mutation(
            CreateSpeakingRequestMutation(
                liveRoomId = roomId,
                userId = userId,
                requestType = SpeakingRequestType.demotion
            )
        ).execute()
    }

    suspend fun deleteDemotionRequest(
        roomId: String,
        userId: String,
    ): ApolloResponse<DeleteSpeakingRequestMutation.Data> {
        return apolloClient.mutation(
            DeleteSpeakingRequestMutation(
                liveRoomId = roomId,
                userId = userId,
                requestType = SpeakingRequestType.demotion
            )
        ).execute()
    }

    suspend fun createMuteRequest(
        roomId: String,
        userId: String,
    ): ApolloResponse<CreateSpeakingRequestMutation.Data> {
        return apolloClient.mutation(
            CreateSpeakingRequestMutation(
                liveRoomId = roomId,
                userId = userId,
                requestType = SpeakingRequestType.mute
            )
        ).execute()
    }

    suspend fun deleteMuteRequest(
        roomId: String,
        userId: String,
    ): ApolloResponse<DeleteSpeakingRequestMutation.Data> {
        return apolloClient.mutation(
            DeleteSpeakingRequestMutation(
                liveRoomId = roomId,
                userId = userId,
                requestType = SpeakingRequestType.mute
            )
        ).execute()
    }

    suspend fun endRoom(roomId: String): ApolloResponse<EndLiveRoomMutation.Data> {
        return apolloClient.mutation(
            EndLiveRoomMutation(id = roomId)
        ).execute()
    }

    suspend fun startRoom(roomId: String): ApolloResponse<StartLiveRoomMutation.Data> {
        return apolloClient.mutation(
            StartLiveRoomMutation(id = roomId)
        ).execute()
    }

    suspend fun createLiveRoom(
        input: CreateLiveRoomInput
    ): ApolloResponse<CreateLiveRoomMutation.Data> {
        return apolloClient.mutation(
            CreateLiveRoomMutation(input)
        ).execute()
    }

    suspend fun updateLiveRoom(
        input: UpdateLiveRoomInput
    ): ApolloResponse<UpdateLiveRoomMutation.Data> {
        return apolloClient.mutation(
            UpdateLiveRoomMutation(input)
        ).execute()
    }

    suspend fun getAllTagOptions(): ApolloResponse<LiveRoomTagsQuery.Data> {
        return apolloClient.query(LiveRoomTagsQuery()).execute()
    }

    suspend fun getAllHostOptions(): ApolloResponse<LiveRoomHostsQuery.Data> {
        return apolloClient.query(LiveRoomHostsQuery()).execute()
    }

    suspend fun lockUser(roomId: String, userId: String): ApolloResponse<LockLiveRoomUserMutation.Data> {
        return apolloClient.mutation(
            LockLiveRoomUserMutation(live_room_id = roomId, user_id = userId)
        ).execute()
    }

    suspend fun unlockUser(roomId: String, userId: String): ApolloResponse<UnlockLiveRoomUserMutation.Data> {
        return apolloClient.mutation(
            UnlockLiveRoomUserMutation(live_room_id = roomId, user_id = userId)
        ).execute()
    }

    companion object {
        /**
         * Node Filter to grab Live Rooms in the "created state". Query structure is:
         *
         * and(
         *      type = "liveRoom"
         *      version = "primary"
         *      status = "created
         * )
         */
        private val SCHEDULED_ROOMS_FILTER get() = Optional.present(
            NodeFilterInput(
                and = Optional.present(
                    listOf(
                        NodeFilterInput(
                            type = Optional.presentIfNotNull(
                                StringFilterInput(eq = Optional.presentIfNotNull("liveRoom"))
                            ),
                        ),
                        NodeFilterInput(
                            version = Optional.presentIfNotNull(
                                StringFilterInput(eq = Optional.presentIfNotNull("primary"))
                            ),
                        ),
                        NodeFilterInput(
                            status = Optional.presentIfNotNull(
                                StringFilterInput(eq = Optional.presentIfNotNull("created"))
                            ),
                        ),
                    )
                )
            )
        )
    }
}