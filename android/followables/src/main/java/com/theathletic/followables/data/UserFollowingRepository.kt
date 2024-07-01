package com.theathletic.followables.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.EmptyParams
import com.theathletic.followable.FollowableId
import com.theathletic.followable.FollowableType
import com.theathletic.followables.data.domain.Followable
import com.theathletic.followables.data.remote.FollowableFetcher
import com.theathletic.followables.data.remote.UnfollowFetcher
import com.theathletic.followables.data.remote.UserFollowingFetcher
import com.theathletic.repository.user.UserFollowingDao
import com.theathletic.type.UserFollowType
import com.theathletic.utility.IPreferences
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch

class UserFollowingRepository @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val userFollowingFetcher: UserFollowingFetcher,
    private val followableFetcher: FollowableFetcher,
    private val unfollowFetcher: UnfollowFetcher,
    private val userFollowingDao: UserFollowingDao,
    private val preferences: IPreferences,
) {
    private val repositoryScope = CoroutineScope(dispatcherProvider.io + SupervisorJob())

    val userFollowingStream: Flow<List<Followable>>
        get() = combine(
            userFollowingDao.getFollowingTeamsDistinct(),
            userFollowingDao.getFollowingLeaguesDistinct(),
            userFollowingDao.getFollowingAuthorsDistinct(),
            preferences.followablesOrderStateFlow
        ) { teamsEntity, leaguesEntity, authorsEntity, order ->
            val teams = teamsEntity.map { it.toDomain() }
            val leagues = leaguesEntity.map { it.toDomain() }
            val authors = authorsEntity.map { it.toDomain() }

            (teams + leagues + authors)
                .sortedWith(compareBy(nullsFirst()) { order[it.id.toString()] })
        }

    fun getFollowingTeams() = userFollowingDao.getFollowingTeams()
    fun getFollowingLeagues() = userFollowingDao.getFollowingLeagues()

    suspend fun isFollowing(followableId: FollowableId): Boolean {
        return userFollowingStream.lastOrNull()?.firstOrNull {
            it.id == followableId
        } != null
    }

    fun fetchUserFollowingItems() {
        repositoryScope.launch {
            userFollowingFetcher.fetchRemote(EmptyParams)
        }
    }

    suspend fun followItem(id: FollowableId) =
        followableFetcher.fetchRemote(
            FollowableFetcher.Params(
                id = id.id,
                type = id.type.toUserFollowType()
            )
        )

    suspend fun unfollowItem(id: com.theathletic.followable.Followable.Id) =
        unfollowFetcher.fetchRemote(
            UnfollowFetcher.Params(
                id = id.id,
                type = id.type.toUserFollowType()
            )
        )

    fun clearFollowing() = repositoryScope.launch {
        userFollowingDao.clearFollowing()
    }

    fun saveFollowablesReordering(newOrder: Map<String, Int>) {
        preferences.hasCustomFollowableOrder = true
        preferences.followablesOrder = newOrder
    }

    private fun com.theathletic.followable.Followable.Type.toUserFollowType(): UserFollowType =
        when (this) {
            FollowableType.TEAM -> UserFollowType.team
            FollowableType.LEAGUE -> UserFollowType.league
            FollowableType.AUTHOR -> UserFollowType.author
        }
}