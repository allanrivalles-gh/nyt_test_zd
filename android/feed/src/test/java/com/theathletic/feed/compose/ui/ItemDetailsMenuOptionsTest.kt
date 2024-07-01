package com.theathletic.feed.compose.ui

import com.google.common.truth.Truth.assertThat
import com.theathletic.feed.compose.articleUiModelFixture
import com.theathletic.feed.compose.data.PostType
import com.theathletic.feed.compose.discussionUiModelFixture
import com.theathletic.feed.compose.itemFixture
import com.theathletic.feed.compose.qandaUiModelFixture
import com.theathletic.feed.compose.ui.components.FeedDetailsMenuOption
import org.junit.Test

class ItemDetailsMenuOptionsTest {
    @Test
    fun `contains Share option when item has permalink`() {
        val permalink = "https://theathletic.com/4675110/2023/07/08/bruins-prospects-fabian-lysell-mason-lohrei/"
        val item = itemFixture(permalink = permalink)
        val options = item.detailsMenuOptions
        assertThat(options).contains(FeedDetailsMenuOption.Share(permalink))
    }

    @Test
    fun `does not contain Share option when item does not have a permalink`() {
        val item = itemFixture(permalink = null)
        val shareOption = item.detailsMenuOptions.firstOrNull { it is FeedDetailsMenuOption.Share }
        assertThat(shareOption).isNull()
    }

    @Test
    fun `contains Save option when item is an article`() {
        for (isBookmarked in listOf(false, true)) {
            val item = articleUiModelFixture(isBookmarked = isBookmarked)
            val options = item.detailsMenuOptions
            assertThat(options).contains(FeedDetailsMenuOption.Save(item.id.toLong(), isBookmarked))
        }
    }

    @Test
    fun `does not contain Save option when item is a discussion`() {
        val item = discussionUiModelFixture()
        val saveOption = item.detailsMenuOptions.firstOrNull { it is FeedDetailsMenuOption.Save }
        assertThat(saveOption).isNull()
    }

    @Test
    fun `does not contain Save option when item is a QandA`() {
        for (qAndA in qAndAList) {
            val saveOption = qAndA.detailsMenuOptions.firstOrNull { it is FeedDetailsMenuOption.Save }
            assertThat(saveOption).isNull()
        }
    }

    @Test
    fun `contains Mark Read option when item is an article`() {
        for (isRead in listOf(false, true)) {
            val item = articleUiModelFixture(isRead = isRead)
            val options = item.detailsMenuOptions
            assertThat(options).contains(FeedDetailsMenuOption.MarkRead(item.id.toLong(), isRead))
        }
    }

    @Test
    fun `does not contain Mark Read option when item is a discussion`() {
        val item = discussionUiModelFixture()
        val markReadOption = item.detailsMenuOptions.firstOrNull { it is FeedDetailsMenuOption.MarkRead }
        assertThat(markReadOption).isNull()
    }

    @Test
    fun `does not contain Mark Read option when item is a QandA`() {
        for (qAndA in qAndAList) {
            val markReadOption = qAndA.detailsMenuOptions.firstOrNull { it is FeedDetailsMenuOption.MarkRead }
            assertThat(markReadOption).isNull()
        }
    }

    private val qAndAList = listOf(
        qandaUiModelFixture(postType = PostType.Q_AND_A_UPCOMING),
        qandaUiModelFixture(postType = PostType.Q_AND_A_LIVE),
        qandaUiModelFixture(postType = PostType.Q_AND_A_RECAP)
    )
}