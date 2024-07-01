package com.theathletic.ui.widgets

import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

@Composable
fun SimpleShrinkToFitText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    maxLines: Int = Int.MAX_VALUE,
    textAlign: TextAlign? = null,
    onFontSizeChanged: (newSize: TextUnit) -> Unit = {},
) {
    var readyToDraw by remember { mutableStateOf(false) }
    var styleToFit by remember { mutableStateOf(style) }
    Text(
        text = text,
        style = styleToFit,
        overflow = TextOverflow.Visible,
        modifier = modifier.drawWithContent {
            if (readyToDraw) {
                drawContent()
            }
        },
        onTextLayout = {
            if (it.didOverflowWidth || it.didOverflowHeight) {
                val newFontSize = styleToFit.fontSize * 0.9
                onFontSizeChanged(newFontSize)
                styleToFit = styleToFit.copy(fontSize = newFontSize)
            } else {
                readyToDraw = true
            }
        },
        maxLines = maxLines,
        textAlign = textAlign
    )
}