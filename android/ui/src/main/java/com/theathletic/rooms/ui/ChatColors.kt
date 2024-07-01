package com.theathletic.rooms.ui

import androidx.compose.ui.graphics.Color
import kotlin.math.abs

private val ChatAvatarColors = listOf(
    Color(0xFF403C5C),
    Color(0xFF1C3C64),
    Color(0xFF497AB8),
    Color(0xFF105E5E),
    Color(0xFF3C5634),
    Color(0xFFF89A1E),
    Color(0xFFE95F33),
    Color(0xFFCB3939),
    Color(0xFF943848),
    Color(0xFF969693),
)

fun chatAvatarColor(id: String): Color {
    val colorSeed = abs(id.hashCode())
    return ChatAvatarColors[colorSeed % ChatAvatarColors.size]
}