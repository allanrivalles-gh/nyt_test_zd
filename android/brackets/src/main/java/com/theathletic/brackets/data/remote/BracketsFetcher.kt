package com.theathletic.brackets.data.remote

import com.theathletic.GetTournamentQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.brackets.data.BracketsGraphqlApi
import com.theathletic.brackets.data.local.BracketsLocalDataSource
import com.theathletic.brackets.data.local.BracketsLocalModel
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.type.LeagueCode
import com.theathletic.utility.coroutines.DispatcherProvider

class BracketsFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val localDataSource: BracketsLocalDataSource,
    private val bracketsGraphqlApi: BracketsGraphqlApi,
) : RemoteToLocalFetcher<BracketsFetcher.Params, GetTournamentQuery.Data, BracketsLocalModel>(dispatcherProvider) {

    data class Params(
        val leagueCode: LeagueCode,
        val seasonId: String?,
    )

    override suspend fun makeRemoteRequest(params: Params): GetTournamentQuery.Data? {
        return bracketsGraphqlApi.getTournament(params.leagueCode, params.seasonId).data
    }

    override fun mapToLocalModel(params: Params, remoteModel: GetTournamentQuery.Data): BracketsLocalModel = remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: BracketsLocalModel) {
        localDataSource.update(params.leagueCode, dbModel)
    }
}