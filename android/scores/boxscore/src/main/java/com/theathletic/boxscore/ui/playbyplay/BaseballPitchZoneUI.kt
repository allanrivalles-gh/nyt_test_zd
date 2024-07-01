package com.theathletic.boxscore.ui.playbyplay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTheme

@Composable
internal fun BaseballPitchZone(
    pitchZone: Int?,
    pitchOutcomeColor: Color
) {
    Box(modifier = Modifier.size(24.dp)) {
        if (pitchZone != null) {
            PitchingGrid()
            PitchZone(
                pitchZone = pitchZone,
                pitchOutcomeColor = pitchOutcomeColor
            )
        }
    }
}

@Composable
private fun PitchingGrid() {
    val gridColor = AthTheme.colors.dark400
    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridWidth = size.width
        val gridHeight = size.height
        drawLine(
            start = Offset(0f, (gridHeight * 0.2).toFloat()),
            end = Offset(gridWidth, (gridHeight * 0.2).toFloat()),
            color = gridColor
        )
        drawLine(
            start = Offset(0f, (gridHeight * 0.8).toFloat()),
            end = Offset(gridWidth, (gridHeight * 0.8).toFloat()),
            color = gridColor
        )
        drawLine(
            start = Offset((gridHeight * 0.2).toFloat(), 0f),
            end = Offset((gridHeight * 0.2).toFloat(), gridHeight),
            color = gridColor
        )
        drawLine(
            start = Offset((gridHeight * 0.8).toFloat(), 0f),
            end = Offset((gridHeight * 0.8).toFloat(), gridHeight),
            color = gridColor
        )
    }
}

@Composable
private fun PitchZone(
    pitchZone: Int,
    pitchOutcomeColor: Color
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val (ratioX, ratioY) = pitchZone.toGridRatio()
        val hitX = size.width * ratioX
        val hitY = size.height * ratioY
        drawCircle(
            color = pitchOutcomeColor,
            center = Offset(
                x = hitX,
                y = hitY
            ),
            radius = 2.dp.toPx()
        )
    }
}

private fun Int.toGridRatio(): Offset {
    val (gridX, gridY) = when (this) {
        1 -> Pair(3, 3)
        2 -> Pair(5, 3)
        3 -> Pair(7, 3)
        4 -> Pair(3, 5)
        5 -> Pair(5, 5)
        6 -> Pair(7, 5)
        7 -> Pair(3, 7)
        8 -> Pair(5, 7)
        9 -> Pair(7, 7)
        101 -> Pair(3, 1)
        102 -> Pair(5, 1)
        103 -> Pair(7, 1)
        111 -> Pair(9, 1)
        112 -> Pair(9, 3)
        113 -> Pair(9, 5)
        114 -> Pair(9, 7)
        115 -> Pair(9, 9)
        121 -> Pair(3, 9)
        122 -> Pair(5, 9)
        123 -> Pair(7, 9)
        131 -> Pair(1, 1)
        132 -> Pair(1, 3)
        133 -> Pair(1, 5)
        134 -> Pair(1, 7)
        135 -> Pair(1, 9)
        else -> Pair(0, 0)
    }
    return Offset(
        gridX / 10f,
        gridY / 10f
    )
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_1() {
    BaseballPitchZone(1, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_2() {
    BaseballPitchZone(2, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_3() {
    BaseballPitchZone(3, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_4() {
    BaseballPitchZone(4, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_5() {
    BaseballPitchZone(5, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_6() {
    BaseballPitchZone(6, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_7() {
    BaseballPitchZone(7, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_8() {
    BaseballPitchZone(8, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_9() {
    BaseballPitchZone(9, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_101() {
    BaseballPitchZone(101, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_102() {
    BaseballPitchZone(102, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_103() {
    BaseballPitchZone(103, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_111() {
    BaseballPitchZone(111, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_112() {
    BaseballPitchZone(112, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_113() {
    BaseballPitchZone(113, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_114() {
    BaseballPitchZone(114, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_115() {
    BaseballPitchZone(115, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_121() {
    BaseballPitchZone(121, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_122() {
    BaseballPitchZone(122, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_123() {
    BaseballPitchZone(123, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_131() {
    BaseballPitchZone(131, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_132() {
    BaseballPitchZone(132, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_133() {
    BaseballPitchZone(133, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_134() {
    BaseballPitchZone(134, AthTheme.colors.green)
}

@Preview
@Composable
fun BaseballPitchZonePreview_Pitch_Zone_135() {
    BaseballPitchZone(135, AthTheme.colors.green)
}