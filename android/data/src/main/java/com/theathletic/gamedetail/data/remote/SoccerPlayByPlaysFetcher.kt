package com.theathletic.gamedetail.data.remote

import com.theathletic.GetSoccerPlayByPlaysQuery
import com.theathletic.SoccerPlayUpdatesSubscription
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

class SoccerPlayByPlaysFetcher @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider,
    private val localDataSource: PlayByPlaysLocalDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi
) : RemoteToLocalFetcher<
    SoccerPlayByPlaysFetcher.Params,
    GetSoccerPlayByPlaysQuery.Data,
    PlayByPlayLocalModel?
    >(dispatcherProvider) {

    data class Params(
        val gameId: String
    )

    override suspend fun makeRemoteRequest(params: Params): GetSoccerPlayByPlaysQuery.Data? {
        return scoresGraphqlApi.getSoccerPlayByPlays(params.gameId).data
    }

    override fun mapToLocalModel(
        params: Params,
        remoteModel: GetSoccerPlayByPlaysQuery.Data
    ) = remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: PlayByPlayLocalModel?) {
        dbModel?.let {
            localDataSource.update(params.gameId, it)
        }
    }
}

class SoccerPlayByPlaysSubscriber @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val localDataSource: PlayByPlaysLocalDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi
) : RemoteToLocalSubscriber<
    SoccerPlayByPlaysSubscriber.Params,
    SoccerPlayUpdatesSubscription.Data,
    PlayByPlayLocalModel?
    >(dispatcherProvider) {

    data class Params(
        val gameId: String
    )

    override suspend fun makeRemoteRequest(params: Params): Flow<SoccerPlayUpdatesSubscription.Data> {
        return scoresGraphqlApi.getSoccerPlayUpdatesSubscription(params.gameId)
    }

    override fun mapToLocalModel(
        params: Params,
        remoteModel: SoccerPlayUpdatesSubscription.Data
    ) = remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: PlayByPlayLocalModel?) {
        dbModel?.let {
            localDataSource.update(params.gameId, it)
        }
    }
}