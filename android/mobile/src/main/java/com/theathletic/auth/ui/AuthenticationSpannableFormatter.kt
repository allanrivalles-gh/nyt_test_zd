package com.theathletic.auth.ui

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.annotation.StringRes

class AuthenticationSpannableFormatter {

    companion object {
        fun configureLoginRegisterSpannableCTA(
            textView: TextView,
            @StringRes fullTextResource: Int,
            @StringRes subtextResource: Int
        ) {
            val spannableString = SpannableString(textView.resources.getString(fullTextResource))
            val subtextString = textView.resources.getString(subtextResource)
            val subtextIndex = spannableString.indexOf(subtextString)

            if (subtextIndex >= 0) {
                val ctaTextSpan = StyleSpan(Typeface.BOLD)
                spannableString.setSpan(
                    ctaTextSpan,
                    subtextIndex,
                    spannableString.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            textView.text = spannableString
        }
    }
}