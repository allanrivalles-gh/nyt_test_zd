package com.theathletic.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme

@Composable
fun BoxWithBadge(
    modifier: Modifier = Modifier,
    badgeContent: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    Layout(
        {
            Box(
                contentAlignment = Alignment.Center,
                content = content,
                modifier = Modifier.layoutId("anchor")
            )

            Box(
                content = badgeContent,
                modifier = Modifier.layoutId("badge"),
            )
        },
        modifier = modifier
    ) { measurables, constraints ->

        val anchor = measurables.first { it.layoutId == "anchor" }.measure(constraints)
        val badge = measurables.firstOrNull { it.layoutId == "badge" }?.measure(Constraints())

        layout(
            anchor.width,
            anchor.height
        ) {
            anchor.placeRelative(0, 0)
            badge?.placeRelative(
                x = anchor.width - (2 * badge.width / 3),
                y = -badge.height / 4,
            )
        }
    }
}

@Composable
fun FixedSizeBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundShape: Shape = CircleShape,
    backgroundColor: Color = AthTheme.colors.red,
    fontSize: TextUnit = 12.sp,
    size: Dp = 20.dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .background(backgroundColor, backgroundShape)
    ) {
        Text(
            text = text,
            color = AthTheme.colors.dark800,
            fontSize = fontSize,
        )
    }
}

@Composable
fun VariableSizeBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundShape: Shape = RoundedCornerShape(1.dp),
    backgroundColor: Color = AthTheme.colors.red,
    fontSize: TextUnit = 12.sp,
    contentPadding: PaddingValues = PaddingValues(vertical = 1.dp, horizontal = 4.dp),
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(backgroundColor, backgroundShape)
            .padding(contentPadding)
    ) {
        Text(
            text = text,
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall.copy(fontSize = fontSize),
        )
    }
}