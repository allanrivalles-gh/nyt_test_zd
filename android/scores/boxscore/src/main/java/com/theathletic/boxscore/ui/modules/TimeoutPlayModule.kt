package com.theathletic.boxscore.ui.modules

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.theathletic.boxscore.ui.playbyplay.TimeoutPlay
import com.theathletic.feed.ui.FeedModule
import com.theathletic.themes.AthleticTheme

data class TimeoutPlayModule(
    val id: String,
    val title: String,
    val showDivider: Boolean
) : FeedModule {

    @Composable
    override fun Render() {
        TimeoutPlay(
            title = title,
            showDivider = showDivider
        )
    }
}

@Preview
@Composable
fun TimeoutPlayPreview() {
    TimeoutPlayModule(
        id = "uniqueId",
        title = "TV Timeout",
        showDivider = true
    ).Render()
}

@Preview
@Composable
fun TimeoutPlayPreview_Light() {
    AthleticTheme(lightMode = true) {
        TimeoutPlayModule(
            id = "uniqueId",
            title = "TV Timeout",
            showDivider = true
        ).Render()
    }
}