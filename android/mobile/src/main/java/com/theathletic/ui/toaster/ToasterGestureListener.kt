package com.theathletic.ui.toaster

import android.view.MotionEvent
import android.view.View
import com.theathletic.ui.utility.SimpleGestureListener

internal class ToasterGestureListener(
    val layout: View,
    val state: Toaster.RequestState,
    val onDismissed: (View, Toaster.RequestState) -> Unit
) : SimpleGestureListener() {

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean = false

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean = when {
        velocityY < 0 -> {
            onDismissed(layout, state)
            true
        }
        else -> false
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        onDismissed(layout, state)
        return true
    }

    override fun onDown(e: MotionEvent) = true
}