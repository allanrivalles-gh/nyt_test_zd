package com.theathletic.gamedetail.data

import com.apollographql.apollo3.api.ApolloResponse
import com.theathletic.GradePlayerMutation
import com.theathletic.UngradePlayerMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.entity.main.Sport
import com.theathletic.gamedetail.data.local.PlayerGradesLocalDataSource
import com.theathletic.gamedetail.data.local.PlayerGradesLocalModel
import com.theathletic.gamedetail.data.local.toLocalModel
import com.theathletic.gamedetail.data.remote.PlayerGradesGraphqlApi
import com.theathletic.repository.CoroutineRepository
import com.theathletic.repository.safeApiRequest
import com.theathletic.utility.coroutines.DispatcherProvider
import com.theathletic.utility.safeLet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber

@Suppress("RedundantAsync")
class PlayerGradesRepository @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val playerGradesApi: PlayerGradesGraphqlApi,
    private val playerGradesLocalDataSource: PlayerGradesLocalDataSource,
    private val localDataSource: PlayerGradesLocalDataSource
) : CoroutineRepository {

    class PlayerGradesException(message: String) : Throwable()

    override val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    fun observePlayerGrades(gameId: String) = localDataSource.observeItem(gameId)

    fun getPlayerGrades(gameId: String) = localDataSource.getItem(gameId)

    suspend fun fetchPlayerGrades(gameId: String, sport: Sport) = repositoryScope.async {
        when (sport) {
            Sport.FOOTBALL -> fetchAmericanFootballPlayerGrades(gameId)
            Sport.SOCCER -> fetchSoccerPlayerGrades(gameId)
            Sport.BASKETBALL -> fetchBasketballPlayerGrades(gameId)
            Sport.HOCKEY -> fetchHockeyPlayerGrades(gameId)
            Sport.BASEBALL -> fetchBaseballPlayerGrades(gameId)
            else -> { /* Do Nothing */
            }
        }
    }.await()

    private suspend fun fetchAmericanFootballPlayerGrades(gameId: String) {
        try {
            val response = playerGradesApi.getPlayerGradesForAmericanFootballGame(gameId)
            if (response.hasErrors()) {
                throw PlayerGradesException("Error fetching American Football Player Grades for game - $gameId")
            }
            response.data?.toLocalModel()?.let { localModel ->
                playerGradesLocalDataSource.update(gameId, localModel)
            }
        } catch (exception: Exception) {
            Timber.e(exception)
            throw PlayerGradesException("Error fetching American Football Player Grades for game - $gameId")
        }
    }

    private suspend fun fetchSoccerPlayerGrades(gameId: String) {
        try {
            val response = playerGradesApi.getPlayerGradesForSoccerGame(gameId)
            if (response.hasErrors()) {
                throw PlayerGradesException("Error fetching Soccer Player Grades for game - $gameId")
            }
            response.data?.toLocalModel()?.let { localModel ->
                playerGradesLocalDataSource.update(gameId, localModel)
            }
        } catch (exception: Exception) {
            throw PlayerGradesException("Error fetching Soccer Player Grades for game - $gameId")
        }
    }

    private suspend fun fetchBasketballPlayerGrades(gameId: String) {
        try {
            val response = playerGradesApi.getPlayerGradesForBasketballGame(gameId)
            if (response.hasErrors()) {
                throw PlayerGradesException("Error fetching Basketball Player Grades for game - $gameId")
            }
            response.data?.toLocalModel()?.let { localModel ->
                playerGradesLocalDataSource.update(gameId, localModel)
            }
        } catch (exception: Exception) {
            throw PlayerGradesException("Error fetching Basketball Player Grades for game - $gameId")
        }
    }

    private suspend fun fetchHockeyPlayerGrades(gameId: String) {
        try {
            val response = playerGradesApi.getPlayerGradesForHockeyGame(gameId)
            if (response.hasErrors()) {
                throw PlayerGradesException("Error fetching Basketball Player Grades for game - $gameId")
            }
            response.data?.toLocalModel()?.let { localModel ->
                playerGradesLocalDataSource.update(gameId, localModel)
            }
        } catch (exception: Exception) {
            throw PlayerGradesException("Error fetching Basketball Player Grades for game - $gameId")
        }
    }

    private suspend fun fetchBaseballPlayerGrades(gameId: String) {
        try {
            val response = playerGradesApi.getPlayerGradesForBaseballGame(gameId)
            if (response.hasErrors()) {
                throw PlayerGradesException("Error fetching Basketball Player Grades for game - $gameId")
            }
            response.data?.toLocalModel()?.let { localModel ->
                playerGradesLocalDataSource.update(gameId, localModel)
            }
        } catch (exception: Exception) {
            throw PlayerGradesException("Error fetching Basketball Player Grades for game - $gameId")
        }
    }

    suspend fun gradePlayer(gameId: String, isHomeTeam: Boolean, playerId: String, grade: Int) = safeApiRequest {
        mapApolloGradePlayerResponseSuccess(
            gameId = gameId,
            isHomeTeam = isHomeTeam,
            fromApollo = playerGradesApi.gradePlayer(gameId, playerId, grade)
        )
    }

    suspend fun ungradePlayer(gameId: String, isHomeTeam: Boolean, playerId: String) = safeApiRequest {
        mapApolloUngradePlayerResponseSuccess(
            gameId = gameId,
            isHomeTeam = isHomeTeam,
            playerGradesApi.ungradePlayer(gameId, playerId)
        )
    }

    private fun mapApolloGradePlayerResponseSuccess(
        gameId: String,
        isHomeTeam: Boolean,
        fromApollo: ApolloResponse<GradePlayerMutation.Data>
    ): PlayerGradesLocalModel.Grading? {
        val playerGrade = fromApollo.data?.gradePlayer?.fragments?.playerGrade?.toLocalModel()
        updatePlayerGrading(gameId, isHomeTeam, playerGrade)
        return playerGrade
    }

    private fun mapApolloUngradePlayerResponseSuccess(
        gameId: String,
        isHomeTeam: Boolean,
        fromApollo: ApolloResponse<UngradePlayerMutation.Data>
    ): PlayerGradesLocalModel.Grading? {
        val playerGrade = fromApollo.data?.ungradePlayer?.fragments?.playerGrade?.toLocalModel()
        updatePlayerGrading(gameId, isHomeTeam, playerGrade)
        return playerGrade
    }

    suspend fun subscribeForPlayerGradesUpdates(gameId: String, sport: Sport) {
        when (sport) {
            Sport.FOOTBALL -> subscribeToAmericanFootballUpdates(gameId)
            Sport.SOCCER -> subscribeToSoccerUpdates(gameId)
            Sport.BASKETBALL -> subscribeToBasketballUpdates(gameId)
            Sport.HOCKEY -> subscribeToHockeyUpdates(gameId)
            Sport.BASEBALL -> subscribeToBaseballUpdates(gameId)
            else -> { /* Not Supported */
            }
        }
    }

    private suspend fun subscribeToAmericanFootballUpdates(gameId: String) {
        repositoryScope.async {
            try {
                playerGradesApi.getAmericanFootballPlayerGradesUpdatesSubscription(gameId).collect { data ->
                    data.toLocalModel()?.let { playerGradesLocalDataSource.update(gameId, it) }
                }
            } catch (exception: Throwable) {
                PlayerGradesException(
                    "Error subscribing to American Football game `$gameId` with message: ${exception.message}"
                )
            }
        }.await()
    }

    private suspend fun subscribeToSoccerUpdates(gameId: String) {
        repositoryScope.async {
            try {
                playerGradesApi.getSoccerPlayerGradesUpdatesSubscription(gameId).collect { data ->
                    data.toLocalModel()?.let { playerGradesLocalDataSource.update(gameId, it) }
                }
            } catch (exception: Throwable) {
                PlayerGradesException(
                    "Error subscribing to Soccer game `$gameId` with message: ${exception.message}"
                )
            }
        }.await()
    }

    private suspend fun subscribeToBasketballUpdates(gameId: String) {
        repositoryScope.async {
            try {
                playerGradesApi.getBasketballPlayerGradesUpdatesSubscription(gameId).collect { data ->
                    data.toLocalModel()?.let { playerGradesLocalDataSource.update(gameId, it) }
                }
            } catch (exception: Throwable) {
                PlayerGradesException(
                    "Error subscribing to Basketball game `$gameId` with message: ${exception.message}"
                )
            }
        }.await()
    }

    private suspend fun subscribeToHockeyUpdates(gameId: String) {
        repositoryScope.async {
            try {
                playerGradesApi.getHockeyPlayerGradesUpdatesSubscription(gameId).collect { data ->
                    data.toLocalModel()?.let { playerGradesLocalDataSource.update(gameId, it) }
                }
            } catch (exception: Throwable) {
                PlayerGradesException(
                    "Error subscribing to Hockey game `$gameId` with message: ${exception.message}"
                )
            }
        }.await()
    }

    private suspend fun subscribeToBaseballUpdates(gameId: String) {
        repositoryScope.async {
            try {
                playerGradesApi.getBaseballPlayerGradesUpdatesSubscription(gameId).collect { data ->
                    data.toLocalModel()?.let { playerGradesLocalDataSource.update(gameId, it) }
                }
            } catch (exception: Throwable) {
                PlayerGradesException(
                    "Error subscribing to Baseball game `$gameId` with message: ${exception.message}"
                )
            }
        }.await()
    }

    private fun updatePlayerGrading(gameId: String, isHomeTeam: Boolean, playerGrade: PlayerGradesLocalModel.Grading?) {
        repositoryScope.launch {
            safeLet(playerGrade, localDataSource.observeItem(gameId).firstOrNull()) { safeGrade, currentModel ->

                var homeTeam = currentModel.homeTeam
                var awayTeam = currentModel.awayTeam
                if (isHomeTeam) {
                    homeTeam?.players?.toMutableList()?.let { players ->
                        val index = players.indexOfFirst { it.playerId == safeGrade.playerId }
                        if (index != -1) {
                            val player = players[index]
                            players[index] = player.copy(grading = safeGrade.copy(order = player.grading?.order ?: 0))
                            homeTeam = homeTeam?.copy(players = players)
                        }
                    }
                } else {
                    awayTeam?.players?.toMutableList()?.let { players ->
                        val index = players.indexOfFirst { it.playerId == safeGrade.playerId }
                        if (index != -1) {
                            val player = players[index]
                            players[index] = player.copy(grading = safeGrade.copy(order = player.grading?.order ?: 0))
                            awayTeam = awayTeam?.copy(players = players)
                        }
                    }
                }
                localDataSource.update(
                    gameId,
                    currentModel.copy(
                        homeTeam = homeTeam,
                        awayTeam = awayTeam
                    )
                )
            }
        }
    }
}