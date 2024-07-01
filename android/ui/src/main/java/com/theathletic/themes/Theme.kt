package com.theathletic.themes

import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.google.accompanist.insets.ProvideWindowInsets

internal val LocalAthColors = staticCompositionLocalOf { darkColors }

object AthTheme {
    val colors: AthColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAthColors.current
}

@Composable
fun AthleticTheme(
    lightMode: Boolean,
    content: @Composable () -> Unit,
) {
    val colors = remember(lightMode) {
        if (lightMode) lightColors else darkColors
    }
    MaterialTheme(
        colors = if (lightMode) LightModeColors else NightModeColors
    ) {
        ProvideWindowInsets {
            CompositionLocalProvider(
                LocalAthColors provides colors,
                LocalElevationOverlay provides null,
                LocalRippleTheme provides CustomRippleTheme
            ) {
                content()
            }
        }
    }
}

@Immutable
private object CustomRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = AthTheme.colors.dark500

    @Composable
    override fun rippleAlpha() = RippleAlpha(
        pressedAlpha = 0.24f,
        focusedAlpha = 0.24f,
        draggedAlpha = 0.16f,
        hoveredAlpha = 0.08f
    )
}