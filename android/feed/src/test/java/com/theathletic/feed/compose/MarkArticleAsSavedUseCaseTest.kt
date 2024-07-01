package com.theathletic.feed.compose

import com.theathletic.article.data.ArticleRepository
import com.theathletic.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class MarkArticleAsSavedUseCaseTest {
    private lateinit var markArticleAsSaved: MarkArticleAsSavedUseCase

    @Mock private lateinit var articleRepository: ArticleRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        markArticleAsSaved = MarkArticleAsSavedUseCase(articleRepository)
    }

    @Test
    fun `call article repository to set article as saved`() = runTest {
        markArticleAsSaved(123, true)

        verify(articleRepository).markArticleBookmarked(123, true)
    }
}