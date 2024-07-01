package com.theathletic.extension

import android.graphics.Typeface
import android.text.Spannable
import android.text.Spanned
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.view.View

fun Spannable.extSetClickableSpanBold(clickString: String, clickSpan: ClickableSpan) {
    fun setSpan(spannable: CharacterStyle) {
        val spanIndex = indexOf(clickString)
        if (spanIndex >= 0) {
            setSpan(spannable, spanIndex, spanIndex + clickString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    // Set clickable Span
    setSpan(clickSpan)

    // Make it BOLD
    setSpan(StyleSpan(Typeface.BOLD))

    // Remove the underline
    setSpan(object : URLSpan(clickString) {
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
        }
    })
}

fun Spannable.extSetClickableSpanUnderlineBold(clickString: String, clickSpan: ClickableSpan) {
    fun setSpan(spannable: CharacterStyle) {
        val spanIndex = indexOf(clickString)
        if (spanIndex >= 0) {
            setSpan(spannable, spanIndex, spanIndex + clickString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    // Set clickable Span
    setSpan(clickSpan)

    // Make it BOLD
    setSpan(StyleSpan(Typeface.BOLD))
}

fun Spannable.extSetClickableSpan(
    clickString: String,
    useBold: Boolean = true,
    removeUnderline: Boolean = true,
    clickAction: () -> Unit
) {
    fun setSpan(spannable: CharacterStyle) {
        val spanIndex = indexOf(clickString)
        if (spanIndex >= 0) {
            setSpan(spannable, spanIndex, spanIndex + clickString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    // Set clickable Span
    setSpan(object : ClickableSpan() {
        override fun onClick(textView: View) {
            clickAction()
        }
    })

    // Make it BOLD
    if (useBold) {
        setSpan(StyleSpan(Typeface.BOLD))
    }

    // Remove the underline
    if (removeUnderline) {
        setSpan(object : URLSpan(clickString) {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        })
    }
}