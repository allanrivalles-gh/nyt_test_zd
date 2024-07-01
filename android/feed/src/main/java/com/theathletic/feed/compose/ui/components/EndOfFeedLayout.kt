package com.theathletic.feed.compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.theathletic.feed.R
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DayNightPreview

@Composable
internal fun EndOfFeedLayout(onScrollToTopClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable { onScrollToTopClick() }
            .fillMaxWidth()
            .background(AthTheme.colors.dark100)
            .padding(vertical = 32.dp)
    ) {
        Text(
            text = stringResource(id = R.string.feed_you_reached_the_end),
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark500,
        )
        Text(
            text = stringResource(id = R.string.feed_back_to_top),
            style = AthTextStyle.Calibre.Utility.Medium.Small,
            color = AthTheme.colors.dark700,
            modifier = Modifier
                .padding(start = 4.dp),
        )
    }
}

@DayNightPreview
@Composable
private fun EndOfFeedPreview() {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        EndOfFeedLayout(onScrollToTopClick = {})
    }
}