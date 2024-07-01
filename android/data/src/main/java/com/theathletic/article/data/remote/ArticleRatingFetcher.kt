package com.theathletic.article.data.remote

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.user.IUserManager
import com.theathletic.utility.coroutines.DispatcherProvider

class ArticleRatingFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val articleRestApi: ArticleRestApi,
    private val userManager: IUserManager,
    private val userDataRepository: IUserDataRepository
) : RemoteToLocalFetcher<ArticleRatingFetcher.Params, Unit, Unit>(dispatcherProvider) {

    data class Params(
        val articleId: Long,
        val articleRating: Long
    )

    override suspend fun makeRemoteRequest(params: Params): Unit? {
        return articleRestApi.setArticleRated(
            articleId = params.articleId,
            ratingId = params.articleRating
        ).body()
    }

    override fun mapToLocalModel(params: Params, remoteModel: Unit) = Unit

    override suspend fun saveLocally(params: Params, dbModel: Unit) {
        userManager.addArticleRating(params.articleId, params.articleRating)
        userDataRepository.markItemRated(params.articleId, true)
    }
}