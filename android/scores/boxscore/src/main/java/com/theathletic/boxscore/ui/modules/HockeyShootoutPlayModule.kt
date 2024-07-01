package com.theathletic.boxscore.ui.modules

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.theathletic.boxscore.ui.playbyplay.HockeyShootoutPlay
import com.theathletic.data.SizedImages
import com.theathletic.feed.ui.FeedModule
import com.theathletic.themes.AthleticTheme

data class HockeyShootoutPlayModule(
    val id: String,
    val headshots: SizedImages,
    val teamLogos: SizedImages,
    val teamColor: Color,
    val playerName: String,
    val teamAlias: String,
    val description: String,
    val isGoal: Boolean,
    val showDivider: Boolean
) : FeedModule {

    @Composable
    override fun Render() {
        HockeyShootoutPlay(
            headshots = headshots,
            teamLogos = teamLogos,
            teamColor = teamColor,
            playerName = playerName,
            teamAlias = teamAlias,
            description = description,
            isGoal = isGoal,
            showDivider = showDivider
        )
    }
}

@Preview
@Composable
fun HockeyShootoutPlayPreview() {
    HockeyShootoutPlayModule(
        id = "uniqueId",
        headshots = emptyList(),
        teamLogos = emptyList(),
        teamColor = Color.Cyan,
        playerName = "A.Beauvillier",
        teamAlias = "NYI",
        description = "Shot saved by A. Georgiev",
        isGoal = true,
        showDivider = true
    ).Render()
}

@Preview
@Composable
fun HockeyShootoutPlayPreview_Light() {
    AthleticTheme(lightMode = true) {
        HockeyShootoutPlayModule(
            id = "uniqueId",
            headshots = emptyList(),
            teamLogos = emptyList(),
            teamColor = Color.Cyan,
            playerName = "A.Beauvillier",
            teamAlias = "NYI",
            description = "Shot saved by A. Georgiev",
            isGoal = false,
            showDivider = true
        ).Render()
    }
}