package com.theathletic.article.data.remote

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.EmptyParams
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.entity.local.EntityQueries
import com.theathletic.feed.data.remote.ArticleGraphqlApi
import com.theathletic.fragment.SavedArticle
import com.theathletic.utility.coroutines.DispatcherProvider

class SavedStoriesFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val articleApi: ArticleGraphqlApi,
    private val entityDataSource: EntityDataSource,
    private val entityQueries: EntityQueries
) : RemoteToLocalFetcher<
    EmptyParams,
    List<SavedArticle>,
    List<ArticleEntity>>(dispatcherProvider) {

    override suspend fun makeRemoteRequest(
        params: EmptyParams
    ) = articleApi.getSavedStories().data?.userArticles?.map {
        it.fragments.savedArticle
    } ?: emptyList()

    override fun mapToLocalModel(
        params: EmptyParams,
        remoteModel: List<SavedArticle>
    ) = remoteModel.map { it.toArticleEntity() }

    override suspend fun saveLocally(params: EmptyParams, dbModel: List<ArticleEntity>) {
        entityDataSource.insertOrUpdate(dbModel)
        entityQueries.replaceSavedByType(AthleticEntity.Type.ARTICLE, dbModel)
    }
}