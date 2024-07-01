package com.theathletic.gamedetail.data.remote

import com.theathletic.GetHockeyPlayByPlaysQuery
import com.theathletic.HockeyPlayUpdatesSubscription
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

class HockeyPlayByPlaysFetcher @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider,
    private val localDataSource: PlayByPlaysLocalDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi
) : RemoteToLocalFetcher<
    HockeyPlayByPlaysFetcher.Params,
    GetHockeyPlayByPlaysQuery.Data,
    PlayByPlayLocalModel?
    >(dispatcherProvider) {

    data class Params(
        val gameId: String
    )

    override suspend fun makeRemoteRequest(params: Params): GetHockeyPlayByPlaysQuery.Data? {
        return scoresGraphqlApi.getHockeyPlayByPlays(params.gameId).data
    }

    override fun mapToLocalModel(
        params: Params,
        remoteModel: GetHockeyPlayByPlaysQuery.Data
    ) = remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: PlayByPlayLocalModel?) {
        dbModel?.let {
            localDataSource.update(params.gameId, it)
        }
    }
}

class HockeyPlayByPlaysSubscriber @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val localDataSource: PlayByPlaysLocalDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi
) : RemoteToLocalSubscriber<
    HockeyPlayByPlaysSubscriber.Params,
    HockeyPlayUpdatesSubscription.Data,
    PlayByPlayLocalModel?
    >(dispatcherProvider) {

    data class Params(
        val gameId: String
    )

    override suspend fun makeRemoteRequest(params: Params): Flow<HockeyPlayUpdatesSubscription.Data> {
        return scoresGraphqlApi.getHockeyPlayUpdatesSubscription(params.gameId)
    }

    override fun mapToLocalModel(
        params: Params,
        remoteModel: HockeyPlayUpdatesSubscription.Data
    ) = remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: PlayByPlayLocalModel?) {
        dbModel?.let {
            localDataSource.update(params.gameId, it)
        }
    }
}