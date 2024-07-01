package com.theathletic.extension

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat

fun View.visibleIf(isVisible: Boolean) {
    visibility = if (isVisible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun Activity.getScreenHeight(): Int {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.heightPixels
}

fun ImageView.setTint(context: Context, @ColorRes colorId: Int) {
    val color = ContextCompat.getColor(context, colorId)
    val colorStateList = ColorStateList.valueOf(color)
    ImageViewCompat.setImageTintList(this, colorStateList)
}

val Int.toDp get() = (this / Resources.getSystem().displayMetrics.density).toInt()