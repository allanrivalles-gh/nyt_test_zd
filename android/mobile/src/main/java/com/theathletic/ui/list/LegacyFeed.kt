package com.theathletic.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.databinding.DataBindingUtil
import com.theathletic.BR
import com.theathletic.feed.ui.FeedContract
import com.theathletic.feed.ui.models.FeedCarouselModel
import com.theathletic.feed.ui.models.FeedScoresCarousel
import com.theathletic.feed.ui.models.FeedSideBySideCarousel
import com.theathletic.themes.AthTheme
import com.theathletic.ui.CarouselUiModel
import com.theathletic.ui.widgets.InfiniteListHandler

data class CarouselParams(
    val modifier: Modifier = Modifier,
    val horizontalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(0.dp),
    val contentPadding: PaddingValues = PaddingValues(0.dp)
)

@Composable
private fun CarouselUiModel.createCarouselParams() = when (this) {
    is FeedScoresCarousel -> CarouselParams(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = PaddingValues(vertical = 2.dp)
    )
    is FeedCarouselModel -> CarouselParams(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.background(color = AthTheme.colors.dark200),
        contentPadding = PaddingValues(vertical = 20.dp, horizontal = 16.dp)
    )
    is FeedSideBySideCarousel -> CarouselParams(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxSize()
            .background(color = AthTheme.colors.dark200)
    )
    else -> CarouselParams()
}

@Composable
private fun LegacyCarousel(
    carouselUiModel: CarouselUiModel,
    interactor: FeedContract.Presenter
) {
    val carouselParams = carouselUiModel.createCarouselParams()

    val listState = rememberLazyListState()
    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .then(carouselParams.modifier),
        horizontalArrangement = carouselParams.horizontalArrangement,
        contentPadding = carouselParams.contentPadding
    ) {
        itemsIndexed(carouselUiModel.carouselItemModels) { index, uiModel ->
            val layoutId = uiModel.carouselLayoutId()

            AndroidViewBinding(
                factory = { inflater, parent, _ ->
                    DataBindingUtil.inflate(inflater, layoutId, parent, false)
                },
                update = {
                    setVariable(BR.view, interactor)
                    setVariable(BR.interactor, interactor)
                    setVariable(BR.data, uiModel)
                    executePendingBindings()
                }
            )
        }
    }
    InfiniteListHandler(
        listState = listState,
        onLoadMore = interactor::onLoadMore,
    )
}