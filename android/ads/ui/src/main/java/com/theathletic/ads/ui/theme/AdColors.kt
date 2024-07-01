package com.theathletic.ads.ui.theme

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

class AdColors(
    lightMode: Boolean
) {
    val adBackground: Color = if (lightMode) AdBackgroundLight else AdBackgroundDark
    val adSlugText: Color = if (lightMode) AdTextLight else AdTextDark
    val adDivider: Color = if (lightMode) AdDividerLight else AdDividerDark

    companion object {
        @Stable val AdBackgroundLight = Color(0xFFF7F7F4)
        @Stable val AdBackgroundDark = Color(0xFF323232)
        @Stable val AdDividerLight = Color(0xFFF0F0EE)
        @Stable val AdDividerDark = Color(0xFF52524F)
        @Stable val AdTextLight = Color(0xFF969693)
        @Stable val AdTextDark = Color(0xFF969693)
    }
}