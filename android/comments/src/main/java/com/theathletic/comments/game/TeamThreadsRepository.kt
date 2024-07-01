package com.theathletic.comments.game

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.comments.game.local.TeamThreadsLocalDataSource
import com.theathletic.comments.game.remote.TeamThreadsApi
import kotlinx.coroutines.flow.Flow

class TeamThreadsRepository @AutoKoin constructor(
    private val teamThreadsApi: TeamThreadsApi,
    private val teamThreadDataSource: TeamThreadsLocalDataSource
) {
    fun observeTeamThreads(gameId: String): Flow<TeamThreads?> = teamThreadDataSource.observeItem(gameId)

    suspend fun fetchTeamThreads(gameId: String): TeamThreads? {
        val teamThreads = teamThreadsApi.getTeamThreads(gameId).toDomain()
        teamThreadDataSource.update(gameId, teamThreads)
        return teamThreadDataSource.getItem(gameId)
    }

    suspend fun switchTeamThread(gameId: String, teamId: String) {
        val teamThreads = teamThreadsApi.updateTeamThread(gameId, teamId).toDomain()
        teamThreadDataSource.update(gameId, teamThreads)
    }
}