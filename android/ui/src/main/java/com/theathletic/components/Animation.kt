package com.theathletic.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.ResourceIcon

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimateBaseballBase(
    occupied: Boolean,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        ResourceIcon(
            modifier = Modifier.size(size),
            resourceId = R.drawable.ic_baseball_base_empty,
            tint = AthTheme.colors.dark400
        )
        AnimatedVisibility(
            visible = occupied,
            enter = scaleIn(animationSpec = tween(durationMillis = 800)),
            exit = scaleOut(animationSpec = tween(durationMillis = 800)),
        ) {
            ResourceIcon(
                modifier = Modifier.size(size),
                resourceId = R.drawable.ic_baseball_base_empty,
                tint = AthTheme.colors.red
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedFadingText(
    text: String,
    animatedTextField: @Composable (text: String) -> Unit
) {
    AnimatedContent(
        targetState = text,
        transitionSpec = { textFadeEntryExitTransition() }
    ) { animatedTextField(it) }
}

@OptIn(ExperimentalAnimationApi::class)
private fun textFadeEntryExitTransition(): ContentTransform =
    // Incoming/Entry score text
    fadeIn(animationSpec = tween(durationMillis = 800, delayMillis = 300)) with
        // Outgoing/Exit score text
        fadeOut(animationSpec = tween(durationMillis = 800))