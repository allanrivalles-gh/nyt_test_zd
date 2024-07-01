package com.theathletic.main.ui.misc

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Icon
import androidx.compose.material.Snackbar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.utility.conditional

@Composable
fun ActionTextSnackbar(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    actionTag: String? = null,
    isSuccess: Boolean = true,
    onActionStringClick: ((AnnotatedString.Range<String>) -> Unit)? = null
) {
    Snackbar(backgroundColor = AthTheme.colors.dark300, modifier = modifier.padding(24.dp)) {
        Row {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = if (isSuccess) {
                        AthTheme.colors.dark800
                    } else {
                        AthTheme.colors.red
                    },
                    modifier = Modifier
                        .size(30.dp)
                        .conditional(isSuccess.not()) {
                            drawBehind {
                                drawCircle(AthColor.Gray800, radius = size.minDimension / 3.0f)
                            }
                        }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            ClickableText(
                text = text,
                style = AthTextStyle.Calibre.Utility.Medium.Small.copy(
                    color = AthTheme.colors.dark800
                ),
            ) { offset ->
                actionTag?.let {
                    text.getStringAnnotations(tag = it, start = offset, end = offset).firstOrNull()?.let { tagAction ->
                        onActionStringClick?.invoke(tagAction)
                    }
                }
            }
        }
    }
}