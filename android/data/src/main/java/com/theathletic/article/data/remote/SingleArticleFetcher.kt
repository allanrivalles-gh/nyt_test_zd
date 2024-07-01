package com.theathletic.article.data.remote

import com.theathletic.ArticleCommentsQuery
import com.theathletic.ArticleQuery
import com.theathletic.ArticleRelatedContentQuery
import com.theathletic.ads.AdConfigClient
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.entity.remote.toLocalModel
import com.theathletic.featureswitch.Features
import com.theathletic.feed.data.remote.ArticleGraphqlApi
import com.theathletic.utility.coroutines.DispatcherProvider
import com.theathletic.utility.logging.ICrashLogHandler
import kotlinx.coroutines.coroutineScope

@Suppress("LongParameterList")
class SingleArticleFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val articleGraphqlApi: ArticleGraphqlApi,
    private val entityDataSource: EntityDataSource,
    private val crashLogHandler: ICrashLogHandler,
    private val features: Features,
    private val adConfigClient: AdConfigClient
) : RemoteToLocalFetcher<
    SingleArticleFetcher.Params,
    SingleArticleFetcher.ArticleDataWithExtensions,
    ArticleEntity>(dispatcherProvider) {

    data class Params(
        val id: Long,

        /**
         * Also make network request to get the featured author and the related stories
         */
        val fetchExtensions: Boolean = false,
        val useCachedArticleDontFetch: Boolean = false,
    )

    data class ArticleDataWithExtensions(
        val cachedArticle: ArticleEntity?,
        val articleData: ArticleQuery.Data?,
        val articleComments: ArticleCommentsQuery.Data?,
        val articleExtension: ArticleRelatedContentQuery.Data?,
    )

    data class RelatedContentTags(
        val teamIds: List<String>,
        val leagueIds: List<String>,
        val authorIds: List<String>,
        val excludeId: String
    ) {
        val isEmpty: Boolean
            get() = teamIds.isEmpty() && leagueIds.isEmpty() && authorIds.isEmpty()
    }

    private fun ArticleQuery.Data.toRelatedContentTags(): RelatedContentTags? {
        return this.articleById?.let {
            val teamIds = it.team_ids?.filterNotNull() ?: emptyList()
            val leagueIds = it.league_ids?.filterNotNull() ?: emptyList()
            val authorIds = it.authors.map { author -> author.id }
            RelatedContentTags(teamIds, leagueIds, authorIds, it.id)
        }
    }

    private suspend fun fetchRelatedContent(article: ArticleQuery.Data): ArticleRelatedContentQuery.Data? {
        return article.toRelatedContentTags()?.let {
            if (it.isEmpty.not()) {
                try {
                    articleGraphqlApi.getArticleRelatedContent(
                        it.teamIds,
                        it.leagueIds,
                        it.authorIds,
                        it.excludeId
                    ).data
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }
    }

    private suspend fun fetchArticle(id: String, adsEnabled: Boolean) = try {
        articleGraphqlApi.getArticle(id, adConfigClient.platform, adConfigClient.property, adsEnabled).data
    } catch (e: Exception) {
        null
    }

    private suspend fun fetchComments(id: String) = try {
        articleGraphqlApi.getArticleComments(id).data
    } catch (e: Exception) {
        null
    }

    override suspend fun makeRemoteRequest(params: Params): ArticleDataWithExtensions =
        coroutineScope {
            val cachedArticle: ArticleEntity? = if (params.useCachedArticleDontFetch) {
                entityDataSource.get(params.id.toString())
            } else {
                null
            }

            val article = if (params.useCachedArticleDontFetch) {
                null
            } else {
                fetchArticle(params.id.toString(), features.isArticleAdsEnabled)
            }

            val comments = fetchComments(params.id.toString())

            val extension = when {
                article != null && params.fetchExtensions -> {
                    fetchRelatedContent(article)
                }
                else -> null
            }

            ArticleDataWithExtensions(
                cachedArticle = cachedArticle,
                articleData = article,
                articleComments = comments,
                articleExtension = extension
            )
        }

    override fun mapToLocalModel(
        params: Params,
        remoteModel: ArticleDataWithExtensions
    ): ArticleEntity {
        return remoteModel.toLocalModel()
    }

    override suspend fun saveLocally(params: Params, dbModel: ArticleEntity) {
        entityDataSource.insertOrUpdate(dbModel)
    }

    override fun logFetchRemoteException(t: Throwable) {
        super.logFetchRemoteException(t)
        crashLogHandler.trackException(t, cause = "SINGLE_ARTICLE_FETCHER_EXCEPTION")
    }
}