package com.theathletic.ui.markdown

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BulletSpan
import android.text.style.URLSpan
import androidx.core.text.HtmlCompat
import com.theathletic.core.R
import com.theathletic.ui.ContentTextSize

object MarkdownRenderer {
    fun fromMarkdown(
        context: Context,
        markdown: CharSequence,
        textSize: ContentTextSize = ContentTextSize.DEFAULT,
        urlOnClickListener: ClickableUrlSpan.OnClickListener? = null
    ): Spanned {
        val replacedString = replaceTags(markdown.toString())

        var spanned = HtmlCompat.fromHtml(
            "<html>$replacedString</html>",
            HtmlCompat.FROM_HTML_MODE_LEGACY or
                HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM,
            null,
            AthleticMarkdownTagHandler(context, textSize)
        )
        urlOnClickListener?.let {
            spanned = setUrlClickListener(spanned, it)
        }
        return postProcess(context, spanned)
    }

    private fun replaceTags(string: String): String {
        return string
            .replaceTag("h5", "ath-heading")
            .replaceTag("h2", "ath-heading")
    }

    private fun String.replaceTag(from: String, to: String): String {
        return this
            .replace("<$from>", "<$to>")
            .replace("</$from>", "</$to>")
    }

    private fun postProcess(
        context: Context,
        spanned: Spanned
    ): Spanned {
        val spans = spanned.getSpans(0, spanned.length, BulletSpan::class.java)

        val spannableBuilder = SpannableStringBuilder(spanned)
        spans?.forEach {
            val start = spannableBuilder.getSpanStart(it)
            val end = spannableBuilder.getSpanEnd(it)

            val resources = context.resources
            val bulletSpan = CustomBulletSpan(
                resources.getDimensionPixelSize(R.dimen.markdown_bullet_point_radius),
                resources.getDimensionPixelSize(R.dimen.markdown_bullet_point_spacing),
                context.getColor(R.color.ath_grey_20)
            )

            spannableBuilder.removeSpan(it)
            spannableBuilder.setSpan(bulletSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }

        return spannableBuilder
    }

    private fun setUrlClickListener(
        spanned: Spanned,
        urlOnClickListener: ClickableUrlSpan.OnClickListener
    ): Spanned {
        val spannableBuilder = SpannableStringBuilder(spanned)

        spanned.getSpans(0, spanned.length, URLSpan::class.java).forEach { span ->
            val start = spannableBuilder.getSpanStart(span)
            val end = spannableBuilder.getSpanEnd(span)
            val flags = spannableBuilder.getSpanFlags(span)

            spannableBuilder.run {
                setSpan(
                    ClickableUrlSpan(
                        span.url,
                        urlOnClickListener
                    ),
                    start,
                    end,
                    flags
                )
                removeSpan(span)
            }
        }
        return spannableBuilder
    }
}