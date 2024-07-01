package com.theathletic.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import com.theathletic.R
import com.theathletic.extension.dpToPx
import com.theathletic.extension.setTint

class GameTimeoutView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val imagePadding = 2
    private val startMarginPaddingPx = context.dpToPx(imagePadding).toInt()

    init {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        }
        orientation = HORIZONTAL
    }

    fun renderIndicators(usedTimeouts: Int, remainingTimeouts: Int) {
        removeAllViews()
        val totalTimeouts = usedTimeouts + remainingTimeouts
        for (i in 0 until totalTimeouts) {
            addView(
                createImageIndicator(i < remainingTimeouts)
            )
        }
    }

    private fun createImageIndicator(highlighted: Boolean) = ImageView(context).apply {
        setImageResource(
            R.drawable.ic_timeout_indicator
        )
        setTint(context, if (highlighted) R.color.ath_yellow else R.color.ath_grey_45)
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            marginStart = startMarginPaddingPx
        }
    }
}