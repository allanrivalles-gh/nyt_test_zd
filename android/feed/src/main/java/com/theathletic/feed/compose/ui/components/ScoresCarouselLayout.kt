package com.theathletic.feed.compose.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.interaction.ItemInteractor
import com.theathletic.feed.compose.ui.items.scores.ScoresCarouselItem
import com.theathletic.feed.compose.ui.items.scores.ScoresCarouselItemUiModel

data class ScoresCarouselLayoutUiModel(private val layout: LayoutUiModel) : LayoutUiModel by layout {
    override val items: List<ScoresCarouselItemUiModel> = layout.items.mapNotNull { (it as? ScoresCarouselItemUiModel) }
    val scrollIndex = items.indexOfFirst { it.scrollIndex == 0 }
}

@Composable
fun ScoresCarouselLayout(layout: ScoresCarouselLayoutUiModel, itemInteractor: ItemInteractor, listState: LazyListState) {
    if (layout.items.isEmpty()) return

    LazyRow(
        modifier = Modifier.padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        state = listState
    ) {
        items(layout.items) { uiModel ->
            ScoresCarouselItem(
                uiModel = uiModel,
                itemInteractor = itemInteractor
            )
        }
    }
}