package com.theathletic.gamedetail.data.remote

import com.theathletic.AmericanFootballPlayUpdatesSubscription
import com.theathletic.GetAmericanFootballPlayByPlaysQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.data.RemoteToLocalSubscriber
import com.theathletic.gamedetail.data.local.PlayByPlayLocalModel
import com.theathletic.gamedetail.data.local.PlayByPlaysLocalDataSource
import com.theathletic.gamedetail.data.local.toLocalModel
import com.theathletic.scores.remote.ScoresGraphqlApi
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.flow.Flow

class AmericanFootballPlayByPlaysFetcher @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider,
    private val localDataSource: PlayByPlaysLocalDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi
) : RemoteToLocalFetcher<
    AmericanFootballPlayByPlaysFetcher.Params,
    GetAmericanFootballPlayByPlaysQuery.Data,
    PlayByPlayLocalModel?
    >(dispatcherProvider) {

    data class Params(
        val gameId: String
    )

    override suspend fun makeRemoteRequest(params: Params): GetAmericanFootballPlayByPlaysQuery.Data? {
        return scoresGraphqlApi.getAmericanFootballPlayByPlays(params.gameId).data
    }

    override fun mapToLocalModel(
        params: Params,
        remoteModel: GetAmericanFootballPlayByPlaysQuery.Data
    ) = remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: PlayByPlayLocalModel?) {
        dbModel?.let {
            localDataSource.update(params.gameId, it)
        }
    }
}

class AmericanFootballPlayByPlaysSubscriber @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val localDataSource: PlayByPlaysLocalDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi
) : RemoteToLocalSubscriber<
    AmericanFootballPlayByPlaysSubscriber.Params,
    AmericanFootballPlayUpdatesSubscription.Data,
    PlayByPlayLocalModel?
    >(dispatcherProvider) {

    data class Params(
        val gameId: String
    )

    override suspend fun makeRemoteRequest(params: Params): Flow<AmericanFootballPlayUpdatesSubscription.Data> {
        return scoresGraphqlApi.getAmericanFootballPlayUpdatesSubscription(params.gameId)
    }

    override fun mapToLocalModel(
        params: Params,
        remoteModel: AmericanFootballPlayUpdatesSubscription.Data
    ) = remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: PlayByPlayLocalModel?) {
        dbModel?.let {
            localDataSource.update(params.gameId, it)
        }
    }
}