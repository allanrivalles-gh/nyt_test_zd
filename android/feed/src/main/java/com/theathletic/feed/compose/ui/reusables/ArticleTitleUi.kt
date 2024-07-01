package com.theathletic.feed.compose.ui.reusables

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.theathletic.themes.AthTheme

@Composable
fun ArticleTitle(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle,
    maxLines: Int = 3,
    minLines: Int = 1,
    isRead: Boolean = false
) {
    val titleColor = if (isRead) AthTheme.colors.dark500 else AthTheme.colors.dark700
    Text(
        color = titleColor,
        style = style,
        text = text,
        maxLines = maxLines,
        modifier = modifier,
        overflow = TextOverflow.Ellipsis,
        minLines = minLines,
    )
}