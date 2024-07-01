package com.theathletic.article.ui

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

class PreserveChildHeightLayout(
    context: Context,
    attributeSet: AttributeSet?
) : ViewGroup(context, attributeSet) {
    private var preservedHeight = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val child = getChildAt(0)

        if (child != null) {
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            // the initial measurements of the child return height of 0
            if (child.measuredHeight != 0) {
                preservedHeight = child.measuredHeight
            }
        }

        setMeasuredDimension(widthMeasureSpec, preservedHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val child = getChildAt(0)

        child?.layout(l, t, r, t + preservedHeight)
    }
}