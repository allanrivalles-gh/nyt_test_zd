package com.theathletic.ui.widgets

import android.content.Context
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.withStyledAttributes
import com.theathletic.R
import java.util.Collections
import kotlin.math.max

/**
 * Most of this has been adapted from this lib: https://github.com/dinuscxj/EllipsizeTextView
 *
 * Added was the ability to style a portion of the ellipsize text to be different from the
 * rest of the text view.
 */

class EllipsizeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle) {

    private var _originalText: CharSequence? = null
    private var _enableUpdateOriginalText = true
    private var _isExactlyMode = true
    private var _maxLines = 0

    private var _ellipsizeText: CharSequence? = null
    private var _ellipsizeFormattableText: String? = null
    private var _ellipsizeFormatTextColor: Int = 0
    private var _ellipsizeFormatTextSize: Float = 0f
    private var _ellipsizeFormatFontFamily: String? = null

    init {
        context.withStyledAttributes(attrs, R.styleable.EllipsizeTextView) {
            _ellipsizeFormattableText = getString(R.styleable.EllipsizeTextView_ellipsizeFormattableText)
            _ellipsizeFormatTextColor = getColor(R.styleable.EllipsizeTextView_ellipsizeFormatTextColor, 0)
            _ellipsizeFormatTextSize = getDimension(R.styleable.EllipsizeTextView_ellipsizeFormatTextSize, 0f)
            _ellipsizeFormatFontFamily = getString(R.styleable.EllipsizeTextView_ellipsizeFormatFontFamily)
            _ellipsizeText = getText(R.styleable.EllipsizeTextView_ellipsizeText)
        }
        _ellipsizeText = if (_ellipsizeText.isNullOrEmpty()) {
            "..."
        } else {
            formatEllipsize(_ellipsizeText)
        }
    }

    override fun setMaxLines(maxLines: Int) {
        if (_maxLines != maxLines) {
            super.setMaxLines(maxLines)
            _maxLines = maxLines
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        text = _originalText
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        _isExactlyMode = MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY
        layout?.let {
            if (isExceedMaxLines(it) || isOutOfBounds(it)) {
                adjustEllipsizeEndText(it)
            }
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        if (_enableUpdateOriginalText) _originalText = text
        super.setText(text, type)
        if (_isExactlyMode) requestLayout()
    }

    fun setEllipsizeText(ellipsizeText: CharSequence) {
        _ellipsizeText = ellipsizeText
    }

    private fun isExceedMaxLines(layout: Layout) = layout.lineCount > _maxLines && _maxLines > 0

    private fun isOutOfBounds(layout: Layout) = layout.height > measuredHeight - paddingBottom - paddingTop

    private fun adjustEllipsizeEndText(layout: Layout) {
        val originalText = _originalText
        val width = layout.width - paddingLeft - paddingRight
        val maxLineCount = max(1, calculateMaxLineCount(layout))
        val lastLineWidth = layout.getLineWidth(maxLineCount - 1).toInt()
        val lastCharIndex = layout.getLineEnd(maxLineCount - 1)
        val suffixWidth = Layout.getDesiredWidth(_ellipsizeText, paint).toInt()

        _enableUpdateOriginalText = false
        text = if (lastLineWidth + suffixWidth > width) {
            val widthDiff = lastLineWidth + suffixWidth - width
            val removedCharCount = calculateEllipsizeEndCharacterCount(
                widthDiff,
                originalText?.subSequence(0, lastCharIndex) ?: ""
            )
            originalText?.subSequence(0, lastCharIndex - removedCharCount)
        } else {
            originalText?.subSequence(0, lastCharIndex)
        }
        append(_ellipsizeText)

        _enableUpdateOriginalText = true
    }

    private fun calculateMaxLineCount(layout: Layout): Int {
        val availableHeight = measuredHeight - paddingTop - paddingBottom
        for (index in 0 until layout.lineCount) {
            if (availableHeight < layout.getLineBottom(index)) return index
        }
        return layout.lineCount
    }

    private fun calculateEllipsizeEndCharacterCount(widthDiff: Int, csText: CharSequence): Int {
        if (csText.isEmpty()) return 0

        val characterStyleRanges = computeCharacterStyleRanges(text)
        val textStr = text.toString()

        var characterIndex: Int/* = text.length*/
        var codePointIndex = textStr.codePointCount(0, text.length)
        var currentRemovedWidth = 0

        while (codePointIndex > 0 && widthDiff > currentRemovedWidth) {
            codePointIndex--
            characterIndex = textStr.offsetByCodePoints(0, codePointIndex)

            // prevent the subString from containing messy code when the given string contains CharacterStyle
            val characterStyleRange = computeCharacterStyleRange(characterStyleRanges, characterIndex)
            if (characterStyleRange != null) {
                characterIndex = characterStyleRange.lower
                codePointIndex = textStr.codePointCount(0, characterIndex)
            }
            currentRemovedWidth = Layout.getDesiredWidth(
                text.subSequence(characterIndex, text.length),
                paint
            ).toInt()
        }

        return text.length - textStr.offsetByCodePoints(0, codePointIndex)
    }

    private fun computeCharacterStyleRange(characterStyleRanges: List<Range<Int>>?, index: Int): Range<Int>? {
        if (characterStyleRanges == null || characterStyleRanges.isEmpty()) {
            return null
        }
        for (characterStyleRange in characterStyleRanges) {
            if (characterStyleRange.contains(index)) {
                return characterStyleRange
            }
        }
        return null
    }

    private fun formatEllipsize(unformatted: CharSequence?): CharSequence? {
        unformatted ?: return unformatted
        _ellipsizeFormattableText?.let { formattableText ->
            val formatted = SpannableString(unformatted)
            val start = unformatted.indexOf(formattableText, 0, true)
            val end = formattableText.length + start
            if (start == -1) return unformatted

            if (_ellipsizeFormatTextColor != 0) {
                formatted.setSpan(
                    ForegroundColorSpan(_ellipsizeFormatTextColor),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            if (_ellipsizeFormatTextSize != 0f) {
                formatted.setSpan(
                    AbsoluteSizeSpan(_ellipsizeFormatTextSize.toInt(), false),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            _ellipsizeFormatFontFamily?.let { font ->
                formatted.setSpan(
                    TypefaceSpan(font),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            return formatted
        }
        return unformatted
    }

    private fun computeCharacterStyleRanges(text: CharSequence): List<Range<Int>>? {
        val ssb = SpannableStringBuilder.valueOf(text)
        val characterStyles = ssb.getSpans(0, ssb.length, CharacterStyle::class.java)
        if (characterStyles == null || characterStyles.isEmpty()) {
            return Collections.emptyList()
        }
        val ranges: MutableList<Range<Int>> = ArrayList()
        for (characterStyle in characterStyles) {
            ranges.add(Range(ssb.getSpanStart(characterStyle), ssb.getSpanEnd(characterStyle)))
        }
        return ranges
    }

    class Range<T : Comparable<T>?>(val lower: T, val upper: T) {

        operator fun contains(value: T): Boolean {
            value?.let {
                val gteLower = it >= lower
                val lteUpper = it < upper
                return gteLower && lteUpper
            } ?: return false
        }
    }
}