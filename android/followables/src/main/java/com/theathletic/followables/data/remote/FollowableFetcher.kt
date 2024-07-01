package com.theathletic.followables.data.remote

import com.theathletic.FollowTopicMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalFetcherWithResponse
import com.theathletic.followables.data.toLocalFollowedModels
import com.theathletic.navigation.data.local.NavigationDao
import com.theathletic.navigation.data.local.NavigationSource
import com.theathletic.navigation.data.local.RoomNavigationEntity
import com.theathletic.repository.user.UserFollowingDao
import com.theathletic.repository.user.UserFollowingItem
import com.theathletic.topics.data.remote.FollowableItemsApi
import com.theathletic.topics.data.remote.toLocalModel
import com.theathletic.type.UserFollow
import com.theathletic.type.UserFollowType
import com.theathletic.utility.coroutines.DispatcherProvider

class FollowableFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val followableItemsApi: FollowableItemsApi,
    private val navigationDao: NavigationDao,
    private val userFollowingDao: UserFollowingDao
) : RemoteToLocalFetcherWithResponse<
    FollowableFetcher.Params,
    FollowTopicMutation.Data,
    FollowableFetcher.LocalModels
    >(dispatcherProvider) {

    data class Params(
        val id: String,
        val type: UserFollowType
    )

    data class LocalModels(
        val navEntities: List<RoomNavigationEntity>,
        val userFollowingItems: List<UserFollowingItem>,
    )

    override suspend fun makeRemoteRequest(params: Params): FollowTopicMutation.Data? {
        return followableItemsApi.followItem(
            UserFollow(
                id = params.id,
                type = params.type
            )
        ).data
    }

    override fun mapToLocalModel(
        params: Params,
        remoteModel: FollowTopicMutation.Data
    ): LocalModels {
        return LocalModels(
            navEntities = remoteModel.addUserFollow.appNav.mapIndexedNotNull { index, item ->
                item?.fragments?.tabNavigationItem?.toLocalModel(index)
            },
            userFollowingItems = remoteModel.addUserFollow.fragments.followResponseFragment.following.toLocalFollowedModels()
        )
    }

    override suspend fun saveLocally(params: Params, dbModel: LocalModels) {
        navigationDao.replaceNavigationEntities(NavigationSource.FEED, dbModel.navEntities)
        userFollowingDao.updateUserFollowingItems(dbModel.userFollowingItems)
    }
}