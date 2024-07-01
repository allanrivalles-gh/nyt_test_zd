package com.theathletic.followables.data.remote

import com.theathletic.UserFollowingQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.EmptyParams
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.followables.data.extractNotificationSettings
import com.theathletic.followables.data.toLocalModel
import com.theathletic.repository.user.UserFollowingDao
import com.theathletic.repository.user.UserFollowingItem
import com.theathletic.topics.data.remote.FollowableItemsApi
import com.theathletic.user.FollowableNotificationSettingsDao
import com.theathletic.user.LocalFollowableNotificationSettings
import com.theathletic.utility.IPreferences
import com.theathletic.utility.coroutines.DispatcherProvider

class UserFollowingFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val followableItemsApi: FollowableItemsApi,
    private val userFollowingDao: UserFollowingDao,
    private val followableNotificationSettingsDao: FollowableNotificationSettingsDao,
    private val preferences: IPreferences
) : RemoteToLocalFetcher<
    EmptyParams,
    UserFollowingQuery.Data,
    UserFollowingFetcher.LocalModels
    >(dispatcherProvider) {

    data class LocalModels(
        val userFollowingItems: List<UserFollowingItem>,
        val followableNotificationList: List<LocalFollowableNotificationSettings>
    )

    override suspend fun makeRemoteRequest(params: EmptyParams): UserFollowingQuery.Data? {
        return followableItemsApi.getUserFollowingItems().data
    }

    override fun mapToLocalModel(
        params: EmptyParams,
        remoteModel: UserFollowingQuery.Data
    ): LocalModels {
        val followingItems = remoteModel.customer.asCustomer?.following
        val notificationSettingsList = followingItems?.extractNotificationSettings() ?: emptyList()

        val teams = followingTeamsToLocalModel(followingItems)
        val leagues = followingLeaguesToLocalModel(followingItems)
        val authors = followingAuthorsToLocalModel(followingItems)
        val userFollowingItems = listOf(teams, leagues, authors).flatten()

        return LocalModels(userFollowingItems, notificationSettingsList)
    }

    private fun followingTeamsToLocalModel(followingItems: UserFollowingQuery.Following?): List<UserFollowingItem> {
        val teams = followingItems?.teams ?: listOf()
        return teams.sortedBy { it.nav_order }.map { it.toLocalModel() }
    }

    private fun followingLeaguesToLocalModel(followingItems: UserFollowingQuery.Following?): List<UserFollowingItem> {
        val leagues = followingItems?.leagues ?: emptyList()
        return leagues.sortedBy { it.nav_order }.map { it.toLocalModel() }
    }

    private fun followingAuthorsToLocalModel(followingItems: UserFollowingQuery.Following?): List<UserFollowingItem> {
        val authors = followingItems?.authors ?: emptyList()
        return authors.sortedBy { it.nav_order }.map { it.toLocalModel() }
    }

    override suspend fun saveLocally(params: EmptyParams, dbModel: LocalModels) {
        saveDefaultFollowableOrder(dbModel.userFollowingItems)
        userFollowingDao.insertUserFollowingItems(dbModel.userFollowingItems)
        followableNotificationSettingsDao.insertSettings(dbModel.followableNotificationList)
    }

    private fun saveDefaultFollowableOrder(userFollowingItems: List<UserFollowingItem>) {
        if (preferences.hasCustomFollowableOrder.not()) {
            preferences.followablesOrder = userFollowingItems.withIndex().associate { it.value.id.toString() to it.index }
        }
    }
}