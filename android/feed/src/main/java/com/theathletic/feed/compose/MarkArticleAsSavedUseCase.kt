package com.theathletic.feed.compose

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.article.data.ArticleRepository
import timber.log.Timber

class MarkArticleAsSavedUseCase @AutoKoin constructor(
    private val articleRepository: ArticleRepository
) {
    operator fun invoke(articleId: Long, isSaved: Boolean): Result<Unit> {
        return try {
            articleRepository.markArticleBookmarked(articleId, isSaved)
            Result.success(Unit)
        } catch (error: Throwable) {
            Timber.e(error)
            Result.failure(error)
        }
    }
}