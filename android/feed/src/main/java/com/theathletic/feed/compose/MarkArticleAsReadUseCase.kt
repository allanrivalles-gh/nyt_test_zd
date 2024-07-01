package com.theathletic.feed.compose

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.article.data.ArticleRepository
import timber.log.Timber

class MarkArticleAsReadUseCase @AutoKoin constructor(
    private val articleRepository: ArticleRepository
) {
    suspend operator fun invoke(articleId: Long, isRead: Boolean): Result<Unit> {
        return try {
            articleRepository.markArticleRead(articleId, isRead)
            Result.success(Unit)
        } catch (error: Throwable) {
            Timber.e(error)
            Result.failure(error)
        }
    }
}