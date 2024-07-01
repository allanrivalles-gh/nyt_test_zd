package com.theathletic.boxscore.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.playbyplay.HockeyShootoutPlay
import com.theathletic.boxscore.ui.playbyplay.Play
import com.theathletic.data.SizedImages
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.UiModel
import com.theathletic.ui.asResourceString
import com.theathletic.ui.asString

sealed class ScoringSummaryUi {
    data class Title(
        val title: ResourceString
    ) : ScoringSummaryUi()

    data class Play(
        val id: String,
        val teamLogos: SizedImages,
        val title: String?,
        val description: String,
        val clock: String,
        val awayTeamAlias: String? = null,
        val homeTeamAlias: String? = null,
        val awayTeamScore: String? = null,
        val homeTeamScore: String? = null,
        val showScores: Boolean = false,
        val showDivider: Boolean
    ) : ScoringSummaryUi()

    data class HockeyShootoutPlay(
        val id: String,
        val headshots: SizedImages,
        val teamLogos: SizedImages,
        val teamColor: Color,
        val playerName: String,
        val teamAlias: String,
        val description: String,
        val isGoal: Boolean,
        val showDivider: Boolean
    ) : ScoringSummaryUi()
}

data class ScoringSummaryUiModel(
    val id: String,
    val includeDivider: Boolean,
    val plays: List<ScoringSummaryUi>
) : UiModel {
    override val stableId = "ScoringSummary:$id"
}

@Composable
fun ScoringSummary(
    includeHeaderDivider: Boolean,
    plays: List<ScoringSummaryUi>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AthTheme.colors.dark200)
    ) {

        if (includeHeaderDivider) {
            BoxScoreHeaderDivider()
        }

        BoxScoreHeaderTitle(R.string.box_score_scoring_summary_title)

        plays.forEach {
            when (it) {
                is ScoringSummaryUi.Title -> {
                    PeriodHeader(title = it.title.asString())
                }
                is ScoringSummaryUi.Play -> {
                    Play(
                        teamLogos = it.teamLogos,
                        teamColor = null,
                        title = it.title,
                        description = it.description,
                        clock = it.clock,
                        awayTeamAlias = it.awayTeamAlias,
                        homeTeamAlias = it.homeTeamAlias,
                        awayTeamScore = it.awayTeamScore,
                        homeTeamScore = it.homeTeamScore,
                        showScores = it.showScores,
                        showDivider = it.showDivider
                    )
                }
                is ScoringSummaryUi.HockeyShootoutPlay -> {
                    HockeyShootoutPlay(
                        headshots = it.headshots,
                        teamLogos = it.teamLogos,
                        teamColor = it.teamColor,
                        playerName = it.playerName,
                        teamAlias = it.teamAlias,
                        description = it.description,
                        isGoal = it.isGoal,
                        showDivider = it.showDivider
                    )
                }
            }
        }
    }
}

@Composable
private fun PeriodHeader(title: String) {
    Text(
        text = title,
        color = AthTheme.colors.dark500,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .padding(top = 12.dp),
        style = AthTextStyle.Calibre.Utility.Medium.Large,
        textAlign = TextAlign.Start
    )
}

@Preview
@Composable
private fun ScoringSummary_Preview() {
    val p1 = ScoringSummaryUi.Title("Period 1".asResourceString())
    val p2 = ScoringSummaryUi.Title("Period 2".asResourceString())
    val p3 = ScoringSummaryUi.Title("Shootout".asResourceString())

    val play = ScoringSummaryUi.Play(
        id = "uniqueId",
        teamLogos = emptyList(),
        title = "Defensive Rebound",
        description = "Tre Jones defensive rebound",
        clock = "1:32",
        awayTeamAlias = "SAS",
        homeTeamAlias = "LAL",
        awayTeamScore = "37",
        homeTeamScore = "31",
        showScores = true,
        showDivider = false
    )

    val shootout = ScoringSummaryUi.HockeyShootoutPlay(
        id = "uniqueId",
        headshots = emptyList(),
        teamLogos = emptyList(),
        teamColor = Color.Cyan,
        playerName = "A.Beauvillier",
        teamAlias = "NYI",
        description = "Shot saved by A. Georgiev",
        isGoal = false,
        showDivider = true
    )

    ScoringSummary(
        true,
        listOf(p1, play, p2, play.copy(showDivider = true), play, p3, shootout)
    )
}

@Preview
@Composable
private fun ScoringSummary_PreviewLight() {
    val p1 = ScoringSummaryUi.Title("Period 1".asResourceString())
    val p2 = ScoringSummaryUi.Title("Period 2".asResourceString())
    val p3 = ScoringSummaryUi.Title("Shootout".asResourceString())

    val play = ScoringSummaryUi.Play(
        id = "uniqueId",
        teamLogos = emptyList(),
        title = "Defensive Rebound",
        description = "Tre Jones defensive rebound",
        clock = "1:32",
        awayTeamAlias = "SAS",
        homeTeamAlias = "LAL",
        awayTeamScore = "37",
        homeTeamScore = "31",
        showScores = true,
        showDivider = false
    )

    val shootout = ScoringSummaryUi.HockeyShootoutPlay(
        id = "uniqueId",
        headshots = emptyList(),
        teamLogos = emptyList(),
        teamColor = Color.Cyan,
        playerName = "A.Beauvillier",
        teamAlias = "NYI",
        description = "Shot saved by A. Georgiev",
        isGoal = true,
        showDivider = true
    )

    AthleticTheme(lightMode = true) {
        ScoringSummary(
            true,
            listOf(p1, play, p2, play.copy(showDivider = true), play, p3, shootout)
        )
    }
}