package com.theathletic.feed.compose

import com.theathletic.article.data.ArticleRepository
import com.theathletic.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class MarkArticleAsReadUseCaseTest {
    private lateinit var markArticleAsRead: MarkArticleAsReadUseCase

    @Mock private lateinit var articleRepository: ArticleRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        markArticleAsRead = MarkArticleAsReadUseCase(articleRepository)
    }

    @Test
    fun `call article repository to set article as read`() = runTest {
        markArticleAsRead(123, true)

        verify(articleRepository).markArticleRead(123, true)
    }
}