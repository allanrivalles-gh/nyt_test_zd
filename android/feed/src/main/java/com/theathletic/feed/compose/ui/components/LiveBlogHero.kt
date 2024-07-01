package com.theathletic.feed.compose.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.theathletic.feed.R
import com.theathletic.feed.compose.SOURCE_FEED
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.analytics.AnalyticsData
import com.theathletic.feed.compose.ui.analyticsPreviewData
import com.theathletic.feed.compose.ui.interaction.ItemInteractor
import com.theathletic.feed.compose.ui.interaction.interactive
import com.theathletic.feed.compose.ui.reusables.Image
import com.theathletic.feed.compose.ui.reusables.SmallLiveTag
import com.theathletic.links.deep.Deeplink
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asResourceString
import com.theathletic.ui.asString
import com.theathletic.ui.preview.DayNightPreview

internal data class LiveBlogUiModel(
    override val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val isLive: Boolean,
    val lastActivity: ResourceString,
    override val permalink: String,
    override val analyticsData: AnalyticsData,
) : LayoutUiModel.Item {
    override fun deepLink(): Deeplink = Deeplink.liveBlog(id).addSource(SOURCE_FEED)
}

@Composable
internal fun LiveBlogHero(
    uiModel: LiveBlogUiModel,
    itemInteractor: ItemInteractor,
    modifier: Modifier
) {
    val image = Image.RemoteImage(url = uiModel.imageUrl, error = R.drawable.ic_feed_placeholder_offline_large)

    Hero(
        image = { HeroImage(image = image, modifier = modifier) },
        title = { LiveBlogHeroTitle(title = uiModel.title, isLive = uiModel.isLive) },
        footer = { LiveBlogFooter(lastActivity = uiModel.lastActivity.asString()) },
        modifier = Modifier.interactive(uiModel, itemInteractor)
    )
}

@Composable
fun LiveBlogHeroTitle(title: String, isLive: Boolean) {
    Column {
        if (isLive) {
            SmallLiveTag(
                modifier = Modifier
                    .align(Start)
                    .padding(top = 16.dp, bottom = 8.dp),
            )
        }
        HeroTitle(title = title)
    }
}

@Composable
fun ColumnScope.LiveBlogFooter(lastActivity: String) {
    Text(
        color = AthTheme.colors.dark500,
        style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall,
        text = lastActivity,
        modifier = Modifier.align(Start)
    )
}

@DayNightPreview
@Composable
private fun LiveBlogHeroPreview(
    @PreviewParameter(LiveBlogParameterProvider::class) uiModel: LiveBlogUiModel,
) {
    return AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        LiveBlogHero(modifier = Modifier.heroAspect(), uiModel = uiModel, itemInteractor = ItemInteractor())
    }
}

@Preview
@Composable
private fun LiveBlogTopperHeroPreview(@PreviewParameter(LiveBlogParameterProvider::class) uiModel: LiveBlogUiModel) {
    return AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        LiveBlogHero(modifier = Modifier.topperHeroAspect(), uiModel = uiModel, itemInteractor = ItemInteractor())
    }
}

internal class LiveBlogParameterProvider : PreviewParameterProvider<LiveBlogUiModel> {
    override val values: Sequence<LiveBlogUiModel> = sequenceOf(
        liveBlogPreviewData(isLive = true),
        liveBlogPreviewData(isLive = false)
    )
}

internal fun liveBlogPreviewData(
    isLive: Boolean = false,
    lastActivity: String = "Last Updated 2 min ago",
) = LiveBlogUiModel(
    id = "id",
    title = "2021 NFL Draft: Live updates, analysis, grades and more from pick No. 1 to 259",
    imageUrl = "",
    description = "Join Lindsay Jones, Matt Fortuna, Ted Nguyen and more as they give reactions and analysis.",
    isLive = isLive,
    permalink = "",
    lastActivity = lastActivity.asResourceString(),
    analyticsData = analyticsPreviewData()
)