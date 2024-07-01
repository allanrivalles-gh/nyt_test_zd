package com.theathletic.hub.game.di

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.hub.game.data.GameHubRepository
import timber.log.Timber

class FetchGameSummaryUseCase @AutoKoin constructor(
    private val repository: GameHubRepository
) {
    suspend operator fun invoke(gameId: String): Result<Unit> {
        return try {
            repository.fetchGameSummary(gameId)
            Result.success(Unit)
        } catch (error: Throwable) {
            Timber.e(error)
            Result.failure(error)
        }
    }
}