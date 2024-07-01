package com.theathletic.analytics.impressions

import android.app.Activity
import android.graphics.Rect
import android.os.Handler
import android.view.View
import android.view.ViewTreeObserver
import timber.log.Timber
import java.util.WeakHashMap

/**
 * This class keeps track of when a View's visible amount on the screen changes. This includes
 * entering the screen, leaving the screen, and when the amount on screen changes. A view can
 * register itself using the [registerView] function with a callback for when it's visibility
 * percentage changes.
 *
 * Each [Fragment] should contain its own [ViewVisibilityTracker] so do not share instances between
 * screens.
 *
 * Use in conjunction with [ImpressionCalculator] to easily track impression information.
 */
class ViewVisibilityTracker(
    private val getActivity: () -> Activity
) {
    companion object {
        private const val MS_BETWEEN_VISIBILITY_CHECKS = 100L
    }

    class TrackingInfo(
        var visibilityListener: ((Float) -> Unit)? = null,
        var lastPercentShowing: Float = 0f
    )

    private val trackedViews = WeakHashMap<View, TrackingInfo>()
    private val clipRect = Rect()
    private val handler = Handler()
    private var isVisibilityCheckScheduled = false

    private val visibilityCheck = {
        isVisibilityCheckScheduled = false

        trackedViews.keys.forEach { view ->
            val trackingInfo = trackedViews[view]
            val percentShowing = getVisiblePercentage(view)
            // Only notify if the percent showing has changed
            if (percentShowing != trackingInfo?.lastPercentShowing) {
                trackingInfo?.visibilityListener?.invoke(percentShowing)
            }
            trackingInfo?.lastPercentShowing = percentShowing
        }
    }

    private val preDrawListener = ViewTreeObserver.OnPreDrawListener {
        scheduleVisibilityCheck()
        true
    }

    /**
     * Start listening for visibility changes to a view
     * @param view The view you want to get notified when it's visibility percent changes
     * @param visibilityListener The callback you want to run when the percent changes
     */
    fun registerView(
        view: View,
        visibilityListener: (Float) -> Unit
    ) {
        val trackingInfo = trackedViews.getOrPut(view) {
            scheduleVisibilityCheck()
            TrackingInfo()
        }

        trackingInfo.visibilityListener = visibilityListener
        trackingInfo.lastPercentShowing = 0f
    }

    fun unregisterView(view: View) {
        trackedViews.remove(view)?.visibilityListener?.invoke(0f)
    }

    fun startTracking() {
        val rootView = getActivity().window?.peekDecorView()
        if (rootView == null) {
            Timber.e("Can't start tracking because activity has been released")
            return
        }

        if (rootView.viewTreeObserver.isAlive) {
            // Remove if it already exists to avoid duplicate
            rootView.viewTreeObserver.removeOnPreDrawListener(preDrawListener)
            rootView.viewTreeObserver.addOnPreDrawListener(preDrawListener)
        } else {
            Timber.e("Visibility tracker root view is not alive")
        }
    }

    fun stopTracking() {
        val rootView = getActivity().window?.peekDecorView()
        if (rootView == null) {
            Timber.e("Can't stop tracking because activity has been released")
            return
        }

        rootView.viewTreeObserver.removeOnPreDrawListener(preDrawListener)

        // Mark all tracked views as hidden
        trackedViews.values.forEach {
            it.visibilityListener?.invoke(0f)
        }

        trackedViews.clear()
    }

    private fun scheduleVisibilityCheck() {
        if (isVisibilityCheckScheduled) return

        isVisibilityCheckScheduled = true
        handler.postDelayed(visibilityCheck, MS_BETWEEN_VISIBILITY_CHECKS)
    }

    private fun getVisiblePercentage(view: View?): Float {
        if (view == null || view.visibility != View.VISIBLE || view.parent == null) return 0f
        if (!view.getGlobalVisibleRect(clipRect)) return 0f

        val visibleArea = clipRect.height() * clipRect.width()
        val totalArea = view.height * view.width
        return visibleArea.toFloat() / totalArea.toFloat()
    }
}