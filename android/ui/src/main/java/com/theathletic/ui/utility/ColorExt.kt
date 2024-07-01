package com.theathletic.ui.utility

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.theathletic.themes.AthColor

val Color.asAndroidColorInt: Int get() {
    val a = (alpha * 255).toInt() shl 24
    val r = (red * 255).toInt() shl 16
    val g = (green * 255).toInt() shl 8
    val b = (blue * 255).toInt()

    return a or r or g or b
}

val String.asHexColor: Color? get() {
    return try {
        val colorString = when {
            this.startsWith("#") -> this
            else -> "#$this"
        }
        Color(android.graphics.Color.parseColor(colorString))
    } catch (e: IllegalArgumentException) {
        null
    }
}

fun String?.parseHexColor(defaultColor: Color = Color.Black): Color {
    return this?.let {
        try {
            Color(android.graphics.Color.parseColor("#FF$it"))
        } catch (ex: IllegalArgumentException) {
            defaultColor
        }
    } ?: defaultColor
}

/**
 * Calculates the contrast between the current color and white / black then returns
 * the corresponding contrast color for that given color
 *
 * @param foregroundLightColor Color that is returned when the background is darker
 * @param foregroundDarkColor Color that is returned when the background is lighter
 */
fun Color.getContrastColor(
    foregroundLightColor: Color = AthColor.Gray800,
    foregroundDarkColor: Color = AthColor.Gray100
): Color {
    return if (isLightContrast()) foregroundLightColor else foregroundDarkColor
}

/*
 * Calculates the contrast between the current color and white / black then returns
 * if the current color is white contrast, meaning that the foreground color must be darker
 */
fun Color.isLightContrast(): Boolean {
    val whiteContrast = ColorUtils.calculateContrast(Color.White.toArgb(), this.toArgb())
    val blackContrast = ColorUtils.calculateContrast(Color.Black.toArgb(), this.toArgb())

    return whiteContrast > blackContrast
}