package com.theathletic.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.theathletic.themes.AthColor

/**
 * Use this SwipeRefreshIndicator instead of the Compose provided SwipeRefreshIndicator
 * as this one sets the correct background and content color values to match our
 * Design System
 */
@Composable
fun SwipeRefreshIndicator(
    state: SwipeRefreshState,
    refreshTriggerDistance: Dp = 80.dp
) {
    SwipeRefreshIndicator(
        state = state,
        refreshTriggerDistance = refreshTriggerDistance,
        backgroundColor = AthColor.Gray800,
        contentColor = AthColor.Gray100
    )
}