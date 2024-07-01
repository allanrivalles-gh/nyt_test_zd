package com.theathletic.article.data.remote

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.utility.coroutines.DispatcherProvider

class ArticleReadFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val articleRestApi: ArticleRestApi,
    private val userDataRepository: IUserDataRepository
) : RemoteToLocalFetcher<
    ArticleReadFetcher.Params,
    Unit,
    ArticleReadFetcher.Params
    >(dispatcherProvider) {

    data class Params(
        val articleId: Long,
        val isRead: Boolean
    )

    override suspend fun makeRemoteRequest(params: Params) = articleRestApi.setArticleRead(
        params.articleId,
        params.isRead
    )

    override fun mapToLocalModel(params: Params, remoteModel: Unit) = params

    override suspend fun saveLocally(params: Params, dbModel: Params) {
        userDataRepository.markItemRead(
            params.articleId,
            params.isRead
        )
    }
}