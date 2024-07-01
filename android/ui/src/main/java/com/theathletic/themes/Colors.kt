package com.theathletic.themes

import android.annotation.SuppressLint
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

/**
 * These are the raw colors from the Design System palette. You should usually be using
 * [AthTheme.colors] to refer to color as that will properly flip between light and dark mode.
 * Only use [AthColor] directly when you want the same color in light and dark mode.
 */
class AthColor {
    companion object {
        @Stable val Gray100 = Color(0xFF000000)
        @Stable val Gray200 = Color(0xFF121212)
        @Stable val Gray300 = Color(0xFF323232)
        @Stable val Gray400 = Color(0xFF52524F)
        @Stable val Gray500 = Color(0xFF969693)
        @Stable val Gray600 = Color(0xFFC4C4C0)
        @Stable val Gray700 = Color(0xFFF0F0EE)
        @Stable val Gray800 = Color(0xFFFFFFFF)

        @Stable val Red800 = Color(0xFFFD5757)
        @Stable val Yellow800 = Color(0xFFF89A1E)
        @Stable val Green800 = Color(0xFF4EAB75)
        @Stable val Blue800 = Color(0xFF6DA4E4)
        @Stable val Purple800 = Color(0xFF987FF7)
        @Stable val Red100 = Color(0xFFB72424)
        @Stable val Yellow100 = Color(0xFFC04300)
        @Stable val Green100 = Color(0xFF026A2E)
        @Stable val Blue100 = Color(0xFF225FA7)
        @Stable val Purple100 = Color(0xFF6D30BB)
        @Stable val Link100 = Color(0xFF386C92)
        @Stable val Link800 = Color(0xFF94B4DA)
        @Stable val PurpleUser = Color(0xFF403C5C)
        @Stable val NavyUser = Color(0xFF1C3C64)
        @Stable val BlueUser = Color(0xFF497AB8)
        @Stable val TurquoiseUser = Color(0xFF105E5E)
        @Stable val GreenUser = Color(0xFF3C5634)
        @Stable val YellowUser = Color(0xFFF89A1E)
        @Stable val OrangeUser = Color(0xFFE95F33)
        @Stable val RedUser = Color(0xFFCB3939)
        @Stable val MaroonUser = Color(0xFF943848)
        @Stable val GrayUser = Color(0xFF969693)
    }
}

/**
 * Wrapper class for our themed colors. Provided by [LocalAthColors] in a composition local so it
 * can be referred to by [AthTheme.colors] when needed, as long as your component has an
 * [AthleticTheme] parent somewhere in the tree.
 */
class AthColors(
    val dark800: Color,
    val dark700: Color,
    val dark600: Color,
    val dark500: Color,
    val dark400: Color,
    val dark300: Color,
    val dark200: Color,
    val dark100: Color,
    val red: Color,
    val yellow: Color,
    val green: Color,
    val blue: Color,
    val purple: Color
) {
    val namedColors: Map<String, Color> = mapOf(
        Pair("dark800", dark800),
        Pair("dark700", dark700),
        Pair("dark600", dark600),
        Pair("dark500", dark500),
        Pair("dark400", dark400),
        Pair("dark300", dark300),
        Pair("dark200", dark200),
        Pair("dark100", dark100),
        Pair("red", red),
        Pair("yellow", yellow),
        Pair("green", green),
        Pair("blue", blue),
        Pair("purple", purple)
    )
}

internal val darkColors = AthColors(
    dark800 = AthColor.Gray800,
    dark700 = AthColor.Gray700,
    dark600 = AthColor.Gray600,
    dark500 = AthColor.Gray500,
    dark400 = AthColor.Gray400,
    dark300 = AthColor.Gray300,
    dark200 = AthColor.Gray200,
    dark100 = AthColor.Gray100,
    yellow = AthColor.Yellow800,
    blue = AthColor.Blue800,
    red = AthColor.Red800,
    green = AthColor.Green800,
    purple = AthColor.Purple800
)

internal val lightColors = AthColors(
    dark800 = AthColor.Gray100,
    dark700 = AthColor.Gray200,
    dark600 = AthColor.Gray300,
    dark500 = AthColor.Gray400,
    dark400 = AthColor.Gray500,
    dark300 = AthColor.Gray700,
    dark200 = AthColor.Gray800,
    dark100 = AthColor.Gray700,
    yellow = AthColor.Yellow100,
    blue = AthColor.Blue100,
    red = AthColor.Red100,
    green = AthColor.Green100,
    purple = AthColor.Purple100
)

// Material Theme colors for Material Components. Try to override whenever possible with AthTheme.
@SuppressLint("ConflictingOnColor")
val NightModeColors = darkColors(
    primary = AthColor.Gray100,
    primaryVariant = AthColor.Gray200,
    onPrimary = AthColor.Gray800,
    secondary = AthColor.Gray400,
    secondaryVariant = AthColor.Gray200,
    onSecondary = AthColor.Gray800,
    error = AthColor.RedUser,
    onError = AthColor.Gray800,
    surface = AthColor.Gray200,
    onSurface = AthColor.Gray800,
    background = AthColor.Gray100,
    onBackground = AthColor.Gray800,
)

@SuppressLint("ConflictingOnColor")
val LightModeColors = lightColors(
    primary = AthColor.Gray800,
    primaryVariant = AthColor.Gray800,
    onPrimary = AthColor.Gray300,
    secondary = AthColor.Gray600,
    secondaryVariant = AthColor.Gray800,
    onSecondary = AthColor.Gray300,
    error = AthColor.RedUser,
    onError = AthColor.Gray800,
    surface = AthColor.Gray800,
    onSurface = AthColor.Gray100,
    background = AthColor.Gray800,
    onBackground = AthColor.Gray300,
)