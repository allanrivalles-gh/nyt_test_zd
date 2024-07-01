package com.theathletic.boxscore.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.BoxScorePreviewData.downAndDistanceMockModule
import com.theathletic.data.SizedImages
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.TeamLogo

@Composable
fun DownAndDistance(
    teamLogos: SizedImages,
    title: ResourceString,
    subtitle: ResourceString
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AthTheme.colors.dark200)
    ) {

        Text(
            text = stringResource(R.string.box_score_down_and_distance_title),
            color = AthTheme.colors.dark700,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            style = AthTextStyle.Slab.Bold.Small
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp),
        ) {

            TeamLogo(
                teamUrls = teamLogos,
                preferredSize = 32.dp,
                modifier = Modifier.size(32.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            ) {

                Text(
                    text = title.asString(),
                    style = AthTextStyle.Calibre.Utility.Medium.Large,
                    color = AthTheme.colors.dark800,
                )

                Text(
                    text = subtitle.asString(),
                    style = AthTextStyle.Calibre.Utility.Regular.Small,
                    color = AthTheme.colors.dark500,
                    modifier = Modifier.padding(
                        top = 2.dp,
                        bottom = 16.dp
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun DownAndDistance_Preview() {
    downAndDistanceMockModule.Render()
}

@Preview
@Composable
private fun DownAndDistance_PreviewLight() {
    AthleticTheme(lightMode = true) {
        downAndDistanceMockModule.Render()
    }
}