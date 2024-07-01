package com.theathletic.widget.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout

/**
 * Workaround AppBarLayout.Behavior for https://issuetracker.google.com/66996774
 *
 *
 * See https://gist.github.com/chrisbanes/8391b5adb9ee42180893300850ed02f2 for example usage.
 *
 *
 * Original issue described here: https://stackoverflow.com/questions/46862910/click-not-working-on-recyclerview-in-coordinatorlayout-when-scrolling
 *
 *
 * Change the package name as you wish.
 */
class FixAppBarLayoutBehavior : AppBarLayout.Behavior {
    constructor() : super()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
        stopNestedScrollIfNeeded(dyUnconsumed, child, target, type)
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        stopNestedScrollIfNeeded(dy, child, target, type)
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: AppBarLayout, ev: MotionEvent): Boolean {
        // This is a little bit of hack. We will basically interrupt any touch on AppBar.
        // This way the user cannot scroll and hide the appBar in case of empty content view.
        return true
    }

    private fun stopNestedScrollIfNeeded(dy: Int, child: AppBarLayout, target: View, type: Int) {
        if (type == ViewCompat.TYPE_NON_TOUCH) {
            val currOffset = topAndBottomOffset
            val isNegativeScrollRange = (dy > 0 && currOffset == -child.totalScrollRange)
            if (dy < 0 && currOffset == 0 || isNegativeScrollRange) {
                ViewCompat.stopNestedScroll(target, ViewCompat.TYPE_NON_TOUCH)
            }
        }
    }
}