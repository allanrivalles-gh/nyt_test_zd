package com.theathletic.liveblog.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.liveblog.data.local.LiveBlogLinks
import com.theathletic.liveblog.data.local.LiveBlogLinksLocalDataSource
import com.theathletic.liveblog.data.remote.LiveBlogApi
import timber.log.Timber

class LiveBlogLinksRepository @AutoKoin(Scope.SINGLE) constructor(
    private val liveBlogLinksDataSource: LiveBlogLinksLocalDataSource,
    private val liveBlogApi: LiveBlogApi,
) {
    class LiveBlogLinksException(message: String) : Exception(message)

    suspend fun fetchLiveBlogLinks(id: String) {
        try {
            val response = liveBlogApi.getLiveBlogLinks(id)

            val data = response.data
            if (data == null) {
                val errorMessage = response.errors?.joinToString(" ") { it.message } ?: "No data returned."
                throw LiveBlogLinksException(errorMessage)
            }

            val liveBlogsLinks = data.liveBlog.fragments.liveBlogLinksFragment.let { liveBlog ->
                LiveBlogLinks(
                    id = liveBlog.id,
                    permalink = liveBlog.permalink,
                    linkForEmbed = liveBlog.permalinkForEmbed,
                )
            }
            liveBlogLinksDataSource.update(id, liveBlogsLinks)
        } catch (error: Throwable) {
            Timber.e(error)
            throw LiveBlogLinksException("Exception fetching Live Blog links, message: ${error.message}")
        }
    }

    fun getLiveBlogLinks(id: String) = liveBlogLinksDataSource.observeItem(id)
}