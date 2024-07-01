package com.theathletic.feed.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.theathletic.ArticleCommentsQuery
import com.theathletic.ArticleQuery
import com.theathletic.ArticleRelatedContentQuery
import com.theathletic.UserArticlesQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.network.apollo.FetchPolicy
import com.theathletic.network.apollo.httpFetchPolicy
import com.theathletic.type.CommentSortBy
import com.theathletic.type.ContentType
import com.theathletic.type.QueryCommentsInput
import com.theathletic.type.TagInput
import com.theathletic.type.TagsInput

class ArticleGraphqlApi @AutoKoin(Scope.SINGLE) constructor(
    private val client: ApolloClient
) {
    suspend fun getArticle(id: String, plat: String?, prop: String?, isAdsEnabled: Boolean): ApolloResponse<ArticleQuery.Data> {
        return client
            .query(
                ArticleQuery(
                    id = id,
                    plat = Optional.presentIfNotNull(plat),
                    prop = Optional.presentIfNotNull(prop),
                    isAdsEnabled = Optional.present(isAdsEnabled)
                )
            )
            .httpFetchPolicy(FetchPolicy.NetworkFirst)
            .execute()
    }

    suspend fun getArticleComments(id: String): ApolloResponse<ArticleCommentsQuery.Data> {
        return client
            .query(
                ArticleCommentsQuery(
                    input = QueryCommentsInput(
                        content_id = id,
                        content_type = ContentType.post,
                        sort_by = Optional.present(CommentSortBy.time)
                    )
                )
            )
            .httpFetchPolicy(FetchPolicy.NetworkFirst)
            .execute()
    }

    suspend fun getArticleRelatedContent(
        teamIds: List<String>,
        leagueIds: List<String>,
        authorIds: List<String>,
        excludeId: String
    ): ApolloResponse<ArticleRelatedContentQuery.Data> {
        val tagsInput = TagsInput(
            leagues = Optional.presentIfNotNull(leagueIds.map { TagInput(id = it) }),
            teams = Optional.presentIfNotNull(teamIds.map { TagInput(id = it) }),
            authors = Optional.presentIfNotNull(authorIds.map { TagInput(id = it) })
        )
        return client.query(
            ArticleRelatedContentQuery(
                tags = tagsInput,
                excludeIds = listOf(excludeId)
            )
        ).execute()
    }

    suspend fun getSavedStories(): ApolloResponse<UserArticlesQuery.Data> {
        return client.query(UserArticlesQuery()).execute()
    }
}