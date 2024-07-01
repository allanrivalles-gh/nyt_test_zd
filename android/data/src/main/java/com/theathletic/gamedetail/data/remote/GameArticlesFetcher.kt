package com.theathletic.gamedetail.data.remote

import com.theathletic.GetGameArticlesQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.gamedetail.data.local.GameArticlesLocalDataSource
import com.theathletic.gamedetail.data.local.GameArticlesLocalModel
import com.theathletic.scores.remote.ScoresGraphqlApi
import com.theathletic.utility.coroutines.DispatcherProvider

class GameArticlesFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val localDataSource: GameArticlesLocalDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi
) : RemoteToLocalFetcher<
    GameArticlesFetcher.Params,
    GetGameArticlesQuery.Data,
    GameArticlesLocalModel
    >(dispatcherProvider) {

    data class Params(
        val gameId: String,
        val leagueId: Long
    )

    override suspend fun makeRemoteRequest(params: Params): GetGameArticlesQuery.Data? {
        return scoresGraphqlApi.getGameArticles(
            params.gameId,
            params.leagueId.toString()
        ).data
    }

    override fun mapToLocalModel(
        params: Params,
        remoteModel: GetGameArticlesQuery.Data
    ) = remoteModel.toLocalModel(params.gameId)

    override suspend fun saveLocally(params: Params, dbModel: GameArticlesLocalModel) {
        localDataSource.update(params.gameId, dbModel)
    }
}