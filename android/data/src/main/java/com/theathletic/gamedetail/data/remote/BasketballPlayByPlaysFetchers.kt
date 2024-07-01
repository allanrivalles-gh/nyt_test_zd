package com.theathletic.gamedetail.data.remote

import com.theathletic.BasketballPlayUpdatesSubscription
import com.theathletic.GetBasketballPlayByPlaysQuery
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

class BasketballPlayByPlaysFetcher @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider,
    private val localDataSource: PlayByPlaysLocalDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi
) : RemoteToLocalFetcher<
    BasketballPlayByPlaysFetcher.Params,
    GetBasketballPlayByPlaysQuery.Data,
    PlayByPlayLocalModel?
    >(dispatcherProvider) {

    data class Params(
        val gameId: String
    )

    override suspend fun makeRemoteRequest(params: Params): GetBasketballPlayByPlaysQuery.Data? {
        return scoresGraphqlApi.getBasketballPlayByPlays(params.gameId).data
    }

    override fun mapToLocalModel(
        params: Params,
        remoteModel: GetBasketballPlayByPlaysQuery.Data
    ) = remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: PlayByPlayLocalModel?) {
        dbModel?.let {
            localDataSource.update(params.gameId, it)
        }
    }
}

class BasketballPlayByPlaysSubscriber @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val localDataSource: PlayByPlaysLocalDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi
) : RemoteToLocalSubscriber<
    BasketballPlayByPlaysSubscriber.Params,
    BasketballPlayUpdatesSubscription.Data,
    PlayByPlayLocalModel?
    >(dispatcherProvider) {

    data class Params(
        val gameId: String
    )

    override suspend fun makeRemoteRequest(params: Params): Flow<BasketballPlayUpdatesSubscription.Data> {
        return scoresGraphqlApi.getBasketballPlayUpdatesSubscription(params.gameId)
    }

    override fun mapToLocalModel(
        params: Params,
        remoteModel: BasketballPlayUpdatesSubscription.Data
    ) = remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: PlayByPlayLocalModel?) {
        dbModel?.let {
            localDataSource.update(params.gameId, it)
        }
    }
}