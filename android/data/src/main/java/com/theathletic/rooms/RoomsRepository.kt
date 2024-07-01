package com.theathletic.rooms

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.data.EmptyParams
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.repository.CoroutineRepository
import com.theathletic.rooms.local.LiveAudioRoomUserDetails
import com.theathletic.rooms.local.LiveAudioRoomUserDetailsDataSource
import com.theathletic.rooms.local.LiveAudioRoomUserFollowingDataSource
import com.theathletic.rooms.local.RoomsTokenLocalStorage
import com.theathletic.rooms.local.ScheduledLiveRoomsDataSource
import com.theathletic.rooms.remote.CreateDemotionRequestFetcher
import com.theathletic.rooms.remote.CreateMuteRequestFetcher
import com.theathletic.rooms.remote.CreateSpeakingRequestFetcher
import com.theathletic.rooms.remote.DeleteDemotionRequestFetcher
import com.theathletic.rooms.remote.DeleteMuteRequestFetcher
import com.theathletic.rooms.remote.DeleteSpeakingRequestFetcher
import com.theathletic.rooms.remote.EndLiveRoomFetcher
import com.theathletic.rooms.remote.LiveAudioRoomDetailFetcher
import com.theathletic.rooms.remote.LiveRoomDetailSubscriber
import com.theathletic.rooms.remote.LiveRoomLockUserMutator
import com.theathletic.rooms.remote.LiveRoomUnlockUserMutator
import com.theathletic.rooms.remote.LiveRoomUserFollowingFetcher
import com.theathletic.rooms.remote.RoomsTokenFetcher
import com.theathletic.rooms.remote.ScheduledLiveRoomsFetcher
import com.theathletic.rooms.remote.StartLiveRoomFetcher
import com.theathletic.rooms.remote.UpdateSpeakingRequestFetcher
import com.theathletic.user.IUserManager
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoomsRepository @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider,
    private val tokenFetcher: RoomsTokenFetcher,
    private val userManager: IUserManager,
    private val tokenLocalStorage: RoomsTokenLocalStorage,
    private val entityDataSource: EntityDataSource,
    private val roomFetcher: LiveAudioRoomDetailFetcher,
    private val roomDetailSubscriber: LiveRoomDetailSubscriber,
    private val roomStartFetcher: StartLiveRoomFetcher,
    private val roomEndFetcher: EndLiveRoomFetcher,
    private val userDetailsDataSource: LiveAudioRoomUserDetailsDataSource,
    private val createSpeakingRequestFetcher: CreateSpeakingRequestFetcher,
    private val deleteSpeakingRequestFetcher: DeleteSpeakingRequestFetcher,
    private val updateSpeakingRequestFetcher: UpdateSpeakingRequestFetcher,
    private val createDemotionRequestFetcher: CreateDemotionRequestFetcher,
    private val deleteDemotionRequestFetcher: DeleteDemotionRequestFetcher,
    private val createMuteRequestFetcher: CreateMuteRequestFetcher,
    private val deleteMuteRequestFetcher: DeleteMuteRequestFetcher,
    private val scheduledLiveRoomsFetcher: ScheduledLiveRoomsFetcher,
    private val userFollowingFetcher: LiveRoomUserFollowingFetcher,
    private val lockUserMutator: LiveRoomLockUserMutator,
    private val unlockUserMutator: LiveRoomUnlockUserMutator,
    private val followingDataSource: LiveAudioRoomUserFollowingDataSource,
    private val scheduledLiveRoomsDataSource: ScheduledLiveRoomsDataSource,
) : CoroutineRepository {

    override val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    suspend fun fetchTokenForRoom(roomId: String): String? {
        val token = tokenLocalStorage.get(roomId)
        if (!token.isNullOrEmpty()) {
            return token
        }

        tokenFetcher.fetchRemote(
            RoomsTokenFetcher.Params(
                roomId = roomId,
                userId = userManager.getCurrentUserId()
            )
        )
        return tokenLocalStorage.get(roomId)
    }

    suspend fun getLiveAudioRoom(
        id: String,
        forceFetch: Boolean = false
    ): LiveAudioRoomEntity? = withContext(repositoryScope.coroutineContext) {
        val room = entityDataSource.get<LiveAudioRoomEntity>(id)

        if (room == null || forceFetch) {
            roomFetcher.fetchRemote(LiveAudioRoomDetailFetcher.Params(roomId = id))
        }

        entityDataSource.get(id)
    }

    fun getScheduledLiveRooms(fetch: Boolean = false): Flow<List<LiveAudioRoomEntity>> {
        if (fetch) {
            repositoryScope.launch {
                scheduledLiveRoomsFetcher.fetchRemote(EmptyParams)
            }
        }
        return scheduledLiveRoomsDataSource.item.map { it ?: emptyList() }
    }

    fun getLiveAudioRoomFlow(
        roomId: String
    ): Flow<LiveAudioRoomEntity?> {
        return entityDataSource.getFlow(roomId)
    }

    suspend fun subscribeLiveRoom(id: String) {
        roomDetailSubscriber.subscribe(LiveRoomDetailSubscriber.Params(id))
    }

    fun createSpeakingRequest(
        roomId: String
    ) = repositoryScope.launch {
        createSpeakingRequestFetcher.fetchRemote(CreateSpeakingRequestFetcher.Params(roomId))
    }

    fun deleteSpeakingRequest(
        userId: String,
        roomId: String
    ) = repositoryScope.launch {
        deleteSpeakingRequestFetcher.fetchRemote(
            DeleteSpeakingRequestFetcher.Params(
                userId = userId,
                roomId = roomId,
            )
        )
    }

    fun approveSpeakingRequest(
        userId: String,
        roomId: String
    ) = repositoryScope.launch {
        updateSpeakingRequestFetcher.fetchRemote(
            UpdateSpeakingRequestFetcher.Params(
                approved = true,
                roomId = roomId,
                userId = userId
            )
        )
    }

    fun createDemotionRequest(
        userId: String,
        roomId: String
    ) = repositoryScope.launch {
        createDemotionRequestFetcher.fetchRemote(
            CreateDemotionRequestFetcher.Params(
                roomId = roomId,
                userId = userId
            )
        )
    }

    fun deleteDemotionRequest(
        userId: String,
        roomId: String
    ) = repositoryScope.launch {
        deleteDemotionRequestFetcher.fetchRemote(
            DeleteDemotionRequestFetcher.Params(
                roomId = roomId,
                userId = userId
            )
        )
    }

    fun createMuteRequest(
        userId: String,
        roomId: String,
    ) = repositoryScope.launch {
        createMuteRequestFetcher.fetchRemote(
            CreateMuteRequestFetcher.Params(
                roomId = roomId,
                userId = userId
            )
        )
    }

    fun deleteMuteRequest(
        userId: String,
        roomId: String,
    ) = repositoryScope.launch {
        deleteMuteRequestFetcher.fetchRemote(
            DeleteMuteRequestFetcher.Params(
                roomId = roomId,
                userId = userId
            )
        )
    }

    fun startRoom(
        roomId: String,
    ) = repositoryScope.launch {
        roomStartFetcher.fetchRemote(StartLiveRoomFetcher.Params(roomId = roomId))
    }

    fun endRoom(
        roomId: String,
    ) = repositoryScope.launch {
        roomEndFetcher.fetchRemote(EndLiveRoomFetcher.Params(roomId = roomId))
    }

    fun lockUser(
        roomId: String,
        userId: String,
    ) = repositoryScope.launch {
        lockUserMutator.fetchRemote(
            LiveRoomLockUserMutator.Params(roomId = roomId, userId = userId)
        )
    }

    fun unlockUser(
        roomId: String,
        userId: String,
    ) = repositoryScope.launch {
        unlockUserMutator.fetchRemote(
            LiveRoomUnlockUserMutator.Params(roomId = roomId, userId = userId)
        )
    }

    fun getUserDetailsForRoom(roomId: String) = userDetailsDataSource.observeItem(roomId)

    suspend fun getUserFollowingDetails(
        roomId: String,
        userId: String,
    ): List<LiveAudioRoomUserDetails.FollowableItem>? {
        val followedItems = followingDataSource.get(userId)
        if (followedItems?.isNotEmpty() == true) {
            return followedItems
        }

        userFollowingFetcher.fetchRemote(LiveRoomUserFollowingFetcher.Params(roomId, userId))
        return followingDataSource.get(userId)
    }
}