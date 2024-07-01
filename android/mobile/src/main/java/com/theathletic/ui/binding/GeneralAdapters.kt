package com.theathletic.ui.binding

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.databinding.BindingAdapter
import kotlin.math.min

@BindingAdapter(value = ["maxWidthDp", "maxWidthPct"])
fun maxWidth(
    view: View,
    maxWidthDp: Float,
    maxWidthPct: Int
) {
    val resources = view.context.resources

    val screenPctDp = (resources.displayMetrics.widthPixels * (maxWidthPct / 100f)).toInt()

    view.let {
        it.layoutParams = it.layoutParams.apply {
            width = min(maxWidthDp.toInt(), screenPctDp)
        }
    }
}

/**
 * Inspired from this article: https://chris.banes.dev/insets-listeners-to-layouts/
 */
@BindingAdapter("statusBarTopPadding")
fun statusBarTopPadding(view: View, apply: Boolean) {
    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
        val statusInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
        view.updatePadding(top = statusInsets.top)
        insets
    }
    if (view.isAttachedToWindow) {
        view.requestApplyInsets()
    } else {
        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                view.removeOnAttachStateChangeListener(this)
                view.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}