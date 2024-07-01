package com.theathletic.entity.local

import com.google.common.truth.Truth.assertThat
import com.theathletic.entity.article.ArticleEntity
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

internal class EntityDataSourceTest {
    private lateinit var entityDataSource: EntityDataSource

    @Before
    fun setUp() {
        entityDataSource = EntityDataSource(mock(), mock(), mock())
    }

    @Test
    fun `deduplicateEntities merges similar entities`() {
        val articleOne = ArticleEntity(
            articleId = 1,
            articleTitle = "one-title"
        )
        val articleOneRepeat = ArticleEntity(
            articleId = 1,
            articleTitle = "one-title",
            excerpt = "one-excerpt"
        )
        val articleTwo = ArticleEntity(
            articleId = 2,
            articleTitle = "two-title"
        )
        val consolidatedArticles = entityDataSource.deduplicateEntities(
            listOf(articleOne, articleOneRepeat, articleTwo)
        )
        val assertArticleOne = consolidatedArticles.filter { it.id == "1" }.first()
        assertThat(assertArticleOne.excerpt).isEqualTo("one-excerpt")
        assertThat(assertArticleOne.articleTitle).isEqualTo("one-title")
        assertThat(consolidatedArticles.size).isEqualTo(2)
    }
}