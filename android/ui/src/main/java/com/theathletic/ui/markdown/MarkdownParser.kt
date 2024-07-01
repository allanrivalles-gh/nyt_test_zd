package com.theathletic.ui.markdown

import com.theathletic.utility.safeLet

class MarkdownParser {
    data class ParsedSpan(val startIndex: Int, val endIndex: Int, val spanStyle: Style)

    data class ParsedResult(val contentString: String, val parsedSpans: List<ParsedSpan>)

    sealed class Style {
        object Bold : Style()
        object Italic : Style()
        object Strikethrough : Style()
    }

    private fun String.tokenToStyle(): Style? = when (this) {
        "*", "_" -> Style.Italic
        "**", "__" -> Style.Bold
        "~", "~~" -> Style.Strikethrough
        else -> null
    }

    fun parseMarkdown(text: String): ParsedResult {
        val pattern = Regex("""([*_~]{1,3})([^*_~]+)\1""")
        var matchResult = pattern.find(text)
        val builder = StringBuilder(text)
        val spans = mutableListOf<ParsedSpan>()
        var qualifierAdjustments = 0
        while (matchResult != null) {
            val qualifier = matchResult.groupValues[1]
            val modifiedText = matchResult.groupValues[2]
            safeLet(qualifier.tokenToStyle(), matchResult) { style, result ->
                val startIndex = result.range.first - qualifierAdjustments
                val endIndex = result.range.last - qualifierAdjustments + 1
                spans.add(ParsedSpan(startIndex, startIndex + modifiedText.length, style))
                builder.replace(startIndex, endIndex, modifiedText)
                qualifierAdjustments += qualifier.length * 2
            }
            matchResult = matchResult.next()
        }
        return ParsedResult(builder.toString(), spans)
    }
}