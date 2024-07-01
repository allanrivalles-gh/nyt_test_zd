package com.theathletic.boxscore.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.data.local.BoxScoreLocalDataSource
import com.theathletic.boxscore.data.remote.BoxScoreApi

class BoxScoreRepository @AutoKoin constructor(
    private val boxScoreApi: BoxScoreApi,
    private val boxScoreLocalDataSource: BoxScoreLocalDataSource
) {
    suspend fun fetchBoxScoreFeed(gameId: String) {
        boxScoreApi.getBoxScoreFeed(gameId)?.let { data ->
            val boxScore = data.toDomain()
            boxScoreLocalDataSource.update(gameId, boxScore)
        }
    }

    fun getBoxScoreFeed(gameId: String) = boxScoreLocalDataSource.observeItem(gameId)
}