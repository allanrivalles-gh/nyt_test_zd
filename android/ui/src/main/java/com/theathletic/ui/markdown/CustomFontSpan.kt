package com.theathletic.ui.markdown

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan

/**
 * Knock-off of [TypefaceSpan] because [TypefaceSpan] constructor using a [Typeface] requires
 * > API 28
 */
class CustomFontSpan(val typeface: Typeface) : MetricAffectingSpan() {

    override fun updateDrawState(textPaint: TextPaint) {
        textPaint.typeface = typeface
    }

    override fun updateMeasureState(textPaint: TextPaint) {
        textPaint.typeface = typeface
    }
}