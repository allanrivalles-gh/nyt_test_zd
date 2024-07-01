package com.theathletic.ui.widgets

import android.content.res.Resources
import android.graphics.Typeface
import android.text.Annotation
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.SpannedString
import android.text.style.AbsoluteSizeSpan
import android.text.style.BulletSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.TypefaceSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.annotation.StringRes
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.core.text.HtmlCompat
import androidx.core.text.toSpanned
import com.theathletic.themes.AthColors
import com.theathletic.themes.AthTheme
import com.theathletic.ui.utility.asAndroidColorInt
import timber.log.Timber
import java.util.Locale

const val URL_ANNOTATION_KEY = "url"

/**
 * Allows for basic HTML formatting tags in resource strings, along with custom annotation tags with a url attribute;
 * these are them treated as clickable links.
 *
 * Based on this StackOverflow answer: https://stackoverflow.com/a/72369830/315702
 */
@OptIn(ExperimentalTextApi::class)
@Composable
fun FormattedText(
    @StringRes id: Int,
    style: TextStyle,
    modifier: Modifier = Modifier,
    clickHandler: ((String) -> Unit)? = null
) {
    val annotatedString = annotatedStringResource(id)

    ClickableText(
        text = annotatedString,
        style = style,
        onClick = { offset ->
            annotatedString.getUrlAnnotations(
                start = offset,
                end = offset
            ).firstOrNull()?.let {
                clickHandler?.invoke(it.item.url)
            }
        },
        modifier = modifier
    )
}

/**
 * Allows for basic HTML formatting tags in resource strings, in combination with formatting args to be applied
 * to the string resource. If you are including embedded links, do so using HTML anchor tags with the href param.
 * When a link is clicked, your clickHandler will be invoked with the href value as its String parameter.
 *
 * If you don't need string formatting and you want to use string-resource annotation tags, use the FormattedText()
 * composable instead. (When string formatting is applied, we do a round-trip conversion to and from HTML that
 * strips out the annotation tags from the original string-resource.)
 */
@OptIn(ExperimentalTextApi::class)
@Composable
fun FormattedTextWithArgs(
    @StringRes id: Int,
    style: TextStyle,
    modifier: Modifier = Modifier,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    clickHandler: (String) -> Unit = {},
    formatArgs: Array<Any>
) {
    val annotatedString = annotatedStringResource(id, *formatArgs)

    ClickableText(
        text = annotatedString,
        style = style,
        maxLines = maxLines,
        softWrap = softWrap,
        overflow = overflow,
        onClick = { offset ->
            annotatedString.getUrlAnnotations(
                start = offset,
                end = offset
            ).firstOrNull()?.let {
                clickHandler(it.item.url)
            }
        },
        modifier = modifier,
    )
}

@Composable
@ReadOnlyComposable
private fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}

fun Spanned.toHtmlWithoutParagraphs(): String {
    return HtmlCompat.toHtml(this, HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
        .substringAfter("<p dir=\"ltr\">").substringBeforeLast("</p>")
}

fun Resources.getText(@StringRes id: Int, themedColors: AthColors, vararg args: Any): CharSequence {
    val escapedArgs = args.map {
        if (it is Spanned) it.toHtmlWithoutParagraphs() else it
    }.toTypedArray()
    val resource = SpannedString(getText(id))

    val builder = SpannableString(resource.toString())
    resource.getSpans(0, resource.length, Any::class.java).forEach { span ->
        val start = resource.getSpanStart(span)
        val end = resource.getSpanEnd(span)
        val flag = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE

        if (span is Annotation) {
            if (span.key == "color") {
                themedColors.namedColors[span.value.lowercase(Locale.getDefault())]?.also { themeColor ->
                    builder.setSpan(ForegroundColorSpan(themeColor.asAndroidColorInt), start, end, flag)
                } ?: {
                    Timber.w("Unknown color found in string resource: ${span.value}")
                }
            } else {
                builder.setSpan(span, start, end, flag)
            }
        } else {
            builder.setSpan(span, start, end, flag)
        }
    }
    val tweakedResource = SpannedString(builder.toSpanned())

    val htmlResource = tweakedResource.toHtmlWithoutParagraphs()
    val formattedHtml = String.format(htmlResource, *escapedArgs)
    return HtmlCompat.fromHtml(formattedHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

private fun Color.toHexString(): String {
    return java.lang.String.format("#%06X", 0xFFFFFF and this.toArgb())
}

@Composable
fun annotatedStringResource(@StringRes id: Int, vararg formatArgs: Any): AnnotatedString {
    val resources = resources()
    val density = LocalDensity.current
    val themedColors = AthTheme.colors
    return remember(id, formatArgs) {
        val text = resources.getText(id, themedColors, *formatArgs)
        spannableStringToAnnotatedString(text, density, themedColors)
    }
}

@Composable
fun annotatedStringResource(@StringRes id: Int): AnnotatedString {
    val resources = resources()
    val density = LocalDensity.current
    val colors = AthTheme.colors
    return remember(id) {
        val text = resources.getText(id)
        spannableStringToAnnotatedString(text, density, colors)
    }
}

private fun spannableStringToAnnotatedString(
    text: CharSequence,
    density: Density,
    themeColors: AthColors
): AnnotatedString {
    return if (text is Spanned) {
        buildAnnotatedString {
            append((text.toString()))
            applySpans(text, density)
        }
    } else {
        AnnotatedString(text.toString())
    }
}

@OptIn(ExperimentalTextApi::class)
private fun AnnotatedString.Builder.applySpans(
    text: Spanned,
    density: Density
) {
    text.getSpans(0, text.length, Any::class.java).forEach {
        val start = text.getSpanStart(it)
        val end = text.getSpanEnd(it)
        when (it) {
            is StyleSpan -> addStyle(it.toSpanStyle(), start, end)
            is TypefaceSpan -> addStyle(it.toSpanStyle(), start, end)
            is BulletSpan -> {
                Timber.d("BulletSpan not supported yet")
                addStyle(SpanStyle(), start, end)
            }
            is AbsoluteSizeSpan -> addStyle(it.toSpanStyle(density), start, end)
            is RelativeSizeSpan -> addStyle(it.toSpanStyle(), start, end)
            is StrikethroughSpan -> addStyle(SpanStyle(textDecoration = TextDecoration.LineThrough), start, end)
            is UnderlineSpan -> addStyle(SpanStyle(textDecoration = TextDecoration.Underline), start, end)
            is SuperscriptSpan -> addStyle(SpanStyle(baselineShift = BaselineShift.Superscript), start, end)
            is SubscriptSpan -> addStyle(SpanStyle(baselineShift = BaselineShift.Subscript), start, end)
            is ForegroundColorSpan -> addStyle(it.toForegroundColorSpan(), start, end)
            is URLSpan -> addUrlAnnotation(it.toUrlAnnotation(), start, end)
            is Annotation -> {
                if (it.key == URL_ANNOTATION_KEY) {
                    addUrlAnnotation(
                        urlAnnotation = UrlAnnotation(it.value),
                        start = start,
                        end = end
                    )
                } else {
                    addStringAnnotation(it.key, it.value, start, end)
                }
            }
            else -> addStyle(SpanStyle(), start, end)
        }
    }
}

private fun StyleSpan.toSpanStyle(): SpanStyle {
    return when (style) {
        Typeface.NORMAL -> SpanStyle(fontWeight = FontWeight.Normal, fontStyle = FontStyle.Normal)
        Typeface.BOLD -> SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Normal)
        Typeface.ITALIC -> SpanStyle(fontWeight = FontWeight.Normal, fontStyle = FontStyle.Italic)
        Typeface.BOLD_ITALIC -> SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
        else -> {
            Timber.w("Unsupported style: $style")
            SpanStyle(fontWeight = FontWeight.Normal, fontStyle = FontStyle.Normal)
        }
    }
}

private fun TypefaceSpan.toSpanStyle(): SpanStyle {
    return SpanStyle(
        fontFamily = when (family) {
            FontFamily.SansSerif.name -> FontFamily.SansSerif
            FontFamily.Serif.name -> FontFamily.Serif
            FontFamily.Monospace.name -> FontFamily.Monospace
            FontFamily.Cursive.name -> FontFamily.Cursive
            else -> FontFamily.Default
        }
    )
}

private fun AbsoluteSizeSpan.toSpanStyle(density: Density): SpanStyle {
    with(density) {
        return SpanStyle(fontSize = if (dip) size.dp.toSp() else size.toSp())
    }
}

private fun RelativeSizeSpan.toSpanStyle(): SpanStyle = SpanStyle(fontSize = sizeChange.em)

private fun ForegroundColorSpan.toForegroundColorSpan() = SpanStyle(color = Color(this.foregroundColor))

@OptIn(ExperimentalTextApi::class)
private fun URLSpan.toUrlAnnotation() = UrlAnnotation(url)