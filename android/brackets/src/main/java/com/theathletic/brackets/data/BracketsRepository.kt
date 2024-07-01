package com.theathletic.brackets.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.brackets.data.local.BracketsLocalDataSource
import com.theathletic.brackets.data.remote.BracketsFetcher
import com.theathletic.brackets.data.remote.BracketsGamesSubscriber
import com.theathletic.repository.CoroutineRepository
import com.theathletic.type.LeagueCode
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class BracketsRepository @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val bracketsFetcher: BracketsFetcher,
    private val bracketsGamesSubscriber: BracketsGamesSubscriber,
    private val bracketsLocalDataSource: BracketsLocalDataSource,
) : CoroutineRepository {
    override val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    suspend fun fetchTournament(leagueCode: LeagueCode, seasonId: String?) = repositoryScope.launch {
        bracketsFetcher.fetchRemote(
            BracketsFetcher.Params(leagueCode, seasonId)
        )
    }

    suspend fun subscribeToTournamentGamesUpdates(
        leagueCode: LeagueCode,
        placeholderTeamsMap: Map<String, PlaceholderTeams>,
        gamesIds: List<String>,
    ) {
        bracketsGamesSubscriber.subscribe(
            BracketsGamesSubscriber.Params(
                leagueCode,
                placeholderTeamsMap.mapToRemote(),
                gamesIds,
            )
        )
    }

    fun getTournament(leagueCode: LeagueCode) = bracketsLocalDataSource.observeItem(leagueCode)
}