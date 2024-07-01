package com.theathletic.feed.compose.ui.reusables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.theathletic.themes.AthTheme

@Composable
internal fun ContentDivider(modifier: Modifier = Modifier) {
    Divider(
        color = AthTheme.colors.dark300,
        modifier = modifier.fillMaxWidth()
    )
}