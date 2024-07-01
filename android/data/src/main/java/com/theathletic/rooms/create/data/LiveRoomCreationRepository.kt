package com.theathletic.rooms.create.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.data.EmptyParams
import com.theathletic.repository.CoroutineRepository
import com.theathletic.rooms.create.data.local.LiveRoomCreationInput
import com.theathletic.rooms.create.data.local.LiveRoomHostOption
import com.theathletic.rooms.create.data.local.LiveRoomHostOptionsLocalDataSource
import com.theathletic.rooms.create.data.local.LiveRoomTagOption
import com.theathletic.rooms.create.data.local.LiveRoomTagOptionsLocalDataSource
import com.theathletic.rooms.create.data.remote.CreateLiveRoomFetcher
import com.theathletic.rooms.create.data.remote.LiveRoomHostOptionsFetcher
import com.theathletic.rooms.create.data.remote.LiveRoomTagOptionsFetcher
import com.theathletic.rooms.create.data.remote.UpdateLiveRoomFetcher
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class LiveRoomCreationRepository @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider,
    private val tagsDataSource: LiveRoomTagOptionsLocalDataSource,
    private val tagsFetcher: LiveRoomTagOptionsFetcher,
    private val hostsDataSource: LiveRoomHostOptionsLocalDataSource,
    private val hostsFetcher: LiveRoomHostOptionsFetcher,
    private val createLiveRoomFetcher: CreateLiveRoomFetcher,
    private val updateLiveRoomFetcher: UpdateLiveRoomFetcher,
) : CoroutineRepository {

    override val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    fun getTagsOptions(fetch: Boolean = true): Flow<List<LiveRoomTagOption>> {
        if (fetch) {
            repositoryScope.launch {
                tagsFetcher.fetchRemote(EmptyParams)
            }
        }

        return tagsDataSource.item.map { it ?: emptyList() }
    }

    fun getHostsOptions(fetch: Boolean = true): Flow<List<LiveRoomHostOption>> {
        if (fetch) {
            repositoryScope.launch {
                hostsFetcher.fetchRemote(EmptyParams)
            }
        }

        return hostsDataSource.item.map { it ?: emptyList() }
    }

    suspend fun createLiveRoom(
        userId: String,
        input: LiveRoomCreationInput
    ) = createLiveRoomFetcher.fetchRemote(
        CreateLiveRoomFetcher.Params(currentUserId = userId, input = input)
    )

    suspend fun updateLiveRoom(
        roomId: String,
        input: LiveRoomCreationInput,
    ) = updateLiveRoomFetcher.fetchRemote(
        UpdateLiveRoomFetcher.Params(roomId = roomId, input = input)
    )
}