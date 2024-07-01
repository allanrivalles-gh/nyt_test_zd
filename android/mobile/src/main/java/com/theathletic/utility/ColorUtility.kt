package com.theathletic.utility

import android.graphics.Color
import androidx.annotation.ColorInt
import com.theathletic.R
import com.theathletic.extension.extGetColor

object ColorUtility {
    fun getBestHexFromString(color: String?): String {
        if (color == null || color.isEmpty())
            return "#000000"
        if (color.contains("#"))
            return color
        return if (!(color.length == 6 || color.length == 8)) "#000000" else "#$color"
    }

    @ColorInt
    fun getBestColorFromString(color: String?): Int {
        return if (color == null || color.isEmpty()) R.color.ath_grey_70.extGetColor() else Color.parseColor(getBestHexFromString(color))
    }

    @ColorInt
    fun getContrastFontColor(@ColorInt color: Int): Int {
        // Counting the perceptive luminance - human eye favors green color...
        val a = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255

        val d: Int
        d = if (a < 0.5) {
            0 // bright colors - black font
        } else {
            255 // dark colors - white font
        }

        return Color.rgb(d, d, d)
    }
}