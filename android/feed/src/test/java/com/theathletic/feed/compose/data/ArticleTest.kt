package com.theathletic.feed.compose.data

import com.google.common.truth.Truth.assertThat
import com.theathletic.datetime.Datetime
import com.theathletic.feed.compose.articleFixture
import org.junit.Test

internal class ArticleTest {
    private val date_2023_03_01 = Datetime(1677639600000)
    private val date_2023_03_30 = Datetime(1680145200000)
    private val date_2023_04_01 = Datetime(1682823600000)
    private val date_far_future = Datetime(Long.MAX_VALUE)

    @Test
    fun `post type is ARTICLE for article post id`() {
        val article = articleFixture(postTypeId = POST_ID_ARTICLE)

        val postType = article.getPostType()

        assertThat(postType).isEqualTo(PostType.ARTICLE)
    }

    @Test
    fun `post type is DISCUSSION for discussion post id`() {
        val article = articleFixture(postTypeId = POST_ID_DISCUSSION)

        val postType = article.getPostType()

        assertThat(postType).isEqualTo(PostType.DISCUSSION)
    }

    @Test
    fun `post type is Q_AND_A_UPCOMING for q&a post id and current time is before startedAt`() {
        val article = articleQAndAFixture(startedAt = date_2023_03_30, endedAt = date_far_future)

        val postType = article.getPostType(date_2023_03_01)

        assertThat(postType).isEqualTo(PostType.Q_AND_A_UPCOMING)
    }

    @Test
    fun `post type is Q_AND_A_LIVE for q&a post id and current time is between startedAt and endedAt`() {
        val article = articleQAndAFixture(startedAt = date_2023_03_01, endedAt = date_far_future)

        val postType = article.getPostType(date_2023_03_01)

        assertThat(postType).isEqualTo(PostType.Q_AND_A_LIVE)
    }

    @Test
    fun `post type is Q_AND_A_RECAP for q&a post id and current time is greater than endedAt`() {
        val article = articleQAndAFixture(startedAt = date_2023_03_01, endedAt = date_2023_03_30)

        val postType = article.getPostType(date_2023_04_01)

        assertThat(postType).isEqualTo(PostType.Q_AND_A_RECAP)
    }

    private fun articleQAndAFixture(
        startedAt: Datetime,
        endedAt: Datetime
    ) = articleFixture(startedAt = startedAt, endedAt = endedAt, postTypeId = POST_ID_Q_AND_A)
}