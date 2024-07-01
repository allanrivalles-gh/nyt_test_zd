package com.theathletic.ui.markdown

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

internal class MarkdownParserTest {
    private lateinit var stringBuilder: MarkdownParser

    @Before
    fun setUp() {
        stringBuilder = MarkdownParser()
    }

    @Test
    fun `string without markdown returns original string and no spans`() {
        val string = "Hello World!"
        val result = stringBuilder.parseMarkdown(string)

        assertThat(result.contentString).isEqualTo(string)
        assertThat(result.parsedSpans).isEmpty()
    }

    @Test
    fun `string without malformed markdown returns original string and no spans`() {
        val string = "Hello *World!"
        val result = stringBuilder.parseMarkdown(string)

        assertThat(result.contentString).isEqualTo(string)
        assertThat(result.parsedSpans).isEmpty()
    }

    @Test
    fun `string with asterisk returns parsed string and 1 Italic span`() {
        val string = "Hello *everyone in the* World!"
        val parsedString = "Hello everyone in the World!"
        val result = stringBuilder.parseMarkdown(string)

        assertThat(result.contentString).isEqualTo("Hello everyone in the World!")
        assertThat(result.parsedSpans).hasSize(1)
        assertThat(result.parsedSpans.first().spanStyle).isEqualTo(MarkdownParser.Style.Italic)
        assertThat(result.parsedSpans.first().startIndex).isEqualTo(6)
        assertThat(result.parsedSpans.first().endIndex).isEqualTo(21)
    }

    @Test
    fun `string with asterisk returns parsed string and 2 Italic spans`() {
        val string = "Hello *everyone in the* World, *it's a good* day"
        val result = stringBuilder.parseMarkdown(string)

        assertThat(result.contentString).isEqualTo("Hello everyone in the World, it's a good day")
        assertThat(result.parsedSpans).hasSize(2)
        assertThat(result.parsedSpans[0].spanStyle).isEqualTo(MarkdownParser.Style.Italic)
        assertThat(result.parsedSpans[1].spanStyle).isEqualTo(MarkdownParser.Style.Italic)
    }

    @Test
    fun `string with asterisk and double asterisk returns parsed string, 1 Italic span and 1 Bold span`() {
        val string = "Hello *everyone in the* **World!**"
        val result = stringBuilder.parseMarkdown(string)

        assertThat(result.contentString).isEqualTo("Hello everyone in the World!")
        assertThat(result.parsedSpans).hasSize(2)
        assertThat(result.parsedSpans.map { it.spanStyle }).containsExactlyElementsIn(
            listOf(MarkdownParser.Style.Italic, MarkdownParser.Style.Bold)
        )
        assertThat(result.parsedSpans.first().startIndex).isEqualTo(6)
        assertThat(result.parsedSpans.first().endIndex).isEqualTo(21)
        assertThat(result.parsedSpans.last().startIndex).isEqualTo(22)
        assertThat(result.parsedSpans.last().endIndex).isEqualTo(28)
    }

    @Test
    fun `string with asterisk in wrong place does not parse`() {
        val string = "Hello ever*yone in the World"
        val result = stringBuilder.parseMarkdown(string)

        assertThat(result.contentString).isEqualTo(string)
        assertThat(result.parsedSpans).isEmpty()
    }

    @Test
    fun `string with double asterisk returns parsed string and 1 Bold span`() {
        val string = "Hello **everyone in the** World!"
        val result = stringBuilder.parseMarkdown(string)

        assertThat(result.contentString).isEqualTo("Hello everyone in the World!")
        assertThat(result.parsedSpans).hasSize(1)
        assertThat(result.parsedSpans[0].spanStyle).isEqualTo(MarkdownParser.Style.Bold)
    }

    @Test
    fun `string with squiggly returns parsed string and 1 Strikethrough span`() {
        val string = "Hello ~everyone in the~ World!"
        val result = stringBuilder.parseMarkdown(string)

        assertThat(result.contentString).isEqualTo("Hello everyone in the World!")
        assertThat(result.parsedSpans).hasSize(1)
        assertThat(result.parsedSpans[0].spanStyle).isEqualTo(MarkdownParser.Style.Strikethrough)
    }

    @Test
    fun `string with double asterisk returns parsed string and 2 Bold spans`() {
        val string = "Hello **everyone in the** World, **it's a good** day"
        val result = stringBuilder.parseMarkdown(string)

        assertThat(result.contentString).isEqualTo("Hello everyone in the World, it's a good day")
        assertThat(result.parsedSpans).hasSize(2)
        assertThat(result.parsedSpans[0].spanStyle).isEqualTo(MarkdownParser.Style.Bold)
        assertThat(result.parsedSpans[1].spanStyle).isEqualTo(MarkdownParser.Style.Bold)
    }

    @Test
    fun `string with underscore returns parsed string and 1 Italic span`() {
        val string = "Hello _everyone in the_ World!"
        val result = stringBuilder.parseMarkdown(string)

        assertThat(result.contentString).isEqualTo("Hello everyone in the World!")
        assertThat(result.parsedSpans).hasSize(1)
        assertThat(result.parsedSpans.first().spanStyle).isEqualTo(MarkdownParser.Style.Italic)
    }

    @Test
    fun `string with underscore returns parsed string and 2 Italic spans`() {
        val string = "Hello _everyone in the_ World, _it's a good_ day"
        val result = stringBuilder.parseMarkdown(string)

        assertThat(result.contentString).isEqualTo("Hello everyone in the World, it's a good day")
        assertThat(result.parsedSpans).hasSize(2)
        assertThat(result.parsedSpans[0].spanStyle).isEqualTo(MarkdownParser.Style.Italic)
        assertThat(result.parsedSpans[1].spanStyle).isEqualTo(MarkdownParser.Style.Italic)
    }

    @Test
    fun `string with underscore in wrong place does not parse`() {
        val string = "Hello ever_yone in the World"
        val result = stringBuilder.parseMarkdown(string)

        assertThat(result.contentString).isEqualTo(string)
        assertThat(result.parsedSpans).isEmpty()
    }

    @Test
    fun `string with double underscore returns parsed string and 1 Bold span`() {
        val string = "Hello __everyone in the__ World!"
        val result = stringBuilder.parseMarkdown(string)

        assertThat(result.contentString).isEqualTo("Hello everyone in the World!")
        assertThat(result.parsedSpans).hasSize(1)
        assertThat(result.parsedSpans[0].spanStyle).isEqualTo(MarkdownParser.Style.Bold)
    }

    @Test
    fun `string with double underscore returns parsed string and 2 Bold spans`() {
        val string = "Hello __everyone in the__ World, __it's a good__ day"
        val result = stringBuilder.parseMarkdown(string)

        assertThat(result.contentString).isEqualTo("Hello everyone in the World, it's a good day")
        assertThat(result.parsedSpans).hasSize(2)
        assertThat(result.parsedSpans[0].spanStyle).isEqualTo(MarkdownParser.Style.Bold)
        assertThat(result.parsedSpans[1].spanStyle).isEqualTo(MarkdownParser.Style.Bold)
    }
}