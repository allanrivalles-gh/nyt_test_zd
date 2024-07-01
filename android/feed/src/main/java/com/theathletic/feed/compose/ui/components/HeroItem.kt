package com.theathletic.feed.compose.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.feed.compose.data.PostType
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.interaction.ItemInteractor
import com.theathletic.feed.compose.ui.items.imageitem.ArticleRightImageHero
import com.theathletic.feed.compose.ui.items.imageitem.ArticleTopImage
import com.theathletic.feed.compose.ui.items.imageitem.LiveBlogRightImageHero
import com.theathletic.feed.compose.ui.items.imageitem.LiveBlogTopImage
import com.theathletic.feed.compose.ui.reusables.ArticleTitle
import com.theathletic.feed.compose.ui.reusables.ArticleUiModel
import com.theathletic.feed.compose.ui.reusables.ContentDivider
import com.theathletic.feed.compose.ui.reusables.ContentImage
import com.theathletic.feed.compose.ui.reusables.Image
import com.theathletic.feed.compose.ui.reusables.ReadIndicator
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DayNightPreview

@Composable
internal fun Hero(
    image: @Composable ColumnScope.() -> Unit,
    title: @Composable ColumnScope.() -> Unit,
    footer: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(color = AthTheme.colors.dark200)
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    ) {
        image()
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            title()
            Spacer(Modifier.padding(top = 12.dp))
            footer()
        }
    }
}

@Composable
internal fun HeroList(items: List<LayoutUiModel.Item>, itemInteractor: ItemInteractor) {
    Column {
        items.forEachIndexed { index, item ->
            when (item) {
                is ArticleUiModel -> ArticleRightImageHero(uiModel = item, itemInteractor)
                is LiveBlogUiModel -> LiveBlogRightImageHero(uiModel = item, itemInteractor)
            }
            if (index < items.lastIndex) ContentDivider()
        }
    }
}

@Composable
internal fun HeroCarousel(items: List<LayoutUiModel.Item>, itemInteractor: ItemInteractor) {
    LazyRow(
        modifier = Modifier
            .padding(vertical = 20.dp)
            .padding(start = 16.dp)
    ) {
        items(items, key = { it.hashCode() }, contentType = { it::class }) { item ->
            when (item) {
                is ArticleUiModel -> ArticleTopImage(uiModel = item, itemInteractor = itemInteractor)
                is LiveBlogUiModel -> LiveBlogTopImage(uiModel = item, itemInteractor = itemInteractor)
            }
            Spacer(Modifier.padding(end = 16.dp))
        }
    }
}

@Composable
internal fun HeroImage(
    image: Image,
    isRead: Boolean = false,
    modifier: Modifier
) {
    ContentImage(
        image = image,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        isRead = isRead,
        overlay = { if (isRead) ReadIndicator(Modifier.padding(12.dp)) }
    )
}

internal fun Modifier.heroAspect() =
    aspectRatio(3f / 2)
        .fillMaxWidth()
        .padding(horizontal = 16.dp)

internal fun Modifier.topperHeroAspect() =
    aspectRatio(16f / 9)
        .fillMaxWidth()

@Composable
internal fun HeroTitle(title: String, isRead: Boolean = false) {
    ArticleTitle(
        text = title,
        style = AthTextStyle.TiemposHeadline.Regular.ExtraSmall,
        isRead = isRead
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ArticleHeroPreview() {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        ArticleHero(
            uiModel = articlePreviewData(postType = PostType.DISCUSSION),
            itemInteractor = ItemInteractor(),
            modifier = Modifier.heroAspect()
        )
    }
}

@DayNightPreview
@Composable
private fun HeroCarouselPreview() {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        HeroCarousel(items = previewContentItems, itemInteractor = ItemInteractor())
    }
}

@DayNightPreview
@Composable
private fun HeroListPreview() {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        HeroList(items = previewContentItems, itemInteractor = ItemInteractor())
    }
}

private val previewContentItems = listOf(
    articlePreviewData(postType = PostType.DISCUSSION),
    articlePreviewData(postType = PostType.DISCUSSION),
    articlePreviewData(postType = PostType.DISCUSSION)
)