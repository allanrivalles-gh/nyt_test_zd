package com.theathletic.ui.utility

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.theathletic.themes.AthTheme

fun Modifier.conditional(
    condition: Boolean,
    modifier: Modifier.() -> Modifier
) = if (condition) {
    then(modifier(Modifier))
} else {
    this
}

fun Modifier.ternary(
    condition: Boolean,
    ifTrue: Modifier.() -> Modifier,
    ifFalse: (Modifier.() -> Modifier)? = null
) = if (condition) {
    then(ifTrue(Modifier))
} else if (ifFalse != null) {
    then(ifFalse(Modifier))
} else {
    this
}

fun Modifier.athPlaceholder(
    visible: Boolean,
    shape: Shape = RoundedCornerShape(2.dp)
) = composed {
    val placeholderColor = AthTheme.colors.dark300
    val highlightColor = if (isSystemInDarkTheme()) {
        AthTheme.colors.dark400.copy(alpha = 0.2f)
    } else {
        AthTheme.colors.dark400.copy(alpha = 0.1f)
    }
    placeholder(
        visible = visible,
        color = placeholderColor,
        shape = shape,
        highlight = PlaceholderHighlight.fade(
            highlightColor = highlightColor
        )
    )
}