package com.theathletic.boxscore.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme

data class SlideStoriesLaunchUiModel(
    val id: String
)

@Composable
fun SlideStoriesLaunchUi(
    uiModel: SlideStoriesLaunchUiModel,
    onClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AthTheme.colors.dark200)
            .clickable { onClick(uiModel.id) }
            .padding(16.dp)
    ) {
        // This layout is only temporary until the BE has an query for Slide Stories and
        // we start receiving some real data. This here is to assist with the initial work
        Text(
            text = "Launch Slide Story >",
            color = AthTheme.colors.dark700,
            style = AthTextStyle.Calibre.Headline.Medium.Small,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Preview
@Composable
private fun SlideStoriesLaunch_Preview() {
    BoxScorePreviewData.slideStoriesLaunchModule.Render()
}