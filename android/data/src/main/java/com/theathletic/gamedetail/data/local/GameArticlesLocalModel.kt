package com.theathletic.gamedetail.data.local

import com.theathletic.data.LocalModel

data class GameArticlesLocalModel(
    val gameId: String,
    val articles: List<GameArticle>
) : LocalModel {

    data class GameArticle(
        val id: String,
        val title: String,
        val imageUrl: String?,
        val authors: List<GameArticleAuthor>,
        val commentCount: Int
    )

    data class GameArticleAuthor(
        val name: String,
        val displayOrder: Int
    )
}