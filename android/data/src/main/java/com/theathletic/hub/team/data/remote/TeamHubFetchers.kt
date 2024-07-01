package com.theathletic.hub.team.data.remote

import com.theathletic.TeamRosterQuery
import com.theathletic.TeamStandingsQuery
import com.theathletic.TeamStatsQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.hub.team.data.local.TeamHubRosterLocalDataSource
import com.theathletic.hub.team.data.local.TeamHubRosterLocalModel
import com.theathletic.hub.team.data.local.TeamHubStandingsLocalDataSource
import com.theathletic.hub.team.data.local.TeamHubStatsLocalDataSource
import com.theathletic.hub.team.data.local.TeamHubStatsLocalModel
import com.theathletic.hub.team.data.local.toLocalModel
import com.theathletic.scores.standings.data.local.TeamStandingsLocalModel
import com.theathletic.scores.standings.data.remote.toLocalModel
import com.theathletic.utility.coroutines.DispatcherProvider

class TeamHubStatsFetcher @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider,
    private val teamHubApi: TeamHubApi,
    private val localDataSource: TeamHubStatsLocalDataSource,
) : RemoteToLocalFetcher<
    TeamHubStatsFetcher.Params,
    TeamStatsQuery.Data,
    TeamHubStatsLocalModel?
    >(dispatcherProvider) {

    data class Params(
        val teamId: String
    )

    override suspend fun makeRemoteRequest(params: Params): TeamStatsQuery.Data? {
        return teamHubApi.getTeamStats(params.teamId).data
    }

    override fun mapToLocalModel(params: Params, remoteModel: TeamStatsQuery.Data) =
        remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: TeamHubStatsLocalModel?) {
        dbModel?.let {
            localDataSource.update(params.teamId, it)
        }
    }
}

class TeamHubRosterFetcher @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider,
    private val teamHubApi: TeamHubApi,
    private val localDataSource: TeamHubRosterLocalDataSource,
) : RemoteToLocalFetcher<
    TeamHubRosterFetcher.Params,
    TeamRosterQuery.Data,
    TeamHubRosterLocalModel?
    >(dispatcherProvider) {

    data class Params(
        val teamId: String
    )

    override suspend fun makeRemoteRequest(params: Params): TeamRosterQuery.Data? {
        return teamHubApi.getTeamRoster(params.teamId).data
    }

    override fun mapToLocalModel(params: Params, remoteModel: TeamRosterQuery.Data) =
        remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: TeamHubRosterLocalModel?) {
        dbModel?.let {
            localDataSource.update(params.teamId, it)
        }
    }
}

class TeamHubStandingsFetcher @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider,
    private val teamHubApi: TeamHubApi,
    private val localDataSource: TeamHubStandingsLocalDataSource
) : RemoteToLocalFetcher<
    TeamHubStandingsFetcher.Params,
    TeamStandingsQuery.Data,
    TeamStandingsLocalModel?
    >(dispatcherProvider) {

    data class Params(
        val teamId: String
    )

    override suspend fun makeRemoteRequest(params: Params): TeamStandingsQuery.Data? {
        return teamHubApi.getTeamStandings(params.teamId).data
    }

    override fun mapToLocalModel(params: Params, remoteModel: TeamStandingsQuery.Data) =
        remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: TeamStandingsLocalModel?) {
        dbModel?.let {
            localDataSource.update(params.teamId, it)
        }
    }
}