package com.theathletic.article.data

import com.theathletic.SlugToTopicQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.article.data.remote.ArticleApi
import com.theathletic.article.data.remote.ArticleRatingFetcher
import com.theathletic.article.data.remote.ArticleReadFetcher
import com.theathletic.article.data.remote.BookmarkArticleRequest
import com.theathletic.article.data.remote.SavedStoriesFetcher
import com.theathletic.article.data.remote.SingleArticleFetcher
import com.theathletic.data.EmptyParams
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.entity.local.EntityQueries
import com.theathletic.repository.CoroutineRepository
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ArticleRepository @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider,
    private val articleFetcher: SingleArticleFetcher,
    private val savedStoryFetcher: SavedStoriesFetcher,
    private val entityDataSource: EntityDataSource,
    private val entityQueries: EntityQueries,
    private val bookmarkArticleRequest: BookmarkArticleRequest,
    private val userDataRepository: IUserDataRepository,
    private val articleRatingFetcher: ArticleRatingFetcher,
    private val articleReadFetcher: ArticleReadFetcher,
    private val articleApi: ArticleApi
) : CoroutineRepository {

    override val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    suspend fun getArticle(id: Long, forceRefresh: Boolean = false): ArticleEntity? {
        if (forceRefresh) {
            fetchArticle(id)
        }

        return entityDataSource.get(id.toString())
    }

    suspend fun getHeadlineArticleId(id: String): String? {
        return articleApi.getHeadlineArticleId(id)?.articleById?.id
    }

    suspend fun fetchArticle(id: Long) {
        articleFetcher.fetchRemote(
            SingleArticleFetcher.Params(id, fetchExtensions = true)
        )
    }

    suspend fun fetchArticleComments(id: Long) {
        articleFetcher.fetchRemote(
            SingleArticleFetcher.Params(id, fetchExtensions = false, useCachedArticleDontFetch = true)
        )
    }

    fun getArticleFlow(
        id: Long,
        networkFetch: Boolean = false
    ): Flow<ArticleEntity?> {
        if (networkFetch) {
            repositoryScope.launch {
                articleFetcher.fetchRemote(SingleArticleFetcher.Params(id, fetchExtensions = true))
            }
        }
        return entityDataSource.getFlow(id.toString())
    }

    suspend fun getTagFeedFromSlug(slug: String): SlugToTopicQuery.SlugToTopic? {
        return articleApi.getIdFromSlug(slug)?.slugToTopic
    }

    fun markArticleBookmarked(
        articleId: Long,
        isBookmarked: Boolean
    ) = repositoryScope.launch {
        if (isBookmarked) {
            articleFetcher.fetchRemote(
                SingleArticleFetcher.Params(id = articleId)
            )
        }
        bookmarkArticleRequest.fetchRemote(BookmarkArticleRequest.Params(articleId, isBookmarked))
    }

    fun isArticleBookmarked(articleId: Long) = userDataRepository.isItemBookmarked(articleId)

    fun isArticleRead(articleId: Long) = userDataRepository.isItemRead(articleId)

    fun getSavedStoriesFlow() = entityQueries.getSavedFlow(AthleticEntity.Type.ARTICLE)

    fun fetchSavedStories() = repositoryScope.launch {
        savedStoryFetcher.fetchRemote(EmptyParams)
    }

    fun deleteAllBookmarked() = repositoryScope.launch {
        val savedArticles = entityQueries.getSavedFlow(AthleticEntity.Type.ARTICLE).first()
        entityQueries.removeSavedByType(AthleticEntity.Type.ARTICLE)

        for (article in savedArticles) {
            bookmarkArticleRequest.fetchRemote(
                BookmarkArticleRequest.Params(article.id.toLong(), false)
            )
        }
    }

    fun rateArticle(articleId: Long, rating: Long) = repositoryScope.launch {
        articleRatingFetcher.fetchRemote(ArticleRatingFetcher.Params(articleId, rating))
    }

    fun saveArticleLastScrollPercentage(articleId: Long, articleLastScrollPercentage: Int) = repositoryScope.launch {
        entityDataSource.update<ArticleEntity>(articleId.toString()) {
            copy(lastScrollPercentage = articleLastScrollPercentage)
        }
    }

    suspend fun markArticleRead(articleId: Long, isRead: Boolean) {
        articleReadFetcher.fetchRemote(ArticleReadFetcher.Params(articleId, isRead))
    }
}