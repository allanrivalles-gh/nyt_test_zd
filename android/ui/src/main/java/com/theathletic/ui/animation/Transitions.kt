package com.theathletic.ui.animation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.theathletic.ui.utility.conditional

/**
 * An Enter/Exit Transition pair which slides the old content out to the left of the screen and
 * slides the new content in from the right of the screen.
 */
@OptIn(ExperimentalAnimationApi::class)
fun slideInOutLeft(
    initialOffset: (Int) -> Int = { it }
) = slideInHorizontally(initialOffsetX = { initialOffset(it) }) with
    slideOutHorizontally(targetOffsetX = { -initialOffset(it) })

/**
 * An Enter/Exit Transition pair which slides the old content out to the right of the screen and
 * slides the new content in from the left of the screen.
 */
@OptIn(ExperimentalAnimationApi::class)
fun slideInOutRight(
    initialOffset: (Int) -> Int = { it }
) = slideInHorizontally(initialOffsetX = { -initialOffset(it) }) with
    slideOutHorizontally(targetOffsetX = { initialOffset(it) })

@Composable
fun ViewSlideAnimation(
    modifier: Modifier = Modifier,
    view: @Composable BoxScope.() -> Unit,
    slideDown: Boolean = false,
    performAnimation: Boolean = true,
) {
    var isVisible by remember { mutableStateOf(false) }
    val offSetValue = if (slideDown) -300f else 200f

    val offset by animateFloatAsState(
        targetValue = if (isVisible) 0f else offSetValue,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing, delayMillis = 100)
    )

    LaunchedEffect(Unit) { isVisible = true }

    val offsetY = if (slideDown) 91.dp else 0.dp
    Box(
        modifier = Modifier
            .conditional(performAnimation) {
                this.graphicsLayer { translationY = offset }
            }.offset(y = offsetY)
            .then(modifier)
    ) { view() }
}