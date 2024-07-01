package com.theathletic.feed.data.remote

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.author.data.AuthorDetailResponse
import com.theathletic.author.data.remote.AuthorApi
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.feed.data.local.AuthorDetailLocalDataSource
import com.theathletic.feed.data.local.AuthorDetails
import com.theathletic.utility.coroutines.DispatcherProvider

class AuthorDetailFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val authorApi: AuthorApi,
    private val authorDetailLocalDataSource: AuthorDetailLocalDataSource
) : RemoteToLocalFetcher<
    AuthorDetailFetcher.Params,
    AuthorDetailResponse,
    AuthorDetails
    >(dispatcherProvider) {

    data class Params(
        val authorId: Long
    )

    override suspend fun makeRemoteRequest(params: Params): AuthorDetailResponse? {
        return authorApi.getAuthorDetail(params.authorId).body()
    }

    override fun mapToLocalModel(params: Params, remoteModel: AuthorDetailResponse): AuthorDetails {
        val author = remoteModel.author
        return AuthorDetails(
            id = author.id,
            name = author.displayName,
            imageUrl = author.featuredPhoto,
            description = author.description,
            twitterHandle = author.twitter
        )
    }

    override suspend fun saveLocally(params: Params, dbModel: AuthorDetails) {
        authorDetailLocalDataSource.update(
            params.authorId,
            dbModel
        )
    }
}