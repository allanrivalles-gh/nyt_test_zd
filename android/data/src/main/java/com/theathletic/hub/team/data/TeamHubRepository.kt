package com.theathletic.hub.team.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.hub.team.data.local.TeamHubRosterLocalDataSource
import com.theathletic.hub.team.data.local.TeamHubStandingsLocalDataSource
import com.theathletic.hub.team.data.local.TeamHubStatsLocalDataSource
import com.theathletic.hub.team.data.remote.TeamHubRosterFetcher
import com.theathletic.hub.team.data.remote.TeamHubStandingsFetcher
import com.theathletic.hub.team.data.remote.TeamHubStatsFetcher
import com.theathletic.repository.CoroutineRepository
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TeamHubRepository @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val statsFetcher: TeamHubStatsFetcher,
    private val rosterFetcher: TeamHubRosterFetcher,
    private val standingsFetcher: TeamHubStandingsFetcher,
    private val statsLocalDataSource: TeamHubStatsLocalDataSource,
    private val rosterLocalDataSource: TeamHubRosterLocalDataSource,
    private val standingsLocalDataSource: TeamHubStandingsLocalDataSource
) : CoroutineRepository {

    override val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    fun fetchTeamStats(teamId: String) = repositoryScope.launch {
        statsFetcher.fetchRemote(TeamHubStatsFetcher.Params(teamId))
    }

    fun getTeamStats(teamId: String) = statsLocalDataSource.observeItem(teamId)

    fun fetchTeamRoster(teamId: String) = repositoryScope.launch {
        rosterFetcher.fetchRemote(TeamHubRosterFetcher.Params(teamId))
    }

    fun getTeamRoster(teamId: String) = rosterLocalDataSource.observeItem(teamId)

    fun fetchTeamStandings(teamId: String) = repositoryScope.launch {
        standingsFetcher.fetchRemote(TeamHubStandingsFetcher.Params(teamId))
    }

    fun getTeamStandings(teamId: String) = standingsLocalDataSource.observeItem(teamId)
}