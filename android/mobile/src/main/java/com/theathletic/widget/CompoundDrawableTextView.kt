package com.theathletic.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import androidx.core.graphics.drawable.toBitmap

class CompoundDrawableTextView : androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @SuppressLint("RtlHardcoded")
    fun setDrawable(
        drawable: Drawable?,
        gravity: Int,
        size: Int,
        space: Int? = null,
        tint: Int? = null
    ) {
        tint?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                drawable?.colorFilter = BlendModeColorFilter(it, BlendMode.SRC_IN)
            } else {
                @Suppress("DEPRECATION")
                drawable?.setColorFilter(it, PorterDuff.Mode.SRC_IN)
            }
        }
        val newDrawable = BitmapDrawable(resources, drawable?.toBitmap(size, size, Bitmap.Config.ARGB_8888))

        space?.let { compoundDrawablePadding = space }

        when (gravity) {
            Gravity.LEFT, Gravity.START -> setCompoundDrawablesRelativeWithIntrinsicBounds(newDrawable, null, null, null)
            Gravity.RIGHT, Gravity.END -> setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, newDrawable, null)
            Gravity.TOP -> setCompoundDrawablesRelativeWithIntrinsicBounds(null, newDrawable, null, null)
            Gravity.BOTTOM -> setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, newDrawable)
        }
    }
}