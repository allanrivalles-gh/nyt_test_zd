package com.theathletic.scores.data.remote

import com.theathletic.GetTeamDetailsQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalFetcherWithResponse
import com.theathletic.scores.data.local.TeamDetailsLocalModel
import com.theathletic.scores.remote.ScoresGraphqlApi
import com.theathletic.utility.coroutines.DispatcherProvider

class TeamDetailsFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val scoresApi: ScoresGraphqlApi
) : RemoteToLocalFetcherWithResponse<
    TeamDetailsFetcher.Params,
    GetTeamDetailsQuery.Data,
    TeamDetailsLocalModel?
    >(dispatcherProvider) {

    data class Params(
        val teamId: String
    )

    override suspend fun makeRemoteRequest(params: Params): GetTeamDetailsQuery.Data? {
        return scoresApi.getTeamDetails(params.teamId).data
    }

    override fun mapToLocalModel(params: Params, remoteModel: GetTeamDetailsQuery.Data) =
        remoteModel.toLocal()

    override suspend fun saveLocally(params: Params, dbModel: TeamDetailsLocalModel?) {
        // Not saving locally
    }
}