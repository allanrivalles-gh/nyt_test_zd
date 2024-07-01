package com.theathletic.gamedetail.data.remote

import com.theathletic.BaseballPlayUpdatesSubscription
import com.theathletic.GetBaseballPlayByPlaysQuery
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

class BaseballPlayByPlaysFetcher @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider,
    private val localDataSource: PlayByPlaysLocalDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi
) : RemoteToLocalFetcher<
    BaseballPlayByPlaysFetcher.Params,
    GetBaseballPlayByPlaysQuery.Data,
    PlayByPlayLocalModel?
    >(dispatcherProvider) {

    data class Params(
        val gameId: String
    )

    override suspend fun makeRemoteRequest(params: Params): GetBaseballPlayByPlaysQuery.Data? {
        return scoresGraphqlApi.getBaseballPlayByPlays(params.gameId).data
    }

    override fun mapToLocalModel(
        params: Params,
        remoteModel: GetBaseballPlayByPlaysQuery.Data
    ) = remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: PlayByPlayLocalModel?) {
        dbModel?.let {
            localDataSource.update(params.gameId, it)
        }
    }
}

class BaseballPlayByPlaysSubscriber @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val localDataSource: PlayByPlaysLocalDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi
) : RemoteToLocalSubscriber<
    BaseballPlayByPlaysSubscriber.Params,
    BaseballPlayUpdatesSubscription.Data,
    PlayByPlayLocalModel?
    >(dispatcherProvider) {

    data class Params(
        val gameId: String
    )

    override suspend fun makeRemoteRequest(params: Params): Flow<BaseballPlayUpdatesSubscription.Data> {
        return scoresGraphqlApi.getBaseballPlayUpdatesSubscription(params.gameId)
    }

    override fun mapToLocalModel(
        params: Params,
        remoteModel: BaseballPlayUpdatesSubscription.Data
    ) = remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: PlayByPlayLocalModel?) {
        dbModel?.let {
            localDataSource.update(params.gameId, it)
        }
    }
}