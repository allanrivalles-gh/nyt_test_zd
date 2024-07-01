package com.theathletic.article

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.article.data.ArticleRepository
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.remoteconfig.RemoteConfigRepository
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.user.IUserManager
import kotlinx.coroutines.flow.first

class ArticleHasPaywallUseCase @AutoKoin(Scope.SINGLE) constructor(
    private val repository: ArticleRepository,
    private val userDataRepository: IUserDataRepository,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val userManager: IUserManager,
    private val freeArticleTrackerRepository: FreeArticleTrackerRepository,
) {
    suspend operator fun invoke(articleId: Long, article: ArticleEntity? = null): Boolean {
        val article = article ?: repository.getArticle(articleId) ?: return false

        if (userManager.isUserSubscribed()) return false

        if (userDataRepository.isItemRead(articleId)) return false
        if (article.isTeaser) return false

        if (!userManager.isUserLoggedIn()) return true

        article.subscriberScore?.let {
            if (it >= remoteConfigRepository.articleSubscriberScoreThreshold.first()) return true
        }

        val freeArticleTracker = freeArticleTrackerRepository.getTracker()
        return !freeArticleTracker.isArticleFree(articleId, remoteConfigRepository.freeArticlesPerMonthCount.first())
    }
}