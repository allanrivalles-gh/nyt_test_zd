package com.theathletic.utility

import androidx.viewpager.widget.ViewPager

@Suppress("unused")
object ReportCardsUtility {
    @JvmStatic
    fun createContentPagerTransformer(
        cardWidthWithPadding: Int,
        horizonBottom: Double,
        horizonUpper: Double,
        scaleFactor: Double
    ): androidx.viewpager.widget.ViewPager.PageTransformer {
        return androidx.viewpager.widget.ViewPager.PageTransformer { page, position ->
            var scale = position / scaleFactor

            if (scale > 0)
                scale *= -1
            scale++

            page.scaleX = scale.toFloat()
            page.scaleY = scale.toFloat()

            if (position < horizonBottom) {
                val diff = position - horizonBottom
                val space = cardWidthWithPadding * diff / scaleFactor
                page.translationX = -space.toFloat()
            } else if (position > horizonUpper) {
                val diff = position - horizonUpper
                val space = cardWidthWithPadding * diff / scaleFactor
                page.translationX = -space.toFloat()
            } else
                page.translationX = 0f
        }
    }

    @FunctionalInterface
    interface OnPageSelectListener {
        fun onPageSelect(position: Int)
    }

    fun androidx.viewpager.widget.ViewPager.setOnPageSelectListener(listener: (Int) -> Unit) {
        addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                listener.invoke(position)
            }
        })
    }
}