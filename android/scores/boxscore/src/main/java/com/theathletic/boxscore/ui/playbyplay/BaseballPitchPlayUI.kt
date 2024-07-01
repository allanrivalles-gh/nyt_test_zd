package com.theathletic.boxscore.ui.playbyplay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme

@Composable
internal fun BaseballPitchPlay(
    title: String,
    description: String?,
    pitchNumber: Int,
    pitchOutcomeColor: Color,
    occupiedBases: List<Int>,
    hitZone: Int?,
    pitchZone: Int?
) {
    Row(
        modifier = Modifier.background(AthTheme.colors.dark200)
    ) {
        PitchNumber(
            pitchNumber = pitchNumber,
            pitchOutcomeColor = pitchOutcomeColor,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        PitchDescription(
            title = title,
            description = description,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
                .padding(start = 12.dp, end = 4.dp)
        )
        PitchOutcomeIcons(
            occupiedBases = occupiedBases,
            hitZone = hitZone,
            pitchZone = pitchZone,
            pitchOutcomeColor = pitchOutcomeColor,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
internal fun IndentedBaseballPitchPlay(
    title: String,
    description: String?,
    pitchNumber: Int,
    pitchOutcomeColor: Color,
    occupiedBases: List<Int>,
    hitZone: Int?,
    pitchZone: Int?
) {
    Row(
        modifier = Modifier
            .background(AthTheme.colors.dark200)
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Spacer(modifier = Modifier.width(56.dp))
        BaseballPitchPlay(
            title = title,
            description = description,
            pitchNumber = pitchNumber,
            pitchOutcomeColor = pitchOutcomeColor,
            occupiedBases = occupiedBases,
            hitZone = hitZone,
            pitchZone = pitchZone
        )
    }
}

@Composable fun IndentedStandardPlay(
    description: String
) {
    Row(
        modifier = Modifier
            .background(AthTheme.colors.dark200)
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Spacer(modifier = Modifier.width(85.dp))
        Text(
            text = description,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark800
        )
    }
}

@Composable
private fun PitchNumber(
    pitchNumber: Int,
    pitchOutcomeColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(16.dp)
            .drawBehind {
                val width = size.width
                val height = size.height
                drawCircle(
                    color = pitchOutcomeColor,
                    radius = height / 2,
                    center = Offset(width / 2, height / 2)
                )
            }
    ) {
        Text(
            text = pitchNumber.toString(),
            style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall,
            color = AthColor.Gray800,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 1.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
private fun PitchDescription(
    title: String,
    description: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark800

        )
        if (description != null) {
            Text(
                text = description,
                style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                color = AthTheme.colors.dark500
            )
        }
    }
}

@Composable
private fun PitchOutcomeIcons(
    occupiedBases: List<Int>,
    hitZone: Int?,
    pitchZone: Int?,
    pitchOutcomeColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.requiredWidth(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BaseballOccupiedBases(
            occupiedBases = occupiedBases,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        BaseballPitchZone(
            pitchZone = pitchZone,
            pitchOutcomeColor = pitchOutcomeColor
        )
        BaseballHitZone(hitZone = hitZone)
    }
}

@Composable
@Preview
fun BaseballPitchPlayPreview() {
    Box(modifier = Modifier.width(250.dp)) {
        BaseballPitchPlay(
            title = "Fly Out",
            description = "91mph four seam FB",
            pitchNumber = 4,
            pitchOutcomeColor = PlayByPlayColors.BaseballHitStatus,
            occupiedBases = listOf(1, 3),
            hitZone = 15,
            pitchZone = 7
        )
    }
}

@Composable
@Preview
fun BaseballPitchPlayPreview_NoHitZone() {
    Box(modifier = Modifier.width(250.dp)) {
        BaseballPitchPlay(
            title = "Foul Ball",
            description = null,
            pitchNumber = 3,
            pitchOutcomeColor = AthTheme.colors.dark500,
            occupiedBases = listOf(2),
            hitZone = null,
            pitchZone = 131
        )
    }
}

@Composable
@Preview
fun BaseballPitchPlayPreview_Light() {
    AthleticTheme(lightMode = true) {
        Box(modifier = Modifier.width(250.dp)) {
            BaseballPitchPlay(
                title = "Fly Out",
                description = "91mph four seam FB",
                pitchNumber = 2,
                pitchOutcomeColor = AthTheme.colors.red,
                occupiedBases = emptyList(),
                hitZone = 35,
                pitchZone = 1
            )
        }
    }
}

@Composable
@Preview
fun BaseballPitchPlayPreview_LongDescription() {
    Box(modifier = Modifier.width(250.dp)) {
        BaseballPitchPlay(
            title = "Fly Out",
            description = "91mph four seam FB, which is pretty fast in my books but hey what do I really know about baseball",
            pitchNumber = 1,
            pitchOutcomeColor = AthTheme.colors.green,
            occupiedBases = listOf(1),
            hitZone = 15,
            pitchZone = 7
        )
    }
}

@Preview
@Composable
fun BaseballPitchPlayModulePreview() {
    IndentedBaseballPitchPlay(
        title = "Strike Looking",
        description = "92mph slider",
        pitchNumber = 3,
        pitchOutcomeColor = AthTheme.colors.green,
        occupiedBases = listOf(2),
        hitZone = 16,
        pitchZone = 5
    )
}

@Preview
@Composable
fun IndentedStandardPlayPreview() {
    IndentedStandardPlay(
        description = "Ramírez scored on passed ball by Roberto Perez."
    )
}