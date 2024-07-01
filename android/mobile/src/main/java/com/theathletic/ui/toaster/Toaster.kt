package com.theathletic.ui.toaster

import android.annotation.SuppressLint
import android.app.Activity
import android.view.ContextThemeWrapper
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListenerAdapter
import com.theathletic.databinding.LayoutToasterBinding
import com.theathletic.ui.utility.SimpleOnLayoutChangeListener
import java.util.concurrent.TimeUnit
import timber.log.Timber

object Toaster {

    private const val ANIMATION_DURATION = 300L
    private val STAY_ON_SCREEN_DURATION = TimeUnit.SECONDS.toMillis(4L)

    class RequestState(var isDismissed: Boolean = false)

    private val queue = ToasterQueue()
    @SuppressLint("StaticFieldLeak")
    private var currentRequest: ToasterQueue.Request? = null

    @SuppressLint("ClickableViewAccessibility")
    fun show(
        activity: Activity,
        @StringRes textRes: Int,
        @DrawableRes iconRes: Int? = null,
        @DrawableRes iconMaskRes: Int? = null,
        style: ToasterStyle = ToasterStyle.BASE,
    ) {
        queue.add(ToasterQueue.Request(activity, textRes, iconRes, iconMaskRes, style))
        checkQueue()
    }

    private fun checkQueue() {
        Timber.v("checkQueue")
        if (currentRequest != null) {
            Timber.v("Already showing, wait for animation to end")
            return
        }

        currentRequest = queue.getFirstValidRequest()
        currentRequest?.run()
    }

    private fun ToasterQueue.Request.run() {
        val parent = activity.findViewById<ViewGroup>(android.R.id.content)

        val themedContext = ContextThemeWrapper(activity, style.themeRes)
        val layout = LayoutToasterBinding.inflate(LayoutInflater.from(themedContext), parent, false).apply {
            text = activity.getString(textRes)
            icon = iconRes
            iconMask = iconMaskRes?.let { ContextCompat.getDrawable(themedContext, it) }
        }.root
        parent.addView(layout)

        setupGesturesAndShowView(activity, layout)
    }

    private fun setupGesturesAndShowView(activity: Activity, layout: View) {
        val state = RequestState()

        val gestureListener = ToasterGestureListener(layout, state, this@Toaster::animateToasterOut)
        val gestureDetector = GestureDetector(activity, gestureListener)
        layout.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }

        if (layout.isLaidOut) {
            animateToasterIn(layout, state)
        } else {
            layout.addOnLayoutChangeListener(
                SimpleOnLayoutChangeListener { listener, _ ->
                    layout.removeOnLayoutChangeListener(listener)
                    animateToasterIn(layout, state)
                }
            )
        }
    }

    private fun animateToasterIn(layout: View, state: RequestState) {
        val height = layout.height.toFloat()
        layout.translationY = -height

        ViewCompat.animate(layout)
            .translationY(0f)
            .setDuration(ANIMATION_DURATION)
            .setListener(object : ViewPropertyAnimatorListenerAdapter() {
                override fun onAnimationEnd(view: View) {
                    layout.postDelayed(
                        {
                            if (!state.isDismissed) {
                                animateToasterOut(layout, state)
                            }
                        },
                        STAY_ON_SCREEN_DURATION
                    )
                }
            })
            .start()
    }

    private fun animateToasterOut(layout: View, state: RequestState) {
        if (state.isDismissed) return

        state.isDismissed = true

        ViewCompat.animate(layout)
            .translationY(-layout.height.toFloat())
            .setDuration(ANIMATION_DURATION)
            .setListener(object : ViewPropertyAnimatorListenerAdapter() {
                override fun onAnimationEnd(view: View) {
                    (layout.parent as? ViewGroup)?.removeView(layout)
                    currentRequest = null
                    checkQueue()
                }
            })
            .start()
    }
}