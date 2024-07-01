package com.theathletic.gamedetail.data.remote

import com.theathletic.GetAmericanFootballGameQuery
import com.theathletic.GetBaseballGameQuery
import com.theathletic.GetBasketballGameQuery
import com.theathletic.GetHockeyGameQuery
import com.theathletic.GetSoccerGameQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.gamedetail.data.local.GameDetailLocalDataSource
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.scores.remote.ScoresGraphqlApi
import com.theathletic.utility.coroutines.DispatcherProvider

class GameAmericanFootballFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val localDataSource: GameDetailLocalDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi
) : RemoteToLocalFetcher<
    GameAmericanFootballFetcher.Params,
    GetAmericanFootballGameQuery.Data,
    GameDetailLocalModel?
    >(dispatcherProvider) {

    data class Params(
        val gameId: String
    )

    override suspend fun makeRemoteRequest(params: Params): GetAmericanFootballGameQuery.Data? {
        return scoresGraphqlApi.getAmericanFootballGame(params.gameId).data
    }

    override fun mapToLocalModel(params: Params, remoteModel: GetAmericanFootballGameQuery.Data) =
        remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: GameDetailLocalModel?) {
        dbModel?.let { model ->
            localDataSource.update(
                params.gameId,
                model
            )
        }
    }
}

class GameBasketballFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val localDataSource: GameDetailLocalDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi
) : RemoteToLocalFetcher<
    GameBasketballFetcher.Params,
    GetBasketballGameQuery.Data,
    GameDetailLocalModel?
    >(dispatcherProvider) {

    data class Params(
        val gameId: String
    )

    override suspend fun makeRemoteRequest(params: Params): GetBasketballGameQuery.Data? {
        return scoresGraphqlApi.getBasketballGame(params.gameId).data
    }

    override fun mapToLocalModel(params: Params, remoteModel: GetBasketballGameQuery.Data) =
        remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: GameDetailLocalModel?) {
        dbModel?.let { model ->
            localDataSource.update(
                params.gameId,
                model
            )
        }
    }
}

class GameHockeyFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val localDataSource: GameDetailLocalDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi
) : RemoteToLocalFetcher<
    GameHockeyFetcher.Params,
    GetHockeyGameQuery.Data,
    GameDetailLocalModel?
    >(dispatcherProvider) {

    data class Params(
        val gameId: String
    )

    override suspend fun makeRemoteRequest(params: Params): GetHockeyGameQuery.Data? {
        return scoresGraphqlApi.getHockeyGame(params.gameId).data
    }

    override fun mapToLocalModel(params: Params, remoteModel: GetHockeyGameQuery.Data) =
        remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: GameDetailLocalModel?) {
        dbModel?.let { model ->
            localDataSource.update(
                params.gameId,
                model
            )
        }
    }
}

class GameBaseballFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val localDataSource: GameDetailLocalDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi
) : RemoteToLocalFetcher<
    GameBaseballFetcher.Params,
    GetBaseballGameQuery.Data,
    GameDetailLocalModel?
    >(dispatcherProvider) {

    data class Params(
        val gameId: String,
    )

    override suspend fun makeRemoteRequest(params: Params): GetBaseballGameQuery.Data? {
        return scoresGraphqlApi.getBaseballGame(params.gameId).data
    }

    override fun mapToLocalModel(params: Params, remoteModel: GetBaseballGameQuery.Data) =
        remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: GameDetailLocalModel?) {
        dbModel?.let { model ->
            localDataSource.update(
                params.gameId,
                model
            )
        }
    }
}

class GameSoccerFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val localDataSource: GameDetailLocalDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi
) : RemoteToLocalFetcher<
    GameSoccerFetcher.Params,
    GetSoccerGameQuery.Data,
    GameDetailLocalModel?
    >(dispatcherProvider) {

    data class Params(
        val gameId: String
    )

    override suspend fun makeRemoteRequest(params: Params): GetSoccerGameQuery.Data? {
        return scoresGraphqlApi.getSoccerGame(params.gameId).data
    }

    override fun mapToLocalModel(params: Params, remoteModel: GetSoccerGameQuery.Data) =
        remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: GameDetailLocalModel?) {
        dbModel?.let { model ->
            localDataSource.update(
                params.gameId,
                model
            )
        }
    }
}