package com.theathletic.article.data.remote

import com.apollographql.apollo3.ApolloClient
import com.theathletic.ArticleIdQuery
import com.theathletic.SlugToTopicQuery
import com.theathletic.annotation.autokoin.AutoKoin

class ArticleApi @AutoKoin constructor(
    private val client: ApolloClient
) {

    suspend fun getIdFromSlug(slug: String): SlugToTopicQuery.Data? {
        val query = SlugToTopicQuery(
            slug = slug
        )
        return client.query(query).execute().data
    }

    suspend fun getHeadlineArticleId(headlineId: String): ArticleIdQuery.Data? {
        val query = ArticleIdQuery(
            articleByIdId = headlineId
        )

        return client.query(query).execute().data
    }
}