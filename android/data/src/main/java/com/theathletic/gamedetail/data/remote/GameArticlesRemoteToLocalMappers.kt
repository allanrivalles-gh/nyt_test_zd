package com.theathletic.gamedetail.data.remote

import com.theathletic.GetGameArticlesQuery
import com.theathletic.fragment.ArticleAuthorFragment
import com.theathletic.gamedetail.data.local.GameArticlesLocalModel

fun GetGameArticlesQuery.Data.toLocalModel(gameId: String) = GameArticlesLocalModel(
    gameId = gameId,
    articles = gameArticles.map { it.toLocalModel() }
)

private fun GetGameArticlesQuery.GameArticle.toLocalModel() = GameArticlesLocalModel.GameArticle(
    id = id,
    title = title,
    imageUrl = image_uri,
    authors = authors.map { it.fragments.articleAuthorFragment.toLocalModel() },
    commentCount = comment_count
)

private fun ArticleAuthorFragment.toLocalModel() = GameArticlesLocalModel.GameArticleAuthor(
    name = author.fragments.user.name,
    displayOrder = display_order
)