package com.theathletic.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTheme
import com.theathletic.ui.utility.parseHexColor

sealed class TeamCurtain {
    enum class Orientation { LEFT, RIGHT }
}

private enum class CurtainPanel { MAIN, ALPHA_1, ALPHA_2 }

@Composable
fun TeamCurtain(
    teamColor: String,
    height: Dp,
    width: Dp,
    orientation: TeamCurtain.Orientation,
    modifier: Modifier = Modifier
) {
    val gradient = arrayOf(0.0f to Color.Transparent, 0.6f to AthTheme.colors.dark200)

    Box(modifier = Modifier.width(width)) {
        Box(
            modifier = modifier
                .drawWithCache {
                    onDrawBehind {
                        val heightPx = height.toPx()
                        val widthPx = width.toPx()
                        val paths = orientation.toPaths(widthPx, heightPx, 16.dp.toPx())
                        paths[CurtainPanel.ALPHA_2]?.let {
                            drawOutline(
                                outline = Outline.Generic(it),
                                color = teamColor.parseHexColor(),
                                alpha = 0.25f
                            )
                        }
                        paths[CurtainPanel.ALPHA_1]?.let {
                            drawOutline(
                                outline = Outline.Generic(it),
                                color = teamColor.parseHexColor(),
                                alpha = 0.3f
                            )
                        }
                        paths[CurtainPanel.MAIN]?.let {
                            drawOutline(
                                outline = Outline.Generic(it),
                                color = teamColor.parseHexColor(),
                                alpha = 0.5f
                            )
                        }
                        drawRect(
                            brush = Brush.verticalGradient(colorStops = gradient, startY = heightPx / 4, endY = heightPx),
                            size = Size(width = widthPx, height = heightPx)
                        )
                    }
                }
        )
    }
}

private fun TeamCurtain.Orientation.toPaths(
    widthPx: Float,
    heightPx: Float,
    panelWidthPx: Float
): Map<CurtainPanel, Path> {
    val rect = Rect(
        Offset.Zero,
        Size(width = widthPx, height = heightPx)
    )
    val angleOffset = heightPx * 0.35f // x offset to provide a constant angle at all lengths
    return if (this == TeamCurtain.Orientation.LEFT) {
        mapOf(
            CurtainPanel.MAIN to Path().apply {
                moveTo(rect.topRight.x - panelWidthPx * 2, rect.topLeft.y)
                lineTo(rect.bottomRight.x - angleOffset - panelWidthPx * 2, rect.bottomRight.y)
                lineTo(rect.bottomLeft.x, rect.bottomLeft.y)
                lineTo(rect.topLeft.x, rect.topLeft.y)
                close()
            },
            CurtainPanel.ALPHA_1 to Path().apply {
                moveTo(rect.topRight.x - panelWidthPx, rect.topRight.y)
                lineTo(rect.bottomRight.x - angleOffset - panelWidthPx, rect.bottomRight.y)
                lineTo(rect.bottomRight.x - angleOffset - panelWidthPx * 2, rect.bottomLeft.y)
                lineTo(rect.topRight.x - panelWidthPx * 2, rect.topRight.y)
                close()
            },
            CurtainPanel.ALPHA_2 to Path().apply {
                moveTo(rect.topRight.x, rect.topRight.y)
                lineTo(rect.bottomRight.x - angleOffset, rect.bottomRight.y)
                lineTo(rect.bottomRight.x - angleOffset - panelWidthPx, rect.bottomLeft.y)
                lineTo(rect.topRight.x - panelWidthPx, rect.topRight.y)
                close()
            },
        )
    } else {
        mapOf(
            CurtainPanel.MAIN to Path().apply {
                moveTo(rect.topLeft.x + panelWidthPx * 2, rect.topRight.y)
                lineTo(rect.bottomLeft.x + angleOffset + panelWidthPx * 2, rect.bottomLeft.y)
                lineTo(rect.bottomRight.x, rect.bottomRight.y)
                lineTo(rect.topRight.x, rect.topRight.y)
                close()
            },
            CurtainPanel.ALPHA_1 to Path().apply {
                moveTo(rect.topLeft.x + panelWidthPx, rect.topLeft.y)
                lineTo(rect.bottomLeft.x + angleOffset + panelWidthPx, rect.bottomLeft.y)
                lineTo(rect.bottomLeft.x + angleOffset + panelWidthPx * 2, rect.bottomRight.y)
                lineTo(rect.topLeft.x + panelWidthPx * 2, rect.topLeft.y)
                close()
            },
            CurtainPanel.ALPHA_2 to Path().apply {
                moveTo(rect.topLeft.x, rect.topLeft.y)
                lineTo(rect.bottomLeft.x + angleOffset, rect.bottomLeft.y)
                lineTo(rect.bottomLeft.x + angleOffset + panelWidthPx, rect.bottomLeft.y)
                lineTo(rect.topLeft.x + panelWidthPx, rect.topLeft.y)
                close()
            },
        )
    }
}

@Preview
@Composable
private fun TeamCurtain_Preview120Dp() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .size(width = 300.dp, height = 100.dp)
            .background(AthTheme.colors.dark200)
    ) {
        TeamCurtain(
            teamColor = "6DC1FF",
            height = 200.dp,
            width = 120.dp,
            orientation = TeamCurtain.Orientation.LEFT,
            modifier = Modifier.weight(1f)
        )
        TeamCurtain(
            teamColor = "FF6600",
            height = 200.dp,
            width = 120.dp,
            orientation = TeamCurtain.Orientation.RIGHT,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
private fun TeamCurtain_Preview80Dp() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .size(width = 300.dp, height = 100.dp)
            .background(AthTheme.colors.dark200)
    ) {
        TeamCurtain(
            teamColor = "6DC1FF",
            height = 150.dp,
            width = 80.dp,
            orientation = TeamCurtain.Orientation.LEFT,
            modifier = Modifier.weight(1f)
        )
        TeamCurtain(
            teamColor = "FF6600",
            height = 150.dp,
            width = 80.dp,
            orientation = TeamCurtain.Orientation.RIGHT,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
private fun TeamCurtain_Preview300Dp() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .size(width = 300.dp, height = 180.dp)
            .background(AthTheme.colors.dark200)
    ) {
        TeamCurtain(
            teamColor = "6DC1FF",
            height = 260.dp,
            width = 120.dp,
            orientation = TeamCurtain.Orientation.LEFT,
            modifier = Modifier.weight(1f)
        )
        TeamCurtain(
            teamColor = "FF6600",
            height = 260.dp,
            width = 120.dp,
            orientation = TeamCurtain.Orientation.RIGHT,
            modifier = Modifier.weight(1f)
        )
    }
}