package com.theathletic.boxscore.ui.modules

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.theathletic.boxscore.ui.playbyplay.StoppagePlay
import com.theathletic.feed.ui.FeedModule
import com.theathletic.themes.AthleticTheme

data class StoppagePlayModule(
    val id: String,
    val title: String,
    val description: String,
    val showDivider: Boolean
) : FeedModule {

    @Composable
    override fun Render() {
        StoppagePlay(
            title = title,
            description = description,
            showDivider = showDivider
        )
    }
}

@Preview
@Composable
fun StoppagePlayPreview() {
    StoppagePlayModule(
        id = "uniqueId",
        title = "Stoppage",
        description = "Stoppage (Out of bounds)",
        showDivider = true
    ).Render()
}

@Preview
@Composable
fun StoppagePlayPreview_Light() {
    AthleticTheme(lightMode = true) {
        StoppagePlayModule(
            id = "uniqueId",
            title = "Stoppage",
            description = "Stoppage (Out of bounds)",
            showDivider = true
        ).Render()
    }
}