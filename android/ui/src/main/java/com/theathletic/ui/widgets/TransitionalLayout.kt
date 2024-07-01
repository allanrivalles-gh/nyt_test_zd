package com.theathletic.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Layout

enum class TransitionDirection {
    UP,
    DOWN,
}

/**
 * This is a [Layout] which supports transitioning between two separate composables with a vertical
 * animation. This will throw an error if you try to compose 3 or more composables in the [content]
 * block.
 *
 * This layout will set its height to the max height of its children, so this works best if
 * all of the children composables are of the same height.
 *
 * @param transitionPercent Value from 0f to 1f to indicate the current percentage of the
 * transition between the two children composables.
 * @param direction Whether you want the exit and enter animations to be upwards or downwards.
 */
@Composable
fun TransitionalLayout(
    transitionPercent: Float,
    modifier: Modifier = Modifier,
    direction: TransitionDirection = TransitionDirection.UP,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier.clipToBounds(),
        content = content,
    ) { measureables, constraints ->
        assert(measureables.size <= 2) { "TransitionalLayout only supports 2 children" }
        val placeables = measureables.map { it.measure(constraints) }

        val (first, second) = if (placeables.size == 2) {
            placeables.getOrNull(0) to placeables.getOrNull(1)
        } else {
            null to placeables.getOrNull(0)
        }

        val height = placeables.maxOf { it.height }
        val deltaY = (height * transitionPercent.coerceIn(0f, 1f)).toInt()

        layout(constraints.maxWidth, height) {
            when (direction) {
                TransitionDirection.UP -> {
                    first?.placeRelative(x = 0, y = -deltaY)
                    second?.placeRelative(x = 0, y = height - deltaY)
                }
                TransitionDirection.DOWN -> {
                    first?.placeRelative(x = 0, y = deltaY)
                    second?.placeRelative(x = 0, y = deltaY - height)
                }
            }
        }
    }
}