package com.theathletic.ui.widgets

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp

/**
 * A simple copy of [Row] which allows an [overlap] parameter to specify the amount that each
 * subsequent item should overlap its predecessor.
 *
 * @param leftOnTop Controls the z-order of child items as they are drawn from left-to-right.
 * Set this to false if you want the rightmost composable to appear on top.
 */
@Composable
fun OverlappingRow(
    overlap: Dp,
    modifier: Modifier = Modifier,
    leftOnTop: Boolean = true,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val placeables = measurables.map { measurable -> measurable.measure(constraints) }

        val width = (placeables.sumOf { it.width } - placeables.indices.last * overlap.toPx())
            .toInt()
            .coerceIn(constraints.minWidth.rangeTo(constraints.maxWidth))

        val height = placeables.maxOf { it.height }
            .coerceIn(constraints.minHeight.rangeTo(constraints.maxHeight))

        // To have the first item remain on top, we push subsequent items down in z-order
        val zIndexStep = if (leftOnTop) -1f else 0f

        layout(width, height) {
            var xPosition = 0.0
            var zIndex = 0f

            placeables.forEach { placeable ->
                placeable.placeRelative(x = xPosition.toInt(), y = 0, zIndex = zIndex)
                xPosition += placeable.width - overlap.toPx()
                zIndex += zIndexStep
            }
        }
    }
}