package com.theathletic.topics.data.remote

import com.theathletic.FollowTopicMutation
import com.theathletic.UnfollowTopicMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.entity.settings.UserTopics
import com.theathletic.fragment.TabNavigationItem
import com.theathletic.navigation.data.local.NavigationDao
import com.theathletic.navigation.data.local.NavigationSource
import com.theathletic.navigation.data.local.RoomNavigationEntity
import com.theathletic.profile.manage.UserTopicId
import com.theathletic.repository.user.UserTopicsDao
import com.theathletic.topics.LegacyUserTopicsManager
import com.theathletic.utility.coroutines.DispatcherProvider

class FollowTopicFetcher @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider,
    private val settingsApi: SettingsGraphqlApi,
    private val navigationDao: NavigationDao,
    private val legacyUserTopicsManager: LegacyUserTopicsManager,
    private val userTopicsDao: UserTopicsDao,
) : RemoteToLocalFetcher<
    FollowTopicFetcher.Params,
    FollowTopicMutation.Data,
    FollowTopicFetcher.LocalModels
    >(dispatcherProvider) {

    data class Params(val id: UserTopicId)

    data class LocalModels(
        val navEntities: List<RoomNavigationEntity>,
        val userTopics: UserTopics,
    )

    override suspend fun makeRemoteRequest(params: Params) = settingsApi.followTopic(params.id).data

    override fun mapToLocalModel(
        params: Params,
        remoteModel: FollowTopicMutation.Data
    ): LocalModels {
        return LocalModels(
            navEntities = remoteModel.addUserFollow.appNav.mapIndexedNotNull { index, item ->
                item?.fragments?.tabNavigationItem?.toLocalModel(index)
            },
            userTopics = remoteModel.addUserFollow.fragments.followResponseFragment.toLocalFollowedModels()
        )
    }

    override suspend fun saveLocally(
        params: Params,
        dbModel: LocalModels
    ) {
        navigationDao.replaceNavigationEntities(NavigationSource.FEED, dbModel.navEntities)
        legacyUserTopicsManager.setFollowedTopics(dbModel.userTopics)
        userTopicsDao.updateFollowedTopics(dbModel.userTopics)
    }
}

class UnfollowTopicFetcher @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider,
    private val settingsApi: SettingsGraphqlApi,
    private val navigationDao: NavigationDao,
    private val legacyUserTopicsManager: LegacyUserTopicsManager,
    private val userTopicsDao: UserTopicsDao,
) : RemoteToLocalFetcher<
    UnfollowTopicFetcher.Params,
    UnfollowTopicMutation.Data,
    UnfollowTopicFetcher.LocalModels,
    >(dispatcherProvider) {

    data class Params(val id: UserTopicId)

    data class LocalModels(
        val navEntities: List<RoomNavigationEntity>,
        val userTopics: UserTopics,
    )

    override suspend fun makeRemoteRequest(params: Params) = settingsApi.unfollowTopic(params.id).data

    override fun mapToLocalModel(
        params: Params,
        remoteModel: UnfollowTopicMutation.Data
    ): LocalModels {
        return LocalModels(
            navEntities = remoteModel.removeUserFollow.appNav.mapIndexedNotNull { index, item ->
                item?.fragments?.tabNavigationItem?.toLocalModel(index)
            },
            userTopics = remoteModel.removeUserFollow.fragments.followResponseFragment.toLocalFollowedModels()
        )
    }

    override suspend fun saveLocally(
        params: Params,
        dbModel: LocalModels
    ) {
        navigationDao.replaceNavigationEntities(NavigationSource.FEED, dbModel.navEntities)
        legacyUserTopicsManager.setFollowedTopics(dbModel.userTopics)
        userTopicsDao.updateFollowedTopics(dbModel.userTopics)
    }
}

fun TabNavigationItem.toLocalModel(index: Int) = RoomNavigationEntity(
    sourceKey = NavigationSource.FEED.dbKey,
    title = title,
    deeplinkUrl = deeplink_url.orEmpty(),
    entityType = entity_type.orEmpty(),
    index = index
)