package com.theathletic.feed.compose.ui.items.mostpopular

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.theathletic.feed.R
import com.theathletic.feed.compose.SOURCE_FEED
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.analytics.AnalyticsData
import com.theathletic.feed.compose.ui.analyticsPreviewData
import com.theathletic.feed.compose.ui.reusables.ContentImage
import com.theathletic.feed.compose.ui.reusables.Image
import com.theathletic.links.deep.Deeplink
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DayNightPreview

internal data class MostPopularItemUiModel(
    override val id: String,
    override val permalink: String,
    override val analyticsData: AnalyticsData,
    val number: String,
    val text: String,
    val image: Image,
    val isRead: Boolean = false
) : LayoutUiModel.Item {
    override fun deepLink(): Deeplink = Deeplink.article(id).addSource(SOURCE_FEED)
}

@Composable
internal fun MostPopularItem(uiModel: MostPopularItemUiModel, modifier: Modifier = Modifier) {
    val textColor = if (uiModel.isRead) AthTheme.colors.dark500 else AthTheme.colors.dark700
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(AthTheme.colors.dark200)
            .height(IntrinsicSize.Min)
    ) {
        Text(
            text = uiModel.number,
            style = AthTextStyle.Slab.Inline.Medium,
            color = AthTheme.colors.dark400
        )
        Text(
            text = uiModel.text,
            style = AthTextStyle.TiemposHeadline.Regular.ExtraExtraSmall,
            color = textColor,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .width(231.dp)
                .weight(1f)
        )
        ContentImage(
            image = uiModel.image,
            isRead = uiModel.isRead,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(63.dp)
                .width(63.dp)
        )
    }
}

@DayNightPreview
@Composable
private fun Preview(@PreviewParameter(MostPopularPreviewProvider::class) uiModel: MostPopularItemUiModel) {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        MostPopularItem(uiModel = uiModel)
    }
}

internal class MostPopularPreviewProvider : PreviewParameterProvider<MostPopularItemUiModel> {
    override val values: Sequence<MostPopularItemUiModel>
        get() = mostPopularPreviewItems
}

internal fun mostPopularPreviewItem(
    number: String,
    text: String,
    isRead: Boolean = false
) = MostPopularItemUiModel(
    id = "mostPopularId",
    number = number,
    text = text,
    image = Image.ResourceImage(id = R.drawable.img_feed_q_and_a_live),
    isRead = isRead,
    permalink = "",
    analyticsData = analyticsPreviewData()
)

internal val mostPopularPreviewItems = sequenceOf(
    mostPopularPreviewItem("1", "The randomness of Klay Thompson: ‘It’s impossible’"),
    mostPopularPreviewItem("2", "The randomness of Klay Thompson: ‘It’s impossible to know him and not love him’ Adding some extra text to test the long headline condition."),
    mostPopularPreviewItem("3", "The randomness of Klay Thompson: ‘It’s impossible to know him and not love him’", true),
    mostPopularPreviewItem("4", "The randomness of Klay Thompson: ‘It’s impossible to know him and not love him’", true)
)