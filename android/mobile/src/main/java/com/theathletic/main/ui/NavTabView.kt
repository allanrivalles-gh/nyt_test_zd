package com.theathletic.main.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.theathletic.R
import com.theathletic.extension.getColorFromAttr

class NavTabView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val textView: TextView by lazy { findViewById(R.id.text) }
    private val icon: ImageView by lazy { findViewById(R.id.icon) }
    private val liveIndicator: View by lazy { findViewById(R.id.live_indicator) }

    init {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        LayoutInflater.from(context).inflate(R.layout.tab_secondary_nav, this)

        setActive(false)
    }

    fun setText(text: CharSequence?) {
        textView.text = text
    }

    fun setActive(active: Boolean) {
        val color = context.getColorFromAttr(
            if (active) R.attr.colorOnBackground else R.attr.colorOnBackgroundVariant2
        )

        textView.setTextColor(color)
        icon.setColorFilter(color)
    }

    fun showLiveIndicator(show: Boolean) {
        liveIndicator.isVisible = show
    }

    fun setHasMoreIcon(hasArrows: Boolean) {
        icon.isVisible = hasArrows
    }

    fun setFixedWidth(fixedWidth: Boolean) {
        layoutParams = LayoutParams(
            when {
                fixedWidth -> ViewGroup.LayoutParams.MATCH_PARENT
                else -> ViewGroup.LayoutParams.WRAP_CONTENT
            },
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}