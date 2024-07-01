package com.theathletic.liveblog.data.remote

import com.theathletic.LiveBlogPostsQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.liveblog.data.local.LiveBlogLocalStorage
import com.theathletic.liveblog.data.local.NativeLiveBlogPost
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.flow.firstOrNull

class LiveBlogPostsFetcher @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider,
    private val liveBlogApi: LiveBlogApi,
    private val liveBlogLocalStorage: LiveBlogLocalStorage
) : RemoteToLocalFetcher<
    LiveBlogPostsFetcher.Params,
    LiveBlogPostsQuery.Data,
    LiveBlogPostsFetcher.LocalModel
    >(dispatcherProvider) {

    data class Params(
        val liveBlogId: String,
        val page: Int,
        val includeAds: Boolean = true
    )

    data class LocalModel(
        val currentPage: Int,
        val hasNextPage: Boolean,
        val liveBlogPosts: List<NativeLiveBlogPost>
    )

    override suspend fun makeRemoteRequest(params: Params) = liveBlogApi.getLiveBlogPosts(
        id = params.liveBlogId,
        page = params.page,
        includeAds = params.includeAds
    ).data

    override fun mapToLocalModel(
        params: Params,
        remoteModel: LiveBlogPostsQuery.Data
    ) = LocalModel(
        currentPage = remoteModel.liveBlog.posts.pageInfo.currentPage,
        hasNextPage = remoteModel.liveBlog.posts.pageInfo.hasNextPage,
        liveBlogPosts = remoteModel.liveBlog.posts.items.mapNotNull { it?.toLocal() }
    )

    override suspend fun saveLocally(params: Params, dbModel: LocalModel) {
        liveBlogLocalStorage.observeItem(params.liveBlogId).firstOrNull()?.let { liveBlog ->
            val updatedLiveBlog = liveBlog.copy(
                posts = (liveBlog.posts + dbModel.liveBlogPosts).distinctBy { it.id },
                currentPage = dbModel.currentPage,
                hasNextPage = dbModel.hasNextPage
            )
            liveBlogLocalStorage.update(params.liveBlogId, updatedLiveBlog)
        }
    }

    private fun LiveBlogPostsQuery.Item.toLocal(): NativeLiveBlogPost? {
        this.asLiveBlogDropzone?.let { return it.fragments.liveBlogDropzone.toLocal() }
        return this.asLiveBlogPost?.fragments?.liveBlogPostFragment?.toLocal()
    }
}