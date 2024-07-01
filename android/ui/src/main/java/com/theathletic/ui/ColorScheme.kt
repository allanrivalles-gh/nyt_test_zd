package com.theathletic.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class ColorScheme(
    private val lightModeColor: Color,
    private val darkModeColor: Color? = null
) {
    @Composable
    fun getColor(): Color = if (isSystemInDarkTheme()) {
        darkModeColor ?: lightModeColor
    } else {
        lightModeColor
    }
}