package com.theathletic.extension

import android.graphics.drawable.Drawable
import android.text.Annotation
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.SpannedString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.theathletic.AthleticApplication
import com.theathletic.R.font
import com.theathletic.utility.ColorUtility
import com.theathletic.utility.text.CustomTypefaceSpan

@Deprecated("Use a resource accessor with a specific context instead")
fun Int.extGetString(): String = AthleticApplication.getContext().getString(this)
@Deprecated("Use a resource accessor with a specific context instead")
fun Int.extGetBoolean(): Boolean = AthleticApplication.getContext().resources.getBoolean(this)
@Deprecated("Use a resource accessor with a specific context instead")
fun Int.extGetString(vararg arguments: Any): String = AthleticApplication.getContext().getString(this, *arguments)
@Deprecated("Use a resource accessor with a specific context instead")
fun Int.extGetPlural(count: Int): String = AthleticApplication.getContext().resources.getQuantityString(this, count, count)
@Deprecated("Use a resource accessor with a specific context instead")
fun Int.extGetPlural(count: Int, value: String): String = AthleticApplication.getContext().resources.getQuantityString(this, count, value)

@Deprecated("Use a resource accessor with a specific context instead")
fun Int.extGetDimensionPixelSize(): Int = AthleticApplication.getContext().resources.getDimensionPixelSize(this)
@Deprecated("Use a resource accessor with a specific context instead")
fun Int.extGetDrawable(): Drawable? = ContextCompat.getDrawable(AthleticApplication.getContext(), this)
@Deprecated("Use a resource accessor with a specific context instead")
fun Int.extGetColor(): Int = ContextCompat.getColor(AthleticApplication.getContext(), this)
@Deprecated("Use a resource accessor with a specific context instead")
fun Int.extGetInt(): Int = AthleticApplication.getContext().resources.getInteger(this)
@Deprecated("Use a resource accessor with a specific context instead")
fun Int.extGetStyledText(vararg arguments: Any): SpannableString {
    val context = AthleticApplication.getContext()
    val spannedString = context.getText(this) as? SpannedString
        ?: return SpannableString(AthleticApplication.getContext().getString(this, *arguments))
    var spannedStringBuilder = SpannableStringBuilder(spannedString)

    arguments.forEachIndexed { index, any ->
        val find = "%${index + 1}\$s"
        var startIndex = spannedStringBuilder.indexOf(find)
        while (startIndex != -1) {
            spannedStringBuilder = spannedStringBuilder.replace(startIndex, startIndex + find.length, any as String)
            startIndex = spannedStringBuilder.indexOf(find)
        }
    }

    val spannableString = SpannableString(spannedStringBuilder)
    val annotations = spannedStringBuilder.getSpans(0, spannedStringBuilder.length, Annotation::class.java)
    annotations?.forEach { annotation ->
        when (annotation.key) {
            "font" -> {
                when (annotation.value) {
                    "bold" -> {
                        val boldSpan = CustomTypefaceSpan("", ResourcesCompat.getFont(context, font.roboto_bold))
                        spannableString.setSpan(boldSpan, spannableString.getSpanStart(annotation), spannableString.getSpanEnd(annotation), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
            }
            "color" -> {
                val colorSpan = ForegroundColorSpan(ColorUtility.getBestColorFromString(annotation.value))
                spannableString.setSpan(colorSpan, spannableString.getSpanStart(annotation), spannableString.getSpanEnd(annotation), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    return spannableString
}