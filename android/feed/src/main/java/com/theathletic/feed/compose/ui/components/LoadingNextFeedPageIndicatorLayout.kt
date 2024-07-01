package com.theathletic.feed.compose.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DayNightPreview

@Composable
fun LoadingNextFeedPageIndicatorLayout() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        CircularProgressIndicator(
            color = AthTheme.colors.dark500,
        )
    }
}

@DayNightPreview
@Composable
private fun LoadingNextFeedPageIndicatorLayoutPreview() {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        LoadingNextFeedPageIndicatorLayout()
    }
}