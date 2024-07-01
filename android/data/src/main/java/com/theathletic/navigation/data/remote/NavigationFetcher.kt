package com.theathletic.navigation.data.remote

import com.theathletic.TabNavigationQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.EmptyParams
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.navigation.data.local.NavigationDao
import com.theathletic.navigation.data.local.NavigationSource
import com.theathletic.navigation.data.local.RoomNavigationEntity
import com.theathletic.utility.coroutines.DispatcherProvider

class NavigationFetcher @AutoKoin(Scope.SINGLE) constructor(
    private val navigationApi: NavigationApi,
    private val navigationDao: NavigationDao,
    dispatcherProvider: DispatcherProvider
) : RemoteToLocalFetcher<
    EmptyParams,
    TabNavigationQuery.Data,
    List<RoomNavigationEntity>
    >(dispatcherProvider) {

    override suspend fun makeRemoteRequest(params: EmptyParams): TabNavigationQuery.Data? {
        return navigationApi.getNavigationEntities().data
    }

    override fun mapToLocalModel(
        params: EmptyParams,
        remoteModel: TabNavigationQuery.Data
    ): List<RoomNavigationEntity> {
        return remoteModel.appNav.mapIndexed { index, item ->
            with(item.fragments.tabNavigationItem) {
                RoomNavigationEntity(
                    NavigationSource.FEED.dbKey,
                    this.title,
                    this.deeplink_url.orEmpty(),
                    this.entity_type.orEmpty(),
                    index
                )
            }
        }
    }

    override suspend fun saveLocally(
        params: EmptyParams,
        dbModel: List<RoomNavigationEntity>
    ) = navigationDao.replaceNavigationEntities(NavigationSource.FEED, dbModel)
}