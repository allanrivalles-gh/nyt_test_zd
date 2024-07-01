package com.theathletic.followables.data.remote

import com.theathletic.FollowableItemsQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.EmptyParams
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.followables.data.toLocalFollowedModels
import com.theathletic.repository.user.FollowableDao
import com.theathletic.topics.data.remote.FollowableItemsApi
import com.theathletic.topics.local.FollowableItems
import com.theathletic.topics.local.FollowableItemsDataSource
import com.theathletic.topics.local.FollowableLeague
import com.theathletic.topics.local.FollowableTeam
import com.theathletic.utility.coroutines.DispatcherProvider

class FollowableItemsFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val followableItemsApi: FollowableItemsApi,
    private val followableItemsDataSource: FollowableItemsDataSource
) : RemoteToLocalFetcher<
    EmptyParams,
    FollowableItemsQuery.Data,
    FollowableItems
    >(dispatcherProvider) {

    override suspend fun makeRemoteRequest(params: EmptyParams): FollowableItemsQuery.Data? {
        return followableItemsApi.getFollowableItems()?.data
    }

    override fun mapToLocalModel(
        params: EmptyParams,
        remoteModel: FollowableItemsQuery.Data
    ): FollowableItems {
        return FollowableItems(
            teams = remoteModel.followableItems.teams.map {
                FollowableTeam(it.id.toLong(), it.name.orEmpty(), it.url.orEmpty())
            },
            leagues = remoteModel.followableItems.leagues.map {
                FollowableLeague(it.id.toLong(), it.shortname.orEmpty(), it.url.orEmpty())
            }
        )
    }

    override suspend fun saveLocally(params: EmptyParams, dbModel: FollowableItems) {
        followableItemsDataSource.put(dbModel)
    }
}

class FollowableItemsFetcherV2 @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val followableItemsApi: FollowableItemsApi,
    private val followableDao: FollowableDao
) : RemoteToLocalFetcher<
    EmptyParams,
    FollowableItemsQuery.Data,
    FollowableItemsFetcherV2.LocalModels
    >(dispatcherProvider) {

    data class LocalModels(
        val followableItems: com.theathletic.repository.user.FollowableItems
    )

    override suspend fun makeRemoteRequest(params: EmptyParams): FollowableItemsQuery.Data? {
        return followableItemsApi.getFollowableItems()?.data
    }

    override fun mapToLocalModel(
        params: EmptyParams,
        remoteModel: FollowableItemsQuery.Data
    ) = LocalModels(remoteModel.followableItems.toLocalFollowedModels())

    override suspend fun saveLocally(params: EmptyParams, dbModel: LocalModels) {
        followableDao.insertTeams(dbModel.followableItems.teams)
        followableDao.insertLeagues(dbModel.followableItems.leagues)
        followableDao.insertAuthors(dbModel.followableItems.authors)
    }
}