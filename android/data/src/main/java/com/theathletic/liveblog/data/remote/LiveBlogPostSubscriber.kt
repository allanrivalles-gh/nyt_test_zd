package com.theathletic.liveblog.data.remote

import com.theathletic.PublishedPostToLiveBlogSubscription
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalSubscriber
import com.theathletic.liveblog.data.local.LiveBlogLocalStorage
import com.theathletic.liveblog.data.local.NativeLiveBlogPost
import com.theathletic.liveblog.data.local.NativeLiveBlogPostBasic
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class LiveBlogPostSubscriber @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val liveBlogApi: LiveBlogApi,
    private val liveBlogLocalStorage: LiveBlogLocalStorage
) : RemoteToLocalSubscriber<
    LiveBlogPostSubscriber.Params,
    PublishedPostToLiveBlogSubscription.Data,
    NativeLiveBlogPost
    >(dispatcherProvider) {

    data class Params(
        val liveBlogId: String
    )

    override suspend fun makeRemoteRequest(params: Params): Flow<PublishedPostToLiveBlogSubscription.Data> {
        return liveBlogApi.subscribeLiveBlogPost(params.liveBlogId)
    }

    override fun mapToLocalModel(
        params: Params,
        remoteModel: PublishedPostToLiveBlogSubscription.Data
    ): NativeLiveBlogPost = remoteModel.publishedPostToLiveBlog.fragments.liveBlogPostFragment.toLocal()

    override suspend fun saveLocally(params: Params, dbModel: NativeLiveBlogPost) {
        liveBlogLocalStorage.observeItem(params.liveBlogId).firstOrNull()?.let { liveBlog ->
            val updatedAt = if (dbModel is NativeLiveBlogPostBasic) dbModel.updatedAt else null
            val updatedLiveBlog = liveBlog.copy(
                lastActivityAt = updatedAt ?: liveBlog.lastActivityAt,
                posts = (listOf(dbModel) + liveBlog.posts).distinctBy { it.id }
            )
            liveBlogLocalStorage.update(params.liveBlogId, updatedLiveBlog)
        }
    }
}