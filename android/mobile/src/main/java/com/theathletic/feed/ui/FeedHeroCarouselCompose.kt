package com.theathletic.feed.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.theathletic.BR
import com.theathletic.databinding.ListItemFeedCuratedCarouselItemBinding
import com.theathletic.feed.ui.models.FeedCuratedCarouselItem
import com.theathletic.themes.AthTheme
import com.theathletic.ui.UiModel

@Composable
fun FeedHeroComposeCarousel(
    uiModels: List<UiModel>,
    interactor: FeedContract.Presenter
) {
    var maxHeight by remember { mutableStateOf(Dp.Unspecified) }
    val density = LocalDensity.current
    LazyRow(
        contentPadding = PaddingValues(horizontal = 8.dp),
        modifier = Modifier
            .heightIn(min = maxHeight)
            .background(AthTheme.colors.dark200)
            .onSizeChanged {
                if (it.height > maxHeight.value.toInt()) {
                    density.run { maxHeight = it.height.toDp() }
                }
            }
    ) {
        items(uiModels) { model ->
            if (model is FeedCuratedCarouselItem) {
                AndroidViewBinding(ListItemFeedCuratedCarouselItemBinding::inflate) {
                    setVariable(BR.interactor, interactor)
                    setVariable(BR.data, model)
                    executePendingBindings()
                }
            }
        }
    }
}