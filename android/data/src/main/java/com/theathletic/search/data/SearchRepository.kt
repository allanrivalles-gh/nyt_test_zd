package com.theathletic.search.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.main.FeedItemEntryType
import com.theathletic.entity.remote.toEntity
import com.theathletic.search.data.remote.SearchArticlesApi
import com.theathletic.search.data.remote.SearchGraphqlApi

class SearchRepository @AutoKoin constructor(
    private val searchApi: SearchGraphqlApi,
    private val searchArticlesApi: SearchArticlesApi,
) {
    suspend fun getMostPopularArticles() = searchApi.getMostPopularArticles().data
        ?.mostPopularArticles
        ?.map { it.fragments.article.toEntity(FeedItemEntryType.ARTICLE) } ?: emptyList()

    suspend fun getSearchArticles(searchText: String) = searchArticlesApi.getSearchArticles(searchText)
}