package com.theathletic.scores.data.remote

import com.theathletic.LiveGamesSubscription
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalSubscriber
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.scores.data.local.BoxScoreEntity
import com.theathletic.scores.remote.ScoresGraphqlApi
import com.theathletic.utility.coroutines.DispatcherProvider

class LiveGamesSubscriber @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val entityDataSource: EntityDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi,
) : RemoteToLocalSubscriber<
    LiveGamesSubscriber.Params,
    LiveGamesSubscription.Data,
    BoxScoreEntity?
    >(dispatcherProvider) {

    data class Params(val gameIds: Set<String>)

    override suspend fun makeRemoteRequest(
        params: Params
    ) = scoresGraphqlApi.getLiveGamesSubscription(params.gameIds.toList())

    override fun mapToLocalModel(
        params: Params,
        remoteModel: LiveGamesSubscription.Data
    ) = remoteModel.liveScoreUpdates?.fragments?.gameLiteFragment?.toEntity()

    override suspend fun saveLocally(params: Params, dbModel: BoxScoreEntity?) {
        dbModel ?: return
        entityDataSource.insertOrUpdate(dbModel)
    }
}