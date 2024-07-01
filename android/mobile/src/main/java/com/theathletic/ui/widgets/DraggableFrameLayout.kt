package com.theathletic.ui.widgets

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import kotlin.math.max
import kotlin.math.min

class DraggableFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyle, defStyleRes) {

    private val rootChild by lazy { getChildAt(0) }
    private val screenHeight by lazy { context.resources.displayMetrics.heightPixels }
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    /**
     * Called when the dismiss animation completes. Used to delegate to the Fragment/Activity to
     * finish itself.
     */
    lateinit var onSwipeAway: () -> Unit

    /**
     * Represents the progress of the dismissal state from 1f to 0f. At 1f, the view is full screen
     * and the background is fully opaque. At 0f the view is scaled down to 0 and the background
     * is transparent.
     */
    private var dismissProgress: Float = 1f

    /**
     * Stores the y coordinate where the user initially touched so we can calculate the diff in
     * the ACTION_MOVE events.
     */
    var startingY: Float? = null
    private var isDragging = false

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                startingY = ev.y
                false
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    true
                } else {
                    val originY = startingY ?: return false
                    if (ev.y - originY > touchSlop) {
                        isDragging = true
                        true
                    } else {
                        false
                    }
                }
            }
            else -> false
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_MOVE -> {
                startingY?.let {
                    dismissProgress = getProgressForDragDiff(ev.y - it)
                    updateState(dismissProgress)
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                isDragging = false

                val diff = ev.y - startingY!!
                val dismissThreshold = screenHeight / 4

                if (diff > dismissThreshold) {
                    animateAway()
                } else {
                    animateBackToFull()
                }

                startingY = null
            }
        }
        return true
    }

    /**
     * This calculates the progress based on the amount of the screen that's been dragged. Dragging
     * will only account up to 1/3 of the view state change, the animations will finish the rest
     * of the change.
     */
    private fun getProgressForDragDiff(yDiff: Float): Float {
        val diff = 1f - ((yDiff / screenHeight) / 3)
        return max(min(diff, 1f), 0f)
    }

    private fun updateState(progress: Float) {
        val maxTranslationY = screenHeight / 2f

        val zeroToOne = 1f - progress

        background.mutate().alpha = (progress * 255).toInt()
        rootChild.apply {
            translationY = zeroToOne * maxTranslationY
            scaleX = progress
            scaleY = progress
        }
    }

    private fun animateBackToFull() {
        ValueAnimator.ofFloat(dismissProgress, 1f).apply {
            duration = 300
            addUpdateListener {
                updateState(it.animatedValue as Float)
            }
        }.start()
    }

    private fun animateAway() {
        ValueAnimator.ofFloat(dismissProgress, 0f).apply {
            duration = 300
            addUpdateListener {
                updateState(it.animatedValue as Float)
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onSwipeAway()
                }
            })
        }.start()
    }
}