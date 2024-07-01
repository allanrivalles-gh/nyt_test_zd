package com.theathletic.article.data.remote

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.entity.local.EntityQueries
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.rx2.awaitSingleOrNull

class BookmarkArticleRequest @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val articleRestApi: ArticleRestApi,
    private val entityQueries: EntityQueries,
    private val userDataRepository: IUserDataRepository
) : RemoteToLocalFetcher<
    BookmarkArticleRequest.Params,
    Long,
    Long
    >(dispatcherProvider) {

    data class Params(
        val articleId: Long,
        val bookmark: Boolean
    )

    override suspend fun makeRemoteRequest(params: Params) = when {
        params.bookmark -> articleRestApi.saveStory(params.articleId)
        else -> articleRestApi.removeStory(params.articleId)
    }.awaitSingleOrNull()?.body() ?: 0L

    override fun mapToLocalModel(params: Params, networkModel: Long) = networkModel

    override suspend fun saveLocally(params: Params, dbModel: Long) {
        userDataRepository.markItemBookmarked(params.articleId, params.bookmark).get()

        val entityId = AthleticEntity.Id(
            id = params.articleId.toString(),
            type = AthleticEntity.Type.ARTICLE
        )

        when (params.bookmark) {
            true -> entityQueries.addSaved(entityId)
            false -> entityQueries.removeSaved(entityId)
        }
    }
}