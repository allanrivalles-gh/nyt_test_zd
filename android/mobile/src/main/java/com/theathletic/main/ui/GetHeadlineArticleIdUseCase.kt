package com.theathletic.main.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.article.data.ArticleRepository
import timber.log.Timber

class GetHeadlineArticleIdUseCase @AutoKoin constructor(
    val articleRepository: ArticleRepository
) {
    suspend operator fun invoke(
        headlineId: String
    ) = try {
        val id = articleRepository.getHeadlineArticleId(headlineId)
        if (id.isNullOrBlank()) {
            throw Exception("Id is null or blank for $headlineId")
        }
        Result.success(id)
    } catch (e: Throwable) {
        Timber.e(e, "Error on fetching article id for $headlineId")
        Result.failure(e)
    }
}