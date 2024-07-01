package com.theathletic.liveblog.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.theathletic.LiveBlogBannerSubscription
import com.theathletic.LiveBlogLinksQuery
import com.theathletic.LiveBlogPostsQuery
import com.theathletic.LiveBlogQuery
import com.theathletic.PublishedPostToLiveBlogSubscription
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.network.apollo.notPersistedSubscription
import com.theathletic.utility.coroutines.retryWithBackoff
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class LiveBlogApi @AutoKoin(Scope.SINGLE) constructor(
    private val client: ApolloClient
) {
    companion object {
        private const val PER_PAGE = 10
    }

    suspend fun getLiveBlog(id: String, includeAds: Boolean = false): ApolloResponse<LiveBlogQuery.Data> {
        return client.query(
            LiveBlogQuery(id, Optional.present(0), Optional.present(PER_PAGE), Optional.present(includeAds))
        ).execute()
    }

    suspend fun getLiveBlogLinks(id: String): ApolloResponse<LiveBlogLinksQuery.Data> {
        return client.query(LiveBlogLinksQuery(id)).execute()
    }

    suspend fun getLiveBlogPosts(
        id: String,
        page: Int,
        includeAds: Boolean = false
    ): ApolloResponse<LiveBlogPostsQuery.Data> {
        return client.query(
            LiveBlogPostsQuery(id, Optional.present(page), Optional.present(PER_PAGE), Optional.present(includeAds))
        ).execute()
    }

    fun subscribeLiveBlogPost(id: String): Flow<PublishedPostToLiveBlogSubscription.Data> {
        return client.notPersistedSubscription(PublishedPostToLiveBlogSubscription(id))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }

    fun subscribeToLiveBlogBannerUpdates(id: String): Flow<LiveBlogBannerSubscription.Data> {
        return client.notPersistedSubscription(LiveBlogBannerSubscription(id))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }
}