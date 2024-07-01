package com.theathletic.feed.compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.header
import com.theathletic.feed.compose.ui.interaction.ItemInteractor
import com.theathletic.feed.compose.ui.interaction.interactive
import com.theathletic.feed.compose.ui.items.LayoutHeader
import com.theathletic.feed.compose.ui.items.mostpopular.MostPopularItem
import com.theathletic.feed.compose.ui.items.mostpopular.MostPopularItemUiModel
import com.theathletic.feed.compose.ui.items.mostpopular.mostPopularPreviewItems
import com.theathletic.feed.compose.ui.layoutUiModel
import com.theathletic.feed.compose.ui.reusables.ContentDivider
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DayNightPreview

internal data class MostPopularLayoutUiModel(private val layout: LayoutUiModel) : LayoutUiModel by layout

@Composable
internal fun MostPopularLayout(layout: MostPopularLayoutUiModel, itemInteractor: ItemInteractor) {
    Column(
        modifier = Modifier
            .background(color = AthTheme.colors.dark200)
            .padding(top = 24.dp)
    ) {
        LayoutHeader(uiModel = layout.header, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.padding(top = 4.dp))
        ContentList(layout.items, itemInteractor)
        Spacer(modifier = Modifier.padding(bottom = 4.dp))
    }
}

@Composable
private fun ContentList(items: List<LayoutUiModel.Item>, itemInteractor: ItemInteractor) {
    Column {
        items.forEachIndexed { index, item ->
            when (item) {
                is MostPopularItemUiModel -> MostPopularItem(
                    uiModel = item,
                    modifier = Modifier
                        .interactive(item, itemInteractor)
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                )
            }

            if (index < items.lastIndex) {
                ContentDivider()
            }
        }
    }
}

@DayNightPreview
@Composable
private fun Preview(@PreviewParameter(MostPopularPreviewProvider::class) layout: MostPopularLayoutUiModel) {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        MostPopularLayout(layout = layout, itemInteractor = ItemInteractor())
    }
}

private class MostPopularPreviewProvider : PreviewParameterProvider<MostPopularLayoutUiModel> {
    private val layout = layoutUiModel(
        id = "layoutId",
        title = "Most Popular",
        items = mostPopularPreviewItems.toList()
    )
    override val values: Sequence<MostPopularLayoutUiModel>
        get() = sequenceOf(
            MostPopularLayoutUiModel(layout)
        )
}