package com.theathletic.feed.compose.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.interaction.ItemInteractor
import com.theathletic.feed.compose.ui.items.featuredgame.FeaturedGameItem
import com.theathletic.feed.compose.ui.items.featuredgame.FeaturedGameUiModel

data class FeaturedGameLayoutUiModel(private val layout: LayoutUiModel) : LayoutUiModel by layout {
    override val items: List<FeaturedGameUiModel> = layout.items.mapNotNull { (it as? FeaturedGameUiModel) }
}

@Composable
fun FeaturedGameLayout(layout: FeaturedGameLayoutUiModel, itemInteractor: ItemInteractor) {
    if (layout.items.isEmpty()) return

    Column {
        layout.items.forEach { uiModel ->
            FeaturedGameItem(
                model = uiModel,
                itemInteractor = itemInteractor
            )
        }
    }
}