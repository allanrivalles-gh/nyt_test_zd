package com.theathletic.ui.widgets

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.markdown.MarkdownParser
import com.theathletic.ui.markdown.MarkdownRenderer

/**
 * This is a starting point for a MarkdownText that can build an
 * annotatedString to render our own subset of markdown. For now,
 * it mainly just parses out html characters and provides reasonable,
 * if not bland, plain text.
 */
@Composable
fun MarkdownText(
    modifier: Modifier = Modifier,
    markdownText: String,
    color: Color,
    style: TextStyle
) {
    val localContext = LocalContext.current
    val annotatedString = remember(markdownText) { markdownText.markdownToAnnotatedString(localContext) }

    Text(
        text = annotatedString,
        color = color,
        style = style,
        modifier = modifier
    )
}

private fun String.markdownToAnnotatedString(context: Context): AnnotatedString {
    val parsedHtml = MarkdownRenderer.fromMarkdown(
        context = context,
        markdown = this.replace(Regex("\n"), "<br>"),
    ).trim().toString()
    val parsedMarkdown = MarkdownParser().parseMarkdown(parsedHtml)

    val annotated = AnnotatedString.Builder(parsedMarkdown.contentString)
    val stringIndices = parsedMarkdown.contentString.indices
    parsedMarkdown.parsedSpans.forEach {
        // endIndex is exclusive, so removing 1 to check we're in the range
        if (it.startIndex in stringIndices && (it.endIndex - 1) in stringIndices) {
            annotated.addStyle(it.spanStyle.toSpanStyle(), it.startIndex, it.endIndex)
        }
    }
    return annotated.toAnnotatedString()
}

private val boldStyle = SpanStyle(fontWeight = FontWeight.Bold)
private val italicStyle = SpanStyle(fontStyle = FontStyle.Italic)
private val strikethroughStyle = SpanStyle(textDecoration = TextDecoration.LineThrough)

private fun MarkdownParser.Style.toSpanStyle() = when (this) {
    is MarkdownParser.Style.Bold -> boldStyle
    is MarkdownParser.Style.Italic -> italicStyle
    is MarkdownParser.Style.Strikethrough -> strikethroughStyle
}

@Preview
@Composable
private fun MarkdownText_Dark_Preview() {
    AthleticTheme(lightMode = false) {
        Column(modifier = Modifier.background(AthTheme.colors.dark300)) {
            MarkdownText(
                markdownText = "Basic Text",
                color = AthTheme.colors.dark800,
                style = AthTextStyle.Calibre.Utility.Regular.Large
            )
            MarkdownText(
                markdownText = "Text&nbsp;with&nbsp;non-breaking&nbsp;spaces",
                color = AthTheme.colors.dark800,
                style = AthTextStyle.Calibre.Utility.Regular.Large
            )
            MarkdownText(
                markdownText = "Text with _italics_ and *more italics*",
                color = AthTheme.colors.dark800,
                style = AthTextStyle.Calibre.Utility.Regular.Large
            )
            MarkdownText(
                markdownText = "Text with ~strikethrough~ and ~~more strikethrough~~",
                color = AthTheme.colors.dark800,
                style = AthTextStyle.Calibre.Utility.Regular.Large
            )
            MarkdownText(
                markdownText = "So much text with ~strikethrough~ and *italics* and **bold**",
                color = AthTheme.colors.dark800,
                style = AthTextStyle.Calibre.Utility.Regular.Large
            )
            MarkdownText(
                markdownText = "Text with **bold** and __more bold__",
                color = AthTheme.colors.dark800,
                style = AthTextStyle.Calibre.Utility.Regular.Large
            )
            MarkdownText(
                markdownText = "Text with <a href=\"something\">links</a>",
                color = AthTheme.colors.dark800,
                style = AthTextStyle.Calibre.Utility.Regular.Large
            )
        }
    }
}