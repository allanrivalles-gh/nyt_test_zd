package com.theathletic.hub.team.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.theathletic.TeamRosterQuery
import com.theathletic.TeamStandingsQuery
import com.theathletic.TeamStatsQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope

class TeamHubApi @AutoKoin(Scope.SINGLE) constructor(
    private val client: ApolloClient
) {
    suspend fun getTeamStats(teamId: String): ApolloResponse<TeamStatsQuery.Data> {
        return client.query(TeamStatsQuery(teamId)).execute()
    }

    suspend fun getTeamRoster(teamId: String): ApolloResponse<TeamRosterQuery.Data> {
        return client.query(TeamRosterQuery(teamId)).execute()
    }

    suspend fun getTeamStandings(teamId: String): ApolloResponse<TeamStandingsQuery.Data> {
        return client.query(TeamStandingsQuery(teamId)).execute()
    }
}