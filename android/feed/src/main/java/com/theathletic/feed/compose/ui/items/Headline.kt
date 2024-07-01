package com.theathletic.feed.compose.ui.items

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theathletic.feed.compose.SOURCE_FEED
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.analytics.AnalyticsData
import com.theathletic.feed.compose.ui.analyticsPreviewData
import com.theathletic.feed.compose.ui.interaction.ItemInteractor
import com.theathletic.feed.compose.ui.interaction.interactive
import com.theathletic.links.deep.Deeplink
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DayNightPreview

internal data class HeadlineUiModel(
    override val id: String,
    val title: String,
    val image: String,
    override val permalink: String,
    override val analyticsData: AnalyticsData
) : LayoutUiModel.Item {
    override fun deepLink(): Deeplink = Deeplink.headline(id).addSource(SOURCE_FEED)
}

@Composable
internal fun Headline(headline: HeadlineUiModel, itemInteractor: ItemInteractor) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .interactive(headline, itemInteractor)
    ) {
        HeadlineBullet(
            size = 4.dp,
            color = AthTheme.colors.dark400,
            modifier = Modifier
                .padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            color = AthTheme.colors.dark700,
            style = AthTextStyle.Calibre.Headline.Regular.Small,
            text = headline.title,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun HeadlineBullet(size: Dp, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .background(color = color, shape = RectangleShape)
    )
}

private class HeadlinePreviewParamsProvider : PreviewParameterProvider<HeadlineUiModel> {
    override val values: Sequence<HeadlineUiModel> = sequenceOf(
        headlinePreviewData(id = "1", title = "Washington talks to Steelers about trade"),
        headlinePreviewData(
            id = "2",
            title = "This is what a really long wordy headline looks like; " +
                "it should wrap to a max of three lines, with the bullet " +
                "vertically centered on the first line of text"
        )
    )

    private fun headlinePreviewData(id: String, title: String): HeadlineUiModel =
        HeadlineUiModel(
            id = id,
            title = title,
            image = "",
            permalink = "",
            analyticsData = analyticsPreviewData()
        )
}

@DayNightPreview
@Composable
private fun HeadlinePreviewDarkMode(
    @PreviewParameter(HeadlinePreviewParamsProvider::class) model: HeadlineUiModel
) {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        Box(modifier = Modifier.background(AthTheme.colors.dark200)) {
            Headline(model, ItemInteractor())
        }
    }
}