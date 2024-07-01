package com.theathletic.boxscore.ui.playergrades

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.modules.PlayerGradeCardModule
import com.theathletic.data.SizedImages
import com.theathletic.feed.ui.FeedInteractor
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asString
import com.theathletic.ui.utility.isLightContrast
import com.theathletic.ui.utility.parseHexColor

sealed class PlayerGradeModel {
    data class Player(
        val id: String,
        val name: String,
        val position: String,
        val teamLogos: SizedImages,
        val headshots: SizedImages,
        val averageGrade: String,
        val totalGrades: Int,
        val stats: List<Stat>,
        val teamColor: String?
    )

    data class Stat(
        val title: ResourceString,
        val value: String
    )

    data class PlayerGradeCard(
        val player: Player,
        val isGraded: Boolean,
        val awardedGrade: Int
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Suppress("LongMethod")
@Composable
fun PlayerGradeCard(
    playerId: String,
    name: String,
    position: String,
    teamLogos: SizedImages,
    teamColor: String?,
    headshots: SizedImages,
    averageGrade: String,
    totalGrades: Int,
    awardedGrade: Int,
    stats: List<PlayerGradeModel.Stat>,
    isGraded: Boolean,
    interactor: FeedInteractor
) {
    Card(
        modifier = Modifier
            .width(320.dp)
            .clickable {
                interactor.send(PlayerGradeCardModule.Interaction.PlayerGradeDetailsClick(playerId))
            },
        backgroundColor = AthTheme.colors.dark300,
        elevation = 4.dp,
        shape = RoundedCornerShape(4.dp)
    ) {
        Column {
            val background = teamColor.parseHexColor(AthTheme.colors.dark500)
            val useDarkIcons = background.isLightContrast()
            Box(
                modifier = Modifier
                    .height(160.dp)
                    .background(background)
            ) {
                GradedIcon(isGraded, useDarkIcons)
                PlayerGradeHeadshot(
                    headshotsUrls = headshots,
                    teamUrls = teamLogos,
                    preferredSize = 160.dp
                )
            }
            Column(
                modifier = Modifier
                    .background(color = AthTheme.colors.dark300)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    PlayerNameAndPosition(
                        name = name,
                        position = position,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    AverageAndTotalGradeIndicator(
                        averageGrade = averageGrade,
                        hasGrades = totalGrades > 0,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(color = AthTheme.colors.dark300)
                    )
                }
                Box(modifier = Modifier.fillMaxWidth()) {
                    GradeIndicator(
                        awardedGrade,
                        isGraded,
                        modifier = Modifier.align(Alignment.BottomStart)
                    )
                    Text(
                        text = pluralStringResource(id = R.plurals.plural_grades, count = totalGrades, totalGrades),
                        style = AthTextStyle.Calibre.Utility.Regular.Small,
                        color = AthTheme.colors.dark500,
                        textAlign = TextAlign.Right,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                    )
                }
                Divider(
                    color = AthTheme.colors.dark400,
                    thickness = 1.dp,
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .background(color = AthTheme.colors.dark300)
                )
                PlayerStats(stats)
            }
        }
    }
}

@Composable
private fun BoxScope.GradedIcon(isGraded: Boolean, useDarkIcons: Boolean) {
    if (isGraded) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = if (useDarkIcons) {
                AthColor.Gray800
            } else {
                AthColor.Gray100
            },
            modifier = Modifier.Companion
                .align(Alignment.TopEnd)
                .padding(top = 10.dp, end = 10.dp)
        )
    }
}

@Composable
private fun PlayerStats(stats: List<PlayerGradeModel.Stat>) {
    Row(
        modifier = Modifier
            .background(color = AthTheme.colors.dark300)
            .padding(bottom = 12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        stats.forEach { stat ->
            StatsTile(stat.title, stat.value)
        }
    }
}

@Composable
private fun PlayerNameAndPosition(name: String, position: String, modifier: Modifier) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = AthTextStyle.Slab.Bold.Small,
            color = AthTheme.colors.dark700,
        )
        Text(
            text = position,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark500,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
private fun StatsTile(title: ResourceString, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = AthTextStyle.Calibre.Headline.SemiBold.Small,
            color = AthTheme.colors.dark700,
            textAlign = TextAlign.Center
        )
        Text(
            text = title.asString().uppercase(),
            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
            color = AthTheme.colors.dark500,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview
@Composable
private fun PlayerGradeCard_Preview() {
    PlayerGradePreviewData.playerGradeCardModule.Render()
}

@Preview
@Composable
private fun PlayerGradeCard_NotGraded_Preview() {
    PlayerGradePreviewData.playerGradeCardNotGradedModule.Render()
}

@Preview
@Composable
private fun PlayerGradeCard_PreviewLight() {
    AthleticTheme(lightMode = true) {
        PlayerGradePreviewData.playerGradeCardModule.Render()
    }
}