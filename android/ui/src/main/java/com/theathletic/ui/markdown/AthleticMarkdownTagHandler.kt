package com.theathletic.ui.markdown

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.BulletSpan
import android.text.style.ForegroundColorSpan
import androidx.core.content.res.ResourcesCompat
import com.theathletic.ui.ContentTextSize
import com.theathletic.ui.R
import org.xml.sax.XMLReader
import java.lang.ref.WeakReference
import kotlin.reflect.KClass

class AthleticMarkdownTagHandler(
    context: Context,
    private val textSize: ContentTextSize
) : Html.TagHandler {

    private val weakContext = WeakReference(context)

    override fun handleTag(
        opening: Boolean,
        tag: String,
        output: Editable,
        xmlReader: XMLReader
    ) {
        if (output !is SpannableStringBuilder) return

        when (tag) {
            "ath-heading" -> handleHeading(opening, output)

            // https://medium.com/ackee/how-to-make-bulletproof-bullet-lists-in-textview-223c54fb21e6
            "ul" -> if (!opening) output.append("\n")
            "li" -> if (Build.VERSION.SDK_INT <= 23) {
                if (opening) {
                    output.append("\n\n")
                }
            } else {
                handleListItem(opening, output)
            }
        }
    }

    private fun handleHeading(opening: Boolean, output: SpannableStringBuilder) {
        if (opening) {
            start(output, HeadingMark())
        } else {
            val font = weakContext.get()?.let { context ->
                ResourcesCompat.getFont(context, R.font.tiempos_headline_regular)
            } ?: Typeface.SERIF
            end(
                output,
                HeadingMark::class,
                CustomFontSpan(font),
                ForegroundColorSpan(weakContext.get()?.getColor(R.color.ath_grey_30) ?: Color.WHITE),
                AbsoluteSizeSpan(textSize.toAbsoluteSize(), true)
            )
        }
    }

    private fun ContentTextSize.toAbsoluteSize() = when (this) {
        ContentTextSize.MEDIUM -> 27
        ContentTextSize.LARGE -> 30
        ContentTextSize.EXTRA_LARGE -> 33
        else -> 24
    }

    private fun handleListItem(opening: Boolean, output: SpannableStringBuilder) {
        if (opening) {
            start(output, BulletPointMark())
        } else {
            end(output, BulletPointMark::class, BulletSpan())
        }
    }
}

private fun start(text: SpannableStringBuilder, mark: Any) {
    val len = text.length
    text.setSpan(mark, len, len, Spannable.SPAN_MARK_MARK)
}

private fun <T : Any> end(
    text: SpannableStringBuilder,
    kind: KClass<T>,
    vararg spans: Any
) {
    val len = text.length
    val obj = text.getSpans(0, text.length, kind.java).lastOrNull()
    val where = text.getSpanStart(obj)
    text.removeSpan(obj)
    if (where != len) {
        for (span in spans) {
            text.setSpan(span, where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}

private class HeadingMark
private class BulletPointMark