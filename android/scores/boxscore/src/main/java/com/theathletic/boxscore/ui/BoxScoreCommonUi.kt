package com.theathletic.boxscore.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R

@Composable
internal fun BoxScoreHeaderTitle(@StringRes id: Int, subtitle: String? = null) {
    Column(
        modifier = Modifier
            .background(color = AthTheme.colors.dark200)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id),
            color = AthTheme.colors.dark700,
            modifier = Modifier.fillMaxWidth(),
            style = AthTextStyle.Slab.Bold.Small
        )

        subtitle?.let {
            Text(
                text = subtitle,
                color = AthTheme.colors.dark500,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                style = AthTextStyle.Calibre.Utility.Regular.Small
            )
        }
    }
}

@Composable
internal fun BoxScoreHeaderDivider() {
    Spacer(
        modifier = Modifier
            .height(4.dp)
            .fillMaxWidth()
            .background(AthTheme.colors.dark100)
    )
}

@Composable
internal fun BoxScoreFooterDivider(includeBottomBar: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = AthTheme.colors.dark200)
                .height(height = 20.dp)
        )

        if (includeBottomBar) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 4.dp)
            )
        }
    }
}

@Preview
@Composable
fun BoxScoreHeader_Preview() {
    BoxScoreHeaderTitle(R.string.box_score_game_details_title, "Premier League")
}

@Preview
@Composable
fun BoxScoreHeader_PreviewLight() {
    AthleticTheme(lightMode = true) {
        BoxScoreHeaderTitle(R.string.box_score_related_stories_title)
    }
}