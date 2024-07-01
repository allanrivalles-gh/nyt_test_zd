package com.theathletic.feed.compose.ui.items.insiders

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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.theathletic.feed.compose.ui.items.LayoutHeader
import com.theathletic.feed.compose.ui.items.LayoutHeaderUiModel
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DayNightPreview

data class InsidersLayoutUiModel(val title: String, val items: List<InsiderUiModel>)

@Composable
fun InsidersLayout(uiModel: InsidersLayoutUiModel) {
    if (uiModel.items.isEmpty()) {
        return
    }

    Column(
        modifier = Modifier
            .background(AthTheme.colors.dark200)
            .padding(vertical = 24.dp)
    ) {
        LayoutHeader(
            uiModel = LayoutHeaderUiModel(
                title = uiModel.title,
                icon = ""
            ),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 20.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(uiModel.items) {
                Insider(it)
            }
        }
    }
}

@DayNightPreview
@Composable
private fun InsidersLayoutPreview(
    @PreviewParameter(InsidersLayoutPreviewParamProvider::class)
    uiModel: InsidersLayoutUiModel
) {
    return AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        InsidersLayout(uiModel)
    }
}

private class InsidersLayoutPreviewParamProvider :
    PreviewParameterProvider<InsidersLayoutUiModel> {

    override val values: Sequence<InsidersLayoutUiModel> = sequenceOf(
        getUiModel(4),
        getUiModel(1)
    )

    fun getUiModel(count: Int) = InsidersLayoutUiModel(
        title = "Insiders",
        items = listOf(
            getInsiderUiModel(
                author = "Marcus Thompson II",
                authorRole = "Correspondent, Wolverhampton Wolves",
                excerpt = "Malcolm Jenkins: NFL won't get it right until it " +
                    "specifically addresses deflated footballs."
            ),
            getInsiderUiModel(
                author = "Adam Crafto",
                authorRole = "Senior NBA Insider",
                excerpt = "Short to test alignment."
            ),
            getInsiderUiModel(
                author = "Daniel Kaplan",
                authorRole = "Staff Writer, Sports Business",
                excerpt = "NFL will loosen restrictions on vaccinated fans in ticket guidelines."
            ),
            getInsiderUiModel(
                author = "James Horncastle",
                authorRole = "Senior NBA Insider",
                excerpt = "Malcolm Jenkins: NFL won't get it right until it " +
                    "specifically addresses deflated footballs."
            )
        ).take(count)
    )

    fun getInsiderUiModel(
        imageUrl: String = "",
        author: String = "Marcus Thompson II",
        authorRole: String = "Correspondent, Wolverhampton Wolves",
        excerpt: String = "Malcolm Jenkins: NFL won't get it right until it " +
            "specifically addresses deflated footballs.",
        lastUpdated: String = "2d ago",
        commentCount: String = "125"
    ) = InsiderUiModel(
        imageUrl = imageUrl,
        author = author,
        authorRole = authorRole,
        excerpt = excerpt,
        lastUpdated = lastUpdated,
        commentCount = commentCount
    )
}