package com.theathletic.feed.compose.ui.reusables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme

@Composable
fun Badge(
    label: String,
    background: Color = AthTheme.colors.dark700,
    foreground: Color = AthTheme.colors.dark300
) {
    val roundedCorners = RoundedCornerShape(2.dp)
    Box(
        modifier = Modifier
            .background(color = background, shape = roundedCorners)
            .border(width = 0.5.dp, color = AthTheme.colors.dark400, shape = roundedCorners)
            .padding(horizontal = 4.dp, vertical = 1.dp)
    ) {
        Text(
            text = label,
            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
            color = foreground,
            fontSize = 10.sp
        )
    }
}