package com.theathletic.gamedetail.data.remote

import com.theathletic.BaseballPlayerStatsUpdatesSubscription
import com.theathletic.GameSummaryUpdatesSubscription
import com.theathletic.GetBaseballStatsQuery
import com.theathletic.GetGameSummaryQuery
import com.theathletic.GetPlayerStatsQuery
import com.theathletic.PlayerStatsUpdatesSubscription
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.data.RemoteToLocalSubscriber
import com.theathletic.gamedetail.data.local.GameLineUpAndStats
import com.theathletic.gamedetail.data.local.GameSummaryLocalDataSource
import com.theathletic.gamedetail.data.local.GameSummaryLocalModel
import com.theathletic.gamedetail.data.local.LineUpAndStatsLocalDataSource
import com.theathletic.liveblog.data.local.LiveBlogLinksLocalDataSource
import com.theathletic.scores.remote.ScoresGraphqlApi
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.flow.Flow

class GameSummaryFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val localDataSource: GameSummaryLocalDataSource,
    private val liveBlogLinksLocalDataSource: LiveBlogLinksLocalDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi
) : RemoteToLocalFetcher<
    GameSummaryFetcher.Params,
    GetGameSummaryQuery.Data,
    GameSummaryLocalModel?
    >(dispatcherProvider) {

    data class Params(
        val gameId: String
    )

    override suspend fun makeRemoteRequest(params: Params): GetGameSummaryQuery.Data? {
        return scoresGraphqlApi.getGameSummary(params.gameId).data
    }

    override fun mapToLocalModel(params: Params, remoteModel: GetGameSummaryQuery.Data) = remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: GameSummaryLocalModel?) {
        dbModel?.let { model ->
            localDataSource.update(
                params.gameId,
                model
            )
        }
        dbModel?.liveBlog?.let {
            liveBlogLinksLocalDataSource.update(
                it.id,
                it
            )
        }
    }
}

class GameSummarySubscriber @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val localDataSource: GameSummaryLocalDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi
) : RemoteToLocalSubscriber<
    GameSummarySubscriber.Params,
    GameSummaryUpdatesSubscription.Data,
    GameSummaryLocalModel?
    >(dispatcherProvider) {

    data class Params(
        val gameId: String
    )

    override suspend fun makeRemoteRequest(params: Params): Flow<GameSummaryUpdatesSubscription.Data> {
        return scoresGraphqlApi.getGameSummaryUpdatesSubscription(params.gameId)
    }

    override fun mapToLocalModel(
        params: Params,
        remoteModel: GameSummaryUpdatesSubscription.Data
    ) = remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: GameSummaryLocalModel?) {
        dbModel?.let { model ->
            localDataSource.update(
                params.gameId,
                model
            )
        }
    }
}

class PlayerStatsFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val localDataSource: LineUpAndStatsLocalDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi
) : RemoteToLocalFetcher<
    PlayerStatsFetcher.Params,
    GetPlayerStatsQuery.Data,
    GameLineUpAndStats
    >(dispatcherProvider) {

    data class Params(
        val gameId: String
    )

    override suspend fun makeRemoteRequest(params: Params): GetPlayerStatsQuery.Data? {
        return scoresGraphqlApi.getPlayerStats(params.gameId).data
    }

    override fun mapToLocalModel(params: Params, remoteModel: GetPlayerStatsQuery.Data) = remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: GameLineUpAndStats) {
        localDataSource.update(
            params.gameId,
            dbModel
        )
    }
}

class BaseballStatsFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val localDataSource: LineUpAndStatsLocalDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi
) : RemoteToLocalFetcher<
    BaseballStatsFetcher.Params,
    GetBaseballStatsQuery.Data,
    GameLineUpAndStats
    >(dispatcherProvider) {

    data class Params(
        val gameId: String,
        val isPostGame: Boolean
    )

    override suspend fun makeRemoteRequest(params: Params): GetBaseballStatsQuery.Data? {
        return scoresGraphqlApi.getBaseballStats(params.gameId, params.isPostGame).data
    }

    override fun mapToLocalModel(params: Params, remoteModel: GetBaseballStatsQuery.Data) = remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: GameLineUpAndStats) {
        localDataSource.update(
            params.gameId,
            dbModel
        )
    }
}

class BaseballPlayerStatsUpdatesSubscriber @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val lineUpAndStatsLocalDataSource: LineUpAndStatsLocalDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi
) : RemoteToLocalSubscriber<
    BaseballPlayerStatsUpdatesSubscriber.Params,
    BaseballPlayerStatsUpdatesSubscription.Data,
    GameLineUpAndStats?
    >(dispatcherProvider) {

    data class Params(val gameId: String)

    override suspend fun makeRemoteRequest(params: Params): Flow<BaseballPlayerStatsUpdatesSubscription.Data> {
        return scoresGraphqlApi.getBaseballPlayerStatsUpdatesSubscription(params.gameId)
    }

    override fun mapToLocalModel(
        params: Params,
        remoteModel: BaseballPlayerStatsUpdatesSubscription.Data
    ) = remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: GameLineUpAndStats?) {
        dbModel?.let { model ->
            lineUpAndStatsLocalDataSource.update(params.gameId, model)
        }
    }
}

class GamePlayerStatsUpdatesSubscriber @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val lineUpAndStatsLocalDataSource: LineUpAndStatsLocalDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi
) : RemoteToLocalSubscriber<
    GamePlayerStatsUpdatesSubscriber.Params,
    PlayerStatsUpdatesSubscription.Data,
    GameLineUpAndStats?
    >(dispatcherProvider) {

    data class Params(val gameId: String)

    override suspend fun makeRemoteRequest(params: Params): Flow<PlayerStatsUpdatesSubscription.Data> {
        return scoresGraphqlApi.getPlayerStatsUpdatesSubscription(params.gameId)
    }

    override fun mapToLocalModel(
        params: Params,
        remoteModel: PlayerStatsUpdatesSubscription.Data
    ) = remoteModel.toLocalModel()

    override suspend fun saveLocally(params: Params, dbModel: GameLineUpAndStats?) {
        dbModel?.let { model ->
            lineUpAndStatsLocalDataSource.update(params.gameId, model)
        }
    }
}