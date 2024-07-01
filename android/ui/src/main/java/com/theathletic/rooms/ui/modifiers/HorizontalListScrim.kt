package com.theathletic.rooms.ui.modifiers

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTheme

/**
 * This adds a scrim to the left and right of a scrollable layout indicating there is content to be
 * scrolled to. It doesn't show if you are full scrolled in a direction.
 */
fun Modifier.horizontalListScrim(
    scrollState: ScrollState,
    color: Color? = null,
    width: Dp = 20.dp,
): Modifier = composed {

    val scrimColor = color ?: AthTheme.colors.dark100

    Modifier.drawWithContent {
        drawContent()

        val widthPx = width.toPx()
        val fromStart = scrollState.value
        val fromEnd = scrollState.maxValue - scrollState.value

        val startStrength = widthPx * (fromStart / widthPx).coerceAtMost(1f)
        val endStrength = widthPx * (fromEnd / widthPx).coerceAtMost(1f)

        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(scrimColor, Color.Transparent),
                startX = 0f,
                endX = startStrength,
            ),
            topLeft = Offset.Zero,
            size = Size(width = startStrength, height = size.height),
        )

        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Transparent, scrimColor),
                startX = size.width - endStrength,
                endX = size.width,
            ),
            topLeft = Offset(x = size.width - endStrength, y = 0f),
            size = Size(width = endStrength, height = size.height),
        )
    }
}

fun Modifier.topScrim(
    color: Color,
    height: Dp = 20.dp,
) = this.drawWithCache {
    val brush = Brush.verticalGradient(
        colors = listOf(color, Color.Transparent),
        startY = 0f,
        endY = 20.dp.toPx(),
    )
    onDrawWithContent {
        drawContent()
        drawRect(
            brush = brush,
            topLeft = Offset.Zero,
            size = Size(width = size.width, height = height.toPx()),
        )
    }
}

fun Modifier.bottomScrim(
    color: Color,
    height: Dp = 20.dp,
) = this.drawWithCache {
    val brush = Brush.verticalGradient(
        colors = listOf(Color.Transparent, color),
        startY = size.height - height.toPx(),
        endY = size.height,
    )
    onDrawWithContent {
        drawContent()
        drawRect(
            brush = brush,
            topLeft = Offset(x = 0f, y = size.height - height.toPx()),
            size = Size(width = size.width, height = height.toPx()),
        )
    }
}