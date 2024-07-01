package com.theathletic.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import com.theathletic.R

class TapPagingFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyle, defStyleRes) {

    interface OnScrollTapListener {
        companion object {
            const val SCROLL_LEFT = 1
            const val SCROLL_RIGHT = 2
        }

        fun onScrollTap(direction: Int)
    }

    private var tapZoneWidthPx: Int = 0
    private var tapZoneTopMargin: Int = 0
    private var tapZoneBottomMargin: Int = 0
    private var listener: OnScrollTapListener? = null

    init {
        context.withStyledAttributes(attrs, R.styleable.TapPagingFrameLayout) {
            tapZoneWidthPx = getDimensionPixelSize(R.styleable.TapPagingFrameLayout_tapZoneWidth, 0)
            tapZoneTopMargin = getDimensionPixelSize(R.styleable.TapPagingFrameLayout_tapZoneTopMargin, 0)
            tapZoneBottomMargin = getDimensionPixelSize(R.styleable.TapPagingFrameLayout_tapZoneBottomMargin, 0)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_UP) {
            processTap(ev.x.toInt(), ev.y.toInt())
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun processTap(x: Int, y: Int): Boolean {
        return when {
            (
                (y in 0..tapZoneTopMargin) ||
                    (y in (height - tapZoneBottomMargin)..height)
                ) -> false

            x in 0..tapZoneWidthPx -> {
                listener?.onScrollTap(OnScrollTapListener.SCROLL_LEFT)
                true
            }

            x in (width - tapZoneWidthPx)..width -> {
                listener?.onScrollTap(OnScrollTapListener.SCROLL_RIGHT)
                true
            }

            else -> false
        }
    }

    fun setOnTapPagingListener(listener: OnScrollTapListener) {
        this.listener = listener
    }
}