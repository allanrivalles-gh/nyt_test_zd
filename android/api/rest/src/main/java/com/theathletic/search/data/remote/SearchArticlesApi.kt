package com.theathletic.search.data.remote

import com.theathletic.search.data.local.SearchArticleResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SearchArticlesApi {
    @FormUrlEncoded
    @POST("v5/article_search")
    suspend fun getSearchArticles(@Field("search_text") searchText: String): Response<SearchArticleResponse>
}