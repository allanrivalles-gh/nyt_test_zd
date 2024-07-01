package com.theathletic.feed.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.theathletic.BR
import com.theathletic.R
import com.theathletic.databinding.ListItemFeedScoresBinding
import com.theathletic.feed.ui.models.FeedScoresCarouselItem
import com.theathletic.themes.AthColor
import com.theathletic.ui.ColorScheme
import com.theathletic.ui.UiModel
import com.theathletic.ui.widgets.PulsingIcon

@Composable
fun FeedGameComposeCarousel(
    uiModels: List<UiModel>,
    interactor: FeedContract.Presenter,
    listState: LazyListState
) {
    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.Absolute.spacedBy(4.dp),
        modifier = Modifier.padding(top = 4.dp)
    ) {
        items(uiModels) { model ->
            if (model is FeedScoresCarouselItem) {
                AndroidViewBinding(ListItemFeedScoresBinding::inflate) {
                    setVariable(BR.interactor, interactor)
                    setVariable(BR.data, model)
                    discussDiscovery.setContent {
                        PulsingIcon(
                            layoutSize = 40.dp,
                            iconId = R.drawable.ic_news_comment,
                            iconSize = 14.dp,
                            colorScheme = ColorScheme(
                                lightModeColor = AthColor.Gray300,
                                darkModeColor = AthColor.Gray800
                            ),
                            circleSize = 20.dp,
                            strokeWidth = 3.dp
                        )
                    }
                    executePendingBindings()
                }
            }
        }
    }
}