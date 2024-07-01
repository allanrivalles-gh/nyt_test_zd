package com.theathletic.components

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontListFontFamily
import androidx.compose.ui.text.font.ResourceFont
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import com.theathletic.themes.AthTextStyle
import com.theathletic.ui.R

@Composable
fun HtmlText(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = MaterialTheme.typography.body1.copy(color = Color.Black),
    onLinkClick: (String) -> Unit = {}
) {
    AndroidView(
        modifier = modifier,
        update = { textView -> textView.text = fromHtml(html = text).apply { setupLink(style, onLinkClick) } },
        factory = { context -> context.createTextView(style) }
    )
}

private fun fromHtml(html: String): Spannable {
    val flags = HtmlCompat.FROM_HTML_MODE_COMPACT or HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM
    return HtmlCompat.fromHtml(html, flags) as Spannable
}

private fun Context.createTextView(textStyle: TextStyle) = TextView(this).apply {
    layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
    )

    typeface = textStyle.getTypeface(context)
    textSize = textStyle.fontSize.value
    gravity = gravity
    linksClickable = true
    movementMethod = LinkMovementMethod.getInstance()
    setLineSpacing(0f, textStyle.lineHeight.value)
    setTextColor(textStyle.color.toArgb())
    setLinkTextColor(textStyle.color.toArgb())
}

fun TextStyle.getTypeface(context: Context): Typeface? {
    val fonts = (fontFamily as? FontListFontFamily)?.fonts
    val font = fonts?.firstOrNull { it.weight == fontWeight } ?: fonts?.firstOrNull()
    val fontRes = (font as? ResourceFont)?.resId ?: R.font.calibre_regular

    return ResourcesCompat.getFont(context, fontRes)
}

private fun Spannable.setupLink(style: TextStyle, onClick: (String) -> Unit) {
    for (span in getSpans(0, length, URLSpan::class.java)) {
        val spanStart = getSpanStart(span)
        val spanEnd = getSpanEnd(span)
        val spanFlags = getSpanFlags(span)

        removeSpan(span)
        setSpan(LinkSpan(url = span.url, onClick = onClick), spanStart, spanEnd, spanFlags)
        // custom type face to change the link style using the text style
        // setSpan(NoUnderlineTextSpan(), spanStart, spanEnd, spanFlags)
    }
}

class LinkSpan(
    url: String,
    private val onClick: (String) -> Unit = {}
) : URLSpan(url) {

    override fun onClick(widget: View) {
        onClick(url)
    }
}

class NoUnderlineTextSpan : UnderlineSpan() {
    override fun updateDrawState(textPaint: TextPaint) {
        textPaint.isUnderlineText = false
    }
}

@Composable
@Preview(showBackground = true)
fun HtmlText_Preview() {
    HtmlText(
        text = "<p>Pose your questions now and our <a class=\"ath_autolink\" href=\"https://theathletic.com/football/team/newcastle-united/\">Newcastle</a> writers George Caulkin and Jacob Whitehead will join in from St James’ Park a couple of hours before kick-off. Then have your say on Eddie Howe’s team sheet, the overall and individual performances and what the result means for the club’s <a class='ath_autolink' href='https://theathletic.com/football/champions-league/'>Champions League</a> hopes.</p>\n",
        style = AthTextStyle.TiemposBody.Medium.Medium.copy(color = Color.Black)
    )
}