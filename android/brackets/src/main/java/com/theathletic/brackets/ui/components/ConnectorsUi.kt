package com.theathletic.brackets.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.theathletic.brackets.ui.BracketsUi.Companion.ANIMATION_OFFSET
import com.theathletic.brackets.ui.BracketsUi.Companion.CONNECTOR_WIDTH
import com.theathletic.ui.utility.conditional
import kotlin.math.absoluteValue

fun Modifier.drawConnectors(
    verticalConnectorHeight: Dp,
    initialOffset: Dp,
    paddingOffset: Dp,
    numberOfConnectors: Int,
    shouldShow: Boolean,
    connectorColor: Color
) = this.conditional(shouldShow) {
    drawBehind {
        val zeroPixel = 0.dp.toPx()
        val strokeWidth = CONNECTOR_WIDTH.toPx()
        val verticalConnectorHeightPx = verticalConnectorHeight.toPx()
        val paddingOffsetPx = paddingOffset.toPx()
        var offset = initialOffset.toPx()
        repeat(numberOfConnectors) {
            drawLine(
                connectorColor,
                Offset(-paddingOffsetPx, offset),
                Offset(zeroPixel, offset),
                strokeWidth
            )
            drawLine(
                connectorColor,
                Offset(zeroPixel, offset),
                Offset(zeroPixel, offset + verticalConnectorHeightPx),
                strokeWidth
            )
            drawLine(
                connectorColor,
                Offset(-paddingOffsetPx, offset + verticalConnectorHeightPx),
                Offset(zeroPixel, offset + verticalConnectorHeightPx),
                strokeWidth
            )
            offset += (verticalConnectorHeightPx * 2)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
fun PagerState.shouldShowConnector(currentIndex: Int) = when {
    currentIndex <= currentPage -> currentPageOffset.absoluteValue > ANIMATION_OFFSET
    targetPage == currentIndex -> currentPageOffset < ANIMATION_OFFSET
    currentPage.inc().inc() == currentIndex -> currentPageOffset > ANIMATION_OFFSET
    else -> true
}