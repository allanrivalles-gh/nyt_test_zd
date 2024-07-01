package com.theathletic.impressions

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned

fun Modifier.onVisibilityChange(action: (Visibility) -> Unit) = composed {
    val visibilityChanged by rememberUpdatedState(action)
    var inBoundsVisibility by remember { mutableStateOf(Visibility.GONE) }
    val isScreenVisible by rememberIsScreenVisible()

    val visibility by remember {
        derivedStateOf { if (isScreenVisible) inBoundsVisibility else Visibility.GONE }
    }

    LaunchedEffect(visibility) {
        visibilityChanged(visibility)
    }

    DisposableEffect(Unit) {
        onDispose { if (visibility.isNotGone()) visibilityChanged(Visibility.GONE) }
    }

    onGloballyPositioned { coordinates -> inBoundsVisibility = coordinates.measureInBoundsVisibility() }
}

private fun LayoutCoordinates.measureInBoundsVisibility(): Visibility {
    val bounds = boundsInWindow()

    val visibilityRatio = (bounds.height / size.height) * (bounds.width / size.width)
    return if (isAttached) {
        when {
            visibilityRatio <= 0f -> Visibility.GONE
            visibilityRatio >= 0.8 -> Visibility.VISIBLE
            else -> Visibility.PARTIAL
        }
    } else {
        Visibility.GONE
    }
}

enum class Visibility {
    VISIBLE,
    PARTIAL,
    GONE;

    fun isNotGone() = this != GONE
}