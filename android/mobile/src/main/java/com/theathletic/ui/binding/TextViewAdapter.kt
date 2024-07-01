package com.theathletic.ui.binding

import android.graphics.Paint
import android.graphics.Typeface
import android.os.CountDownTimer
import android.os.SystemClock
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import android.widget.Chronometer
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.FontRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter
import com.theathletic.R
import com.theathletic.datetime.Datetime
import com.theathletic.utility.datetime.DateUtilityImpl
import com.theathletic.utility.text.CustomTypefaceSpan
import com.theathletic.widget.CountDownTextView
import java.util.Date

@BindingAdapter("textStyle")
fun setTextStyle(textView: TextView, typeFace: Int) {
    when (typeFace) {
        Typeface.BOLD -> textView.setTypeface(ResourcesCompat.getFont(textView.context, R.font.roboto_bold), Typeface.BOLD)
        Typeface.ITALIC -> textView.setTypeface(ResourcesCompat.getFont(textView.context, R.font.roboto), Typeface.ITALIC)
        Typeface.NORMAL -> textView.setTypeface(ResourcesCompat.getFont(textView.context, R.font.roboto), Typeface.NORMAL)
    }
}

@BindingAdapter("android:nullableText")
fun nullableText(textView: TextView, resource: Int?) {
    textView.text = resource?.let { textView.context.getString(it) } ?: ""
}

@BindingAdapter("android:parameterizedString")
fun parameterizedString(
    textView: TextView,
    value: ParameterizedString?
) {
    if (value == null) {
        textView.text = ""
        return
    }

    textView.text = textView.context.getString(
        value.stringRes,
        *value.parameters.toTypedArray()
    )
}

@BindingAdapter("countDownTimer")
fun countDownTimer(textView: TextView, timeRemaining: Long) {
    object : CountDownTimer(timeRemaining, 1000) {
        val date = Date()
        override fun onTick(millisUntilFinished: Long) {
            textView.text = DateUtilityImpl.formatCountdownDate(date.apply { time = millisUntilFinished })
        }

        override fun onFinish() {
            textView.text = DateUtilityImpl.formatCountdownDate(date.apply { time = 0 })
        }
    }.start()
}

@BindingAdapter("countDownTimer")
fun countDownTimer(textView: CountDownTextView, endDate: Datetime?) {
    textView.countDownTimer?.cancel()
    if (endDate == null) {
        return
    }

    val timeRemaining = endDate.timeMillis - Date().time
    if (timeRemaining <= 0) {
        textView.text = DateUtilityImpl.formatCountdownDate(Date(0))
        textView.countDownTimer = null
        return
    }

    textView.countDownTimer = object : CountDownTimer(timeRemaining, 1000) {
        val date = Date()
        override fun onTick(millisUntilFinished: Long) {
            textView.text = DateUtilityImpl.formatCountdownDate(date.apply { time = millisUntilFinished })
        }

        override fun onFinish() {
            textView.text = DateUtilityImpl.formatCountdownDate(date.apply { time = 0 })
        }
    }.start()
}

@BindingAdapter(value = ["prefixText", "prefixFontRes", "bodyText"])
fun formatPrefixAndBodyTextView(
    textView: TextView,
    prefixText: String,
    @FontRes prefixFontRes: Int,
    bodyText: String
) {
    val spannable = SpannableString(
        Html.fromHtml(
            textView.context.getString(
                R.string.time_prefixed_two_part_string,
                prefixText,
                bodyText
            )
        )
    )

    val prefixSpan = CustomTypefaceSpan(
        "",
        ResourcesCompat.getFont(textView.context, prefixFontRes)
    )

    textView.text = spannable.apply {
        setSpan(
            prefixSpan,
            0,
            prefixText.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}

@BindingAdapter("carouselScalingText")
fun carouselScalingText(textView: TextView, value: String) {
    textView.apply {
        text = value
        setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            textView.resources.getDimension(
                if (value.length < 14) {
                    R.dimen.carousel_font_large
                } else {
                    R.dimen.carousel_font_small
                }
            )
        )
    }
}

@BindingAdapter("isStrikethrough")
fun formatStrikethrough(view: TextView, isStrikethrough: Boolean) {
    if (isStrikethrough) {
        view.paintFlags = view.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    } else {
        view.paintFlags = view.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}

@BindingAdapter("textGravity")
fun formatGravity(view: TextView, textGravity: Int) {
    if (view.gravity != textGravity) {
        view.gravity = textGravity
    }
}

@BindingAdapter(value = ["linkableTags", "tagInteractor"], requireAll = true)
fun linkableTag(
    view: TextView,
    tags: List<LinkableTag>,
    interactor: LinkableTag.Interactor,
) {
    val builder = SpannableStringBuilder()
    val spannedString = tags.joinTo(builder, separator = "  •  ") {
        val span = object : ClickableSpan() {
            override fun onClick(widget: View) {
                interactor.onTagClicked(it.id, it.deeplink)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = true
            }
        }
        SpannableString(it.title).apply {
            setSpan(span, 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    view.text = spannedString
}

@BindingAdapter("running")
fun running(view: Chronometer, isRunning: Boolean) {
    if (isRunning) {
        view.start()
    } else {
        view.stop()
    }
}

@BindingAdapter("startDatetimeMs")
fun startDatetimeMs(view: Chronometer, baseDatetime: Long) {
    val offset = System.currentTimeMillis() - SystemClock.elapsedRealtime()
    val realBase = baseDatetime - offset
    if (view.base != realBase) {
        view.base = realBase
    }
}

@BindingAdapter("drawableTintRes")
fun setDrawableTint(textView: TextView, drawableTintRes: Int) {
    TextViewCompat.setCompoundDrawableTintList(
        textView,
        ContextCompat.getColorStateList(textView.context, drawableTintRes)
    )
}

@BindingAdapter("textSize")
fun setTextSize(textView: TextView, @DimenRes textSize: Int) {
    val size = textView.context.resources.getDimension(textSize)
    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
}

@BindingAdapter("colorSpanColor", "colorSpanSubstring")
fun setColorSpan(
    textView: TextView,
    @ColorRes colorSpanColor: Int,
    colorSpanSubstring: String?
) {
    colorSpanSubstring?.let { substring ->
        val text = textView.text
        val spanStart = text.indexOf(substring)
        val spanEnd = spanStart + substring.length
        val spannable = SpannableString(text)
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(textView.context, colorSpanColor)),
            spanStart,
            spanEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannable
    }
}