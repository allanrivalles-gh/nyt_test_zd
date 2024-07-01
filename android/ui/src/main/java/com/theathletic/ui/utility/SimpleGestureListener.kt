package com.theathletic.ui.utility

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

abstract class SimpleGestureListener : GestureDetector.OnGestureListener {

    override fun onSingleTapUp(e: MotionEvent) = false

    override fun onDown(e: MotionEvent) = false

    override fun onLongPress(e: MotionEvent) {
    }

    override fun onShowPress(e: MotionEvent) {
    }
}

class SimpleOnLayoutChangeListener(
    private val block: (View.OnLayoutChangeListener, View) -> Unit
) : View.OnLayoutChangeListener {

    override fun onLayoutChange(
        v: View,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        oldLeft: Int,
        oldTop: Int,
        oldRight: Int,
        oldBottom: Int
    ) {
        block(this, v)
    }
}