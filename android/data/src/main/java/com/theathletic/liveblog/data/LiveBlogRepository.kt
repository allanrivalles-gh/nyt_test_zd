package com.theathletic.liveblog.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.liveblog.data.local.LiveBlogLocalStorage
import com.theathletic.liveblog.data.local.NativeLiveBlog
import com.theathletic.liveblog.data.remote.LiveBlogApi
import com.theathletic.liveblog.data.remote.LiveBlogFetcher
import com.theathletic.liveblog.data.remote.LiveBlogPostSubscriber
import com.theathletic.liveblog.data.remote.LiveBlogPostsFetcher
import com.theathletic.liveblog.data.remote.toLocal
import com.theathletic.repository.CoroutineRepository
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LiveBlogRepository @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val liveBlogFetcher: LiveBlogFetcher,
    private val liveBlogPostsFetcher: LiveBlogPostsFetcher,
    private val liveBlogPostSubscriber: LiveBlogPostSubscriber,
    private val liveBlogLocalStorage: LiveBlogLocalStorage,
    private val liveBlogApi: LiveBlogApi
) : CoroutineRepository {

    override val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    fun getLiveBlogFlow(liveBlogId: String): Flow<NativeLiveBlog?> = liveBlogLocalStorage.observeItem(liveBlogId)

    fun fetchLiveBlog(liveBlogId: String) = repositoryScope.launch {
        liveBlogFetcher.fetchRemote(LiveBlogFetcher.Params(liveBlogId = liveBlogId))
    }

    fun fetchLiveBlogPosts(liveBlogId: String, page: Int, includeAds: Boolean) = repositoryScope.launch {
        liveBlogPostsFetcher.fetchRemote(
            LiveBlogPostsFetcher.Params(
                liveBlogId = liveBlogId,
                page = page,
                includeAds = includeAds
            )
        )
    }

    suspend fun getLiveBlog(liveBlogId: String): NativeLiveBlog? {
        return liveBlogApi.getLiveBlog(liveBlogId).data?.liveBlog?.fragments?.liveBlogFragment?.toLocal()
    }

    suspend fun subscribeToLiveBlogPosts(liveBlogId: String) {
        liveBlogPostSubscriber.subscribe(LiveBlogPostSubscriber.Params(liveBlogId = liveBlogId))
    }
}