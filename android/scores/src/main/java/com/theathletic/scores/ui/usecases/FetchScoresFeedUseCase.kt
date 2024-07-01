package com.theathletic.scores.ui.usecases

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.scores.data.ScoresFeedRepository
import timber.log.Timber

class FetchScoresFeedUseCase @AutoKoin constructor(
    private val scoresFeedRepository: ScoresFeedRepository
) {
    suspend operator fun invoke(currentFeedIdentifier: String): Result<Unit> {
        return try {
            scoresFeedRepository.fetchScoresFeed(currentFeedIdentifier)
            Result.success(Unit)
        } catch (error: ScoresFeedRepository.ScoresFeedException) {
            Timber.e(error)
            Result.failure(error)
        }
    }
}