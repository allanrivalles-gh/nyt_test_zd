package com.theathletic.feed.compose

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.feed.compose.data.FeedRepository
import com.theathletic.feed.compose.data.FeedRequest
import timber.log.Timber

internal class FetchFeedUseCase @AutoKoin constructor(
    private val feedRepository: FeedRepository
) {
    suspend operator fun invoke(feedRequest: FeedRequest, page: Int): Result<Unit> {
        return try {
            feedRepository.fetchFeed(feedRequest, page = page)
            Result.success(Unit)
        } catch (error: Throwable) {
            Timber.e(error)
            Result.failure(error)
        }
    }
}