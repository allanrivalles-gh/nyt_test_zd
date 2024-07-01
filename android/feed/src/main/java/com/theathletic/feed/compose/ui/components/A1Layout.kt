package com.theathletic.feed.compose.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.analyticsPreviewData
import com.theathletic.feed.compose.ui.header
import com.theathletic.feed.compose.ui.interaction.ItemInteractor
import com.theathletic.feed.compose.ui.interaction.interactive
import com.theathletic.feed.compose.ui.items.A1
import com.theathletic.feed.compose.ui.items.A1UiModel
import com.theathletic.feed.compose.ui.items.LayoutHeader
import com.theathletic.feed.compose.ui.layoutUiModel
import com.theathletic.feed.ui.models.SeeAllAnalyticsPayload
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme

data class A1LayoutUiModel(
    private val layout: LayoutUiModel,
    val seeAllAnalyticsPayload: SeeAllAnalyticsPayload
) : LayoutUiModel by layout {
    override val items: List<A1UiModel> = layout.items.mapNotNull { (it as? A1UiModel) }
}

@Composable
fun A1Layout(layout: A1LayoutUiModel, interactor: ItemInteractor) {
    if (layout.items.isEmpty()) return

    Column(
        modifier = Modifier
            .background(AthTheme.colors.dark200)
            .padding(vertical = 24.dp)
    ) {
        LayoutHeader(
            uiModel = layout.header,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 20.dp),
            onActionClick = { interactor.onSeeAllClick(it, layout.seeAllAnalyticsPayload) }
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(layout.items) { uiModel ->
                A1(uiModel, modifier = Modifier.interactive(uiModel, interactor))
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun A1LayoutPreview(
    @PreviewParameter(A1LayoutPreviewParamProvider::class)
    uiModel: A1LayoutUiModel
) {
    return AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        A1Layout(uiModel, ItemInteractor())
    }
}

private class A1LayoutPreviewParamProvider :
    PreviewParameterProvider<A1LayoutUiModel> {

    override val values: Sequence<A1LayoutUiModel> = sequenceOf(
        getUiModel(action = "See All", deepLink = "https://seealllink"),
        getUiModel()
    )

    fun getUiModel(action: String = "", deepLink: String = "") = A1LayoutUiModel(
        layoutUiModel(
            id = "a1LayoutId",
            title = "Today's Must-Read",
            items = listOf(
                getA1UiModel(),
                getA1UiModel(isRead = true, commentCount = "1k"),
                getA1UiModel(isBookmarked = true)
            ),
            deepLink = deepLink,
            action = action
        ),
        SeeAllAnalyticsPayload("", 0)
    )

    fun getA1UiModel(
        isRead: Boolean = false,
        isBookmarked: Boolean = false,
        commentCount: String = "125"
    ) = A1UiModel(
        id = "a1Id",
        imageUrl = "",
        title = "Top 10 mock draft with The Athletic NFL Staff",
        commentCount = commentCount,
        byLine = "Marc Mazzoni and Jonathan Stewart",
        avatars = listOf("", ""),
        isBookmarked = isBookmarked,
        isRead = isRead,
        publishDate = "5 Jun",
        permalink = "",
        analyticsData = analyticsPreviewData()
    )
}