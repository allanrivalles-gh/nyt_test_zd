package com.theathletic.gamedetail.data.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.local.InMemoryLocalDataSource
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PlayerGradesLocalDataSource @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val latestUpdatesMapper: PlayerGradesLatestUpdatesMapper,
) : InMemoryLocalDataSource<String, PlayerGradesLocalModel>() {

    val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    override fun update(key: String, data: PlayerGradesLocalModel) {
        repositoryScope.launch {
            super.update(
                key = key,
                data = latestUpdatesMapper.map(
                    originalModel = observeItem(key).firstOrNull(),
                    updatedModel = data
                )
            )
        }
    }
}

class PlayerGradesLatestUpdatesMapper @AutoKoin(Scope.SINGLE) constructor() {

    fun map(
        originalModel: PlayerGradesLocalModel?,
        updatedModel: PlayerGradesLocalModel
    ): PlayerGradesLocalModel {

        originalModel ?: return updatedModel

        val updatedAwayTeamPlayers = updatedModel.awayTeam?.copy(
            players = updatePlayerList(
                originalModel.awayTeam?.players,
                updatedModel.awayTeam.players
            )
        )

        val updatedHomeTeamPlayers = updatedModel.homeTeam?.copy(
            players = updatePlayerList(
                originalModel.homeTeam?.players,
                updatedModel.homeTeam.players
            )
        )

        return updatedModel.copy(
            awayTeam = updatedAwayTeamPlayers,
            homeTeam = updatedHomeTeamPlayers
        )
    }

    private fun updatePlayerList(
        originalPlayerList: List<PlayerGradesLocalModel.Player>?,
        updatedPlayerList: List<PlayerGradesLocalModel.Player>?
    ): List<PlayerGradesLocalModel.Player> {
        if (originalPlayerList == null) return updatedPlayerList ?: emptyList()
        if (updatedPlayerList == null) return originalPlayerList

        val originalPlayerMap = originalPlayerList.associateBy { it.playerId }
        return updatedPlayerList.map { updatedPlayer ->
            val originalPlayer = originalPlayerMap[updatedPlayer.playerId]
            when {
                originalPlayer == null -> updatedPlayer

                originalPlayer.updatedAtGradingTimeMillis >
                    updatedPlayer.updatedAtGradingTimeMillis -> originalPlayer

                else -> updatedPlayer
            }
        }
    }

    private val PlayerGradesLocalModel.Player?.updatedAtGradingTimeMillis
        get() = this?.grading?.updatedAt?.timeMillis ?: 0
}