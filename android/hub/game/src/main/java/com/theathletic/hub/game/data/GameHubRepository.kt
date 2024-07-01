package com.theathletic.hub.game.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.hub.game.data.local.GameSummaryLocalDataSource
import com.theathletic.network.apollo.utility.toMessage
import com.theathletic.repository.CoroutineRepository
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async

class GameHubRepository @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val gameHubApi: GameHubApi,
    private val gameSummaryLocalDataSource: GameSummaryLocalDataSource
) : CoroutineRepository {
    class GameHubException(message: String) : Exception(message)

    override val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    suspend fun fetchGameSummary(gameId: String) = repositoryScope.async {
        try {
            val response = gameHubApi.getGameSummary(gameId)
            if (response.hasErrors()) throw Exception(response.errors.toMessage())
            response.data?.toDomain()?.let { domainModel ->
                gameSummaryLocalDataSource.update(gameId, domainModel)
            }
        } catch (error: Throwable) {
            throw GameHubException("Error fetching Game Summary for game: $gameId, message: ${error.message}")
        }
    }.await()

    fun getGameSummary(gameId: String) = gameSummaryLocalDataSource.observeItem(gameId)
}