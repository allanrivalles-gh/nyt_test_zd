package com.theathletic.news.repository

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.news.NewsBackgroundReading
import com.theathletic.news.NewsRelatedArticle
import com.theathletic.news.NewsRelatedDiscussion
import com.theathletic.news.NewsRelatedPodcastEpisode
import com.theathletic.podcast.data.local.PodcastDao
import com.theathletic.repository.CoroutineRepository
import com.theathletic.repository.Repository
import com.theathletic.repository.safeApiRequest
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class NewsRepository @AutoKoin(Scope.SINGLE) constructor(
    private val newsApi: NewsApi,
    dispatcherProvider: DispatcherProvider,
    private val podcastDao: PodcastDao,
    private val entityDataSource: EntityDataSource
) : Repository, KoinComponent, CoroutineRepository {

    override val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    suspend fun getNewsById(id: String, isRefreshing: Boolean) = safeApiRequest {
        val news = mapApolloNewsByIdResponse(newsApi.getNewsByIdAsync(id, isRefreshing))
        val articles = news?.content?.filterIsInstance<NewsBackgroundReading>()
        articles?.let {
            saveArticle(articles)
        }
        val relatedArticle = news?.content?.filterIsInstance<NewsRelatedArticle>()
        relatedArticle?.let {
            saveRelatedArticle(relatedArticle)
        }
        val podcasts = news?.content?.filterIsInstance<NewsRelatedPodcastEpisode>()
        podcasts?.let {
            savePodcastEpisode(podcasts)
        }
        val discussions = news?.content?.filterIsInstance<NewsRelatedDiscussion>()
        discussions?.let {
            saveDiscussion(discussions)
        }
        news
    }

    suspend fun getHeadlineCommentCount(id: String) = safeApiRequest {
        newsApi.getHeadlinesCommentCount(id).toLocalModel()
    }

    private fun saveArticle(articles: List<NewsBackgroundReading>) = repositoryScope.launch {
        entityDataSource.insert(articles.map { it.article })
    }

    private fun saveRelatedArticle(articles: List<NewsRelatedArticle>) = repositoryScope.launch {
        entityDataSource.insert(articles.map { it.article })
    }

    private fun savePodcastEpisode(podcastList: List<NewsRelatedPodcastEpisode>) = repositoryScope.launch {
        podcastDao.insertPodcastEpisodesTransaction(
            podcastList.map { it.podcastEpisode }
        )
    }

    private fun saveDiscussion(discussions: List<NewsRelatedDiscussion>) = repositoryScope.launch {
        entityDataSource.insert(discussions.map { it.discussion })
    }
}