package com.theathletic.boxscore

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.data.BoxScoreRepository
import timber.log.Timber

class FetchBoxScoreFeedUseCase @AutoKoin constructor(
    private val boxScoreRepository: BoxScoreRepository
) {
    suspend operator fun invoke(gameId: String): Result<Unit> {
        return try {
            boxScoreRepository.fetchBoxScoreFeed(gameId)
            Result.success(Unit)
        } catch (error: Throwable) {
            Timber.e(error)
            Result.failure(error)
        }
    }
}