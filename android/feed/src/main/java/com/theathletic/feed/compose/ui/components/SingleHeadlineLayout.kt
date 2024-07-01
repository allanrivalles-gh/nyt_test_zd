package com.theathletic.feed.compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.analyticsPreviewData
import com.theathletic.feed.compose.ui.items.HeadlineUiModel
import com.theathletic.feed.compose.ui.layoutUiModel
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DayNightPreview

internal data class SingleHeadlineLayoutUiModel(private val layout: LayoutUiModel) : LayoutUiModel by layout {
    override val items: List<HeadlineUiModel> = layout.items.mapNotNull { it as? HeadlineUiModel }
}

@Composable
internal fun SingleHeadlineLayoutUi(layout: SingleHeadlineLayoutUiModel) {
    if (layout.items.isEmpty()) {
        return
    }

    Column(
        modifier = Modifier
            .background(color = AthTheme.colors.dark200)
            .padding(16.dp)
    ) {
        Text(
            color = AthTheme.colors.dark500,
            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
            text = layout.title.uppercase(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(8.dp))
        layout.items.first().also { item ->
            Text(
                color = AthTheme.colors.dark700,
                style = AthTextStyle.Calibre.Headline.Regular.Small,
                text = item.title,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@DayNightPreview
@Composable
private fun SingleHeadlineLayoutPreview(@PreviewParameter(SingleHeadlineLayoutPreviewProvider::class) headlines: SingleHeadlineLayoutUiModel) {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        SingleHeadlineLayoutUi(
            layout = headlines
        )
    }
}

internal class SingleHeadlineLayoutPreviewProvider : PreviewParameterProvider<SingleHeadlineLayoutUiModel> {
    override val values: Sequence<SingleHeadlineLayoutUiModel> = sequenceOf(
        singleHeadlineLayoutPreviewData(id = "1", title = "News")
    )

    private fun headlinePreviewData(id: String, title: String): HeadlineUiModel =
        HeadlineUiModel(
            id = id,
            title = title,
            image = "",
            permalink = "",
            analyticsData = analyticsPreviewData()
        )

    private fun singleHeadlineLayoutPreviewData(id: String, title: String): SingleHeadlineLayoutUiModel {
        return SingleHeadlineLayoutUiModel(
            layoutUiModel(
                id = id,
                title = title,
                icon = "",
                action = "",
                deepLink = "",
                items = listOf(
                    headlinePreviewData(id = "1", title = "Steelers outmaneuvered in every quarter")
                )
            )
        )
    }
}