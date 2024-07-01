package com.theathletic.feed.compose.ui.items.imageitem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.theathletic.feed.compose.ui.components.LiveBlogParameterProvider
import com.theathletic.feed.compose.ui.components.LiveBlogUiModel
import com.theathletic.feed.compose.ui.interaction.ItemInteractor
import com.theathletic.feed.compose.ui.interaction.interactive
import com.theathletic.feed.compose.ui.reusables.ExtraSmallLiveTag
import com.theathletic.feed.compose.ui.reusables.Image
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.asString
import com.theathletic.ui.preview.DayNightPreview

@Composable
internal fun LiveBlogTopImage(uiModel: LiveBlogUiModel, itemInteractor: ItemInteractor) {
    val image = Image.RemoteImage(uiModel.imageUrl)

    TopImageItem(
        image = { ItemImage(image = image, modifier = Modifier.carouselAspect()) },
        title = { TopImageItemTitle(title = uiModel.title) },
        footer = { LiveBlogFooter(uiModel) },
        modifier = Modifier.interactive(uiModel, itemInteractor)
    )
}

@Composable
internal fun LiveBlogRightImageHero(uiModel: LiveBlogUiModel, itemInteractor: ItemInteractor) {
    LiveBlogRightImage(uiModel = uiModel, itemInteractor = itemInteractor, modifier = Modifier.heroAspect())
}

@Composable
internal fun LiveBlogRightImageForYou(uiModel: LiveBlogUiModel, itemInteractor: ItemInteractor) {
    LiveBlogRightImage(uiModel = uiModel, itemInteractor = itemInteractor, modifier = Modifier.forYouAspect())
}

@Composable
private fun LiveBlogRightImage(
    uiModel: LiveBlogUiModel,
    itemInteractor: ItemInteractor,
    modifier: Modifier,
    maxLines: Int = 3
) {
    val image = Image.RemoteImage(uiModel.imageUrl)

    RightImageItem(
        title = { HorizontalImageItemTitle(title = uiModel.title, maxLines = maxLines) },
        image = { ItemImage(image = image, modifier = modifier) },
        footer = { LiveBlogFooter(uiModel) },
        modifier = Modifier.interactive(uiModel, itemInteractor)
    )
}

@Composable
private fun LiveBlogFooter(uiModel: LiveBlogUiModel) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (uiModel.isLive) {
            ExtraSmallLiveTag(
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(end = 6.dp)
            )
        }

        Text(
            color = AthTheme.colors.dark500,
            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
            text = uiModel.lastActivity.asString(),
            modifier = Modifier.align(Alignment.Bottom)
        )
    }
}

@DayNightPreview
@Composable
private fun LiveTopImagePreview(@PreviewParameter(LiveBlogParameterProvider::class) uiModel: LiveBlogUiModel) {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        LiveBlogTopImage(uiModel = uiModel, ItemInteractor())
    }
}

@DayNightPreview
@Composable
private fun LiveBlogRightImagePreview(@PreviewParameter(LiveBlogParameterProvider::class) uiModel: LiveBlogUiModel) {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        Column {
            LiveBlogRightImageForYou(uiModel = uiModel, ItemInteractor())
            LiveBlogRightImageHero(uiModel = uiModel, ItemInteractor())
        }
    }
}