package com.theathletic.boxscore.ui.playergrades

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.playergrades.PlayerGradePreviewData.gradedMiniCardModule
import com.theathletic.boxscore.ui.playergrades.PlayerGradePreviewData.lockedUngradedMiniCardModule
import com.theathletic.boxscore.ui.playergrades.PlayerGradePreviewData.ungradedMiniCardModule
import com.theathletic.boxscore.ui.playergrades.PlayerGradePreviewData.unlockedUngradedMiniCardModule
import com.theathletic.data.SizedImages
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.utility.parseHexColor
import com.theathletic.ui.widgets.Headshot

data class PlayerGradeMiniCardModel(
    val id: String,
    val playerName: String,
    val playerStats: String,
    val playerHeadshot: SizedImages,
    val teamLogos: SizedImages,
    val teamColor: String?,
    val awardedGrade: Int,
    val averageGrade: String,
    val totalGrades: Int,
    val isLocked: Boolean,
    val isGraded: Boolean
) {
    interface Interaction {
        data class OnGradePlayer(val playerId: String, val grade: Int) : FeedInteraction
        data class OnNavigateToPlayerGradeDetailScreen(val playerId: String) : FeedInteraction
    }
}

@Composable
fun PlayerGradeMiniCard(playerGradeMiniCard: PlayerGradeMiniCardModel) {
    val interactor = LocalFeedInteractor.current
    val isGradedOrLocked = playerGradeMiniCard.isGraded || playerGradeMiniCard.isLocked
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AthTheme.colors.dark200)
            .clickable {
                interactor.send(
                    PlayerGradeMiniCardModel.Interaction.OnNavigateToPlayerGradeDetailScreen(playerGradeMiniCard.id)
                )
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Headshot(
            headshotsUrls = playerGradeMiniCard.playerHeadshot,
            teamUrls = playerGradeMiniCard.teamLogos,
            teamColor = playerGradeMiniCard.teamColor.parseHexColor(AthTheme.colors.dark500),
            preferredSize = 48.dp,
            modifier = Modifier.size(48.dp)
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            when {
                isGradedOrLocked -> GradedOrLocked(playerGradeMiniCard)
                else -> Ungraded(playerGradeMiniCard)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GradedOrLocked(playerGradeMiniCard: PlayerGradeMiniCardModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .padding(end = 8.dp)
                .weight(1f)
        ) {
            PlayerNameAndStats(
                name = playerGradeMiniCard.playerName,
                position = playerGradeMiniCard.playerStats,
            )
            GradeIndicator(
                playerGradeMiniCard.awardedGrade,
                playerGradeMiniCard.isGraded,
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AverageAndTotalGradeIndicator(
                averageGrade = playerGradeMiniCard.averageGrade,
                hasGrades = playerGradeMiniCard.totalGrades > 0,
                modifier = Modifier
                    .background(color = AthTheme.colors.dark200)
            )
            Text(
                text = pluralStringResource(
                    id = R.plurals.plural_grades,
                    count = playerGradeMiniCard.totalGrades,
                    playerGradeMiniCard.totalGrades
                ),
                style = AthTextStyle.Calibre.Utility.Regular.Small,
                color = AthTheme.colors.dark500,
                textAlign = TextAlign.Right,
            )
        }
    }
}

@Composable
private fun Ungraded(playerGradeMiniCard: PlayerGradeMiniCardModel) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        PlayerNameAndStats(
            name = playerGradeMiniCard.playerName,
            position = playerGradeMiniCard.playerStats,
            Modifier
                .weight(0.55f)
                .padding(start = 16.dp),
            horizontalAlignment = Alignment.Start
        )
        GradeBar(
            playerId = playerGradeMiniCard.id,
            grading = playerGradeMiniCard.awardedGrade,
            modifier = Modifier
                .weight(0.45f)
                .padding(start = 8.dp)
        )
    }
}

@Composable
private fun PlayerNameAndStats(
    name: String,
    position: String,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
) {
    Column(
        modifier = Modifier.then(modifier), horizontalAlignment = horizontalAlignment,
    ) {
        Text(
            text = name,
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            color = AthTheme.colors.dark700,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Text(
            text = position,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark500
        )
    }
}

@Composable
private fun GradeBar(
    playerId: String,
    grading: Int,
    modifier: Modifier,
) {
    val interactor = LocalFeedInteractor.current
    var interactionSource = remember { MutableInteractionSource() }

    val maxGrading = 5
    Row(
        modifier = Modifier.then(modifier),
        horizontalArrangement = Arrangement.End
    ) {
        repeat(grading) {
            Icon(
                Icons.Default.Grade,
                contentDescription = null,
                tint = AthTheme.colors.dark800,
                modifier = Modifier
                    .size(28.dp)
                    .weight(1f)
            )
        }
        repeat(maxGrading - grading) { index ->
            Icon(
                Icons.Outlined.Grade,
                contentDescription = null,
                tint = AthTheme.colors.dark800,
                modifier = Modifier
                    .size(28.dp)
                    .weight(1f)
                    .clickable(
                        interactionSource.also { interactionSource = it },
                        indication = null
                    ) {
                        interactor.send(
                            PlayerGradeMiniCardModel.Interaction.OnGradePlayer(
                                playerId = playerId,
                                grade = grading + index.inc()
                            )
                        )
                    }
            )
        }
    }
}

@Preview
@Composable
private fun PlayerGrades_Locked_Preview() {
    lockedUngradedMiniCardModule.Render()
}

@Preview(device = Devices.PIXEL)
@Composable
private fun PlayerGrades_Graded_SmallDevice_Preview() {
    gradedMiniCardModule.Render()
}

@Preview
@Composable
private fun PlayerGrades_Ungraded_PreviewLight() {
    AthleticTheme(lightMode = true) {
        ungradedMiniCardModule.Render()
    }
}

@Preview(device = Devices.PHONE)
@Composable
private fun PlayerGrades_Unlocked_Ungraded_PreviewLight_Small() {
    AthleticTheme(lightMode = true) {
        unlockedUngradedMiniCardModule.Render()
    }
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun PlayerGrades_Unlocked_Ungraded_PreviewLight_Large() {
    AthleticTheme(lightMode = true) {
        unlockedUngradedMiniCardModule.Render()
    }
}