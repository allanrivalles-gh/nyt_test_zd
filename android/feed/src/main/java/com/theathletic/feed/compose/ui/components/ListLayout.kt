package com.theathletic.feed.compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.feed.compose.data.PostType
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.header
import com.theathletic.feed.compose.ui.interaction.ItemInteractor
import com.theathletic.feed.compose.ui.items.LayoutHeader
import com.theathletic.feed.compose.ui.items.imageitem.ArticleRightImageForYou
import com.theathletic.feed.compose.ui.items.imageitem.LiveBlogRightImageForYou
import com.theathletic.feed.compose.ui.layoutUiModel
import com.theathletic.feed.compose.ui.reusables.ArticleUiModel
import com.theathletic.feed.compose.ui.reusables.ContentDivider
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme

internal data class ListLayoutUiModel(private val layout: LayoutUiModel) : LayoutUiModel by layout

@Composable
internal fun ListLayout(layout: ListLayoutUiModel, itemInteractor: ItemInteractor) {
    Column(Modifier.background(color = AthTheme.colors.dark200)) {
        Column(modifier = Modifier.padding(top = 24.dp)) {
            LayoutHeader(uiModel = layout.header, modifier = Modifier.padding(horizontal = 16.dp))
            ForYouContentList(items = layout.items, itemInteractor)
        }
    }
}

@Composable
private fun ForYouContentList(items: List<LayoutUiModel.Item>, itemInteractor: ItemInteractor) {
    Column {
        items.forEachIndexed { index, item ->
            when (item) {
                is ArticleUiModel -> ArticleRightImageForYou(uiModel = item, itemInteractor = itemInteractor)
                is LiveBlogUiModel -> LiveBlogRightImageForYou(uiModel = item, itemInteractor = itemInteractor)
            }
            if (index < items.lastIndex) ContentDivider()
        }
    }
}

@Composable
@Preview
fun ForYouLayoutPreview() {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        ListLayout(
            layout = ListLayoutUiModel(
                layoutUiModel(
                    id = "1",
                    title = "For You",
                    items = listOf(
                        articlePreviewData(postType = PostType.DISCUSSION),
                        articlePreviewData(postType = PostType.DISCUSSION)
                    )
                )
            ),
            itemInteractor = ItemInteractor()
        )
    }
}