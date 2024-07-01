package com.theathletic.main.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.article.data.ArticleRepository
import com.theathletic.article.data.TagFeed
import timber.log.Timber

class SlugToTagFeedUseCase @AutoKoin constructor(
    val articleRepository: ArticleRepository
) {
    suspend operator fun invoke(
        slugId: String
    ) = try {
        val response = articleRepository.getTagFeedFromSlug(slugId)
        val id = response?.id?.toLongOrNull() ?: throw Exception("Id is null or blank for $slugId")
        Result.success(TagFeed(id, response.title))
    } catch (e: Throwable) {
        Timber.e(e, "Error on fetching article id for $slugId")
        Result.failure(e)
    }
}