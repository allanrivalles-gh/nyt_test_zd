package com.theathletic.boxscore.ui.playbyplay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.ResourceIcon

@Composable
internal fun BaseballHitZone(
    hitZone: Int?
) {
    Box(modifier = Modifier.size(24.dp)) {
        if (hitZone != null) {
            ResourceIcon(
                modifier = Modifier
                    .fillMaxSize(0.8f)
                    .align(Alignment.Center),
                resourceId = R.drawable.ic_baseball_hit_zone,
                tint = AthTheme.colors.dark400
            )
            HitZone(hitZone = hitZone)
        }
    }
}

@Composable
private fun HitZone(hitZone: Int) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val (ratioX, ratioY) = hitZone.toGridRatio()
        val hitX = size.width * ratioX
        val hitY = size.height * ratioY
        drawCircle(
            color = PlayByPlayColors.BaseballHitStatus,
            center = Offset(
                x = hitX,
                y = hitY
            ),
            radius = 2.dp.toPx()
        )
    }
}

/*
    A 20x20 grid sits above the representation of the field. The
    hit zone value relates to a possible 35 positions on the field
    and this function first maps the hit zone to a 20x20 grid cells
    then returns a ratio of the hit zones width and height for displaying.
 */
private fun Int.toGridRatio(): Offset {
    val (gridX, gridY) = when (this) {
        1 -> Pair(2, 5)
        2 -> Pair(4, 3)
        3 -> Pair(10, 2)
        4 -> Pair(16, 3)
        5 -> Pair(18, 5)
        6 -> Pair(4, 8)
        7 -> Pair(7, 4)
        8 -> Pair(10, 3)
        9 -> Pair(13, 4)
        10 -> Pair(16, 8)
        11 -> Pair(5, 10)
        12 -> Pair(7, 8)
        13 -> Pair(10, 5)
        14 -> Pair(13, 8)
        15 -> Pair(15, 10)
        16 -> Pair(5, 12)
        17 -> Pair(7, 10)
        18 -> Pair(10, 7)
        19 -> Pair(13, 10)
        20 -> Pair(15, 12)
        21 -> Pair(7, 13)
        22 -> Pair(9, 11)
        23 -> Pair(11, 11)
        24 -> Pair(13, 13)
        25 -> Pair(9, 14)
        26 -> Pair(11, 14)
        27 -> Pair(10, 15)
        28 -> Pair(9, 16)
        29 -> Pair(10, 16)
        30 -> Pair(11, 16)
        31 -> Pair(4, 14)
        32 -> Pair(6, 16)
        33 -> Pair(10, 18)
        34 -> Pair(14, 16)
        35 -> Pair(16, 14)
        else -> Pair(0, 0)
    }
    return Offset(
        gridX / 20f,
        gridY / 20f
    )
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_1() {
    BaseballHitZone(hitZone = 1)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_2() {
    BaseballHitZone(hitZone = 2)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_3() {
    BaseballHitZone(hitZone = 3)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_4() {
    BaseballHitZone(hitZone = 4)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_5() {
    BaseballHitZone(hitZone = 5)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_6() {
    BaseballHitZone(hitZone = 6)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_7() {
    BaseballHitZone(hitZone = 7)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_8() {
    BaseballHitZone(hitZone = 8)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_9() {
    BaseballHitZone(hitZone = 9)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_10() {
    BaseballHitZone(hitZone = 10)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_11() {
    BaseballHitZone(hitZone = 11)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_12() {
    BaseballHitZone(hitZone = 12)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_13() {
    BaseballHitZone(hitZone = 13)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_14() {
    BaseballHitZone(hitZone = 14)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_15() {
    BaseballHitZone(hitZone = 15)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_16() {
    BaseballHitZone(hitZone = 16)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_17() {
    BaseballHitZone(hitZone = 17)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_18() {
    BaseballHitZone(hitZone = 18)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_19() {
    BaseballHitZone(hitZone = 19)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_20() {
    BaseballHitZone(hitZone = 20)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_21() {
    BaseballHitZone(hitZone = 21)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_22() {
    BaseballHitZone(hitZone = 22)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_23() {
    BaseballHitZone(hitZone = 23)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_24() {
    BaseballHitZone(hitZone = 24)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_25() {
    BaseballHitZone(hitZone = 25)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_26() {
    BaseballHitZone(hitZone = 26)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_27() {
    BaseballHitZone(hitZone = 27)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_28() {
    BaseballHitZone(hitZone = 28)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_29() {
    BaseballHitZone(hitZone = 29)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_30() {
    BaseballHitZone(hitZone = 30)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_31() {
    BaseballHitZone(hitZone = 31)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_32() {
    BaseballHitZone(hitZone = 32)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_33() {
    BaseballHitZone(hitZone = 33)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_34() {
    BaseballHitZone(hitZone = 34)
}

@Preview
@Composable
fun BaseballHitZonePreview_HitZone_35() {
    BaseballHitZone(hitZone = 35)
}