package com.theathletic.boxscore.ui.modules

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.theathletic.boxscore.ui.playbyplay.Play
import com.theathletic.data.SizedImages
import com.theathletic.feed.ui.FeedModule
import com.theathletic.themes.AthleticTheme

data class PlayModule(
    val id: String,
    val teamLogos: SizedImages,
    val teamColor: String? = null,
    val title: String?,
    val description: String,
    val clock: String,
    val awayTeamAlias: String? = null,
    val homeTeamAlias: String? = null,
    val awayTeamScore: String? = null,
    val homeTeamScore: String? = null,
    val showScores: Boolean = false,
    val showTeamCurtain: Boolean = false,
    val showDivider: Boolean
) : FeedModule {

    @Composable
    override fun Render() {
        Play(
            teamLogos = teamLogos,
            teamColor = teamColor,
            title = title,
            description = description,
            clock = clock,
            awayTeamAlias = awayTeamAlias,
            homeTeamAlias = homeTeamAlias,
            awayTeamScore = awayTeamScore,
            homeTeamScore = homeTeamScore,
            showScores = showScores,
            showTeamCurtain = showTeamCurtain,
            showDivider = showDivider
        )
    }
}

@Preview
@Composable
fun PlayPreview() {
    PlayModule(
        id = "uniqueId",
        teamLogos = emptyList(),
        title = "Defensive Rebound",
        description = "Tre Jones defensive rebound",
        clock = "1:32",
        awayTeamAlias = "SAS",
        homeTeamAlias = "LAL",
        awayTeamScore = "37",
        homeTeamScore = "31",
        showScores = false,
        showDivider = true
    ).Render()
}

@Preview
@Composable
fun PlayPreview_NoTitle() {
    PlayModule(
        id = "uniqueId",
        teamLogos = emptyList(),
        title = null,
        description = "Tre Jones defensive rebound",
        clock = "1:32",
        awayTeamAlias = "SAS",
        homeTeamAlias = "LAL",
        awayTeamScore = "37",
        homeTeamScore = "31",
        showScores = false,
        showDivider = true
    ).Render()
}

@Preview
@Composable
fun PlayPreview_Light() {
    AthleticTheme(lightMode = true) {
        PlayModule(
            id = "uniqueId",
            teamLogos = emptyList(),
            title = "Defensive Rebound",
            description = "Jimmy Butler makes two point turnaround jump shot",
            clock = "1:32",
            awayTeamAlias = "SAS",
            homeTeamAlias = "LAL",
            awayTeamScore = "37",
            homeTeamScore = "31",
            showScores = true,
            showDivider = true
        ).Render()
    }
}