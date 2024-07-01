package com.theathletic.scores.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.theathletic.GetLeagueScheduleQuery
import com.theathletic.GetScheduleFeedGroupQuery
import com.theathletic.GetTeamScheduleQuery
import com.theathletic.ScoresFeedUpdatesSubscription
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.entity.main.League
import com.theathletic.network.apollo.notPersistedSubscription
import com.theathletic.type.LeagueCode
import com.theathletic.utility.LocaleUtility
import com.theathletic.utility.coroutines.retryWithBackoff
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class ScheduleApi @AutoKoin(Scope.SINGLE) constructor(
    private val client: ApolloClient,
    private val localeUtility: LocaleUtility
) {
    suspend fun getTeamSchedule(teamId: String): ApolloResponse<GetTeamScheduleQuery.Data> {
        return client.query(
            GetTeamScheduleQuery(
                timeZone = localeUtility.deviceTimeZone.id,
                teamId = teamId
            )
        ).execute()
    }

    suspend fun getLeagueSchedule(league: League): ApolloResponse<GetLeagueScheduleQuery.Data> {
        val leagueCode = league.toGraphqlLeagueCode
        if (leagueCode == LeagueCode.UNKNOWN__) throw Exception("Unknown League")
        return client.query(
            GetLeagueScheduleQuery(
                timeZone = localeUtility.deviceTimeZone.id,
                leagueCode = leagueCode
            )
        ).execute()
    }

    suspend fun getScheduleFeedGroup(
        groupId: String,
        filterId: String?
    ): ApolloResponse<GetScheduleFeedGroupQuery.Data> {
        val filterList = if (filterId != null) listOf(filterId) else null
        return client.query(
            GetScheduleFeedGroupQuery(
                timeZone = localeUtility.deviceTimeZone.id,
                groupId = groupId,
                filterId = Optional.present(filterList)
            )
        ).execute()
    }

    fun getScheduleUpdates(blockIds: List<String>): Flow<ScoresFeedUpdatesSubscription.Data> {
        return client.notPersistedSubscription(ScoresFeedUpdatesSubscription(blockIds))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }
}