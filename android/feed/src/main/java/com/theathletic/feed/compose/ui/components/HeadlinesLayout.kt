package com.theathletic.feed.compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.analyticsPreviewData
import com.theathletic.feed.compose.ui.interaction.ItemInteractor
import com.theathletic.feed.compose.ui.items.Headline
import com.theathletic.feed.compose.ui.items.HeadlineUiModel
import com.theathletic.feed.compose.ui.items.LayoutHeader
import com.theathletic.feed.compose.ui.items.LayoutHeaderUiModel
import com.theathletic.feed.compose.ui.layoutUiModel
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DayNightPreview

internal data class HeadlinesLayoutUiModel(private val layout: LayoutUiModel) : LayoutUiModel by layout {
    override val items: List<HeadlineUiModel> = layout.items.mapNotNull { it as? HeadlineUiModel }
}

@Composable
internal fun HeadlinesLayout(layout: HeadlinesLayoutUiModel, itemInteractor: ItemInteractor) {
    if (layout.items.isEmpty()) {
        return
    }

    Column(
        modifier = Modifier
            .background(color = AthTheme.colors.dark200)
            .padding(
                top = 24.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 24.dp
            )
    ) {
        LayoutHeader(
            uiModel = LayoutHeaderUiModel(
                title = layout.title,
                icon = layout.icon,
                deepLink = layout.deepLink,
                actionText = ""
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        layout.items.forEach {
            Headline(it, itemInteractor)
        }
    }
}

@DayNightPreview
@Composable
private fun HeadlineLayoutPreview(
    @PreviewParameter(HeadlineLayoutPreviewProvider::class) headlines: HeadlinesLayoutUiModel
) {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        HeadlinesLayout(
            layout = headlines,
            itemInteractor = ItemInteractor()
        )
    }
}

internal class HeadlineLayoutPreviewProvider : PreviewParameterProvider<HeadlinesLayoutUiModel> {
    override val values: Sequence<HeadlinesLayoutUiModel> = sequenceOf(
        headlineLayoutPreviewData(id = "1", title = "Top News")
    )

    private fun headlinePreviewData(id: String, title: String): HeadlineUiModel = HeadlineUiModel(
        id = id,
        title = title,
        image = "",
        permalink = "",
        analyticsData = analyticsPreviewData()
    )

    private fun headlineLayoutPreviewData(id: String, title: String): HeadlinesLayoutUiModel {
        return HeadlinesLayoutUiModel(
            layoutUiModel(
                id = id,
                title = title,
                items = listOf(
                    headlinePreviewData(
                        id = "2",
                        title = "Padres defeated again"
                    ),
                    headlinePreviewData(
                        id = "3",
                        title = "Cubs running out of time to turn their once-promising season around"
                    )
                )
            )
        )
    }
}