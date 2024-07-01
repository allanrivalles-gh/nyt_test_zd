package com.theathletic.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTheme

enum class NodeType {
    FIRST,
    MIDDLE,
    LAST,
    SPACER,
    SINGLE,
    NONE
}

@Composable
fun TimelineNode(
    nodeType: NodeType,
    modifier: Modifier = Modifier
) {
    val circleColor = AthTheme.colors.dark800
    val lineColor = AthTheme.colors.dark300
    val nodeSize = 24f

    Canvas(
        modifier = modifier
            .fillMaxHeight()
            .width((nodeSize / 2).dp)
    ) {
        val nodeRadius = nodeSize / 2
        when (nodeType) {
            NodeType.FIRST -> {
                drawNodeCircle(circleColor, nodeRadius)
                drawBottomLine(lineColor, nodeRadius)
            }
            NodeType.MIDDLE -> {
                drawTopLine(lineColor, nodeRadius)
                drawNodeCircle(circleColor, nodeRadius)
                drawBottomLine(lineColor, nodeRadius)
            }
            NodeType.LAST -> {
                drawTopLine(lineColor, nodeRadius)
                drawNodeCircle(circleColor, nodeRadius)
            }
            NodeType.SINGLE -> {
                drawNodeCircle(circleColor, nodeRadius)
            }
            NodeType.SPACER -> {
                drawSpacerLine(lineColor)
            }
            NodeType.NONE -> {
                // do nothing
            }
        }
    }
}

private fun DrawScope.drawNodeCircle(color: Color, radius: Float) {
    val centerOffset = Offset(size.width / 2, size.height / 2)
    val strokeWidth = radius / 2

    drawCircle(
        color,
        radius - strokeWidth / 2,
        centerOffset
    )
}

private fun DrawScope.drawSpacerLine(
    color: Color
) {
    val centerX = size.width / 2
    val topPoint = Offset(centerX, 0f)
    val bottomPoint = Offset(centerX, size.height)
    drawLine(
        color,
        topPoint,
        bottomPoint,
        strokeWidth = 2f
    )
}

private fun DrawScope.drawTopLine(color: Color, circleRadius: Float) {
    val centerX = size.width / 2
    val topPoint = Offset(centerX, 0f)
    val bottomPoint = Offset(centerX, size.height / 2 - circleRadius + 3)

    drawLine(
        color,
        topPoint,
        bottomPoint,
        strokeWidth = 2f
    )
}

private fun DrawScope.drawBottomLine(
    color: Color,
    circleRadius: Float
) {
    val centerX = size.width / 2
    val topPoint = Offset(centerX, size.height / 2 + circleRadius - 3)
    val bottomPoint = Offset(centerX, size.height)

    drawLine(
        color,
        topPoint,
        bottomPoint,
        strokeWidth = 2f
    )
}