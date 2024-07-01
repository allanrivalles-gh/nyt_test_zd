package com.theathletic.widget.span

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.ClickableSpan

abstract class CodeOfConductEmailClickableSpan : ClickableSpan() {
    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = false
        ds.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        ds.isFakeBoldText = true
    }
}