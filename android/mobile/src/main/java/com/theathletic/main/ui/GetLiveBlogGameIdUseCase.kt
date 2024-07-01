package com.theathletic.main.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.liveblog.data.LiveBlogRepository
import timber.log.Timber

class GetLiveBlogGameIdUseCase @AutoKoin constructor(
    private val liveBlogRepository: LiveBlogRepository
) {
    suspend operator fun invoke(
        liveBlogId: String
    ) = try {
        val liveBlog = liveBlogRepository.getLiveBlog(liveBlogId)
        val isGame = liveBlog?.tags?.any {
            it.type == "game"
        } ?: false
        val liveBlogGameId = liveBlog?.gameId.takeIf { isGame }
        Result.success(liveBlogGameId)
    } catch (e: Throwable) {
        Timber.e(e, "Error on fetching live blog id $liveBlogId")
        Result.failure(e)
    }
}