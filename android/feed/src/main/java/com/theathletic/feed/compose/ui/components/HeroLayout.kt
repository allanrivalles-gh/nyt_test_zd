package com.theathletic.feed.compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.theathletic.feed.compose.data.PostType
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.header
import com.theathletic.feed.compose.ui.interaction.ItemInteractor
import com.theathletic.feed.compose.ui.items.LayoutHeader
import com.theathletic.feed.compose.ui.layoutUiModel
import com.theathletic.feed.compose.ui.reusables.ArticleUiModel
import com.theathletic.feed.compose.ui.reusables.ContentDivider
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DayNightPreview

internal data class TopperHeroLayoutUiModel(private val layout: LayoutUiModel) : LayoutUiModel by layout
internal data class HeroListLayoutUiModel(private val layout: LayoutUiModel) : LayoutUiModel by layout
internal data class HeroCarouselLayoutUiModel(private val layout: LayoutUiModel) : LayoutUiModel by layout

@Composable
internal fun TopperHeroLayout(layout: TopperHeroLayoutUiModel, itemInteractor: ItemInteractor) {
    HeroLayout(
        layout = layout,
        hero = { item -> Hero(item = item, itemInteractor = itemInteractor, modifier = Modifier.topperHeroAspect()) },
        content = { items -> HeroList(items, itemInteractor = itemInteractor) }
    )
}

@Composable
internal fun HeroListLayout(layout: HeroListLayoutUiModel, itemInteractor: ItemInteractor) {
    HeroLayout(
        layout = layout,
        hero = { item -> Hero(item, itemInteractor, Modifier.heroAspect()) },
        content = { items -> HeroList(items, itemInteractor) }
    )
}

@Composable
internal fun HeroCarouselLayout(layout: HeroCarouselLayoutUiModel, itemInteractor: ItemInteractor) {
    HeroLayout(
        layout = layout,
        hero = { item -> Hero(item, itemInteractor, Modifier.heroAspect()) },
        content = { items -> HeroCarousel(items, itemInteractor) }
    )
}

@Composable
private fun Hero(item: LayoutUiModel.Item, itemInteractor: ItemInteractor, modifier: Modifier) {
    when (item) {
        is ArticleUiModel -> ArticleHero(uiModel = item, itemInteractor = itemInteractor, modifier = modifier)
        is LiveBlogUiModel -> LiveBlogHero(uiModel = item, itemInteractor = itemInteractor, modifier = modifier)
    }
}

@Composable
private fun HeroLayout(
    layout: LayoutUiModel,
    hero: @Composable (hero: LayoutUiModel.Item) -> Unit,
    content: @Composable (items: List<LayoutUiModel.Item>) -> Unit
) {
    val heroItem = if (layout.items.isNotEmpty()) layout.items.first() else return
    val items = layout.items.drop(1)

    Column(
        modifier = Modifier
            .background(color = AthTheme.colors.dark200)
    ) {
        LayoutHeader(
            uiModel = layout.header,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp, bottom = 20.dp)
        )
        hero(heroItem)
        ContentDivider(modifier = Modifier.padding(horizontal = 16.dp))
        content(items)
    }
}

@DayNightPreview
@Composable
private fun HeroLayoutPreview(@PreviewParameter(HeroLayoutParamProvider::class) layout: LayoutUiModel) {
    val previewInteractor = ItemInteractor()
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        when (layout) {
            is TopperHeroLayoutUiModel -> TopperHeroLayout(layout, previewInteractor)
            is HeroListLayoutUiModel -> HeroListLayout(layout, previewInteractor)
            is HeroCarouselLayoutUiModel -> HeroCarouselLayout(layout, previewInteractor)
        }
    }
}

internal class HeroLayoutParamProvider : PreviewParameterProvider<LayoutUiModel> {
    val layout = layoutUiModel(
        id = "layoutId",
        title = "Layout title",
        items = listOf(
            articlePreviewData(PostType.Q_AND_A_LIVE),
            articlePreviewData(PostType.Q_AND_A_RECAP),
            articlePreviewData(PostType.DISCUSSION)
        )
    )
    override val values: Sequence<LayoutUiModel> = sequenceOf(
        TopperHeroLayoutUiModel(layout),
        HeroListLayoutUiModel(layout),
        HeroCarouselLayoutUiModel(layout),
    )
}