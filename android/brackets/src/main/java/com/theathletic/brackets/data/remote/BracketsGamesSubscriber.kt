package com.theathletic.brackets.data.remote

import com.theathletic.TournamentGamesSubscription
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.brackets.data.BracketsGraphqlApi
import com.theathletic.brackets.data.local.BracketsLocalDataSource
import com.theathletic.brackets.data.local.TournamentRoundGame
import com.theathletic.data.RemoteToLocalSubscriber
import com.theathletic.fragment.TournamentStage
import com.theathletic.type.LeagueCode
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.flow.Flow

class BracketsGamesSubscriber @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val localDataSource: BracketsLocalDataSource,
    private val bracketsGraphqlApi: BracketsGraphqlApi,
) : RemoteToLocalSubscriber<BracketsGamesSubscriber.Params, TournamentGamesSubscription.Data, TournamentRoundGame?>(dispatcherProvider) {

    data class Params(
        val leagueCode: LeagueCode,
        val placeholderTeams: Map<String, TournamentStage.Placeholder_game_team>,
        val gamesIds: List<String>
    )

    override suspend fun makeRemoteRequest(params: Params): Flow<TournamentGamesSubscription.Data> {
        return bracketsGraphqlApi.getTournamentGamesSubscription(params.gamesIds)
    }

    override fun mapToLocalModel(params: Params, remoteModel: TournamentGamesSubscription.Data): TournamentRoundGame? {
        val gameId = remoteModel.liveScoreUpdates?.fragments?.tournamentGame?.id
        return gameId.let { remoteModel.toLocalModel(params.placeholderTeams[it]) }
    }

    override suspend fun saveLocally(params: Params, dbModel: TournamentRoundGame?) {
        dbModel?.let { localDataSource.updateGame(params.leagueCode, it) }
    }
}