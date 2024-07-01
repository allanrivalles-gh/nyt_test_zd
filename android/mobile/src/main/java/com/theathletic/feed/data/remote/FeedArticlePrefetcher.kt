package com.theathletic.feed.data.remote

import com.theathletic.AthleticConfig
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.article.data.ArticleRepository
import com.theathletic.article.data.remote.SingleArticleFetcher
import com.theathletic.datetime.DateUtility
import com.theathletic.datetime.TimeProvider
import com.theathletic.entity.EntityState
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.utility.INetworKManager
import com.theathletic.utility.coroutines.DispatcherProvider
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Used to prefetch articles in a user's feed while on wifi. We keep an in memory cache of the
 * articles that we have already tried prefetching to avoid double-fetching as this is called from
 * multiple presenters at once.
 */
@Suppress("LongParameterList")
class FeedArticlePrefetcher @AutoKoin(Scope.SINGLE) constructor(
    private val articleRepository: ArticleRepository,
    private val articleFetcher: SingleArticleFetcher,
    private val networkManager: INetworKManager,
    private val dateUtility: DateUtility,
    private val timeProvider: TimeProvider,
    dispatcherProvider: DispatcherProvider
) {

    private val scope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    /**
     * This is an in memory cache to avoid fetching the same article twice as two feeds could
     * have the same article in them at the same time.
     */
    private val alreadyChecked = mutableSetOf<Long>()

    fun prefetch(articles: List<ArticleEntity>) {
        if (!networkManager.isOnline() || networkManager.isOnMobileData()) {
            Timber.v("Not on wifi, do not prefetch articles.")
            return
        }

        val articlesToCheck = filterOldArticles(articles)
        startPrefetchLoop(articlesToCheck)
    }

    private fun filterOldArticles(articles: List<ArticleEntity>): List<ArticleEntity> {
        val compareTime = timeProvider.currentTimeMs - TimeUnit.DAYS.toMillis(AthleticConfig.ARTICLE_CACHE_DAYS)

        val idsToCheck = articles
            .filterNot { alreadyChecked.contains(it.articleId) }
            .filter { dateUtility.parseDateFromGMT(it.articlePublishDate.orEmpty()).time > compareTime }
            .distinctBy { it.id }

        alreadyChecked.addAll(articles.map { it.articleId })

        return idsToCheck
    }

    private fun startPrefetchLoop(articles: List<ArticleEntity>) {
        scope.launch {
            val articlesToFetch = articles.mapNotNull { article ->
                val cachedArticle = articleRepository.getArticle(article.articleId)

                when {
                    cachedArticle == null || cachedArticle.state < EntityState.DETAIL -> {
                        Timber.v("Article ${article.id} not cached. Fetching for cache.")
                        article.articleId
                    }
                    else -> {
                        Timber.v("Article ${article.id} already cached.")
                        null
                    }
                }
            }

            for (id in articlesToFetch) {
                delay(TimeUnit.SECONDS.toMillis(1L))
                Timber.v("Fetching article $id")
                articleFetcher.fetchRemote(SingleArticleFetcher.Params(id = id))
            }
        }
    }

    private val Long?.orDefault: Long get() = this ?: -1
}