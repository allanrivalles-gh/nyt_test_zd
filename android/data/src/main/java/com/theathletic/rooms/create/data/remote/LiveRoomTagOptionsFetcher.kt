package com.theathletic.rooms.create.data.remote

import com.theathletic.LiveRoomHostsQuery
import com.theathletic.LiveRoomTagsQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.EmptyParams
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.rooms.RoomsApi
import com.theathletic.rooms.create.data.local.LiveRoomHostOption
import com.theathletic.rooms.create.data.local.LiveRoomHostOptionsLocalDataSource
import com.theathletic.rooms.create.data.local.LiveRoomTagOption
import com.theathletic.rooms.create.data.local.LiveRoomTagOptionsLocalDataSource
import com.theathletic.rooms.create.ui.LiveRoomTagType
import com.theathletic.utility.coroutines.DispatcherProvider

class LiveRoomTagOptionsFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val roomsApi: RoomsApi,
    private val localDataSource: LiveRoomTagOptionsLocalDataSource,
) : RemoteToLocalFetcher<
    EmptyParams,
    LiveRoomTagsQuery.Data,
    List<LiveRoomTagOption>
    >(dispatcherProvider) {

    override suspend fun makeRemoteRequest(params: EmptyParams) = roomsApi.getAllTagOptions().data

    override fun mapToLocalModel(
        params: EmptyParams,
        remoteModel: LiveRoomTagsQuery.Data
    ) = remoteModel.getTagsByType.mapNotNull { remoteTag ->
        remoteTag.asLeagueTag?.let { leagueTag ->
            return@mapNotNull LiveRoomTagOption(
                id = leagueTag.id,
                type = LiveRoomTagType.LEAGUE,
                title = leagueTag.title,
                name = leagueTag.name.orEmpty(),
                shortname = leagueTag.shortname,
            )
        }

        remoteTag.asTeamTag?.let { teamTag ->
            return@mapNotNull LiveRoomTagOption(
                id = teamTag.id,
                type = LiveRoomTagType.TEAM,
                title = teamTag.title,
                name = teamTag.name.orEmpty(),
                shortname = teamTag.shortname,
            )
        }
    }

    override suspend fun saveLocally(params: EmptyParams, dbModel: List<LiveRoomTagOption>) {
        localDataSource.update(dbModel)
    }
}

class LiveRoomHostOptionsFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val roomsApi: RoomsApi,
    private val localDataSource: LiveRoomHostOptionsLocalDataSource,
) : RemoteToLocalFetcher<
    EmptyParams,
    LiveRoomHostsQuery.Data,
    List<LiveRoomHostOption>
    >(dispatcherProvider) {

    override suspend fun makeRemoteRequest(params: EmptyParams) = roomsApi.getAllHostOptions().data

    override fun mapToLocalModel(
        params: EmptyParams,
        remoteModel: LiveRoomHostsQuery.Data
    ) = remoteModel.liveRoomHosts.hosts.map { host ->
        LiveRoomHostOption(
            id = host.id,
            name = host.name.orEmpty(),
            avatarUrl = host.image_url.orEmpty()
        )
    }

    override suspend fun saveLocally(params: EmptyParams, dbModel: List<LiveRoomHostOption>) {
        localDataSource.update(dbModel)
    }
}