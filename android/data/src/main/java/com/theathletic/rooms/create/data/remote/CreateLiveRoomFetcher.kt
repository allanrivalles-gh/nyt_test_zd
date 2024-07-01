package com.theathletic.rooms.create.data.remote

import com.apollographql.apollo3.api.Optional
import com.theathletic.CreateLiveRoomMutation
import com.theathletic.UpdateLiveRoomMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.data.RemoteToLocalFetcherWithResponse
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.entity.room.LiveRoomCategory
import com.theathletic.rooms.RoomsApi
import com.theathletic.rooms.create.data.local.LiveRoomCreationInput
import com.theathletic.rooms.create.ui.LiveRoomTagType
import com.theathletic.rooms.remote.toEntity
import com.theathletic.type.CreateLiveRoomInput
import com.theathletic.type.LiveRoomType
import com.theathletic.type.TagInput
import com.theathletic.type.UpdateLiveRoomInput
import com.theathletic.utility.coroutines.DispatcherProvider

class CreateLiveRoomFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val roomsApi: RoomsApi,
    private val entityDataSource: EntityDataSource,
) : RemoteToLocalFetcherWithResponse<
    CreateLiveRoomFetcher.Params,
    CreateLiveRoomMutation.Data,
    LiveAudioRoomEntity
    >(dispatcherProvider) {

    data class Params(
        val currentUserId: String,
        val input: LiveRoomCreationInput,
    )

    override suspend fun makeRemoteRequest(
        params: Params
    ): CreateLiveRoomMutation.Data? {
        val hostIds = params.input.hosts.map { it.id.toInt() }.toMutableList()

        if (params.input.currentUserIsHost) {
            hostIds.add(params.currentUserId.toInt())
        }

        return roomsApi.createLiveRoom(
            CreateLiveRoomInput(
                title = params.input.title.trim(),
                description = Optional.present(params.input.description.trim()),
                host_ids = hostIds,
                is_recorded = Optional.present(params.input.recorded),
                tags = Optional.present(params.input.tagsForRemote),
                live_room_types = Optional.present(params.input.categories.map { it.graphqlType }),
                auto_push_enabled = Optional.present(params.input.sendAutoPush),
                disable_chat = Optional.present(params.input.disableChat),
            )
        ).data
    }

    override fun mapToLocalModel(
        params: Params,
        remoteModel: CreateLiveRoomMutation.Data
    ) = remoteModel.createLiveRoom.fragments.liveRoomFragment.toEntity()

    override suspend fun saveLocally(params: Params, dbModel: LiveAudioRoomEntity) {
        entityDataSource.insertOrUpdate(dbModel)
    }
}

class UpdateLiveRoomFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val roomsApi: RoomsApi,
    private val entityDataSource: EntityDataSource,
) : RemoteToLocalFetcher<
    UpdateLiveRoomFetcher.Params,
    UpdateLiveRoomMutation.Data,
    LiveAudioRoomEntity
    >(dispatcherProvider) {

    data class Params(
        val roomId: String,
        val input: LiveRoomCreationInput,
    )

    override suspend fun makeRemoteRequest(
        params: Params
    ) = roomsApi.updateLiveRoom(
        UpdateLiveRoomInput(
            id = params.roomId,
            title = Optional.present(params.input.title.trim()),
            description = Optional.present(params.input.description.trim()),
            host_ids = Optional.present(params.input.hosts.map { it.id.toInt() }),
            is_recorded = Optional.present(params.input.recorded),
            tags = Optional.present(params.input.tagsForRemote),
            live_room_types = Optional.present(params.input.categories.map { it.graphqlType }),
            auto_push_enabled = Optional.present(params.input.sendAutoPush),
            disable_chat = Optional.present(params.input.disableChat),
        )
    ).data

    override fun mapToLocalModel(
        params: Params,
        remoteModel: UpdateLiveRoomMutation.Data
    ) = remoteModel.updateLiveRoom.fragments.liveRoomFragment.toEntity()

    override suspend fun saveLocally(params: Params, dbModel: LiveAudioRoomEntity) {
        entityDataSource.insertOrUpdate(dbModel)
    }
}

private val LiveRoomCreationInput.tagsForRemote get() = tags.map { tag ->
    TagInput(
        id = tag.id,
        type = Optional.present(tag.type.graphqlType),
        title = Optional.present(tag.title),
        name = Optional.present(tag.name),
        shortname = Optional.present(tag.shortname),
    )
}

private val LiveRoomTagType.graphqlType: String get() = when (this) {
    LiveRoomTagType.TEAM -> "team"
    LiveRoomTagType.LEAGUE -> "league"
    else -> ""
}

private val LiveRoomCategory.graphqlType get(): LiveRoomType = when (this) {
    LiveRoomCategory.QUESTION_AND_ANSWER -> LiveRoomType.question_and_answer
    LiveRoomCategory.BREAKING_NEWS -> LiveRoomType.breaking_news
    LiveRoomCategory.GAME_PREVIEW_1_TEAM -> LiveRoomType.game_preview_1_team
    LiveRoomCategory.GAME_PREVIEW_2_TEAM -> LiveRoomType.game_preview_2_team
    LiveRoomCategory.GAME_RECAP -> LiveRoomType.game_recap
    LiveRoomCategory.RECURRING -> LiveRoomType.recurring
    LiveRoomCategory.LIVE_PODCAST -> LiveRoomType.live_podcast
}