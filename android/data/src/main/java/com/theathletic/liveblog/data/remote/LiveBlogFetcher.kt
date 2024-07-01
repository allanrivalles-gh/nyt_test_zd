package com.theathletic.liveblog.data.remote

import com.theathletic.LiveBlogQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.liveblog.data.local.LiveBlogLocalStorage
import com.theathletic.liveblog.data.local.NativeLiveBlog
import com.theathletic.utility.coroutines.DispatcherProvider

class LiveBlogFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val liveBlogApi: LiveBlogApi,
    private val liveBlogLocalStorage: LiveBlogLocalStorage
) : RemoteToLocalFetcher<
    LiveBlogFetcher.Params,
    LiveBlogQuery.Data,
    NativeLiveBlog
    >(dispatcherProvider) {

    data class Params(
        val liveBlogId: String,
        val includeAds: Boolean = true
    )

    override suspend fun makeRemoteRequest(params: Params) = liveBlogApi.getLiveBlog(id = params.liveBlogId, includeAds = params.includeAds).data

    override fun mapToLocalModel(
        params: Params,
        remoteModel: LiveBlogQuery.Data
    ) = remoteModel.liveBlog.fragments.liveBlogFragment.toLocal()

    override suspend fun saveLocally(params: Params, dbModel: NativeLiveBlog) {
        liveBlogLocalStorage.update(params.liveBlogId, dbModel)
    }
}