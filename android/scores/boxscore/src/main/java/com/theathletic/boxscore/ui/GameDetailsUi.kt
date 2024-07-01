package com.theathletic.boxscore.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.modules.GameDetailsModule
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.UiModel
import com.theathletic.ui.asString

@Deprecated("Now using FeedModule not UiModel")
data class BoxScoreGameDetailsUiModel(
    val id: String,
    val includeDivider: Boolean,
    val gameDetailsList: List<GameDetailItem>

) : UiModel {
    override val stableId = "BoxScoreGameDetails:$id"

    data class GameDetailItem(
        val label: ResourceString,
        val value: ResourceString,
        val showDivider: Boolean = true
    )
}

@Deprecated("Use GameDetails below")
@Composable
fun GameDetails(
    includeHeaderDivider: Boolean,
    includeFooterDivider: Boolean,
    gameDetailsList: List<BoxScoreGameDetailsUiModel.GameDetailItem>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Transparent)
    ) {

        if (includeHeaderDivider) {
            BoxScoreHeaderDivider()
        }

        BoxScoreHeaderTitle(R.string.box_score_game_details_title)
        gameDetailsList.forEach { item ->
            GameDetailsItemRow(
                label = item.label,
                value = item.value,
                showDivider = item.showDivider
            )
        }
        BoxScoreFooterDivider(includeBottomBar = includeFooterDivider)
    }
}

@Composable
fun GameDetails(
    details: List<GameDetailsModule.DetailsItem>,
    @StringRes titleResId: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Transparent)
    ) {
        BoxScoreHeaderTitle(
            titleResId
        )
        details.forEach { item ->
            GameDetailsItemRow(
                label = item.label,
                value = item.value,
                showDivider = item.showDivider
            )
        }
    }
}

@Composable
fun GameDetailsItemRow(
    label: ResourceString,
    value: ResourceString,
    showDivider: Boolean
) {

    Column(
        modifier = Modifier
            .background(AthTheme.colors.dark200)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 52.dp)
            .padding(horizontal = 16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = label.asString(),
                color = AthTheme.colors.dark600,
                style = AthTextStyle.Calibre.Utility.Regular.Large
            )

            Text(
                text = value.asString(),
                color = AthTheme.colors.dark800,
                style = AthTextStyle.Calibre.Utility.Medium.Large,
                modifier = Modifier
                    .padding(start = 8.dp),
                textAlign = TextAlign.End
            )
        }

        if (showDivider) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = AthTheme.colors.dark300)
            )
        }
    }
}

@Preview
@Composable
private fun GameDetails_Preview() {
    GameDetails(
        details = BoxScorePreviewData.GameDetailsItems,
        titleResId = R.string.box_score_game_details_title
    )
}

@Preview
@Composable
private fun GameDetails_PreviewLight() {
    AthleticTheme(lightMode = true) {
        GameDetails(
            details = BoxScorePreviewData.GameDetailsItems,
            titleResId = R.string.box_score_match_details_title
        )
    }
}