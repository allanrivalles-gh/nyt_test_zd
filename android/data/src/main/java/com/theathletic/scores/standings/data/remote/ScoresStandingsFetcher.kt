package com.theathletic.scores.standings.data.remote

import com.theathletic.GetStandingsQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.entity.main.League
import com.theathletic.scores.remote.ScoresGraphqlApi
import com.theathletic.scores.remote.toGraphqlLeagueCode
import com.theathletic.scores.standings.data.local.ScoresStandingsLocalDataSource
import com.theathletic.scores.standings.data.local.ScoresStandingsLocalModel
import com.theathletic.utility.coroutines.DispatcherProvider

class ScoresStandingsFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val localDataSource: ScoresStandingsLocalDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi,
) : RemoteToLocalFetcher<
    ScoresStandingsFetcher.Params,
    GetStandingsQuery.Data?,
    ScoresStandingsLocalModel?
    >(dispatcherProvider) {

    data class Params(
        val league: League
    )

    override suspend fun makeRemoteRequest(params: Params): GetStandingsQuery.Data? {
        return scoresGraphqlApi.getStandings(params.league.toGraphqlLeagueCode).data
    }

    override fun mapToLocalModel(params: Params, remoteModel: GetStandingsQuery.Data?) = remoteModel?.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: ScoresStandingsLocalModel?) {
        dbModel?.let { localDataSource.update(params.league, it) }
    }
}